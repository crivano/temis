<template>
  <li style="list-style: none;">
    <a href :id="'seeklex-link-' + value.id" @click.prevent="nav(value.id)">{{ value.title }}</a
    ><span v-for="sibling in value.siblings" :key="sibling.id"
      >, <a href :id="'seeklex-link-' + sibling.id" @click.prevent="nav(sibling.id)">{{ sibling.title }}</a></span
    >
    <template v-if="topic.text">
      &nbsp;
      <span v-html="topic.text"></span>
      <b-button
        :id="'seeklex-add-' + topic.id"
        v-if="['artigo', 'paragrafo', 'inciso', 'alinea'].includes(topic.kind)"
        variant="link"
        size="sm"
        @click.prevent="incluirNorma()"
        ><i class="fas fa-plus"></i
      ></b-button>
    </template>
    <ul>
      <processo-seeklex-item
        v-for="item in value.children"
        :value="item"
        :key="item.id"
        :top-level="false"
      ></processo-seeklex-item>
    </ul>
  </li>
</template>
<script>
import lodash from "lodash";

export default {
  name: "processo-seeklex-item",
  props: {
    value: { type: Object },
    topLevel: { type: Boolean, default: false },
  },
  data() {
    return {
      query: undefined,
      items: undefined,
    };
  },
  computed: {
    topic: function() {
      if (this.value.siblings && this.value.siblings.length)
        return this.value.siblings[this.value.siblings.length - 1];
      return this.value;
    },
    mymodel: {
      get() {
        return this.value;
      },
      set(d) {
        this.$emit("input", d);
      },
    },
    myquery: {
      get() {
        return this.query;
      },
      set: lodash.debounce(function(newValue) {
        this.query = newValue;
        this.$http
          .get(
            "https://seeklex.crivano.com/api/v1/query?q=" +
              encodeURIComponent(this.query)
          )
          .then(
            (response) => {
              this.items = response.data.list;
            },
            (response) => {
              console.log(response);
            }
          );
      }, 500),
    },
  },
  methods: {
    incluirNorma: function(o) {
      if (!o) o = {};
      if (!o.id) o.id = this.topic.id;
      var text = this.topic.text;
      if (text) {
        text = text.replace(/<B>/g, "");
        text = text.replace(/<\/B>/g, "");
      }
      o[this.topic.kind + "Ref"] = this.topic.ref;
      o[this.topic.kind + "Texto"] = text;

      if (this.value.siblings && this.value.siblings.length) {
        for (var i = 0; i < this.value.siblings.length; i++) {
          var sibling = this.value.siblings[i];
          var siblingtext = sibling.text;
          if (siblingtext) {
            siblingtext = siblingtext.replace(/<B>/g, "");
            siblingtext = siblingtext.replace(/<\/B>/g, "");
          }
          o[sibling.kind + "Ref"] = sibling.ref;
          o[sibling.kind + "Texto"] = siblingtext;
        }
      }
      if (this.value !== this.topic) {
        var valuetext = this.value.text;
        if (valuetext) {
          valuetext = valuetext.replace(/<B>/g, "");
          valuetext = valuetext.replace(/<\/B>/g, "");
        }
        o[this.value.kind + "Ref"] = this.value.ref;
        o[this.value.kind + "Texto"] = valuetext;
      }
      var p = this.$parent;
      while (!p.incluirNorma && p.$parent) {
        p = p.$parent;
      }
      console.log(o);
      p.incluirNorma(o);
    },
    nav: function(id) {
      if (this.topLevel) id += " ind:1";
      this.localizarNorma(id);
    },
    localizarNorma: function(id) {
      //se for uma norma raiz, então deve incluir um "ind:1" para não exibir tudo, só o índice.
      var p = this.$parent;
      while (!p.localizarNorma) {
        p = p.$parent;
      }
      p.localizarNorma(id);
    },
  },
};
</script>
