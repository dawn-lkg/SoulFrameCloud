<template>
    <hover-container :tooltipContent="'主题设置'" :placement="'bottom'" class="setting-button" @click="app.toggleSettingDrawer()">
        <SettingOutlined />
    </hover-container>
</template>

<script setup>
import { SettingOutlined } from "@ant-design/icons-vue";
import { useAppStore } from '@/stores'
const app = useAppStore()
</script>

<style lang="scss" scoped>
.setting-button {
    font-size: 20px;
    width: 40px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}
</style>
