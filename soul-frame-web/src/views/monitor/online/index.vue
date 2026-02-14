<template>
  <div class="common-container">
    <a-form ref="queryForm" :model="queryParams" class="search-form">
      <a-row :gutter="24">
        <a-col :md="6" :sm="24">
          <a-form-item label="用户名" name="userName">
            <a-input
                v-model:value="queryParams.userName"
                allow-clear
                placeholder="请输入用户名"
                @keyup.enter="handleQuery"
            />
          </a-form-item>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-form-item label="登录IP" name="ipaddr">
            <a-input
                v-model:value="queryParams.ipaddr"
                allow-clear
                placeholder="请输入登录IP"
                @keyup.enter="handleQuery"
            />
          </a-form-item>
        </a-col>
        <a-col :md="12" :sm="24">
          <div class="action-row">
            <a-space>
              <a-button type="primary" @click="handleQuery">
                <template #icon>
                  <search-outlined/>
                </template>
                查询
              </a-button>
              <a-button @click="resetQuery">
                <template #icon>
                  <redo-outlined/>
                </template>
                重置
              </a-button>
            </a-space>
          </div>
        </a-col>
      </a-row>
    </a-form>

    <div class="action-buttons">
      <a-space>
        <a-button
            :disabled="selectedRowKeys.length === 0"
            type="danger"
            @click="handleBatchForceLogout"
        >
          <template #icon>
            <poweroff-outlined/>
          </template>
          批量强退
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
        :columns="tableColumns"
        :data-source="onlineUserList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{
        selectedRowKeys: selectedRowKeys,
        onChange: onSelectChange,
      }"
        row-key="userId"
        @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'operation'">
          <a-space>
            <a-popconfirm
                cancel-text="取消"
                ok-text="确定"
                title="确定要强制退出该用户吗？"
                @confirm="handleForceLogout(record)"
            >
              <a-button style="color: red;" type="link">
                <template #icon>
                  <poweroff-outlined/>
                </template>
                强退
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup>
import {message, Modal} from "ant-design-vue";
import {PoweroffOutlined, RedoOutlined, SearchOutlined,} from "@ant-design/icons-vue";
import {batchForceLogout, forceLogout, getOnlineUserList,} from "@/api/modules/onlineUser";
import {useSSE} from "@/utils/useSSE";

const appStore = useAppStore();

// 列表加载状态
const loading = ref(false);

// 列表数据
const onlineUserList = ref([]);

// 刷新状态
const disconnect = ref();

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ["10", "20", "50", "100"],
});

// 查询参数
const queryParams = reactive({});

// 选中行的键值
const selectedRowKeys = ref([]);

// 查询表单引用
const queryForm = ref(null);

// 表格列配置
const columns = [
  {
    title: "用户ID",
    dataIndex: "userId",
    key: "userId",
    visible: false,
    width: 100,
  },
  {
    title: "用户名",
    dataIndex: "userName",
    key: "userName",
    visible: true,
  },
  {
    title: "用户昵称",
    dataIndex: "nickName",
    key: "nickName",
    visible: true,
  },
  {
    title: "登录IP",
    dataIndex: "ipaddr",
    key: "ipaddr",
    visible: true,
  },
  {
    title: "登录地点",
    dataIndex: "loginLocation",
    key: "loginLocation",
    width: 200,
    visible: true,
  },
  {
    title: "浏览器",
    dataIndex: "browser",
    key: "browser",
    visible: true,
  },
  {
    title: "操作系统",
    dataIndex: "os",
    key: "os",
    visible: true,
  },
  {
    title: "登录时间",
    dataIndex: "loginTime",
    key: "loginTime",
    visible: true,
  },
  {
    title: "操作",
    dataIndex: "operation",
    key: "operation",
    fixed: "right",
    width: 180,
    visible: true,
  },
];

const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: null,
});

const tableColumns = computed(() => {
  return tableConfig.columns.filter((column) => column.visible);
});

// 获取在线用户列表
const getList = async () => {
  try {
    loading.value = true;
    const params = {
      ...queryParams,
    };

    const data = await getOnlineUserList(params);
    onlineUserList.value = data;
    pagination.total = data.length;
  } catch (error) {
    message.error(error.message || "获取在线用户列表失败");
  } finally {
    loading.value = false;
  }
};

// 表格变化事件
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  if (sorter.field) {
    queryParams.orderByColumn = sorter.field;
    queryParams.isAsc = sorter.order === "ascend" ? "asc" : "desc";
  } else {
    queryParams.orderByColumn = undefined;
    queryParams.isAsc = undefined;
  }
  getList();
};

// 执行查询
const handleQuery = () => {
  getList();
};

// 重置查询
const resetQuery = () => {
  queryForm.value.resetFields();
  selectedRowKeys.value = [];
  getList();
};

// 选择行变化
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys;
};

// 处理强制退出
const handleForceLogout = async (record) => {
  try {
    await forceLogout(record.userId);
    message.success("强制退出成功");
  } catch (error) {
    console.error("强制退出失败", error);
    message.error("强制退出失败");
  }
};

// 处理批量强制退出
const handleBatchForceLogout = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning("请至少选择一条记录");
    return;
  }

  Modal.confirm({
    title: "确认强制退出",
    content: `确定要强制退出选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    okText: "确定",
    cancelText: "取消",
    okType: "danger",
    async onOk() {
      try {
        await batchForceLogout(selectedRowKeys.value);
        message.success("批量强制退出成功");
        selectedRowKeys.value = [];
        getList();
      } catch (error) {
        console.error("批量强制退出失败", error);
        message.error("批量强制退出失败");
      }
    },
  });
};

// 组件挂载时获取列表数据
onMounted(async () => {
  getList();
  const {lastMessage, disconnect} = useSSE("/system/online/sse", {
    keepAlive: false 
  });
  disconnect.value = disconnect
  watch(lastMessage, (v) => {
    console.log(v)
    getList()
  })
});
onUnmounted(() => {
  disconnect.value();
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.search-form {
  width: 100%;
}

.search-form :deep(.ant-form-item) {
  margin-bottom: 16px;
  width: 100%;
}

.search-form :deep(.ant-form-item-label) {
  width: 80px;
  text-align: right;
}

.search-form :deep(.ant-form-item-control) {
  flex: 1;
}

.search-form :deep(.ant-row) {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
}

.action-row {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}
</style> 