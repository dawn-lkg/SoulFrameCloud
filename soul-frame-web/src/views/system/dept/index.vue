<template>
  <div class="common-container">
    <!-- 搜索区域 -->
    <a-form
      layout="inline"
      :model="queryParams"
      @finish="handleQuery"
      class="search-form"
    >
      <a-form-item label="部门名称" name="deptName">
        <a-input
          v-model:value="queryParams.deptName"
          placeholder="请输入部门名称"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-select
          v-model:value="queryParams.status"
          placeholder="部门状态"
          allow-clear
          style="width: 200px"
        >
          <a-select-option value="0">正常</a-select-option>
          <a-select-option value="1">停用</a-select-option>
        </a-select>
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
          <a-button v-if="expandedRowKeys.length > 0" type="danger" @click="collapseAllRows">
            <template #icon>
              <MinusOutlined/>
            </template>
            折叠所有
          </a-button>
          <a-button v-else type="danger" @click="expandAllRows">
            <template #icon>
              <UnorderedListOutlined/>
            </template>
            展开所有
          </a-button>
        </a-space>
        <TableTool
          @refresh="getList"
          v-model:tableSize="tableConfig.size"
          :tableColumns="tableConfig.columns"
          :fullScreenElement="tableContainerRef"
        />
      </div>

      <!-- 表格区域 -->
      <a-table
        :columns="tableColumns"
        :data-source="deptList"
        :size="tableConfig.size"
        :row-key="(record) => record.deptId"
        :pagination="false"
        :loading="loading"
        v-model:expandedRowKeys="expandedRowKeys"
        :defaultExpandAllRows="true"
        :defaultExpandedRowKeys="expandedRowKeys"
        :expandable="{
          getRecordKey: (record) => record.deptId,
          getChildrenColumnName: 'children',
          
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === '0' ? '正常' : '停用' }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleAdd(record)">
                <template #icon>
                  <PlusOutlined />
                </template>
                新增
              </a-button>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleEdit(record)">
                <template #icon>
                  <EditOutlined />
                </template>
                编辑
              </a-button>
              <a-button size="small" style="color: red;" type="link" @click="handleDelete(record)">
                <template #icon>
                  <DeleteOutlined />
                </template>
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
    <!-- 部门表单 -->
  <DeptForm
    v-model:open="deptFormOpen"
    :deptData="currentDept"
    :deptOptions="deptOptions"
    @refresh="getList"
  />
  </div>

  
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { 
  SearchOutlined, 
  ReloadOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined,
  MinusOutlined,
  UnorderedListOutlined
} from '@ant-design/icons-vue'
import TableTool from '@/components/common/table-tool.vue'
import DeptForm from './components/DeptForm.vue'
import { getDeptList, removeDept } from '@/api/modules/dept'

// 查询参数
const queryParams = reactive({
  deptName: '',
  status: undefined
})

// 表格配置
const tableConfig = reactive({
  size: 'middle',
  columns: [
    { title: '部门名称', dataIndex: 'deptName', key: 'deptName', ellipsis: true, visible: true },
    { title: '排序', dataIndex: 'orderNum', key: 'orderNum', visible: true },
    { title: '状态', dataIndex: 'status', key: 'status', visible: true },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', visible: true },
    { title: '操作', dataIndex: 'action', key: 'action', width: 220, fixed: 'right', visible: true }
  ]
})

// 表格列
const tableColumns = computed(() => {
  return tableConfig.columns.filter(column => column.visible)
})

// 表格数据
const deptList = ref([])
// 表格加载状态
const loading = ref(false)
// 表格容器引用
const tableContainerRef = ref(null)
// 展开的行
const expandedRowKeys = ref([])

// 部门表单相关
const deptFormOpen = ref(false)
const currentDept = ref({})

// 部门树选项
const deptOptions = ref([])

// 初始化
onMounted(() => {
  getList()
})

// 获取部门列表
async function getList() {
  loading.value = true
  try {
    const data = await getDeptList(queryParams)
    handleData(data);
    deptList.value = data
    deptOptions.value=[{
      deptName: '顶级部门',
      deptId: 0,
      key: 0,
      children: data
    }];
    if (deptList.value.length > 0) {
      const allKeys = getAllKeys(deptList.value)
      expandedRowKeys.value = allKeys
    }
  } catch (error) {
    console.error('获取部门列表失败', error)
    message.error('获取部门列表失败')
  } finally {
    loading.value = false
  }
}

// 处理数据，children长度为0设置为undefined
const handleData = (data) => {
  data.forEach(item => {
    if (item.children && item.children.length === 0) {
      item.children = undefined
    }else{
      handleData(item.children)
    }
  })
}

// 递归获取所有节点的key
function getAllKeys(data) {
  const keys = []
  function traverse(items) {
    items.forEach(item => {
      keys.push(item.deptId)
      if (item.children && item.children.length > 0) {
        traverse(item.children)
      }
    })
  }
  traverse(data)
  return keys
}

// 展开所有行
function expandAllRows() {
  expandedRowKeys.value = getAllKeys(deptList.value)
}

// 折叠所有行
function collapseAllRows() {
  expandedRowKeys.value = []
}

// 搜索
function handleQuery() {
  getList()
}

// 重置搜索
function resetQuery() {
  queryParams.deptName = ''
  queryParams.status = undefined
  getList()
}

// 新增部门
function handleAdd(record) {
  currentDept.value = record ? { parentId: record.deptId } : {}
  deptFormOpen.value = true
}

// 编辑部门
function handleEdit(record) {
  currentDept.value = { ...record }
  deptFormOpen.value = true
}

// 删除部门
function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定删除部门 ${record.deptName} 吗？`,
    okText: '确认',
    cancelText: '取消',
    okType: 'danger',
    onOk: async () => {
      try {
        await removeDept(record.deptId)
        message.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除失败', error)
      }
    }
  })
}
</script>

<style scoped>

</style>
