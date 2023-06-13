<template>
  <b-modal
    ok-only
    ok-variant="secondary"
    ok-title="Fechar"
    size="xl"
    v-model="mymodel"
    title="Busca em Leis"
  >
    <template v-slot:modal-title="{}">
      <!-- Emulate built in modal header close button action -->
      <h5>Busca Textual <a href="https://seeklex.crivano.com">Seeklex</a></h5>
    </template>
    <b-input-group class="mb-2">
      <template v-slot:append>
        <b-button
          id="seeklex-pesquisar"
          @click.prevent="onEnter"
          variant="primary"
          ><i class="fas fa-search"></i> Pesquisar</b-button
        >
        <b-button
          id="seeklex-voltar"
          v-if="history.length"
          @click.prevent="
            query = history.pop();
            fetch();
          "
          variant="secondary"
          ><i class="fas fa-arrow-left"></i> Voltar</b-button
        >
      </template>
      <input
        type="text"
        id="seeklex-query"
        class="form-control"
        v-model="query"
        v-on:keyup.enter="onEnter"
        name="query"
        placeholder="Escreva um ou mais termos para serem buscados"
      />
    </b-input-group>
    <div>
      <processo-seeklex-item
        v-for="item in items"
        :value="item"
        :key="item.id"
        :top-level="true"
      ></processo-seeklex-item>
    </div>
    <div v-if="corpus && !query" style="text-align: center;">
      <table class="table table-sm table-striped">
        <thead class="thead-dark">
          <tr>
            <th scope="col" style="text-align: left;">
              Normas Jurídicas Disponíveis
            </th>
            <th scope="col">Origem</th>
            <th scope="col">Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in corpus" :key="d.id">
            <td style="text-align: left;">
              <a href @click.prevent="localizarNorma(d.id + ' ind:1')">{{
                d.name
              }}</a>
            </td>
            <td>{{ d.source }}</td>
            <td v-if="d.firstUnsucessfulCheck">desatualizada</td>
            <td v-if="!d.firstUnsucessfulCheck">ok</td>
          </tr>
        </tbody>
      </table>
    </div>
  </b-modal>
</template>
<script>
//import lodash from "lodash";
import ProcessoSeeklexItem from "./ProcessoSeeklexItem";

export default {
  props: {
    value: { type: Boolean },
  },
  data() {
    return {
      corpus: undefined,
      query: undefined,
      last: undefined,
      items: undefined,
      history: [],
    };
  },
  mounted() {
    this.$http.get("https://seeklex.crivano.com/api/v1/corpus").then(
      (response) => {
        this.corpus = response.data.list;
      },
      (response) => {
        console.log(response);
      }
    );
  },
  computed: {
    mymodel: {
      get() {
        return this.value;
      },
      set(d) {
        this.$emit("input", d);
      },
    },
  },
  methods: {
    localizarNorma: function(id) {
      this.history.push(this.query);
      this.query = "nav:" + id;
      this.fetch();
    },
    onEnter: function() {
      this.history.push(this.last);
      this.fetch();
    },
    fetch: function() {
      this.last = this.query;
      this.$http
        .get(
          "https://seeklex.crivano.com/api/v1/query?q=" +
            encodeURIComponent(this.query)
        )
        .then(
          (response) => {
            this.items = response.data.list;
          },
          (response) => {
            console.log(response);
          }
        );
    },
  },
  components: {
    "processo-seeklex-item": ProcessoSeeklexItem,
  },
};
</script>
