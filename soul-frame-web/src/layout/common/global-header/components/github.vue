<template>
  <hover-container :tooltipContent="'GitHub'" class="github">
    <GithubOutlined @click="handleGithub" />
  </hover-container>
</template>

<script setup>
import { GithubOutlined } from '@ant-design/icons-vue';

const handleGithub = () => {
  window.open('https://github.com/dawn-lkg/SoulFrame', '_blank');
}

</script>

<style scoped>
.github {
  font-size: 20px;
  width: 40px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>