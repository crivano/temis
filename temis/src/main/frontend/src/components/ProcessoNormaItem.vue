<template>
  <span>
    {{ tree.norma.descrCompleta }}
    <ul style="list-style-type:none;" v-if="tree.filho">
      <li style="list-style-type:none;">
        <processo-norma-item :tree="tree.filho"></processo-norma-item>
      </li>
    </ul>
  </span>
</template>
<script>
export default {
  name: "processo-norma-item",
  props: {
    tree: { type: Object },
  },
};
</script>
