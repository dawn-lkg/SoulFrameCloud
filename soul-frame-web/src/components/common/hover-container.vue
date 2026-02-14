<template>
  <div class="hover-container" v-if="showTooltip">
    <a-tooltip
      :placement="placement"
      trigger="hover"
      :title="tooltipContent"
      :destroyTooltipOnHide="true"
    >
      <div class="hover-container-content">
        <slot></slot>
      </div>
    </a-tooltip>
  </div>
  <div class="hover-container" v-else>
    <slot></slot>
  </div>
</template>

<script setup>
const props = defineProps({
  tooltipContent: {
    type: String,
    default: "",
  },
  placement: {
    type: String,
    default: "bottom",
  },
});
const showTooltip = computed(() => props.tooltipContent);
</script>

<style lang="scss" scoped>
.hover-container {
  cursor: pointer;
  .hover-container-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    
  }
}
.hover-container:hover {
  background-color: #f0f0f0;
}
</style>
