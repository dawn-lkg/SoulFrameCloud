<template>
  <div class="common-container">
    <a-form ref="queryForm" :model="queryParams" class="search-form">
      <a-row :gutter="24">
        <a-col :md="6" :sm="24">
            <a-form-item label="用户账号" name="userName">
              <a-input v-model:value="queryParams.userName" placeholder="请输入用户账号" allow-clear
                @keyup.enter="handleQuery" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="登录状态" name="status">
              <dict-select
                  v-model:value="queryParams.status"
                  allow-clear
                  dict-type="sys_loginLog_status"
                  placeholder="请选择登录状态"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="登录IP" name="ipaddr">
              <a-input v-model:value="queryParams.ipaddr" placeholder="请输入登录IP" allow-clear
                @keyup.enter="handleQuery" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="登录时间" name="loginTimeRange">
              <a-range-picker v-model:value="queryParams.loginTimeRange" style="width: 100%" 
                value-format="YYYY-MM-DD" format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <div class="action-row">
          <a-space>
            <a-button type="primary" @click="handleQuery">
              <template #icon><search-outlined /></template>
              查询
            </a-button>
            <a-button @click="resetQuery">
              <template #icon><redo-outlined /></template>
              重置
            </a-button>
          </a-space>
        </div>
      </a-form>

    <div class="common-table-container" ref="tableContainerRef">
      <div class="action-buttons">
        <a-space>
          <a-button type="primary" :disabled="selectedRowKeys.length === 0" @click="handleBatchDelete">
            <template #icon><delete-outlined /></template>
            批量删除
          </a-button>
          <a-button type="danger" @click="handleClear">
            <template #icon><clear-outlined /></template>
            清空日志
          </a-button>
          <a-button @click="handleExport">
            <template #icon><export-outlined /></template>
            导出
          </a-button>
        </a-space>
        <TableTool
          @refresh="getList"
          v-model:tableSize="tableConfig.size"
          :tableColumns="tableConfig.columns"
          :fullScreenElement="tableConfig.fullScreenElement"
        />
      </div>

      <a-table :loading="loading" :size="tableConfig.size" row-key="infoId" :columns="tableColumns" :data-source="loginLogList" :pagination="pagination"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <dict-tag
                :value="record.status"
                dict-type="sys_loginLog_status"
            />
          </template>
          <template v-else-if="column.dataIndex === 'operation'">
            <a-space>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleDetail(record)">
                <template #icon>
                  <EyeOutlined/>
                </template>
                详情
              </a-button>
              <a-popconfirm title="确定要删除该登录日志吗？" ok-text="确定" cancel-text="取消" @confirm="handleDelete(record)">
                <a-button size="small" style="color: red;" type="link">
                  <template #icon>
                    <delete-outlined/>
                  </template>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 使用登录日志详情组件 -->
    <login-log-detail v-model:visible="detailVisible" :detail-data="detailInfo" />
  </div>
</template>

<script setup>
import {message, Modal} from 'ant-design-vue'
import {
  ClearOutlined,
  DeleteOutlined,
  ExportOutlined,
  EyeOutlined,
  RedoOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import {
  batchDeleteLoginLog,
  clearLoginLog,
  deleteLoginLog,
  exportLoginLog,
  getLoginLogPage
} from '@/api/modules/loginLog'
import LoginLogDetail from './components/LoginLogDetail.vue'

const appStore = useAppStore()

const tableContainerRef = ref(null)

// 列表加载状态
const loading = ref(false)

// 列表数据
const loginLogList = ref([])

// 分页配置
const pagination = reactive({
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
})

// 查询参数
const queryParams = reactive({})

// 选中行的键值
const selectedRowKeys = ref([])

// 详情弹窗可见状态
const detailVisible = ref(false)

// 详情信息
const detailInfo = ref({})

// 查询表单引用
const queryForm = ref(null)

// 表格列配置
const columns = [
  {
    title: '访问编号',
    dataIndex: 'infoId',
    key: 'infoId',
    width: 100,
    visible: true,
  },
  {
    title: '用户账号',
    dataIndex: 'userName',
    key: 'userName',
    width: 120,
    visible: true,
  },
  {
    title: '登录IP',
    dataIndex: 'ipaddr',
    key: 'ipaddr',
    width: 140,
    visible: true,
  },
  {
    title: '登录地点',
    dataIndex: 'loginLocation',
    key: 'loginLocation',
    autoWidth: true,
    visible: true,
  },
  {
    title: '浏览器',
    dataIndex: 'browser',
    key: 'browser',
    width: 140,
    visible: true,
  },
  {
    title: '操作系统',
    dataIndex: 'os',
    key: 'os',
    width: 140,
    visible: true,
  },
  {
    title: '登录状态',
    dataIndex: 'status',
    key: 'status',
    visible: true,
  },
  {
    title: '提示消息',
    dataIndex: 'msg',
    key: 'msg',
    ellipsis: true,
    visible: true,
  },
  {
    title: '登录时间',
    dataIndex: 'loginTime',
    key: 'loginTime',
    width: 180,
    sorter: true,
    visible: true,
  },
  {
    title: '操作',
    dataIndex: 'operation',
    key: 'operation',
    fixed: 'right',
    width: 180,
    visible: true,
  },
]

const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: tableContainerRef,
})

const tableColumns = computed(() => {
  return tableConfig.columns.filter(column => column.visible)
})

// 获取登录日志列表
const getList = async () => {
  try {
    loading.value = true
    // 处理日期范围参数
    const params = {
      ...queryParams,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
    }
    
    if (params.loginTimeRange && params.loginTimeRange.length === 2) {
      params.beginTime = params.loginTimeRange[0]
      params.endTime = params.loginTimeRange[1]
      delete params.loginTimeRange
    }
    
    const data = await getLoginLogPage(params)
    loginLogList.value = data.records
    pagination.total = data.total
  } catch (error) {
    message.error(error.message || '获取登录日志列表失败')
  } finally {
    loading.value = false
  }
}

// 表格变化事件
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  if (sorter.field) {
    queryParams.orderByColumn = sorter.field
    queryParams.isAsc = sorter.order === 'ascend' ? 'asc' : 'desc'
  } else {
    queryParams.orderByColumn = undefined
    queryParams.isAsc = undefined
  }
  getList()
}

// 执行查询
const handleQuery = () => {
  pagination.current = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryForm.value.resetFields()
  selectedRowKeys.value = []
  pagination.current = 1
  getList()
}

// 选择行变化
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

// 处理查看详情
const handleDetail = (record) => {
  detailInfo.value = record
  detailVisible.value = true
}

// 处理单条删除
const handleDelete = async (record) => {
  try {
    await deleteLoginLog(record.infoId)
    getList()
    message.success('删除成功')
  } catch (error) {
    console.error('删除失败', error)
    message.error('删除失败')
  }
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请至少选择一条记录')
    return
  }

  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条数据吗？`,
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      try {
        await batchDeleteLoginLog(selectedRowKeys.value)
        message.success('批量删除成功')
        selectedRowKeys.value = []
        getList()
      } catch (error) {
        console.error('批量删除失败', error)
        message.error('批量删除失败')
      }
    },
  })
}

// 处理清空日志
const handleClear = () => {
  Modal.confirm({
    title: '确认清空',
    content: '确定要清空所有登录日志吗？该操作不可恢复！',
    okText: '确定',
    cancelText: '取消',
    okType: 'danger',
    async onOk() {
      try {
        await clearLoginLog()
        message.success('清空成功')
        pagination.current = 1
        selectedRowKeys.value = []
        await getList()
      } catch (error) {
        console.error('清空失败', error)
        message.error('清空失败')
      }
    },
  })
}

// 导出登录日志
const handleExport = async () => {
  try {
    message.loading('正在导出数据...')
    
    // 处理导出参数
    const exportParams = { ...queryParams }
    
    // 处理日期范围，转换为后端需要的开始和结束时间格式
    if (exportParams.loginTimeRange && exportParams.loginTimeRange.length === 2) {
      exportParams.beginTime = exportParams.loginTimeRange[0]
      exportParams.endTime = exportParams.loginTimeRange[1]
      // 删除原始的loginTimeRange参数，避免传递数组
      delete exportParams.loginTimeRange
    }
    
    await exportLoginLog(exportParams)
    message.success('导出成功')
  } catch (error) {
    console.error('导出失败', error)
    message.error('导出失败')
  }
}

// 组件挂载时获取列表数据
onMounted(() => {
  getList()
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