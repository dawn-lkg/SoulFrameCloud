<script setup>
import {inject, onMounted, watch} from 'vue'
import { useAppStore } from '@/stores/app'
import {useAuthStore} from '@/stores/auth'

const appStore = useAppStore()
const authStore = useAuthStore()
const locale = inject('antLocale')

</script>

<template>
  <a-config-provider :locale="locale" :component-size="appStore.componentSize">
    <router-view />
  </a-config-provider>
</template>

<style scoped>

</style>
