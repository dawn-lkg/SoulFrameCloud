<template>
  <div class="common-container">
    <a-form
      layout="inline"
      :model="queryParams"
      v-show="showSearch"
      class="search-form"
    >
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
        <DictSelect
          v-model:value="queryParams.jobGroup"
          dictType="sys_job_group"
          placeholder="请选择任务组名"
        />
      </a-form-item>
      <a-form-item label="任务状态" name="status">
        <a-select
          v-model:value="queryParams.status"
          placeholder="请选择任务状态"
          allow-clear
          style="width: 200px"
        >
          <a-select-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :value="dict.value"
            >{{ dict.label }}</a-select-option
          >
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

    <div class="action-buttons">
      <a-space>
        <a-button type="primary" @click="handleAdd">
          <template #icon><plus-outlined /></template>
          新增
        </a-button>
      </a-space>
      <TableTool
        @refresh="getList"
        v-model:tableSize="tableConfig.size"
        :tableColumns="tableConfig.columns"
        :fullScreenElement="tableConfig.fullScreenElement"
      />
    </div>

    <a-table
      :loading="loading"
      :data-source="jobList"
      :columns="showColumns"
      :size="tableConfig.size"
      :pagination="{
        total: total,
        current: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total) => `共 ${total} 条`,
      }"
      :row-selection="{ selectedRowKeys: ids, onChange: handleSelectionChange }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'jobGroup'">
          <dict-tag dictType="sys_job_group" :value="record.jobGroup" />
        </template>
        <template v-else-if="column.key === 'status'">
          <a-switch
            :checked="record.status === '0'"
            @change="
              (checked) => {
                record.status = checked ? '0' : '1';
                handleStatusChange(record);
              }
            "
          />
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space>
            <a-button
              type="link"
              @click="handleUpdate(record)"
              style="color: #1890ff"
            >
              <template #icon><edit-outlined /></template>
              修改
            </a-button>
            <a-button
              type="link"
              @click="handleDelete(record)"
              style="color: red"
            >
              <template #icon><delete-outlined /></template>
              删除
            </a-button>
            <a-dropdown>
              <a-button type="link" style="color: #1890ff">
                <template #icon><down-outlined /></template>
                更多
              </a-button>
              <template #overlay>
                <a-menu @click="({ key }) => handleCommand(key, record)">
                  <a-menu-item key="handleRun">
                    <caret-right-outlined />执行一次
                  </a-menu-item>
                  <a-menu-item key="handleView">
                    <eye-outlined />任务详情
                  </a-menu-item>
                  <a-menu-item key="handleJobLog">
                    <unordered-list-outlined />调度日志
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 使用拆分的任务表单组件 -->
    <job-form
      v-model:visible="formVisible"
      :jobData="selectedJobData"
      :isEdit="isEdit"
      @success="getList"
      @cancel="cancelForm"
    />

    <!-- 使用拆分的任务详情组件 -->
    <job-detail
      v-model:visible="detailVisible"
      :data="selectedJobData"
      @cancel="cancelDetail"
    />

    <!-- 调度日志弹窗组件 -->
    <job-log-modal
      v-model:visible="jobLogVisible"
      :jobId="selectedJobId"
      @cancel="cancelJobLog"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { message, Modal } from "ant-design-vue";
import { useRouter } from "vue-router";
import { getJobPage, delJob, runJob, changeJobStatus } from "@/api/modules/job";
import {
  SearchOutlined,
  RedoOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  DownloadOutlined,
  UnorderedListOutlined,
  DownOutlined,
  CaretRightOutlined,
  EyeOutlined,
} from "@ant-design/icons-vue";
import JobForm from "./components/JobForm.vue";
import JobDetail from "./components/JobDetail.vue";
import JobLogModal from "./components/JobLogModal.vue";
import { useAppStore } from "@/stores";
import DictSelect from "@/components/DictSelect/index.vue";

// 页面数据
const router = useRouter();
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const jobList = ref([]);
const tableContainerRef = ref(null);
const appStore = useAppStore();

// 表单和详情弹窗控制
const formVisible = ref(false);
const detailVisible = ref(false);
const jobLogVisible = ref(false);
const selectedJobData = ref({});
const selectedJobId = ref(null);
const isEdit = ref(false);

// 表格列定义
const columns = [
  {
    title: "任务编号",
    dataIndex: "jobId",
    key: "jobId",
    align: "center",
    visible: false,
  },
  {
    title: "任务名称",
    dataIndex: "jobName",
    key: "jobName",
    align: "center",
    ellipsis: true,
    visible: true,
  },
  {
    title: "任务组名",
    dataIndex: "jobGroup",
    key: "jobGroup",
    align: "center",
    visible: true,
  },
  {
    title: "调用目标字符串",
    dataIndex: "invokeTarget",
    key: "invokeTarget",
    align: "center",
    ellipsis: true,
    visible: true,
  },
  {
    title: "cron执行表达式",
    dataIndex: "cronExpression",
    key: "cronExpression",
    align: "center",
    ellipsis: true,
    visible: true,
  },
  {
    title: "状态",
    dataIndex: "status",
    key: "status",
    align: "center",
    visible: true,
  },
  {
    title: "操作",
    key: "action",
    align: "center",
    width: 300,
    visible: true,
  },
];

const tableConfig = ref({
  size: appStore.componentSize,
  columns: columns,
  fullScreenElement: tableContainerRef.value,
});

const showColumns = computed(() => {
  return columns.filter((column) => column.visible);
});

// 字典数据
const statusOptions = ref([
  { value: "0", label: "正常" },
  { value: "1", label: "暂停" },
]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined,
});

// 查询定时任务列表
const getList = () => {
  loading.value = true;
  getJobPage(queryParams)
    .then((data) => {
      jobList.value = data.records;
      total.value = data.total;
      loading.value = false;
    })
    .catch((err) => {
      message.error(err.message || "查询定时任务列表失败");
      loading.value = false;
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

// 任务状态修改
const handleStatusChange = (row) => {
  const text = row.status === "0" ? "启用" : "停用";
  Modal.confirm({
    title: "警告",
    content: '确认要"' + text + '""' + row.jobName + '"任务吗?',
    onOk: () => {
      return changeJobStatus(row).then(() => {
        message.success(text + "成功");
      });
    },
    onCancel: () => {
      row.status = row.status === "0" ? "1" : "0";
    },
  });
};

// 更多操作触发
const handleCommand = (command, row) => {
  switch (command) {
    case "handleRun":
      handleRun(row);
      break;
    case "handleView":
      handleView(row);
      break;
    case "handleJobLog":
      handleJobLog(row);
      break;
    default:
      break;
  }
};

// 立即执行一次
const handleRun = (row) => {
  Modal.confirm({
    title: "警告",
    content: '确认要立即执行一次"' + row.jobName + '"任务吗?',
    onOk: () => {
      return runJob(row.jobId).then(() => {
        message.success("执行成功");
      });
    },
  });
};

// 任务详细信息
const handleView = (row) => {
  selectedJobData.value = {...row};
  detailVisible.value = true;
};

// 调度日志按钮操作
const handleJobLog = (row) => {
  const jobId = row?.jobId || ids.value;
  selectedJobId.value = jobId;
  jobLogVisible.value = true;
};

// 取消调度日志弹窗
const cancelJobLog = () => {
  jobLogVisible.value = false;
  selectedJobId.value = null;
};


// 新增按钮操作
const handleAdd = () => {
  isEdit.value = false;
  selectedJobData.value = {};
  setTimeout(() => {
    formVisible.value = true;
  }, 100);
};

// 修改按钮操作
const handleUpdate = (row) => {
  selectedJobData.value = {...row};
  isEdit.value = true;
  setTimeout(() => {
    formVisible.value = true;
  }, 100);
};

// 删除按钮操作
const handleDelete = (row) => {
  const jobIds = row?.jobId || ids.value;
  Modal.confirm({
    title: "警告",
    content: '是否确认删除定时任务编号为"' + jobIds + '"的数据项?',
    onOk: () => {
      return delJob(jobIds).then(() => {
        getList();
        message.success("删除成功");
      });
    },
  });
};


// 取消表单
const cancelForm = () => {
  formVisible.value = false;
  // selectedJobData.value = null;
};

// 取消详情
const cancelDetail = () => {
  detailVisible.value = false;
  selectedJobData.value = null;
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
</style> 