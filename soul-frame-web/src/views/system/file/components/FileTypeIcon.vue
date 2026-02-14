<template>
  <span class="file-icon">
    <component :is="iconComponent" :style="{ color: iconColor }"/>
  </span>
</template>

<script setup>
import {
  FileExcelOutlined,
  FileImageOutlined,
  FilePdfOutlined,
  FileTextOutlined,
  FileUnknownOutlined,
  FileWordOutlined,
  FileZipOutlined,
  FolderOutlined,
  SoundOutlined,
  VideoCameraOutlined,
  FileMarkdownOutlined,
  FileJpgOutlined,
  FilePptOutlined,
  FileGifOutlined,
  CodeOutlined,
  FileOutlined
} from '@ant-design/icons-vue';

const props = defineProps({
  fileType: {
    type: String,
    required: true
  },
  isFolder: {
    type: Boolean,
    default: false
  },
  fileExtension: {
    type: String,
    default: ''
  }
});

// 根据文件类型返回对应的图标组件
const iconComponent = computed(() => {
  if (props.isFolder) return FolderOutlined;

  const extension = props.fileExtension?.toLowerCase();

  // 根据具体文件扩展名返回更精确的图标
  if (extension) {
    switch (extension) {
      case 'pdf':
        return FilePdfOutlined;
      case 'doc':
      case 'docx':
        return FileWordOutlined;
      case 'xls':
      case 'xlsx':
      case 'csv':
        return FileExcelOutlined;
      case 'ppt':
      case 'pptx':
        return FilePptOutlined;
      case 'jpg':
      case 'jpeg':
        return FileJpgOutlined;
      case 'gif':
        return FileGifOutlined;
      case 'md':
        return FileMarkdownOutlined;
      case 'zip':
      case 'rar':
      case '7z':
      case 'tar':
      case 'gz':
        return FileZipOutlined;
      case 'js':
      case 'ts':
      case 'java':
      case 'py':
      case 'c':
      case 'cpp':
      case 'cs':
      case 'go':
      case 'php':
      case 'rb':
      case 'html':
      case 'css':
      case 'xml':
      case 'json':
        return CodeOutlined;
    }
  }

  // 如果没有具体扩展名匹配，则按文件类型分类
  switch (props.fileType) {
    case 'image':
      return FileImageOutlined;
    case 'document':
    case 'text':
      return FileTextOutlined;
    case 'pdf':
      return FilePdfOutlined;
    case 'excel':
      return FileExcelOutlined;
    case 'word':
      return FileWordOutlined;
    case 'ppt':
      return FilePptOutlined;
    case 'video':
      return VideoCameraOutlined;
    case 'audio':
      return SoundOutlined;
    case 'zip':
      return FileZipOutlined;
    case 'code':
      return CodeOutlined;
    case 'markdown':
      return FileMarkdownOutlined;
    default:
      return FileOutlined;
  }
});

// 根据文件类型返回对应的图标颜色
const iconColor = computed(() => {
  if (props.isFolder) return '#ffc53d';

  const extension = props.fileExtension?.toLowerCase();

  // 根据具体文件扩展名返回更精确的颜色
  if (extension) {
    switch (extension) {
      case 'pdf':
        return '#f5222d';
      case 'doc':
      case 'docx':
        return '#2f54eb';
      case 'xls':
      case 'xlsx':
      case 'csv':
        return '#52c41a';
      case 'ppt':
      case 'pptx':
        return '#fa541c';
      case 'md':
        return '#1677ff';
      case 'zip':
      case 'rar':
      case '7z':
      case 'tar':
      case 'gz':
        return '#fa8c16';
      case 'js':
        return '#faad14';
      case 'ts':
        return '#0958d9';
      case 'java':
        return '#cf222e';
      case 'py':
        return '#1677ff';
      case 'html':
        return '#eb2f96';
      case 'css':
        return '#722ed1';
    }
  }

  switch (props.fileType) {
    case 'image':
      return '#13c2c2';
    case 'document':
    case 'text':
      return '#1677ff';
    case 'pdf':
      return '#f5222d';
    case 'excel':
      return '#52c41a';
    case 'word':
      return '#2f54eb';
    case 'ppt':
      return '#fa541c';
    case 'video':
      return '#722ed1';
    case 'audio':
      return '#eb2f96';
    case 'zip':
      return '#fa8c16';
    case 'code':
      return '#08979c';
    default:
      return '#8c8c8c';
  }
});
</script>

<style scoped>
.file-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 8px;
}
</style> 