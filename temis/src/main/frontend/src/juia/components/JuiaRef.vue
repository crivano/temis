<template>
  <div>
    <label v-if="label" :for="name">{{ label }}</label>
    <div v-if="!edit">{{ nome }}</div>
    <validation-provider
      :rules="disabled ? '' : (validate ? validate : '') + (required === 'true' ? 'required' : '')"
      :immediate="immediate"
      v-slot="{ errors }"
    >
    <span :class="{ 'juia-ref-validation': true, 'is-invalid': errors.length > 0 }">
      <v-selectpage
        :data="url"
        key-field="key"
        show-field="firstLine"
        search-field="key"
        v-if="edit"
        :id="name"
        :disabled="disabled"
        v-bind:value="myValue"
        v-on:input="$emit('input', $event)"
        v-on:change="$emit('change', $event)"
        v-on:values="$emit('values', $event)"
        :name="name"
        :class="{ 'is-invalid': errors.length > 0 }"
        v-bind="$attrs"
        :result-format="resultFormat"
      >
      </v-selectpage>
    </span>
      <div
        class="invalid-feedback"
        v-if="errors[0] != 'Preenchimento obrigatório'"
      >
        {{ errors[0] }}
      </div>
    </validation-provider>
  </div>
</template>

<script>
export default {
  name: "juia-ref",
  props: {
    disabled: { type: Boolean, default: false },
    immediate: { type: Boolean, default: true },
    value: Object,
    label: String,
    name: String,
    locator: String,
    edit: { type: Boolean, default: true },
    validate: String,
    required: String,
    searchField: { type: String, default: "key" },
  },
  data() {
    return {};
  },
  computed: {
    myValue: function() {
      return this.value ? "" + this.value.key : undefined;
    },
    url: function() {
      return (
        process.env.VUE_APP_API_URL + "/" + this.locator + "/juia/selectpage"
      );
    },
    nome: function() {
      for (var i = 0; i < this.list.length; i++) {
        if (this.list[i].id === this.value) return this.list[i].nome;
      }
      return "";
    },
  },
  methods: {
    resultFormat(resp) {
      if (resp && resp.data) return resp.data;
    },
  },
};
</script>

<style>
span.juia-ref-validation.is-invalid div div div.sp-base {
  border-color: #dc3545 !important;
}

span.sp-placeholder {
  display: block;
  white-space: nowrap;
  overflow: hidden;
}
</style>