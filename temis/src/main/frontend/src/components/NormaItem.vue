<template>
  <div>
    <norma-item v-if="norma.normaPai" :norma="norma.normaPai"></norma-item>
    <dl>
      <dt>{{norma.tipo}}: {{ norma.codigo }}</dt>
      <dd>{{ norma.texto }}</dd>

      <!-- other terms and definitions -->
    </dl>
  </div>
</template>
<script>
export default {
  name: "norma-item",
  props: {
    norma: { type: Object },
  },
};
</script>
