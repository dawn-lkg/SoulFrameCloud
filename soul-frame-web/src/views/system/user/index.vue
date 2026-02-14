<template>
  <div>
    <!-- 小屏幕显示部门树按钮 -->
    <div v-if="deptList.length > 0" class="mobile-dept-trigger">
      <a-button type="primary" @click="deptDrawerVisible = true">
        <template #icon><AppstoreOutlined /></template>
        选择部门
      </a-button>
      <a-tag v-if="selectedDeptKeys.length > 0" color="blue" closable @close="handleResetDept">
        已选择部门
      </a-tag>
    </div>

    <!-- 部门树抽屉 - 小屏幕使用 -->
    <a-drawer
      v-model:visible="deptDrawerVisible"
      title="选择部门"
      placement="left"
      width="280"
      class="dept-drawer"
    >
      <a-tree 
        v-model:expandedKeys="expandedRowKeys" 
        v-model:selectedKeys="selectedDeptKeys"
        :default-expanded-all="true" 
        :field-names="{ title: 'title', value: 'id', key: 'id' }"
        :loading="deptLoading"
        :tree-data="deptList" 
        @select="handleDeptSelectFromDrawer"
      >
        <template #title="{ title, userCount }">
          <span class="dept-tree-node">
            <span class="dept-name">{{ title }}</span>
            <span v-if="userCount !== undefined" class="dept-count">({{ userCount }})</span>
          </span>
        </template>
      </a-tree>
      <template #footer>
        <a-space>
          <a-button @click="handleResetDept">重置</a-button>
          <a-button type="primary" @click="deptDrawerVisible = false">确定</a-button>
        </a-space>
      </template>
    </a-drawer>

    <a-row :gutter="16">
      <!-- 部门树 - 小屏幕隐藏 -->
      <a-col v-if="deptList.length > 0" :lg="4" :md="6" :sm="0" :span="4" :xl="4" :xs="0" class="dept-tree-col">
        <div class="common-container dept-tree-container">
          <div class="dept-tree-header">
            <h4>部门列表</h4>
            <a-button type="text" size="small" @click="handleResetDept">
              <template #icon><ReloadOutlined /></template>
            </a-button>
          </div>
          <a-tree v-model:expandedKeys="expandedRowKeys" v-model:selectedKeys="selectedDeptKeys"
                  :default-expanded-all="true" :field-names="{ title: 'title', value: 'id', key: 'id' }"
                  :loading="deptLoading"
                  :tree-data="deptList" @select="handleDeptSelect">
            <template #title="{ title, userCount }">
              <span class="dept-tree-node">
                <span class="dept-name">{{ title }}</span>
                <span v-if="userCount !== undefined" class="dept-count">({{ userCount }})</span>
              </span>
            </template>
          </a-tree>
        </div>
      </a-col>
      
      <!-- 主内容区 - 响应式宽度 -->
      <a-col :lg="deptList.length > 0 ? 20 : 24" :md="deptList.length > 0 ? 18 : 24" :sm="24" :span="deptList.length > 0 ? 20 : 24"
             :xl="deptList.length > 0 ? 20 : 24" :xs="24">
        <div class="common-container">
          <!-- 搜索区域 -->
          <a-form
              :model="queryParams"
              class="search-form"
              layout="inline"
              @finish="handleQuery"
          >
            <a-form-item label="用户名称" name="userName">
              <a-input
                  v-model:value="queryParams.userName"
                  allow-clear
                  placeholder="请输入用户名称"
              />
            </a-form-item>
            <a-form-item label="手机号码" name="phone">
              <a-input
                  v-model:value="queryParams.phone"
                  allow-clear
                  placeholder="请输入手机号码"
              />
            </a-form-item>
            <a-form-item label="状态" name="status">
              <DictSelect
                  v-model:value="queryParams.status"
                  allow-clear
                  dictType="sys_user_status"
                  placeholder="用户状态"
                  style="width: 200px"
              />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button html-type="submit" type="primary">
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
                <a-button type="primary" @click="handleAdd">
                  <template #icon>
                    <PlusOutlined/>
                  </template>
                  新增
                </a-button>
                <a-button
                    :disabled="selectedRowKeys.length === 0"
                    type="danger"
                    @click="handleBatchDelete"
                >
                  <template #icon>
                    <DeleteOutlined/>
                  </template>
                  批量删除
                </a-button>
              </a-space>
              <!-- 表格工具 -->
              <TableTool
                  v-model:tableSize="tableConfig.size"
                  :fullScreenElement="tableContainerRef"
                  :tableColumns="tableConfig.columns"
                  @refresh="getList"
              />
            </div>

            <!-- 表格区域 -->
            <a-table
                :columns="tableColumns"
                :data-source="userList"
                :loading="loading"
                :pagination="{
                total: pagination.total,
                current: queryParams.pageNum,
                pageSize: queryParams.pageSize,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条`,
              }"
                :row-key="(record) => record.userId"
                :row-selection="{
                selectedRowKeys: selectedRowKeys,
                onChange: onSelectChange,
              }"
                :scroll="{ x: 1300 }"
                :size="tableConfig.size"
                @change="handleTableChange"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'status'">
                  <a-tag :color="record.status === '0' ? 'green' : 'red'">
                    {{ record.status === "0" ? "正常" : "停用" }}
                  </a-tag>
                </template>
                <template v-else-if="column.dataIndex === 'sex'">
                  <!-- {{ record.sex === "0" ? "男" : "女" }} -->
                  <DictTag :value="record.sex" dictType="sys_user_sex"/>
                </template>
                <template v-else-if="column.dataIndex === 'action'">
                  <a-space>
                    <a-button
                        size="small"
                        type="link"
                        @click="handleEdit(record)"
                    >
                      <template #icon>
                        <edit-outlined/>
                      </template>
                      编辑
                    </a-button>
                    <a-button
                        size="small"
                        style="color: red"
                        type="link"
                        @click="handleDelete(record)"
                    >
                      <template #icon>
                        <delete-outlined/>
                      </template>
                      删除
                    </a-button>
                    <a-button
                        size="small"
                        style="color: #1890ff"
                        type="link"
                        @click="handleResetPassword(record)"
                    >
                      <template #icon>
                        <key-outlined/>
                      </template>
                      重置密码
                    </a-button>
                  </a-space>
                </template>
              </template>
            </a-table>
            <!-- 引入用户表单组件 -->
          </div>
          <UserForm
              ref="UserFormRef"
              :deptList="deptList"
              :is-edit="isEdit"
              :open="open"
              :roleOptions="roleOptions"
              :userData="currentUser"
              @success="handleFormSuccess"
              @update:open="open = $event"
          />
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import {message, Modal} from "ant-design-vue";
import {
  AppstoreOutlined,
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  KeyOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from "@ant-design/icons-vue";
import UserForm from "./components/UserForm.vue";
import {
  batchDeleteUser,
  deleteUser,
  getUserPage,
  resetPassword,
} from "@/api/modules/user";
import {getRoleList} from "@/api/modules/role";
import {
  handleDeletePagination,
  handleSingleDeletePagination,
} from "@/utils/pagination";
import {deptSelect as getDeptListApi} from "@/api/modules/dept";

const appStore = useAppStore();
const tableContainerRef = ref(null);

// 查询参数
const queryParams = reactive({
  userName: "",
  phone: "",
  status: undefined,
  deptIds: "",
  pageNum: 1,
  pageSize: 10,
});
//表格配置
const tableConfig = reactive({
  size: appStore.tableSize,
  columns: [
    {
      title: "id",
      dataIndex: "userId",
      key: "userId",
      width: 50,
      visible: false,
    },
    {
      title: "用户名称",
      dataIndex: "userName",
      key: "userName",
      width: 150,
      visible: true,
    },
    {
      title: "用户昵称",
      dataIndex: "nickName",
      key: "nickName",
      width: 150,
      visible: true,
    },
    {
      title: "邮箱",
      dataIndex: "email",
      key: "email",
      width: 150,
      ellipsis: true,
      visible: true,
    },
    {
      title: "性别",
      dataIndex: "sex",
      key: "sex",
      width: 100,
      visible: true,
    },
    {
      title: "手机号码",
      dataIndex: "phone",
      key: "phone",
      width: 150,
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
      title: "最后登录ip",
      dataIndex: "loginIp",
      key: "loginIp",
      width: 150,
      visible: true,
    },
    {
      title: "最后登录时间",
      dataIndex: "loginDate",
      key: "loginDate",
      width: 150,
      visible: true,
    },
    {
      title: "操作",
      dataIndex: "action",
      key: "action",
      fixed: "right",
      width: 240,
      visible: true,
    },
  ],
});

const tableColumns = computed(() => {
  return tableConfig.columns.filter((column) => column.visible);
});

// 用户列表数据
const userList = ref();

// 角色选项
const roleOptions = ref([]);

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`,
});

// 加载状态
const loading = ref(false);

// 部门加载状态
const deptLoading = ref(false);

// 选中的行
const selectedRowKeys = ref([]);

// 弹窗相关
const open = ref(false);

// 当前选中的用户数据
const currentUser = ref({});

// 是否编辑
const isEdit = ref(false);

// 部门列表
const deptList = ref([]);

// 展开的行
const expandedRowKeys = ref([]);

// 选中的部门节点
const selectedDeptKeys = ref([]);

// 部门抽屉显示状态
const deptDrawerVisible = ref(false);

// 组件挂载时获取数据
onMounted(() => {
  getList();
  getRoles();
  getDeptList();
});

// 获取角色列表
const getRoles = () => {
  getRoleList().then((data) => {
    roleOptions.value = data;
  });
};

// 获取用户列表
const getList = () => {
  loading.value = true;
  getUserPage(queryParams)
    .then((data) => {
      loading.value = false;
      userList.value = data.records;
      pagination.total = data.total;
    })
    .catch((e) => {
      loading.value = false;
      message.error(e.message || "获取用户列表失败");
    });
};

// 获取部门列表
const getDeptList = () => {
  deptLoading.value = true;
  getDeptListApi().then((data) => {
    expandedRowKeys.value = flattenDeptList(data).map((item) => item.id);
    deptList.value = data;
    console.log(expandedRowKeys.value);

  }).finally(() => {
    deptLoading.value = false;
  });
};

// 数据扁平化
const flattenDeptList = (deptList) => {
  return deptList.map((item) => {
    return [item, ...flattenDeptList(item.children)];
  }).flat();
};
// 处理查询
const handleQuery = () => {
  pagination.current = 1;
  queryParams.pageNum = 1;
  getList();
};

// 重置查询
const resetQuery = () => {
  queryParams.username = "";
  queryParams.phone = "";
  queryParams.deptIds = "";
  queryParams.status = undefined;
  // 清除树选择状态
  selectedDeptKeys.value = [];
  handleQuery();
};

// 重置部门选择
const handleResetDept = () => {
  selectedDeptKeys.value = [];
  queryParams.deptIds = "";
  handleQuery();
  message.success('已重置部门筛选');
};

// 从抽屉选择部门
const handleDeptSelectFromDrawer = (selectedKeys, info) => {
  handleDeptSelect(selectedKeys, info);
  // 选择后自动关闭抽屉
  setTimeout(() => {
    deptDrawerVisible.value = false;
  }, 300);
};

// 表格变化事件
const handleTableChange = (pag) => {
  queryParams.pageNum = pag.current;
  queryParams.pageSize = pag.pageSize;
  getList();
};

// 选择行变化
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys;
};

// 新增用户
const handleAdd = () => {
  currentUser.value = {};
  isEdit.value = false;
  setTimeout(() => {
    open.value = true;
  }, 100);
};

// 编辑用户
const handleEdit = (record) => {
  currentUser.value = {...record};
  isEdit.value = true;
  setTimeout(() => {
    open.value = true;
  }, 100);
};

// 处理表单成功提交
const handleFormSuccess = (formData) => {
  console.log("表单数据:", formData);

  // 重新加载列表数据
  getList();
};

// 删除用户
const handleDelete = (record) => {
  Modal.confirm({
    title: "确认删除",
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除用户 "${record.userName}" ？`,
    okText: "确定",
    cancelText: "取消",
    onOk() {
      return deleteUser(record.userId)
        .then((res) => {
          message.success("删除成功");
          return handleSingleDeletePagination(
            userList.value,
            queryParams,
            getList
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
  const userIds = selectedRowKeys.value;
  Modal.confirm({
    title: "确认删除",
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除选中的 ${userIds.length} 个用户？`,
    okText: "确定",
    cancelText: "取消",
    onOk() {
      return batchDeleteUser(userIds)
        .then((res) => {
          message.success("批量删除成功");
          selectedRowKeys.value = [];
          return handleDeletePagination(
            userList.value,
            userIds,
            queryParams,
            getList
          );
        })
        .catch((error) => {
          message.error(error.message || "批量删除失败，请重试");
        });
    },
  });
};

// 重置密码
const handleResetPassword = (record) => {
  resetPassword(record.userId)
    .then((res) => {
      message.success(`重置密码：${record.userName}`);
    })
    .catch((error) => {
      message.error(error.message || "重置密码失败，请重试");
    });
};

// 选择部门
const handleDeptSelect = (selectedKeys, info) => {
  queryParams.deptIds = getAllDeptIds([info.node]).join(",");
  getList();
};

//获取所有deptIds
const getAllDeptIds = (detpList) => {
  return detpList.map((item) => {
    return [item.id, ...getAllDeptIds(item.children)];
  }).flat();
};
</script>

<style lang="scss" scoped>
// 移动端部门选择触发器
.mobile-dept-trigger {
  display: none;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  // 小屏幕显示
  @media (max-width: 768px) {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .ant-btn {
    flex-shrink: 0;
  }
  
  .ant-tag {
    margin: 0;
  }
}

// 部门抽屉样式
:deep(.dept-drawer) {
  .ant-drawer-body {
    padding: 16px;
  }
  
  .ant-drawer-footer {
    border-top: 1px solid #f0f0f0;
    padding: 12px 16px;
  }
}

// 部门树列 - 响应式隐藏
.dept-tree-col {
  // 在小屏幕时隐藏
  @media (max-width: 768px) {
    display: none !important;
  }
}

.dept-tree-container {
  height: 100%;
  min-height: 400px;
  max-height: calc(100vh - 200px);
  overflow: auto;
  display: flex;
  flex-direction: column;
  
  // 部门树头部
  .dept-tree-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
    margin: -24px -36px 16px;
    background: linear-gradient(to right, rgba(24, 144, 255, 0.05), transparent);
    
    h4 {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
      color: #262626;
    }
  }

  // 树节点样式优化
  :deep(.ant-tree) {
    flex: 1;
    overflow: auto;
    
    .ant-tree-node-content-wrapper {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 100%;
      display: inline-block;
      transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
      
      &:hover {
        background-color: rgba(24, 144, 255, 0.08);
      }
    }
    
    .ant-tree-node-selected {
      .ant-tree-node-content-wrapper {
        background-color: rgba(24, 144, 255, 0.12) !important;
      }
    }
  }

  // 平板尺寸优化
  @media (max-width: 992px) and (min-width: 769px) {
    min-height: 350px;
  }
}

// 部门树节点样式
.dept-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;

  .dept-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dept-count {
    margin-left: 8px;
    color: #8c8c8c;
    font-size: 12px;
  }
}
</style>

