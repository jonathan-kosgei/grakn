/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016  Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package ai.grakn.test.graql.query;

import ai.grakn.concept.Concept;
import ai.grakn.concept.ResourceType;
import ai.grakn.exception.GraphOperationException;
import ai.grakn.exception.GraqlQueryException;
import ai.grakn.exception.InvalidGraphException;
import ai.grakn.graphs.MovieGraph;
import ai.grakn.graql.Graql;
import ai.grakn.graql.MatchQuery;
import ai.grakn.graql.QueryBuilder;
import ai.grakn.test.GraphContext;
import ai.grakn.util.ErrorMessage;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.stream.Stream;

import static ai.grakn.graql.Graql.label;
import static ai.grakn.graql.Graql.var;
import static ai.grakn.util.ErrorMessage.INVALID_VALUE;
import static ai.grakn.util.ErrorMessage.NO_PATTERNS;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;

public class QueryErrorTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @ClassRule
    public static final GraphContext rule = GraphContext.preLoad(MovieGraph.get());

    @ClassRule
    public static final GraphContext empty = GraphContext.empty();

    private QueryBuilder qb;

    @Before
    public void setUp() {
        qb = rule.graph().graql();
    }

    @Test
    public void testErrorNonExistentConceptType() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage("film");
        //noinspection ResultOfMethodCallIgnored
        qb.match(var("x").isa("film")).stream();
    }

    @Test
    public void testErrorNotARole() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(allOf(containsString("role"), containsString("person"), containsString("isa person")));
        //noinspection ResultOfMethodCallIgnored
        qb.match(var("x").isa("movie"), var().rel("person", "y").rel("x")).stream();
    }

    @Test
    public void testErrorNonExistentResourceType() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage("thingy");
        qb.match(var("x").has("thingy", "value")).delete("x").execute();
    }

    @Test
    public void testErrorNotARelation() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(allOf(
                containsString("relation"), containsString("movie"), containsString("separate"), containsString(";")));
        //noinspection ResultOfMethodCallIgnored
        qb.match(var().isa("movie").rel("x").rel("y")).stream();
    }

    @Test
    public void testErrorInvalidNonExistentRole() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(ErrorMessage.NOT_A_ROLE_TYPE.getMessage("character-in-production", "character-in-production"));
        //noinspection ResultOfMethodCallIgnored
        qb.match(var().isa("has-cast").rel("character-in-production", "x")).stream();
    }

    @Test
    public void testErrorMultipleIsa() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(allOf(
                containsString("abc"), containsString("isa"), containsString("person"), containsString("has-cast")
        ));
        //noinspection ResultOfMethodCallIgnored
        qb.match(var("abc").isa("person").isa("has-cast"));
    }

    @Test
    public void testErrorHasGenreQuery() {
        // 'has genre' is not allowed because genre is an entity type
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(ErrorMessage.MUST_BE_RESOURCE_TYPE.getMessage("genre"));
        //noinspection ResultOfMethodCallIgnored
        qb.match(var("x").isa("movie").has("genre", "Drama")).stream();
    }

    @Test
    public void testExceptionWhenNoSelectVariablesProvided() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage("select");
        //noinspection ResultOfMethodCallIgnored
        qb.match(var("x").isa("movie")).select();
    }

    @Test
    public void testExceptionWhenNoPatternsProvided() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(NO_PATTERNS.getMessage());
        //noinspection ResultOfMethodCallIgnored
        qb.match();
    }

    @Test
    public void testExceptionWhenNullValue() {
        exception.expect(NullPointerException.class);
        //noinspection ResultOfMethodCallIgnored
        var("x").val(null);
    }

    @Test
    public void testExceptionWhenNoHasResourceRelation() throws InvalidGraphException {
        // Create a fresh graph, with no has between person and name
        QueryBuilder emptyQb = empty.graph().graql();
        emptyQb.insert(
                label("person").sub("entity"),
                label("name").sub("resource").datatype(ResourceType.DataType.STRING)
        ).execute();

        exception.expect(GraphOperationException.class);
        exception.expectMessage(allOf(
                containsString("person"),
                containsString("name")
        ));
        emptyQb.insert(var().isa("person").has("name", "Bob")).execute();
    }

    @Test
    public void testExceptionInstanceOfRoleType() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(allOf(
                containsString("actor"),
                containsString("role")
        ));
        //noinspection ResultOfMethodCallIgnored
        qb.match(var("x").isa("actor")).stream();
    }

    @Test
    public void testAdditionalSemicolon() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(allOf(containsString("id"), containsString("plays product-type")));
        qb.parse(
                "insert " +
                        "tag-group sub role; product-type sub role;" +
                        "category sub entity, plays tag-group; plays product-type;"
        ).execute();
    }

    @Test
    public void testGetNonExistentVariable() {
        MatchQuery query = qb.match(var("x").isa("movie"));

        Stream<Concept> concepts = query.get("y");

        exception.expect(GraqlQueryException.class);
        exception.expectMessage(ErrorMessage.VARIABLE_NOT_IN_QUERY.getMessage(Graql.var("y")));

        //noinspection ResultOfMethodCallIgnored
        concepts.count();
    }

    @Test
    public void whenUsingInvalidResourceValue_Throw() {
        exception.expect(GraqlQueryException.class);
        exception.expectMessage(INVALID_VALUE.getMessage(qb.getClass()));
        qb.match(var("x").val(qb));
    }
}
