<template>
  <div>
    <h5 :key="'parte-titulo' + event.id">
      {{ event.pessoa.nome }} ({{
        event.tipoDeParteDescr.substring(prefixo.length + 3)
      }})
    </h5>
    <p :key="'parte-texto' + event.id">
      {{ event.pessoa.descrCompleta
      }}<span v-if="event.endereco" v-html="', Endereço: ' + event.endereco.descrCompleta"></span>
    </p>
    <processo-events
      :show-component="showComponent"
      :event="event"
      :events="events"
    ></processo-events>
  </div>
</template>
<script>
import ProcessoEvents from "./ProcessoEvents";

export default {
  props: {
    showComponent: { type: Object },
    event: { type: Object },
    events: { type: Array },
    prefixo: { type: String },
  },
  computed: {
    documentos: function() {
      var l = [];
      this.events.forEach((evt) => {
        if (
          evt.evento &&
          evt.evento.referente &&
          evt.evento.referente.id &&
          evt.evento.referente.id == this.event.id &&
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
    "processo-events": ProcessoEvents,
  },
};
</script>
