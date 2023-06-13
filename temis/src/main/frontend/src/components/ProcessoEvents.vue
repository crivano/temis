<template>
  <div>
    <ol class="mb-3" type="a" v-if="filteredEvents.length > 0">
      <li
        v-for="e in filteredEvents"
        :key="e.id"
        :class="{
          'event-inactive':
            e.tipo === 'Cancelamento' ||
            e.idCanceladora !== undefined ||
            e.idDesabilitadora !== undefined,
        }"
        :title="
          e.dt.substring(0, 10) +
            ' ' +
            e.dt.substring(11, 19) +
            ' - ' +
            e.agente
        "
      >
        <processo-actions
          :show-component="showComponent"
          :event="e.evento"
          :actions="e.action"
        ></processo-actions>
        <span v-html="e.tipo"></span>
        <span v-if="e.tipo && e.descr">: </span>
        <span v-html="e.descr"></span>
      </li>
    </ol>
  </div>
</template>
<script>
import ProcessoActions from "./ProcessoActions";

export default {
  props: {
    showComponent: { type: Object },
    event: { type: Object },
    events: { type: Array },
  },
  computed: {
    filteredEvents: function() {
      var l = [];
      this.events.forEach((evt) => {
        if (
          evt.evento &&
          evt.evento.referente &&
          evt.evento.referente.id &&
          evt.evento.referente.id == this.event.id
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
