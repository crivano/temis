<template>
  <a
    :id="'processo-action-' + act.slug"
    class="btn btn-sm btn-light"
    v-if="act && act.active"
    v-b-popover.hover.top="act.explanation"
    @click.prevent="act.active ? $parent.$parent.$eval(act.click) : null"
  >
    <i :class="[act.icon, act.required ? 'i-required' : '']"></i>
  </a>
</template>
<script>
export default {
  props: {
    act: { type: Object },
  },
};
</script>
