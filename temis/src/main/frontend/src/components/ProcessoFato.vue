<template>
  <div>
    <h5 :key="'fato-titulo' + e.id">{{ e.titulo }}</h5>
    <p :key="'fato-texto' + e.id">{{ e.texto }}</p>
    <ol class="mb-3" type="a" v-if="documentos.length > 0">
      <li v-for="doc in documentos" :key="doc.id">
        <processo-actions
          :show-component="$parent.$parent"
          :event="doc.evento"
          :actions="doc.action"
        ></processo-actions>

        {{ doc.evento.tipoDeDocumentoDescr + ": " + doc.descr }}
      </li>
    </ol>
  </div>
</template>
<script>
import ProcessoActions from "./ProcessoActions";

export default {
  props: {
    e: { type: Object },
    event: { type: Array },
    prefixo: { type: String },
  },
  computed: {
    documentos: function() {
      var l = [];
      this.event.forEach((evt) => {
        if (
          evt.evento &&
          evt.evento.referente &&
          evt.evento.referente.id &&
          evt.evento.referente.id == this.e.id &&
          evt.evento.tipoDeDocumento &&
          evt.evento.arquivo &&
          evt.evento.arquivo.id
        ) {
          l.push(evt);
        }
      });
      return l;
    },
  },
  components: {
    "processo-actions": ProcessoActions,
  },
};
</script>
