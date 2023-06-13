<template>
  <processo-norma-item :tree="tree"></processo-norma-item>
</template>
<script>
import ProcessoNormaItem from "./ProcessoNormaItem";
export default {
  name: "processo-norma",
  props: {
    norma: { type: Object },
  },
  computed: {
    tree: function() {
      var o = { norma: this.norma };
      var n = this.norma;
      while (n.normaPai) {
        o = { norma: n.normaPai, filho: o };
        n = n.normaPai;
      }
      return o;
    },
  },
  components: {
    "processo-norma-item": ProcessoNormaItem,
  },
};
</script>
