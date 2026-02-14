<template>
  <div class="common-container">
      <!-- 搜索区域 -->
      <a-form layout="inline" :model="queryParams" @finish="handleQuery"
      class="search-form">
        <a-form-item label="角色名称" name="roleName">
          <a-input v-model:value="queryParams.roleName" placeholder="请输入角色名称" allow-clear />
        </a-form-item>
        <a-form-item label="角色编码" name="roleKey">
          <a-input v-model:value="queryParams.roleKey" placeholder="请输入角色编码" allow-clear />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <dict-select
              v-model:value="queryParams.status"
              allow-clear
              dict-type="sys_role_status"
              placeholder="角色状态"
              style="width: 200px"
          />

        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">
              <template #icon>
                <SearchOutlined />
              </template>
              搜索
            </a-button>
            <a-button @click="resetQuery">
              <template #icon>
                <ReloadOutlined />
              </template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <div class="common-table-container" ref="tableContainerRef">
        <!-- 操作按钮区域 -->
        <div class="action-buttons">
          <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon>
              <PlusOutlined />
            </template>
            新增
          </a-button>
          <a-button type="danger" :disabled="selectedRowKeys.length === 0" @click="handleBatchDelete">
            <template #icon>
              <DeleteOutlined />
            </template>
            批量删除
          </a-button>
        </a-space>
        <!-- 表格工具 -->
        <TableTool
          @refresh="getList"
          v-model:tableSize="tableConfig.size"
          :tableColumns="tableConfig.columns"
          :fullScreenElement="tableContainerRef"
        />
      </div>

      <!-- 表格区域 -->
      <a-table :columns="tableColumns" :data-source="roleList"
       :size="tableConfig.size"
       :scroll="{ x: 1300 }"
       :row-key="record => record.roleId" :pagination="{
        total: pagination.total,
        current: queryParams.pageNum,
        pageSize: queryParams.pageSize,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total) => `共 ${total} 条`
      }" :loading="loading" :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <dict-tag
                :value="record.status"
                dict-type="sys_role_status"
            />
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleEdit(record)">
                <template #icon>
                  <edit-outlined/>
                </template>
                编辑
              </a-button>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handlePermission(record)">
                <template #icon>
                  <safety-outlined/>
                </template>
                分配权限
              </a-button>
              <a-button size="small" style="color: red;" type="link" @click="handleDelete(record)">
                <template #icon>
                  <delete-outlined/>
                </template>
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
      </div>
      <!-- 引入角色表单组件 -->
    <RoleForm v-model:open="open" :is-edit="isEdit" :role-data="currentRole" @update:open="open = $event"
        @success="handleFormSuccess" />

      <!-- 引入权限分配组件 -->
      <PermissionForm :visible="permissionOpen" :roleId="currentRoleId" @update:visible="permissionOpen = $event"
        @success="handlePermissionSuccess" />
  </div>
</template>

<script setup>
import {message, Modal} from 'ant-design-vue'
import {
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
  SafetyOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import RoleForm from './components/RoleForm.vue'
import PermissionForm from './components/PermissionForm.vue'
import {batchDeleteRole, deleteRole, getRolePage} from '@/api/modules/role'
import {handleDeletePagination, handleSingleDeletePagination} from '@/utils/pagination'

const appStore = useAppStore()

//获取dom
const tableContainerRef = ref(null)
// 查询参数
const queryParams = reactive({
  roleName: '',
  roleKey: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 表格列定义
const columns = [
  {
    title: '角色名称',
    dataIndex: 'roleName',
    key: 'roleName',
    ellipsis: true,
    visible: true
  },
  {
    title: '角色编码',
    dataIndex: 'roleKey',
    key: 'roleKey',
    visible: true
  },
  {
    title: '排序',
    dataIndex: 'roleSort',
    key: 'roleSort',
    visible: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    visible: true
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'action',
    width: 260,
    visible: true,
    fixed: 'right'
  }
]

// 表格配置
const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: tableContainerRef
})

const tableColumns = computed(() => {
  return tableConfig.columns.filter((column) => column.visible);
});

// 角色列表数据
const roleList = ref()

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 加载状态
const loading = ref(false)

// 选中的行
const selectedRowKeys = ref([])

// 角色表单相关
const open = ref(false)
const currentRole = ref({}) // 当前选中的角色数据
const isEdit = ref(false)

// 权限相关
const permissionOpen = ref(false)
const currentRoleId = ref(null)

// 组件挂载时获取数据
onMounted(() => {
  getList()
})

// 获取角色列表
const getList = () => {
  loading.value = true
  getRolePage(queryParams).then(data => {
    roleList.value = data.records;
    pagination.total = data.total
  }).catch((e) => {
    message.error(e.message || '获取角色列表失败')
  }).finally(() => {
    loading.value = false
  })
}

// 处理查询
const handleQuery = () => {
  pagination.current = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.roleKey = ''
  queryParams.status = undefined
  handleQuery()
}

// 表格变化事件
const handleTableChange = (pag) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  getList()
}

// 选择行变化
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

// 新增角色
const handleAdd = () => {
  currentRole.value = {}
  isEdit.value = false
  setTimeout(() => {
    open.value = true
  }, 100)
}

// 编辑角色
const handleEdit = (record) => {
  currentRole.value = { ...record }
  isEdit.value = true
  setTimeout(() => {
    open.value = true
  }, 100)
}

// 处理新增/编辑表单提交成功
const handleFormSuccess = (data) => {
  if (data.isEdit) {
    // 编辑操作
    const index = roleList.value.findIndex(item => item.roleId === data.roleId)
    if (index !== -1) {
      roleList.value[index] = { ...roleList.value[index], ...data }
    }
  } else {
    // 新增操作
    roleList.value.unshift({
      ...data,
      roleId: Date.now(), // 模拟新ID
      createTime: new Date().toLocaleString()
    })
  }
  getList()
}

// 分配权限
const handlePermission = (record) => {
  currentRoleId.value = record.roleId
  setTimeout(() => {
    permissionOpen.value = true
  }, 100)
}

// 处理权限分配成功
const handlePermissionSuccess = (data) => {

}

// 删除角色
const handleDelete = (row) => {
  Modal.confirm({
    title: '确认删除',
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除角色名称为"${row.roleName}"的数据？`,
    okText: '确定',
    cancelText: '取消',
    onOk() {
      return deleteRole(row.roleId).then(() => {
        message.success('删除成功')
        return handleSingleDeletePagination(roleList.value, queryParams, getList)
      }).catch(error => {
        message.error(error.message || '删除失败，请重试')
      })
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  const roleIds = selectedRowKeys.value
  Modal.confirm({
    title: '确认删除',
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除选中的 ${roleIds.length} 个角色？`,
    okText: '确定',
    cancelText: '取消',
    onOk() {
      return batchDeleteRole(roleIds).then(res => {
        message.success('批量删除成功')
        selectedRowKeys.value = []
        return handleDeletePagination(roleList.value, roleIds, queryParams, getList)
      }).catch(error => {
        console.log(error);
        
        message.error(error.message || '批量删除失败，请重试')
      })
    }
  })
}
</script>

<style lang="scss" scoped>
.role-container {
  padding: 0;

  .ant-card {
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }

  .ant-form {
    margin-bottom: 16px;
  }
}
</style>