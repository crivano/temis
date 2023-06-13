<template>
  <div>
    <h5 :key="'pedido-titulo' + e.id">
      <span v-html="e.tipoDePedidoDescr" />
      <span v-if="e.verba" v-html="' - ' + e.verbaDescr" />
      <span v-if="e.valor" v-html="': ' + valorFormatado" />
      <span v-if="e.liminar == 'SIM'" v-html="' (Liminar)'" />
    </h5>
    <p :key="'pedido-texto' + e.id">
      {{ e.texto }}
    </p>
  </div>
</template>
<script>
import UtilsBL from "../utils/utils.js";

export default {
  props: {
    e: { type: Object },
  },
  computed: {
    valorFormatado: function() {
      if (this.e.valor) return "R$" + UtilsBL.formatMoney(this.e.valor);
      return undefined;
    },
  },
};
</script>
