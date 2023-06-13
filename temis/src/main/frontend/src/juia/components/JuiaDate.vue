<template>
  <div>
    <validation-provider :rules="(validate ? validate : '') + (required === 'true' ? 'required' : '')"
      :immediate="immediate" v-slot="{ errors }" ref="vp">
      <span :class="{ 'juia-data-validation': true, 'is-invalid': errors.length > 0 }">
        {{ /* used to be above the validation provider, but seems to work here just fine. */ }}
        <v-date-picker v-if="true" locale="pt" mode="single" v-model="mymodel" />

        <input type="hidden" class="form-control xmyinput" v-bind:value="value"
          :class="{ 'is-invalid': errors.length > 0 }" />
        <div class="invalid-feedback" v-if="errors[0] != 'Preenchimento obrigatório'">
          {{ errors[0] }}
        </div>
      </span>
    </validation-provider>
  </div>
</template>

<script>
// import { mask } from "vue-the-mask";
export default {
  data() {
    return {
      test: undefined,
    };
  },
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
        if (!this.value) return null;
        var date = new Date(this.value);
        var newDate = new Date();
        newDate.setTime(date.getTime() + date.getTimezoneOffset() * 60 * 1000);
        return newDate;
      },
      set(d) {
        this.$emit("input", this.format(d));
      },
    },
  },
  name: "juia-date",
  props: {
    immediate: { type: Boolean, default: true },
    value: String,
    label: String,
    name: String,
    edit: { type: Boolean, default: true },
    error: String,
    validate: String,
    required: String
  },
  methods: {
    format: function (d) {
      if (!d || d === "") return undefined;
      var month = "" + (d.getMonth() + 1),
        day = "" + d.getDate(),
        year = d.getFullYear();

      if (month.length < 2) month = "0" + month;
      if (day.length < 2) day = "0" + day;

      return [year, month, day].join("-");
    },
    //     input: function(e) {
    //       e = this.format(e);
    // //      this.$emit("input", { target: { value: e } });
    //       // this.$emit("input", e);
    //     },
    //     change: function(e) {
    //       e = this.format(e);
    //       this.$emit("change", { target: { value: e } });
    //     },
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

<style>
span.juia-data-validation.is-invalid span input {
  border-color: #dc3545;
}
</style>