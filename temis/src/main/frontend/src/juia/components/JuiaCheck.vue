<template>
  <validation-provider
    :rules="validate"
    :immediate="immediate"
    v-slot="{ errors }"
    ref="vp"
  >
    <input
      type="checkbox"
      :id="name"
      class="form-check-input"
      v-bind:checked="checked"
      v-on:input="$emit('input', $event)"
      v-on:change="$emit('change', $event)"
      :name="name"
      :class="{ 'is-invalid': errors.length > 0 }"
      v-bind="$attrs"
      :true-value="true"
      :false-value="false"
    />
    <label :for="name" class="form-check-label">
      {{ label }}
    </label>
  </validation-provider>
</template>

<script>
// import { mask } from "vue-the-mask";
export default {
  mounted() {
    this.$on("change", () => {
      this.$refs.vp
        .validate()
        .then((r) => this.$emit(r.valid ? "valid" : "invalid"));
    });
  },
  name: "juia-check",
  props: {
    immediate: { type: Boolean, default: true },
    checked: Boolean,
    label: String,
    name: String,
    themask: [String, Array],
    edit: { type: Boolean, default: true },
    error: String,
    validate: String,
  },
  data() {
    return {};
  },
  //,
  //directives: {
  //  themask: mask
  //}
};
</script>
<style scoped>
.cfg {
  margin-bottom: 0.5em;
}
.myinput {
  background-image: none !important;
}
</style>
