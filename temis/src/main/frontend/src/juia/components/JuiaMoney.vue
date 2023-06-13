<template>
  <validation-provider
    :rules="validate"
    :immediate="immediate"
    v-slot="{ errors }"
    ref="vp"
  >
    <vue-numeric
      :id="name"
      class="form-control xmyinput"
      v-model="mymodel"
      :name="name"
      :class="{ 'is-invalid': errors.length > 0 }"
      v-bind="$attrs"
      separator="."
      output-type="Number"
      :empty-value="null"
      currency="R$"
      v-bind:precision="2"
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
  computed: {
    mymodel: {
      get() {
        console.log("get");
        if (this.value === undefined || this.value === null) return null;
        return this.value;
      },
      set(d) {
        this.$emit("input", d === null ? {target: {value: undefined}} : {target: {value: d}});
      },
    },
  },
  name: "juia-money",
  props: {
    immediate: { type: Boolean, default: true },
    value: Number,
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
