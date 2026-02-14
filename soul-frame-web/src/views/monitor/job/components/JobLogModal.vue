<template>
  <a-modal
    :title="title"
    :open="visible"
    :width="1200"
    :footer="null"
    @cancel="handleCancel"
  >
    <div class="job-log-container">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="queryParams" class="search-form">
        <a-form-item label="任务名称" name="jobName">
          <a-input
            v-model:value="queryParams.jobName"
            placeholder="请输入任务名称"
            allow-clear
            style="width: 200px"
            @press-enter="handleQuery"
          />
        </a-form-item>
        <a-form-item label="任务组名" name="jobGroup">
          <a-select
            v-model:value="queryParams.jobGroup"
            placeholder="请选择任务组名"
            allow-clear
            style="width: 200px"
          >
            <a-select-option
              v-for="dict in jobGroupOptions"
              :key="dict.value"
              :value="dict.value"
            >
              {{ dict.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="执行状态" name="status">
          <a-select
            v-model:value="queryParams.status"
            placeholder="请选择执行状态"
            allow-clear
            style="width: 200px"
          >
            <a-select-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :value="dict.value"
            >
              {{ dict.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleQuery">
            <template #icon><search-outlined /></template>
            搜索
          </a-button>
          <a-button style="margin-left: 8px" @click="resetQuery">
            <template #icon><redo-outlined /></template>
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-space>
          <a-button type="danger" :disabled="multiple" @click="handleDelete">
            <template #icon><delete-outlined /></template>
            删除
          </a-button>
          <a-button type="primary" danger @click="handleClean">
            <template #icon><delete-outlined /></template>
            清空
          </a-button>
        </a-space>

        <TableTool @refresh="getList" :showButtons="['refresh']" />
      </div>

      <!-- 数据表格 -->
      <a-table
        :loading="loading"
        :data-source="jobLogList"
        :columns="columns"
        :pagination="{
          total: total,
          current: queryParams.pageNum,
          pageSize: queryParams.pageSize,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: handleTableChange,
        }"
        :row-selection="{
          selectedRowKeys: ids,
          onChange: handleSelectionChange,
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <dict-tag dictType="job_execution_status" :value="record.status" />
          </template>
          <template v-if="column.dataIndex === 'jobGroup'">
            <dict-tag dictType="sys_job_group" :value="record.jobGroup" />
          </template>
          <template v-else-if="column.dataIndex === 'operation'">
            <a-button type="link" @click="handleView(record)">
              <template #icon><eye-outlined /></template>
              详细
            </a-button>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 调度日志详情弹窗 -->
    <a-modal
      title="调度日志详细"
      :open="detailOpen"
      :footer="null"
      width="800px"
      @cancel="detailOpen = false"
    >
      <a-descriptions bordered :column="2">
        <a-descriptions-item label="日志编号" :span="1">
          {{ form.jobLogId }}
        </a-descriptions-item>
        <a-descriptions-item label="任务名称" :span="1">
          {{ form.jobName }}
        </a-descriptions-item>
        <a-descriptions-item label="任务组名" :span="1">
          {{ jobGroupFormat(form) }}
        </a-descriptions-item>
        <a-descriptions-item label="执行时间" :span="1">
          {{ form.createTime }}
        </a-descriptions-item>
        <a-descriptions-item label="调用目标字符串" :span="2">
          {{ form.invokeTarget }}
        </a-descriptions-item>
        <a-descriptions-item label="日志信息" :span="2">
          {{ form.jobMessage }}
        </a-descriptions-item>
        <a-descriptions-item label="异常信息" :span="2">
          {{ form.exceptionInfo || "无" }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from "vue";
import { message, Modal } from "ant-design-vue";
import {
  SearchOutlined,
  RedoOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EyeOutlined,
} from "@ant-design/icons-vue";
import {
  getJobLogPage as getJobLogPageApi,
  clearJobLogByJobLog as clearJobLogByJobLogApi,
} from "@/api/modules/jobLog";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  jobId: {
    type: [Number, String],
    default: undefined,
  },
});

const emit = defineEmits(["update:visible", "cancel"]);

// 标题
const title = ref("调度日志");

// 表格列定义
const columns = [
  {
    title: "日志编号",
    dataIndex: "jobLogId",
    key: "jobLogId",
    align: "center",
  },
  {
    title: "任务名称",
    dataIndex: "jobName",
    key: "jobName",
    align: "center",
    ellipsis: true,
  },
  {
    title: "任务组名",
    dataIndex: "jobGroup",
    key: "jobGroup",
    align: "center",
  },
  {
    title: "调用目标字符串",
    dataIndex: "invokeTarget",
    key: "invokeTarget",
    align: "center",
    ellipsis: true,
  },
  {
    title: "日志信息",
    dataIndex: "jobMessage",
    key: "jobMessage",
    align: "center",
    ellipsis: true,
  },
  {
    title: "执行状态",
    dataIndex: "status",
    key: "status",
    align: "center",
  },
  {
    title: "执行时间",
    dataIndex: "createTime",
    key: "createTime",
    align: "center",
    width: 180,
  },
  {
    title: "操作",
    dataIndex: "operation",
    key: "operation",
    align: "center",
  },
];

// 页面数据
const loading = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const detailOpen = ref(false);
const jobLogList = ref([]);

// 字典数据
const statusOptions = ref([
  { value: "0", label: "正常" },
  { value: "1", label: "失败" },
]);

const jobGroupOptions = ref([
  { value: "DEFAULT", label: "默认" },
  { value: "SYSTEM", label: "系统" },
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined,
  jobId: undefined,
});

// 表单参数
const form = reactive({
  jobLogId: undefined,
  jobName: undefined,
  jobGroup: undefined,
  invokeTarget: undefined,
  jobMessage: undefined,
  status: undefined,
  exceptionInfo: undefined,
  createTime: undefined,
});

// 监听jobId变化
watch(
  () => props.jobId,
  (newVal) => {
    if (newVal) {
      queryParams.jobId = newVal;
      getList();
    }
  },
  { immediate: true }
);

// 监听visible变化
watch(
  () => props.visible,
  (newVal) => {},
  { immediate: true }
);

// 格式化任务组名
const jobGroupFormat = (row) => {
  return (
    jobGroupOptions.value.find((item) => item.value === row.jobGroup)?.label ||
    ""
  );
};

// 查询调度日志列表
const getList = () => {
  loading.value = true;
  getJobLogPageApi(queryParams)
    .then((res) => {
      jobLogList.value = res.records;
      total.value = res.total;
      loading.value = false;
    })
    .catch(() => {
      jobLogList.value = [];
      total.value = 0;
      loading.value = false;
    });
};

// 清空日志
const clearJobLog = () => {
  loading.value = true;
  clearJobLogByJobLogApi(props.jobId)
    .then(() => {
      message.success("清空成功");
      jobLogList.value = [];
      total.value = 0;
      loading.value = false;
    })
    .catch(() => {
      message.error("清空失败");
    });
};

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置按钮操作
const resetQuery = () => {
  queryParams.jobName = undefined;
  queryParams.jobGroup = undefined;
  queryParams.status = undefined;
  handleQuery();
};

// 多选框选中数据
const handleSelectionChange = (selectedRowKeys) => {
  ids.value = selectedRowKeys;
  single.value = selectedRowKeys.length !== 1;
  multiple.value = !selectedRowKeys.length;
};

// 分页变化
const handleTableChange = (page, pageSize) => {
  queryParams.pageNum = page;
  queryParams.pageSize = pageSize;
  getList();
};

// 详细按钮操作
const handleView = (row) => {
  detailOpen.value = true;
  form.jobLogId = row.jobLogId;
  form.jobName = row.jobName;
  form.jobGroup = row.jobGroup;
  form.invokeTarget = row.invokeTarget;
  form.jobMessage = row.jobMessage;
  form.status = row.status;
  form.exceptionInfo = row.exceptionInfo || "无";
  form.createTime = row.createTime;
};

// 删除按钮操作
const handleDelete = () => {
  const jobLogIds = ids.value.toString();
  Modal.confirm({
    title: "警告",
    content: '是否确认删除调度日志编号为"' + jobLogIds + '"的数据项?',
    onOk: () => {
      return delJobLog(jobLogIds).then(() => {
        getList();
        message.success("删除成功");
      });
    },
  });
};

// 清空按钮操作
const handleClean = () => {
  Modal.confirm({
    title: "警告",
    content: "是否确认清空所有调度日志数据项?",
    onOk: () => {
      clearJobLog();
    },
  });
};

// 导出按钮操作
const handleExport = () => {
  exportJobLog(queryParams).then((response) => {
    // 处理下载逻辑
    message.success("导出成功");
  });
};

// 取消弹窗
const handleCancel = () => {
  emit("update:visible", false);
  emit("cancel");
};
</script>

<style scoped>
.job-log-container {
  padding: 0 0 20px 0;
}

.search-form {
  margin-bottom: 20px;
}

.action-buttons {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style> 