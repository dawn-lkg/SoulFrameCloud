<template>
  <div>
    <div class="file-layout">
      <!-- 左侧内容筛选 -->
      <div class="filter-sidebar">
        <a-card :bordered="false" size="small">
          <template #title>
            <span class="filter-header">
              <FilterOutlined/>
              内容筛选
            </span>
          </template>
          <div class="filter-section">
            <div class="filter-title">文件类型</div>
            <a-menu
                :selectedKeys="[queryParams.fileType || 'all']"
                mode="inline"
                @click="onTypeMenuClick"
            >
              <a-menu-item key="all">
                <template #icon>
                  <AppstoreOutlined/>
                </template>
                全部
              </a-menu-item>
              <a-menu-item key="image">
                <template #icon>
                  <PictureOutlined/>
                </template>
                图片
              </a-menu-item>
              <a-menu-item key="document">
                <template #icon>
                  <FileTextOutlined/>
                </template>
                文档
              </a-menu-item>
              <a-menu-item key="video">
                <template #icon>
                  <VideoCameraOutlined/>
                </template>
                视频
              </a-menu-item>
              <a-menu-item key="audio">
                <template #icon>
                  <SoundOutlined/>
                </template>
                音频
              </a-menu-item>
              <a-menu-item key="other">
                <template #icon>
                  <FileOutlined/>
                </template>
                其他
              </a-menu-item>
            </a-menu>
          </div>
        </a-card>
      </div>

      <div class="common-container">
        <!-- 右侧搜索与列表 -->
        <div class="main-content">
          <!-- 搜索区域 -->
          <a-form :model="queryParams" class="search-form" layout="inline">
            <a-form-item label="文件名称">
              <a-input
                  v-model:value="queryParams.originalName"
                  allow-clear
                  placeholder="请输入文件名称"
              />
            </a-form-item>
            <a-form-item label="上传时间">
              <a-range-picker
                  v-model:value="queryParams.dateRange"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
              />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="handleQuery">
                  <template #icon>
                    <SearchOutlined/>
                  </template>
                  搜索
                </a-button>
                <a-button @click="resetQuery">
                  <template #icon>
                    <ReloadOutlined/>
                  </template>
                  重置
                </a-button>
              </a-space>
            </a-form-item>
          </a-form>

          <div ref="tableContainerRef" class="common-table-container">
            <!-- 操作按钮区域 -->
            <div class="action-buttons">
              <a-space>
                <a-button type="primary" @click="handleUpload">
                  <template #icon>
                    <UploadOutlined/>
                  </template>
                  上传文件
                </a-button>
                <a-button
                    :disabled="!selectedRowKeys.length"
                    danger
                    @click="handleBatchDelete"
                >
                  <template #icon>
                    <DeleteOutlined/>
                  </template>
                  批量删除
                </a-button>
              </a-space>
              <TableTool
                  v-model:tableSize="tableConfig.size"
                  :fullScreenElement="tableConfig.fullScreenElement"
                  :tableColumns="tableConfig.columns"
                  @refresh="getFileList"
              />
            </div>

            <!-- 文件列表 -->
            <a-table
                :columns="tableColumns"
                :data-source="fileList"
                :loading="loading"
                :pagination="pagination"
                :row-key="(record) => record.fileId"
                :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
                :size="tableConfig.size"
                @change="handleTableChange"
            >
              <!-- 文件名称列 -->
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'fileName'">
                  <div class="file-name-cell">
                    <file-type-icon
                        :file-type="getFileType(record.fileExtension)"
                    />
                    <a @click="handlePreview(record)">{{
                        record.originalName
                      }}</a>
                  </div>
                </template>

                <!-- 文件类型列 -->
                <template v-if="column.key === 'fileType'">
                  {{ getFileType(record.fileExtension) }}
                </template>

                <!-- 文件大小列 -->
                <template v-if="column.key === 'fileSize'">
                  {{ formatFileSize(record.fileSize) }}
                </template>

                <!-- 状态列 -->
                <template v-if="column.key === 'status'">
                  <a-tag :color="FILE_STATUS[record.status]?.color">
                    {{ FILE_STATUS[record.status]?.text }}
                  </a-tag>
                </template>

                <!-- 操作列 -->
                <template v-if="column.key === 'action'">
                  <a-space>
                    <a-tooltip title="预览">
                      <a-button
                          size="small"
                          type="link"
                          @click="handlePreview(record)"
                      >
                        <template #icon>
                          <EyeOutlined/>
                        </template>
                      </a-button>
                    </a-tooltip>
                    <a-tooltip title="下载">
                      <a-button
                          size="small"
                          type="link"
                          @click="handleDownload(record)"
                      >
                        <template #icon>
                          <DownloadOutlined/>
                        </template>
                      </a-button>
                    </a-tooltip>
                    <a-tooltip title="分享">
                      <a-button
                          size="small"
                          type="link"
                          @click="handleShare(record)"
                      >
                        <template #icon>
                          <ShareAltOutlined/>
                        </template>
                      </a-button>
                    </a-tooltip>
                    <a-dropdown>
                      <template #overlay>
                        <a-menu>
                          <a-menu-item key="1" @click="handleRename(record)">
                            <EditOutlined/>
                            重命名
                          </a-menu-item>
                          <a-menu-item
                              key="3"
                              danger
                              @click="handleDelete(record)"
                          >
                            <DeleteOutlined/>
                            删除
                          </a-menu-item>
                        </a-menu>
                      </template>
                      <a-button size="small" type="link">
                        <template #icon>
                          <EllipsisOutlined/>
                        </template>
                      </a-button>
                    </a-dropdown>
                  </a-space>
                </template>
              </template>
            </a-table>
          </div>

          <!-- 上传文件对话框 -->
          <upload-modal
              v-model:visible="uploadModalVisible"
              @success="handleUploadSuccess"
          />

          <!-- 文件预览对话框 -->
          <preview-modal
              v-model:visible="previewModalVisible"
              :file="previewFile"
          />

          <!-- 文件分享对话框 -->
          <share-modal
              v-model:visible="shareModalVisible"
              :file="previewFile"
              @success="handleShareSuccess"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {message, Modal} from "ant-design-vue";
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  EllipsisOutlined,
  ExclamationCircleOutlined,
  EyeOutlined,
  ReloadOutlined,
  SearchOutlined,
  ShareAltOutlined,
  UploadOutlined,
  AppstoreOutlined,
  PictureOutlined,
  FileTextOutlined,
  VideoCameraOutlined,
  SoundOutlined,
  FileOutlined,
  FilterOutlined,
} from "@ant-design/icons-vue";
import {FILE_STATUS} from "@/config";
import FileTypeIcon from "./components/FileTypeIcon.vue";
import {
  batchDeleteFiles,
  deleteFile,
  getFilePage,
  getFileUrl,
} from "@/api/modules/file";
import UploadModal from "./components/UploadModal.vue";
import PreviewModal from "./components/PreviewModal.vue";
import ShareModal from "./components/ShareModal.vue";
import {
  handleDeletePagination,
  handleSingleDeletePagination,
} from "@/utils/pagination";

// 状态
const loading = ref(false);
const fileList = ref([]);
const selectedRowKeys = ref([]);
const appStore = useAppStore();
const tableContainerRef = ref(null);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条记录`,
});

// 查询参数
const queryParams = reactive({
  originalName: "",
  fileType: undefined,
  dateRange: [],
  pageNum: 1,
  pageSize: 10,
});

// 左侧类型筛选点击
const onTypeMenuClick = ({key}) => {
  queryParams.fileType = key === "all" ? undefined : key;
  pagination.current = 1;
  handleQuery();
};

// 上传相关
const uploadModalVisible = ref(false);

// 预览相关
const previewModalVisible = ref(false);
const previewFile = ref({});

// 分享相关
const shareModalVisible = ref(false);

// 图片类型后缀
const imageSuffix = ["jpg", "png", "gif"];
// 文档类型后缀
const documentSuffix = [
  "doc",
  "pdf",
  "xlsx",
  "docx",
  "xls",
  "ppt",
  "pptx",
  "txt",
  "md",
  "csv",
  "xlsb",
  "xlsm",
  "xltx",
  "xltm",
  "xlam",
  "xlt",
  "xlr",
  "xlc",
  "xlm",
  "xlw",
  "xltx",
  "xltm",
  "xlam",
  "xlt",
  "xlr",
  "xlc",
  "xlm",
  "xlw",
];
// 视频类型后缀
const videoSuffix = [
  "mp4",
  "avi",
  "mov",
  "flv",
  "wmv",
  "mpeg",
  "mpg",
  "m4v",
  "webm",
  "mkv",
];
// 音频类型后缀
const audioSuffix = ["mp3", "wav", "ogg"];

// 根据后缀获取文件类型
const getFileType = (suffix) => {
  if (imageSuffix.includes(suffix)) {
    return "image";
  }
  if (documentSuffix.includes(suffix)) {
    return "document";
  }
  if (videoSuffix.includes(suffix)) {
    return "video";
  }
  if (audioSuffix.includes(suffix)) {
    return "audio";
  }
  return "other";
};

// 根据文件类型返回文件后缀
const getFileSuffix = (fileType) => {
  if (fileType === "image") {
    return imageSuffix.join(",");
  }
  if (fileType === "document") {
    return documentSuffix.join(",");
  }
  if (fileType === "video") {
    return videoSuffix.join(",");
  }
  if (fileType === "audio") {
    return audioSuffix.join(",");
  }
};

// 表格列定义
const columns = [
  {
    title: "文件名称",
    dataIndex: "fileName",
    key: "fileName",
    ellipsis: true,
    sorter: true,
    visible: true,
  },
  {
    title: "文件类型",
    dataIndex: "fileType",
    key: "fileType",
    width: 120,
    visible: true,
  },
  {
    title: "文件大小",
    dataIndex: "fileSize",
    key: "fileSize",
    width: 120,
    sorter: true,
    visible: true,
  },
  {
    title: "上传时间",
    dataIndex: "createTime",
    key: "createTime",
    width: 180,
    sorter: true,
    visible: true,
  },
  {
    title: "状态",
    dataIndex: "status",
    key: "status",
    width: 100,
    visible: true,
  },
  {
    title: "操作",
    key: "action",
    width: 180,
    fixed: "right",
    visible: true,
  },
];

const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: tableContainerRef,
});

const tableColumns = computed(() => {
  return tableConfig.columns.filter((item) => item.visible);
});

// 生命周期钩子
onMounted(() => {
  getFileList();
});

// 获取文件列表
const getFileList = async () => {
  loading.value = true;
  try {
    const params = {
      ...queryParams,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      fileExtensionList: getFileSuffix(queryParams.fileType),
    };

    if (params.dateRange && params.dateRange.length === 2) {
      params.beginTime = params.dateRange[0];
      params.endTime = params.dateRange[1];
      delete params.dateRange;
    }

    const data = await getFilePage(params);
    fileList.value = data.records;
    pagination.total = data.total;
    loading.value = false;
  } catch (error) {
    message.error("获取文件列表失败");
    loading.value = false;
  }
};

// 表格选择变化
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys;
};

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;

  // 处理排序
  if (sorter.field) {
    queryParams.sortField = sorter.field;
    queryParams.sortOrder = sorter.order;
  } else {
    queryParams.sortField = undefined;
    queryParams.sortOrder = undefined;
  }

  getFileList();
};

// 搜索
const handleQuery = () => {
  pagination.current = 1;
  getFileList();
};

// 重置查询（保留左侧类型选择）
const resetQuery = () => {
  queryParams.originalName = "";
  queryParams.dateRange = [];
  pagination.current = 1;
  getFileList();
};

// 上传文件
const handleUpload = () => {
  uploadModalVisible.value = true;
};

// 上传成功回调
const handleUploadSuccess = () => {
  getFileList();
};

// 预览文件
const handlePreview = (record) => {
  previewFile.value = record;
  previewModalVisible.value = true;
};

// 下载文件
const handleDownload = async (file) => {
  message.success(`正在下载: ${file.fileName}`);
  try {
    const data = await getFileUrl(file.fileId);
    const link = document.createElement("a");
    link.href = data;
    link.download = file.originalName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    message.error(error.message || "获取文件url失败");
  }
};

// 分享文件
const handleShare = (record) => {
  previewFile.value = record;
  shareModalVisible.value = true;
};

// 分享成功回调
const handleShareSuccess = () => {
  message.success("分享成功");
};

// 重命名文件
const handleRename = (record) => {
  message.info("重命名功能开发中");
};

// 删除文件
const handleDelete = (record) => {
  Modal.confirm({
    title: "确认删除",
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除文件 "${record.fileName}" ？`,
    okText: "确定",
    cancelText: "取消",
    onOk() {
      return deleteFile(record.fileId)
          .then((res) => {
            message.success("删除成功");
            return handleSingleDeletePagination(
                fileList.value,
                queryParams,
                getFileList
            );
          })
          .catch((error) => {
            message.error(error.message || "删除失败，请重试");
          });
    },
  });
};

// 批量删除
const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning("请选择要删除的文件");
    return;
  }
  const fileIds = selectedRowKeys.value;
  Modal.confirm({
    title: "确认删除",
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除选中的 ${fileIds.length} 个文件？`,
    okText: "确定",
    cancelText: "取消",
    onOk() {
      return batchDeleteFiles(fileIds)
          .then((res) => {
            message.success("批量删除成功");
            selectedRowKeys.value = [];
            return handleDeletePagination(
                fileList.value,
                fileIds,
                queryParams,
                getFileList
            );
          })
          .catch((error) => {
            message.error(error.message || "批量删除失败，请重试");
          });
    },
  });
};

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + " B";
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + " KB";
  } else if (size < 1024 * 1024 * 1024) {
    return (size / 1024 / 1024).toFixed(2) + " MB";
  } else {
    return (size / 1024 / 1024 / 1024).toFixed(2) + " GB";
  }
};
</script>

<style lang="scss" scoped>
.file-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 16px;
  align-items: stretch;
}

.common-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.filter-sidebar {
  height: 100%;

  .filter-header {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    color: #334155;
  }

  .filter-section {
    .filter-title {
      font-weight: 600;
      margin-bottom: 8px;
    }
  }

  :deep(.ant-card) {
    border-radius: 8px;
    height: 100%;
  }

  :deep(.ant-card-body) {
    padding: 12px;
  }

  :deep(.ant-card-head) {
    padding: 12px;
    background: #fafafa;
    border-bottom: 0;
    border-radius: 8px 8px 0 0;
  }

  :deep(.ant-menu) {
    border-inline-end: 0;
    background: transparent;
  }

  :deep(.ant-menu-item .anticon) {
    margin-right: 8px;
  }

  :deep(.ant-menu-item) {
    margin: 4px 0;
    border-radius: 6px;
    padding: 8px 12px !important;
    line-height: 22px;
    position: relative;
    transition: background-color 0.2s ease, color 0.2s ease;
  }

  :deep(.ant-menu-item:hover) {
    background-color: #f5faff;
  }

  :deep(.ant-menu-item-selected) {
    background-color: #e8f3ff;
    color: #1677ff;
    font-weight: 600;
  }

  :deep(.ant-menu-item-selected::before) {
    content: "";
    position: absolute;
    left: 0;
    top: 6px;
    bottom: 6px;
    width: 3px;
    background-color: #1677ff;
    border-radius: 0 2px 2px 0;
  }
}

.main-content {
  .search-form {
    margin-bottom: 16px;

    :deep(.ant-form-item) {
      margin-bottom: 12px;
      margin-right: 12px;
    }
  }

  .action-buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  :deep(.ant-table-thead > tr > th) {
    background-color: #fafafa;
    font-weight: 600;
  }

  :deep(.ant-table-tbody > tr:hover > td) {
    background-color: #fafafa;
  }
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  a {
    display: inline-flex;
    align-items: center;
  }
}

/* 响应式优化 */
@media (max-width: 992px) {
  .file-layout {
    grid-template-columns: 1fr;
  }

  .filter-sidebar {
    position: static;
  }

  .common-container {
    padding: 12px;
  }

  .main-content {
    .action-buttons {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    .search-form {
      :deep(.ant-form-item) {
        margin-right: 8px;
        margin-bottom: 8px;
      }
    }
  }
}
</style> 