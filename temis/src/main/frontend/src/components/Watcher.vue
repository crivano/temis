<template><input type="hidden" name="watcher" :value="isInvalid"/></template>
<script>
export default {
  props: {
    value: { type: Boolean },
  },
  computed: {
    isInvalid: function() {
      return this.value;
    },
  },
  watch: {
    value: function(newVal, oldVal) {
      this.$parent.$parent.$parent.$parent.$parent.$emit(
        "validation-changed",
        newVal
      );
      // watch it
      console.log("Prop changed: ", newVal, " | was: ", oldVal);
    },
  },
};
</script>
