<template>
  <div class="common-container">
      <!-- 搜索区域 -->
        <a-form layout="inline" :model="queryParams" ref="queryRef" class="search-form">
          <a-form-item label="配置名称" name="configName">
            <a-input v-model:value="queryParams.configName" placeholder="请输入配置名称" allow-clear />
          </a-form-item>
          <a-form-item label="配置键名" name="configKey">
            <a-input v-model:value="queryParams.configKey" placeholder="请输入配置键名" allow-clear />
          </a-form-item>
          <a-form-item label="配置分组" name="configGroup">
            <a-select v-model:value="queryParams.configGroup" placeholder="请选择配置分组" allow-clear style="width: 200px">
              <a-select-option v-for="group in groupOptions" :key="group.value" :value="group.value">
                {{ group.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="是否启用" name="isEnabled">
            <a-select v-model:value="queryParams.isEnabled" placeholder="请选择状态" allow-clear style="width: 200px">
              <a-select-option v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                {{ dict.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleQuery">
                <template #icon><search-outlined /></template>
                搜索
              </a-button>
              <a-button @click="resetQuery">
                <template #icon><reload-outlined /></template>
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      <div class="common-table-container" ref="tableContainerRef">
        <div class="action-buttons">
          <a-space>
            <a-button type="primary" @click="handleAdd">
              <template #icon><plus-outlined /></template>
              新增
            </a-button>
            <a-button type="primary" @click="handleRefreshConfig">
              <template #icon>
                <reload-outlined/>
              </template>
              刷新缓存
            </a-button>
            <a-button type="danger" :disabled="multiple" @click="handleBatchDelete">
              <template #icon><delete-outlined /></template>
              批量删除
            </a-button>
          </a-space>
          <TableTool
            @refresh="getList"
            v-model:tableSize="tableConfig.size"
            :tableColumns="tableConfig.columns"
            :fullScreenElement="tableConfig.fullScreenElement"
          />
        </div>
        <!-- 表格区域 -->
        <a-table
          :columns="tableColumns"
        :data-source="configList"
        :row-key="(record) => record.id"
        :size="tableConfig.size"
        :pagination="{
          total: pagination.total,
          current: queryParams.pageNum,
          pageSize: queryParams.pageSize,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
        }"
        :loading="loading"
        :row-selection="{
          selectedRowKeys: selectedRowKeys,
          onChange: onSelectChange,
        }"
        @change="handleTableChange"
        :scroll="{ x: 1300 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'configType'">
            <a-tag v-if="record.configType === 'string'">字符串</a-tag>
            <a-tag color="green" v-else-if="record.configType === 'number'">数字</a-tag>
            <a-tag color="orange" v-else-if="record.configType === 'boolean'">布尔值</a-tag>
            <a-tag color="red" v-else-if="record.configType === 'json'">JSON对象</a-tag>
            <a-tag color="blue" v-else>{{ record.configType }}</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'isEnabled'">
            <a-switch
              v-model:checked="record.isEnabled"
              :checked-value="1"
              :unchecked-value="0"
              @change="() => handleStatusChange(record)"
            
            />
          </template>
          <template v-else-if="column.dataIndex === 'isSystem'">
            <a-tag color="red" v-if="record.isSystem === 1">是</a-tag>
            <a-tag v-else>否</a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleUpdate(record)">
                <template #icon>
                  <edit-outlined/>
                </template>
                修改
              </a-button>
              <a-button size="small" style="color: #1890ff;" type="link"
                        @click="handleRefreshConfigByKey(record.configKey)">
                <template #icon>
                  <reload-outlined/>
                </template>
                刷新
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

    <!-- 引入配置表单组件 -->
    <ConfigForm
      v-model:open="open"
      :title="title"
      :configData="currentConfig"
      @success="handleFormSuccess"
    />
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
  SearchOutlined
} from '@ant-design/icons-vue'
import {
  batchDeleteConfig,
  deleteConfig,
  getConfigPage,
  refreshConfig,
  refreshConfigByKey,
  updateConfig
} from '@/api/modules/config'
import {handleDeletePagination, handleSingleDeletePagination} from '@/utils/pagination'
import ConfigForm from './components/ConfigForm.vue'

const appStore = useAppStore()

// 表格容器引用
const tableContainerRef = ref(null)

// 遍历字典数据
const sys_normal_disable = ref([
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
])

// 配置分组选项
const groupOptions = ref([
  { label: '默认分组', value: 'default' },
  { label: '系统设置', value: 'system' },
  { label: '邮件设置', value: 'mail' },
  { label: '文件存储', value: 'storage' }
])

// 表格列定义
const columns = [
  {
    title: '配置名称',
    dataIndex: 'configName',
    key: 'configName',
    ellipsis: true,
    visible: true
  },
  {
    title: '配置键名',
    dataIndex: 'configKey',
    key: 'configKey',
    ellipsis: true,
    visible: true
  },
  {
    title: '配置值',
    dataIndex: 'configValue',
    key: 'configValue',
    ellipsis: true,
    visible: true
  },
  {
    title: '配置类型',
    dataIndex: 'configType',
    key: 'configType',
    visible: true
  },
  {
    title: '配置分组',
    dataIndex: 'configGroup',
    key: 'configGroup',
    visible: true
  },
  {
    title: '是否启用',
    dataIndex: 'isEnabled',
    key: 'isEnabled',
    visible: true
  },
  {
    title: '系统配置',
    dataIndex: 'isSystem',
    key: 'isSystem',
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
  return tableConfig.columns.filter((column) => column.visible)
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: undefined,
  configKey: undefined,
  configGroup: undefined,
  isEnabled: undefined
})

// 分页配置
const pagination = reactive({
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100']
})

// 系统配置表格数据
const configList = ref([])
// 弹出层标题
const title = ref('')
// 是否显示弹出层
const open = ref(false)
// 当前操作的配置对象
const currentConfig = ref({})
// 加载状态
const loading = ref(false)
// 选中数组
const selectedRowKeys = ref([])
// 非单个禁用
const single = computed(() => selectedRowKeys.value.length !== 1)
// 非多个禁用
const multiple = computed(() => !selectedRowKeys.value.length)
// 查询表单引用
const queryRef = ref(null)

/** 查询系统配置列表 */
const getList = () => {
  loading.value = true
  getConfigPage(queryParams).then(data => {
    configList.value = data.records
    pagination.total = data.total
  }).catch((e) => {
    message.error(e.message || '获取系统配置列表失败')
  }).finally(() => {
    loading.value = false
  })
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryRef.value.resetFields()
  handleQuery()
}

/** 多选框选中数据 */
const onSelectChange = (selection) => {
  selectedRowKeys.value = selection
}

/** 表格变化事件 */
const handleTableChange = (pagination, filters, sorter) => {
  queryParams.pageNum = pagination.current
  queryParams.pageSize = pagination.pageSize
  getList()
}

/** 新增按钮操作 */
const handleAdd = () => {
  open.value = true
  title.value = '添加系统配置'
  currentConfig.value = {}
}

/** 修改按钮操作 */
const handleUpdate = (row) => {
  currentConfig.value = row
    open.value = true
    title.value = '修改系统配置'
}

/** 表单提交成功回调 */
const handleFormSuccess = () => {
  getList()
}

/** 删除按钮操作 */
const handleDelete = (row) => {
  const id = row?.id;
  Modal.confirm({
    title: '系统提示',
    icon: () => h(ExclamationCircleOutlined),
    content: '确定要删除选中的系统配置项吗？',
    onOk() {
      return deleteConfig(id).then(() => {
        message.success('删除成功')
        handleSingleDeletePagination(configList.value.length, {
          ...queryParams,
          pageNum: pagination.pageNum - 1
        }, getList)
        getList()
      })
    }
  })
}

/** 批量删除按钮操作 */
const handleBatchDelete = () => {
  Modal.confirm({
    title: '系统提示',
    icon: () => h(ExclamationCircleOutlined),
    content: '确定要删除选中的系统配置项吗？',
    onOk() {
      return batchDeleteConfig(selectedRowKeys.value).then(() => {
        message.success('删除成功')
        handleDeletePagination(configList.value, selectedRowKeys.value, {
          ...queryParams,
          pageNum: queryParams.pageNum - 1
        }, getList)
        getList()
      })
    }
  })
}
/** 状态修改 */
const handleStatusChange = (row) => {
  const text = row.isEnabled === 1 ? '启用' : '停用'
  Modal.confirm({
    title: '系统提示',
    icon: () => h(ExclamationCircleOutlined),
    content: `确认要${text}${row.configName}配置吗?`,
    onOk() {
      return updateConfig({ id: row.id, isEnabled: row.isEnabled }).then(() => {
        message.success(`${text}成功`)
      }).catch(() => {
        row.isEnabled = row.isEnabled === 1 ? 0 : 1
      })
    },
    onCancel() {
      row.isEnabled = row.isEnabled === 1 ? 0 : 1
    }
  })
}

/** 刷新系统配置 */
const handleRefreshConfig = () => {
  refreshConfig().then(() => {
    message.success('刷新成功')
  })
}

/** 刷新指定key系统配置 */
const handleRefreshConfigByKey = (key) => {
  refreshConfigByKey(key).then(() => {
    message.success('刷新成功')
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
</style> 