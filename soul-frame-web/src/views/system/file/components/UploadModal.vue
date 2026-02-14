<template>
  <a-modal
      :footer="null"
      :open="visible"
      title="上传文件"
      @cancel="handleCancel"
  >
    <a-upload-dragger
        v-model:file-list="fileList"
        :before-upload="beforeUpload"
        :customRequest="customUpload"
        :multiple="true"
        name="file"
    >
      <p class="ant-upload-drag-icon">
        <InboxOutlined/>
      </p>
      <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
      <p class="ant-upload-hint">
        支持单个或批量上传，单个文件大小不超过100MB
      </p>
    </a-upload-dragger>

    <!-- 自定义上传列表 -->
    <div v-if="fileList.length > 0" class="upload-list">
      <div v-for="file in fileList" :key="file.uid" class="upload-item">
        <div class="upload-info">
          <div :title="file.name" class="upload-name">
            {{ file.name }}
          </div>
          <div class="upload-status">
            <LoadingOutlined v-if="getFileProgress(file.uid).status === 'uploading'" style="color: #1890ff"/>
            <CheckCircleOutlined v-else-if="getFileProgress(file.uid).status === 'done'" style="color: #52c41a"/>
            <CloseCircleOutlined v-else style="color: #ff4d4f"/>
            <span class="upload-status-text">
              {{
                getFileProgress(file.uid).status === 'uploading' ? '上传中' :
                    getFileProgress(file.uid).status === 'done' ? '上传成功' : '上传失败'
              }}
            </span>
          </div>
        </div>
        <a-progress
            :percent="getFileProgress(file.uid).percent"
            :status="getFileProgress(file.uid).status === 'error' ? 'exception' :
                  getFileProgress(file.uid).status === 'done' ? 'success' : 'active'"
            size="small"
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import {message} from 'ant-design-vue';
import {CheckCircleOutlined, CloseCircleOutlined, InboxOutlined, LoadingOutlined} from '@ant-design/icons-vue';
import {uploadFile} from '@/api/modules/file';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:visible', 'success', 'cancel']);

// 上传文件列表
const fileList = ref([]);

// 文件上传进度
const uploadProgress = reactive({});

// 获取文件进度
const getFileProgress = (uid) => {
  return uploadProgress[uid] || {percent: 0, status: 'uploading'};
};

// 监听visible变化，重置文件列表
watch(() => props.visible, (val) => {
  if (!val) {
    fileList.value = [];
    Object.keys(uploadProgress).forEach(key => delete uploadProgress[key]);
  }
});

// 上传前检查
const beforeUpload = (file) => {
  const isLt100M = file.size / 1024 / 1024 < 500;
  if (!isLt100M) {
    message.error('文件大小不能超过500MB!');
  }
  return isLt100M || Upload.LIST_IGNORE;
};

// 自定义上传方法
const customUpload = (options) => {
  const {file, onSuccess, onError, onProgress} = options;

  // 初始化进度
  uploadProgress[file.uid] = {
    percent: 0,
    status: 'uploading'
  };

  const formData = new FormData();
  formData.append('file', file);

  uploadFile(formData, (progressEvent) => {
    if (progressEvent.total > 0) {
      const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      uploadProgress[file.uid].percent = percent;

      if (onProgress) {
        onProgress({percent});
      }
    }
  }).then(response => {
    uploadProgress[file.uid].status = 'done';
    message.success(`${file.name} 上传成功`);
    if (onSuccess) {
      onSuccess(response);
    }
    emit('success');
  }).catch(error => {
    uploadProgress[file.uid].status = 'error';
    message.error(`${file.name} 上传失败: ${error.message || '未知错误'}`);
    if (onError) {
      onError(error);
    }
  });
};

// 取消上传
const handleCancel = () => {
  emit('update:visible', false);
  emit('cancel');
};
</script>

<style lang="scss" scoped>
.upload-list {
  margin-top: 16px;
}

.upload-item {
  margin-bottom: 16px;

  .upload-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;

    .upload-name {
      flex: 1;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      margin-right: 8px;
    }

    .upload-status {
      display: flex;
      align-items: center;

      .upload-status-text {
        margin-left: 8px;
        font-size: 14px;
      }
    }
  }
}
</style> 