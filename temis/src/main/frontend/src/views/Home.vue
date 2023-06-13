<template>
  <div class="home">
   <input type="text" name="oi1"/>
   <juia-numeric v-if="false" v-model="date"></juia-numeric>
   money
   <juia-money v-model="money"></juia-money>
   <juia-money :value="money" autocomplete="off" name="valorDaCausa1" id="valorDaCausa1" @input="money = $event; proxify()"></juia-money>
   <juia-money :value="data.valorDaCausa" autocomplete="off" name="valorDaCausa" id="valorDaCausa" @input="data.valorDaCausa = (($event||{}).target||{}).value; proxify()"></juia-money>
   <input type="text" name="oi2"/>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      data: {
valorDaCausa: undefined
      },
      date: undefined,
      money: undefined,
    };
  },
  methods: {
    addProxy: function(prefix, o, k, v) {
      delete o[k];
      this.$set(o, k, v);
    },

    proxify: function(variable) {
            var obj = (variable !== undefined) ? variable : this.data;
            var s = "";
            var f = (prefix, jsonObj) => {
                if (typeof jsonObj == "object" && jsonObj !== null) {
                    for (const [k, v] of Object.entries(jsonObj)) {
                        if (k == "$$hashKey" || v === null)
                            return;
                        if (typeof v == "object" && v !== null) {
                            var nextprefix = prefix;
                            if (Array.isArray(jsonObj)) {
                                if (prefix.endsWith(".")) {
                                    nextprefix = nextprefix.substring(0, nextprefix.length - 1);
                                }
                                nextprefix += "[" + k + "].";
                            } else
                                nextprefix += k + ".";
                            f(nextprefix, v);
                            this.addProxy(prefix, jsonObj, k, v);
                        } else if (Array.isArray(jsonObj)) {
                            if (prefix.endsWith(".")) {
                                nextprefix = prefix.substring(0, prefix.length - 1);
                            }
                            if (s != "")
                                s = s + "&";
                            s += nextprefix + "[" + k + "]=" + v;
                            this.addProxy(nextprefix + "[" + k + "]", jsonObj, k, v);
                        } else {
                            if (s != "")
                                s = s + "&";
                            s = s + prefix + k + "=" + encodeURIComponent(v);
                            this.addProxy(prefix + k, jsonObj, k, v);
                        }
                    }
                }
            }

            f("", obj);
            console.log(this.dataProxies)
        },
  },
  computed: {},
  components: {},
};
</script>

<style scoped>
a {
  color: grey;
}

.home {
  padding-top: 1rem;
}
</style>
