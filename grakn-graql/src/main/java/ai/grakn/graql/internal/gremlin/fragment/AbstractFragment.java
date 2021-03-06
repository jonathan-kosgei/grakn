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

package ai.grakn.graql.internal.gremlin.fragment;

import ai.grakn.graql.Var;
import ai.grakn.graql.internal.gremlin.EquivalentFragmentSet;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

import static ai.grakn.util.CommonUtil.optionalToStream;

abstract class AbstractFragment implements Fragment{

    // TODO: Find a better way to represent these values (either abstractly, or better estimates)
    static final long NUM_INSTANCES_PER_TYPE = 100;
    static final long NUM_INSTANCES_PER_SCOPE = 100;
    static final long NUM_RELATIONS_PER_CASTING = 10;
    static final long NUM_SUBTYPES_PER_TYPE = 3;
    static final long NUM_CASTINGS_PER_INSTANCE = 3;
    static final long NUM_RELATIONS_PER_INSTANCE = NUM_RELATIONS_PER_CASTING * NUM_CASTINGS_PER_INSTANCE;
    static final long NUM_SCOPES_PER_INSTANCE = 3;
    static final long NUM_TYPES_PER_ROLE = 3;
    static final long NUM_ROLES_PER_TYPE = 3;
    static final long NUM_ROLE_PLAYERS_PER_RELATION = 2;
    static final long NUM_ROLE_PLAYERS_PER_ROLE = 1;
    static final long NUM_RESOURCES_PER_VALUE = 2;

    private final Var start;
    private final Optional<Var> end;
    private final ImmutableSet<Var> varNames;
    private EquivalentFragmentSet equivalentFragmentSet = null;

    AbstractFragment(Var start) {
        this.start = start;
        this.end = Optional.empty();
        this.varNames = ImmutableSet.of(start);
    }

    AbstractFragment(Var start, Var end, Var... others) {
        this.start = start;
        this.end = Optional.of(end);
        this.varNames = ImmutableSet.<Var>builder().add(start).add(end).add(others).build();
    }

    AbstractFragment(Var start, Var end, Var other, Var... others) {
        this.start = start;
        this.end = Optional.of(end);
        this.varNames = ImmutableSet.<Var>builder().add(start).add(end).add(other).add(others).build();
    }

    static Var[] optionalVarToArray(Optional<Var> var) {
        return optionalToStream(var).toArray(Var[]::new);
    }

    @Override
    public final EquivalentFragmentSet getEquivalentFragmentSet() {
        Preconditions.checkNotNull(equivalentFragmentSet, "Should not call getEquivalentFragmentSet before setEquivalentFragmentSet");
        return equivalentFragmentSet;
    }

    @Override
    public final void setEquivalentFragmentSet(EquivalentFragmentSet equivalentFragmentSet) {
        this.equivalentFragmentSet = equivalentFragmentSet;
    }

    @Override
    public final Var getStart() {
        return start;
    }

    @Override
    public final Optional<Var> getEnd() {
        return end;
    }

    @Override
    public Set<Var> getDependencies() {
        return ImmutableSet.of();
    }

    @Override
    public Set<Var> getVariableNames() {
        return varNames;
    }

    @Override
    public String toString() {
        return start + getName() + end.map(Object::toString).orElse("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractFragment that = (AbstractFragment) o;

        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
