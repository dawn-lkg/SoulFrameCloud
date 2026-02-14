<template>
  <div class="common-container">
    <div class="empty-container">
      <div class="empty-content">
        <div class="empty-icon">
          <svg viewBox="0 0 64 41" xmlns="http://www.w3.org/2000/svg">
            <g transform="translate(0 1)" fill="none" fill-rule="evenodd">
              <ellipse fill="#f5f5f5" cx="32" cy="33" rx="32" ry="8"></ellipse>
              <g fill-rule="nonzero" stroke="#d9d9d9">
                <path
                  d="M55 12.76L44.854 1.258C44.367.474 43.656 0 42.907 0H21.093c-.749 0-1.46.474-1.947 1.257L9 12.761V22h46v-9.24z"
                ></path>
                <path
                  d="M41.613 15.931c0-1.605.994-2.93 2.227-2.93H55v18.137C55 33.26 53.68 35 52.05 35h-40.1C10.32 35 9 33.259 9 31.137V13h11.16c1.233 0 2.227 1.323 2.227 2.928v.022c0 1.605 1.005 2.901 2.237 2.901h14.752c1.232 0 2.237-1.308 2.237-2.913v-.007z"
                  fill="#fafafa"
                ></path>
              </g>
            </g>
          </svg>
        </div>
        <div class="empty-text">暂无数据</div>
        <div class="empty-description">
          页面内容为空，可能是数据未加载或无可用数据
        </div>
        <div class="empty-actions">
          <a-button type="primary">刷新页面</a-button>
          <a-button>返回首页</a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";

const props = defineProps({
  text: {
    type: String,
    default: "暂无数据",
  },
  description: {
    type: String,
    default: "页面内容为空，可能是数据未加载或无可用数据",
  },
  showActions: {
    type: Boolean,
    default: true,
  },
});
</script>

<style lang="scss" scoped>
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  min-height: 400px;
  // background-color: #fafafa;
  border-radius: 8px;

  .empty-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px;
  }

  .empty-icon {
    width: 100px;
    height: 100px;
    margin-bottom: 20px;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  .empty-text {
    font-size: 20px;
    font-weight: 500;
    color: #262626;
    margin-bottom: 8px;
  }

  .empty-description {
    font-size: 14px;
    color: #8c8c8c;
    text-align: center;
    margin-bottom: 24px;
  }

  .empty-actions {
    display: flex;
    gap: 12px;

    button {
      min-width: 88px;
    }
  }
}
</style> 