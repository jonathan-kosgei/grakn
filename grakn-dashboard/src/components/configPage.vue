<!--
Grakn - A Distributed Semantic Database
Copyright (C) 2016  Grakn Labs Limited

Grakn is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Grakn is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
-->

<template>
<div class="container">
    <div class="inline-flex-1"></div>
    <div class="panel-body" v-if="configuration">
        <div class="table-responsive">
            <table class="table table-hover table-stripped">
                <thead>
                    <tr>
                        <th>Config Item</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Hostname</td>
                        <td>{{configuration['server.host']}}</td>
                    </tr>
                    <tr>
                        <td>Server Port</td>
                        <td>{{configuration['server.port']}}</td>
                    </tr>
                    <tr>
                        <td>Graph Factory</td>
                        <td>{{configuration['factory.internal']}}</td>
                    </tr>
                    <tr>
                        <td>Graph Analytics Factory</td>
                        <td>{{configuration['factory.analytics']}}</td>
                    </tr>
                    <tr>
                        <td>Default Keyspace</td>
                        <td>{{configuration['graph.default-keyspace']}}</td>
                    </tr>
                    <tr>
                        <td>Graph Sharding Threshold</td>
                        <td>{{configuration['graph.sharding-threshold']}}</td>
                    </tr>
                    <tr>
                        <td>Server Executor Threads</td>
                        <td>{{configuration['loader.threads']}}</td>
                    </tr>
                    <tr>
                        <td>Engine assets directory</td>
                        <td>{{configuration['server.static-file-dir']}}</td>
                    </tr>
                    <tr>
                        <td>Log Directory</td>
                        <td>{{configuration['log.dirs']}}</td>
                    </tr>
                    <tr>
                        <td>Logging Level</td>
                        <td>{{configuration['log.level']}}</td>
                    </tr>
                    <tr>
                        <td>Batch size</td>
                        <td>{{configuration['blockingLoader.batch-size']}}</td>
                    </tr>
                    <tr>
                        <td>Default Keyspace</td>
                        <td>{{configuration['graph.default-keyspace']}}</td>
                    </tr>
                    <tr>
                        <td>Repeat Commits</td>
                        <td>{{configuration['loader.repeat-commits']}}</td>
                    </tr>
                    <tr>
                        <td>HAL builder degree</td>
                        <td>{{configuration['halBuilder.degree']}}</td>
                    </tr>
                    <tr>
                        <td>Version</td>
                        <td>{{configuration['grakn.version']}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div class="panel panel-filled panel-c-danger" v-else>
        <div class="panel-heading">
            Could not connect to Grakn
        </div>
        <div class="panel-body">
            Have you tried turning it off and on again?
            <pre class="error-pre" v-show="errorMessage">{{errorMessage}}</pre>
        </div>
        <div class="panel-footer">
            <button @click="loadConfig" class="btn btn-default">Retry Connection<i class="pe-7s-refresh"></i></button>
        </div>
    </div>
    <div class="inline-flex-1"></div>
</div>
</template>

<style scoped>
table {
    border-collapse: separate;
    border-spacing: 15px;
}

th {
    font-weight: bold;
}

td {
    border-bottom: 1px solid #606060;
    padding: 5px;
}

.container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    height: 100%;
    width: 100%;
    position: absolute;
}

.table-responsive {
    margin-top: 10px;
}

.inline-flex-1 {
    display: inline-flex;
    flex: 1;
}

.panel-body {
    display: inline-flex;
    justify-content: center;
    flex: 3;
    background-color: green;
    margin-top: 10px;
    margin-bottom: 25px;
    background-color: #0f0f0f;
    margin-left: 15px;
    margin-right: 15px;
    padding-top: 30px;
    overflow: scroll;
}
</style>

<script>
import EngineClient from '../js/EngineClient';

export default {
  name: 'ConfigurationPage',
  data: () => ({
    configuration: undefined,
    errorMessage: undefined,
  }),

  created() {},
  mounted() {
    this.$nextTick(function () {
      this.loadConfig();
    });
  },

  methods: {
    showError(msg) {
      this.response = undefined;
      this.errorMessage = msg;
    },
    loadConfig() {
      EngineClient.getConfig().then((resp) => {
        this.configuration = JSON.parse(resp);
      }, (err) => {
        this.showError(err);
      });
    },
  },

};
</script>
