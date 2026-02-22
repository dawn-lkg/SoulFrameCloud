<template>
  <div class="common-container">
      <a-form ref="queryForm" :model="queryParams" class="search-form">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="日志标题" name="title">
              <a-input v-model:value="queryParams.title" placeholder="请输入日志标题" allow-clear
                @keyup.enter="handleQuery" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="操作类型" name="businessType">
              <dict-select
                  v-model:value="queryParams.businessType"
                  allow-clear
                  dict-type="sys_operate_type"
                  placeholder="请选择操作类型"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="操作人员" name="operName">
              <a-input v-model:value="queryParams.operName" placeholder="请输入操作人员" allow-clear
                @keyup.enter="handleQuery" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="操作时间" name="operTimeRange">
              <a-range-picker v-model:value="queryParams.operTimeRange" style="width: 100%" 
                value-format="YYYY-MM-DD" format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
          <div v-if="advanced" class="advanced-search">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="操作状态" name="status">
                  <dict-select
                      v-model:value="queryParams.status"
                      allow-clear
                      dict-type="sys_operate_status"
                      placeholder="请选择操作状态"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="请求方式" name="requestMethod">
                  <a-select v-model:value="queryParams.requestMethod" placeholder="请选择请求方式" allow-clear>
                    <a-select-option value="GET">GET</a-select-option>
                    <a-select-option value="POST">POST</a-select-option>
                    <a-select-option value="PUT">PUT</a-select-option>
                    <a-select-option value="DELETE">DELETE</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="操作IP" name="operIp">
                  <a-input v-model:value="queryParams.operIp" placeholder="请输入操作IP" allow-clear
                    @keyup.enter="handleQuery" />
                </a-form-item>
              </a-col>
            </a-row>
          </div>
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
            <a-button type="link" @click="toggleAdvanced">
              {{ advanced ? '收起' : '展开' }}
              <template #icon>
                <down-outlined v-if="!advanced" />
                <up-outlined v-else />
              </template>
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

      <a-table :loading="loading" :row-key="(record) => record.operId" :size="tableConfig.size" :columns="tableColumns" :data-source="dataLogList" :pagination="pagination"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }" @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'businessType'">
            <dict-tag
                :value="record.businessType"
                dictType="sys_operate_type"
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
              <a-popconfirm title="确定要删除该数据日志吗？" ok-text="确定" cancel-text="取消" @confirm="handleDelete(record)">
                <a-button size="small" style="color: red;" type="link">
                  <template #icon>
                    <delete-outlined/>
                  </template>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <dict-tag
                :value="record.status"
                dict-type="sys_operate_status"
            />
          </template>
        </template>
      </a-table>
    </div>

    <!-- 数据日志详情 -->
    <data-log-detail v-model:visible="detailVisible" :operId="detailInfo.operId" />
  </div>
</template>

<script setup>
import {message, Modal} from 'ant-design-vue'
import {
  ClearOutlined,
  DeleteOutlined,
  DownOutlined,
  ExportOutlined,
  EyeOutlined,
  RedoOutlined,
  SearchOutlined,
  UpOutlined
} from '@ant-design/icons-vue'
import {batchDeleteDataLog, clearDataLog, deleteDataLog, exportDataLog, getDataLogPage} from '@/api/modules/dataLog'
import {handleDeletePagination} from '@/utils/pagination'
import DataLogDetail from './components/DataLogDetail.vue'

const appStore = useAppStore();

// 表格容器引用
const tableContainerRef = ref(null)

// 列表加载状态
const loading = ref(false)

// 列表数据
const dataLogList = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
})

// 查询参数
const queryParams = reactive({
  title: '',
  businessType: '',
  operName: '',
  operTimeRange: [],
  status: '',
  requestMethod: '',
  operIp: '',
})

// 选中行的键值
const selectedRowKeys = ref([])

// 详情弹窗可见状态
const detailVisible = ref(false)

// 详情信息
const detailInfo = ref({})

// 表格列配置
const columns = [
  {
    title: '日志标题',
    dataIndex: 'title',
    key: 'title',
    visible: true,
  },
  {
    title: '请求方式',
    dataIndex: 'requestMethod',
    key: 'requestMethod',
    width: 100,
    visible: true,
  },
  {
    title: '方法',
    dataIndex: 'method',
    key: 'method',
    ellipsis: true,
    visible: true,
  },
  {
    title: '操作类型',
    dataIndex: 'businessType',
    key: 'businessType',
    width: 100,
    visible: true,
  },
  {
    title: '耗费时间',
    dataIndex: 'costTimeDesc',
    key: 'costTimeDesc',
    width: 100,
    visible: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 120,
    visible: true,
  },
  {
    title: '操作人员',
    dataIndex: 'operName',
    key: 'operName',
    width: 120,
    visible: true,
  },
  {
    title: '操作时间',
    dataIndex: 'operTime',
    key: 'operTime',
    width: 180,
    visible: true,
    sorter: true,
  },
  {
    title: '操作IP',
    dataIndex: 'operIp',
    key: 'operIp',
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
    title: '浏览器',
    dataIndex: 'browser',
    key: 'browser',
    width: 140,
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

// 表格配置
const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: tableContainerRef,
})

const tableColumns = computed(() => {
  return tableConfig.columns.filter((column) => column.visible);
});

// 获取数据日志列表
const getList = async () => {
  try {
    loading.value = true
    // 处理日期范围参数
    const params = {
      ...queryParams,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
    }
    
    if (params.operTimeRange && params.operTimeRange.length === 2) {
      params.beginTime = params.operTimeRange[0]
      params.endTime = params.operTimeRange[1]
      delete params.operTimeRange
    }
    
    const data = await getDataLogPage(params)
    dataLogList.value = data.records
    pagination.total = data.total
  } catch (error) {
    message.error(error.message || '获取数据日志列表失败')
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
const handleDetail = async (record) => {
  try {
    loading.value = true
    detailInfo.value = record
    detailVisible.value = true
  } catch (error) {
    console.error('获取日志详情失败', error)
    message.error('获取日志详情失败')
  } finally {
    loading.value = false
  }
}

// 处理单条删除
const handleDelete = async (record) => {
  try {

    await deleteDataLog(record.operId)
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
        await handleDeletePagination(dataLogList.value, selectedRowKeys.value, { ...queryParams, pageNum: pagination.current, pageSize: pagination.pageSize }, getList)
        await batchDeleteDataLog(selectedRowKeys.value)
        getList();
        message.success('批量删除成功')
        selectedRowKeys.value = []
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
    content: '确定要清空所有数据日志吗？该操作不可恢复！',
    okText: '确定',
    cancelText: '取消',
    okType: 'danger',
    async onOk() {
      try {
        await clearDataLog()
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

// 导出数据日志
const handleExport = async () => {
  try {
    message.loading('正在导出数据...')
    
    // 处理导出参数
    const exportParams = { ...queryParams }
    
    // 处理日期范围，转换为后端需要的开始和结束时间格式
    if (exportParams.operTimeRange && exportParams.operTimeRange.length === 2) {
      exportParams.beginTime = exportParams.operTimeRange[0]
      exportParams.endTime = exportParams.operTimeRange[1]
      // 删除原始的operTimeRange参数，避免传递数组
      delete exportParams.operTimeRange
    }
    
    await exportDataLog(exportParams)
    message.success('导出成功')
  } catch (error) {
    console.error('导出失败', error)
    message.error('导出失败')
  }
}

// 查询表单引用
const queryForm = ref(null)

// 组件挂载时获取列表数据
onMounted(() => {
  getList()
})

// 展开收起高级搜索
const advanced = ref(false)
const toggleAdvanced = () => {
  advanced.value = !advanced.value
}
</script>

<style scoped>
</style>