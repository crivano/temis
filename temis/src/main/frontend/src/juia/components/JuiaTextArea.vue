<template>
  <validation-provider
    :rules="validate"
    :immediate="immediate"
    v-slot="{ errors }"
    ref="vp"
  >
    <textarea
      :id="name"
      class="form-control xmyinput"
      v-bind:value="value"
      v-on:input="$emit('input', $event)"
      v-on:change="$emit('change', $event)"
      :name="name"
      :class="{ 'is-invalid': errors.length > 0 }"
      v-bind="$attrs"
    />
    <div
      class="invalid-feedback"
      v-if="errors[0] != 'Preenchimento obrigatório'"
    >
      {{ errors[0] }}
    </div>
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
  name: "juia-text-area",
  props: {
    immediate: { type: Boolean, default: true },
    type: { type: String, default: "text" },
    value: String,
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
