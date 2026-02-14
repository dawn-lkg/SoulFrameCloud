<template>
  <a-modal
      :open="visible"
      title="分享文件"
      @cancel="handleCancel"
      @ok="handleConfirm"
  >
    <a-form :model="formState">
      <a-form-item label="分享方式">
        <a-radio-group v-model:value="formState.shareType">
          <a-radio value="link">链接分享</a-radio>
          <a-radio value="email">邮件分享</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item v-if="formState.shareType === 'link'" label="有效期">
        <a-select v-model:value="formState.expireTime">
          <a-select-option value="1">1天</a-select-option>
          <a-select-option value="7">7天</a-select-option>
          <a-select-option value="30">30天</a-select-option>
          <a-select-option value="0">永久</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item v-if="formState.shareType === 'email'" label="收件人">
        <a-input v-model:value="formState.email" placeholder="请输入邮箱地址"/>
      </a-form-item>
      <a-form-item label="访问密码">
        <a-input-password v-model:value="formState.password" placeholder="可选，不设置则无密码"/>
      </a-form-item>
    </a-form>
    <div v-if="formState.shareType === 'link' && shareLink" class="share-link-box">
      <p>分享链接：</p>
      <a-input-group compact>
        <a-input
            v-model:value="shareLink"
            readonly
            style="width: calc(100% - 80px)"
        />
        <a-tooltip title="复制链接">
          <a-button @click="copyShareLink">
            <template #icon>
              <CopyOutlined/>
            </template>
          </a-button>
        </a-tooltip>
      </a-input-group>
    </div>
  </a-modal>
</template>

<script setup>
import {message} from 'ant-design-vue';
import {CopyOutlined} from '@ant-design/icons-vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  file: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:visible', 'success', 'cancel']);

// 分享表单状态
const formState = reactive({
  shareType: 'link',
  expireTime: '7',
  email: '',
  password: ''
});

// 分享链接
const shareLink = ref('');

// 监听visible变化，重置表单
watch(() => props.visible, (val) => {
  if (val) {
    formState.shareType = 'link';
    formState.expireTime = '7';
    formState.email = '';
    formState.password = '';
    shareLink.value = '';
  }
});

// 确认分享
const handleConfirm = async () => {
  try {
    if (formState.shareType === 'email' && !formState.email) {
      message.warning('请输入邮箱地址');
      return;
    }

    // 构建分享参数
    const params = {
      fileId: props.file.fileId,
      shareType: formState.shareType,
      password: formState.password || undefined
    };

    if (formState.shareType === 'link') {
      params.expireTime = formState.expireTime;
    } else {
      params.email = formState.email;
    }

    // 这里应该调用实际的API
    if (formState.shareType === 'link') {
      // 模拟API调用
      // const result = await shareFile(params);
      // shareLink.value = result.shareUrl;

      // 使用模拟数据
      shareLink.value = `https://example.com/share/${props.file.fileId}?expire=${formState.expireTime}`;
      message.success('分享链接已生成');
    } else {
      // 模拟API调用
      // await shareFile(params);

      message.success(`文件已通过邮件分享到 ${formState.email}`);
      emit('update:visible', false);
      emit('success');
    }
  } catch (error) {
    message.error(error.message || '分享失败');
  }
};

// 复制分享链接
const copyShareLink = () => {
  navigator.clipboard.writeText(shareLink.value).then(() => {
    message.success('链接已复制到剪贴板');
  }).catch(() => {
    message.error('复制失败，请手动复制');
  });
};

// 取消分享
const handleCancel = () => {
  emit('update:visible', false);
  emit('cancel');
};
</script>

<style lang="scss" scoped>
.share-link-box {
  margin-top: 16px;
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
}
</style> 