import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersist from 'vuex-persist'

const vuexPersist = new VuexPersist({
    key: 'temis-data',
    storage: window.localStorage,
})
Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        model: undefined
    },
    mutations: {
        model(state, val) {
            state.model = val
        },
    },
    actions: {
        inicializar({
            commit
        }) {
            Vue.http.get("http://localhost:8080/temis/app/resources/model.json").then(
                response => {
                    var d = response.data;
                    commit('model', d);
                },
                () => {}
            );
        },
        consultarCep({
            commit,
            state
        }) {
            Vue.http.get("https://viacep.com.br/ws/" + state.entrega.cep + "/json/").then(
                response => {
                    var d = response.data;
                    if (!d.localidade) {
                        commit('localidade', "");
                        commit('bairro', "");
                        commit('logradouro', "");
                        commit('complemento', "");
                        return;
                    }
                    commit('localidade', d.localidade + "/" + d.uf);
                    commit('bairro', d.bairro);
                    commit('logradouro', d.logradouro);
                    // commit('complemento', d.complemento);
                    commit('ultimoCepPesquisado', d.cep);
                },
                () => {}
            );
        }
    },
    plugins: [vuexPersist.plugin]
})