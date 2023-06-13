<template>
  <div>
    <div class="row">
      <div class="col">
        <h2 class="mb-3">{{ data.referencia }}</h2>
        <norma-item :norma="data"></norma-item>
      </div>
    </div>
  </div>
</template>

<script>
import NormaItem from "./NormaItem";

export default {
  props: {
    data: { type: Object },
  },
  components: {
    "norma-item": NormaItem,
  },
};
</script>
