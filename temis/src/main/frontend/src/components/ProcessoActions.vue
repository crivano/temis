<template>
  <ul class="list-unstyled blog-tags float-right">
    <li
      :class="{
        'blog-tags-enabled-true': act.active,
        'blog-tags-enabled-false': !act.active,
        'blog-tags-required': act.required,
      }"
      v-for="act in filteredActions"
      :key="'mini-action' + act.name"
      style="margin-bottom: 0px;padding-bottom:0px;padding-top:0px;"
      @click.prevent="showComponent.$eval(act.click)"
      :id="
        'processo-eventaction-' +
          (event.slug ? event.slug : event.id) +
          '-' +
          act.slug
      "
    >
      <i
        :class="act.icon"
        v-b-popover.hover.top="
          showComponent.actMiniTitle(act.name, act.active, act.explanation)
        "
      ></i>
    </li>
  </ul>
</template>
<script>
export default {
  props: {
    showComponent: { type: Object },
    event: { type: Object },
    actions: { type: Array },
  },
  computed: {
    filteredActions: function() {
      return this.actions.filter((a) => a.active || this.showComponent.audit);
    },
  },
};
</script>
