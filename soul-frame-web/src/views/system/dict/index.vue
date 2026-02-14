<template>
  <div class="common-container">
    <!-- 搜索区域 -->
    <a-form :model="queryParams" class="search-form" layout="inline">
      <a-form-item label="字典名称">
        <a-input v-model:value="queryParams.dictName" allow-clear placeholder="请输入字典名称"/>
      </a-form-item>
      <a-form-item label="字典类型">
        <a-input v-model:value="queryParams.dictType" allow-clear placeholder="请输入字典类型"/>
      </a-form-item>
      <a-form-item label="状态">
        <a-select v-model:value="queryParams.status" allow-clear placeholder="请选择状态" style="width: 120px">
          <a-select-option v-for="dict in statusOptions" :key="dict.value" :value="dict.value">
            {{ dict.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="创建时间">
        <a-range-picker v-model:value="queryParams.dateRange" format="YYYY-MM-DD" value-format="YYYY-MM-DD"/>
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
          <a-button type="primary" @click="handleAdd">
            <template #icon>
              <PlusOutlined/>
            </template>
            新增
          </a-button>
          <a-button :disabled="!selectedRowKeys.length" danger @click="handleBatchDelete">
            <template #icon>
              <DeleteOutlined/>
            </template>
            批量删除
          </a-button>
          <a-button @click="handleRefreshCache">
            <template #icon>
              <ReloadOutlined/>
            </template>
            刷新缓存
          </a-button>
        </a-space>
        <TableTool
            v-model:tableSize="tableConfig.size"
            :fullScreenElement="tableConfig.fullScreenElement"
            :tableColumns="tableConfig.columns"
            @refresh="getDictTypeList"
        />
      </div>

      <!-- 字典类型列表 -->
      <a-table
          :columns="tableColumns"
          :data-source="dictTypeList"
          :loading="loading"
          :pagination="pagination"
          :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
          :size="tableConfig.size"
          @change="handleTableChange"
      >
        <!-- 状态列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === '0' ? '正常' : '停用' }}
            </a-tag>
          </template>

          <!-- 操作列 -->
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleData(record)">
                <template #icon>
                  <database-outlined/>
                </template>
                数据
              </a-button>
              <a-button size="small" style="color: #1890ff;" type="link" @click="handleEdit(record)">
                <template #icon>
                  <edit-outlined/>
                </template>
                修改
              </a-button>
              <a-popconfirm
                  cancel-text="取消"
                  ok-text="确定"
                  title="确定删除该字典类型吗？"
                  @confirm="handleDelete(record)"
              >
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

    <!-- 字典类型表单 -->
    <dict-type-form
        v-model:open="dictTypeFormOpen"
        :dict-type-data="currentDictType"
        :is-edit="isEdit"
        @success="handleDictTypeSuccess"
    />

    <!-- 字典数据列表 -->
    <dict-data
        v-model:open="dictDataOpen"
        :dict-type="currentDictType"
    />
  </div>
</template>

<script setup>
import {message, Modal} from 'ant-design-vue';
import {
  DatabaseOutlined,
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined
} from '@ant-design/icons-vue';
import {batchDeleteDictType, deleteDictType, getDictTypePage, refreshDictCache} from '@/api/modules/dict';
import {handleDeletePagination, handleSingleDeletePagination} from '@/utils/pagination';
import DictTypeForm from './components/DictTypeForm.vue';
import DictData from './components/DictData.vue';

const appStore = useAppStore();

// 表格容器引用
const tableContainerRef = ref(null);

// 状态选项
const statusOptions = [
  {label: '正常', value: '0'},
  {label: '停用', value: '1'}
];

// 表格列定义
const columns = [
  {
    title: '字典编号',
    dataIndex: 'dictId',
    key: 'dictId',
    width: 100,
    visible: true
  },
  {
    title: '字典名称',
    dataIndex: 'dictName',
    key: 'dictName',
    ellipsis: true,
    visible: true
  },
  {
    title: '字典类型',
    dataIndex: 'dictType',
    key: 'dictType',
    ellipsis: true,
    visible: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    visible: true
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    ellipsis: true,
    visible: true
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    sorter: true,
    visible: true
  },
  {
    title: '操作',
    key: 'action',
    width: 250,
    fixed: 'right',
    visible: true
  }
];

const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: tableContainerRef
});

const tableColumns = computed(() => {
  return tableConfig.columns.filter(item => item.visible);
});

// 状态
const loading = ref(false);
const dictTypeList = ref([]);
const selectedRowKeys = ref([]);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条记录`
});

// 查询参数
const queryParams = reactive({
  dictName: '',
  dictType: '',
  status: undefined,
  dateRange: [],
  pageNum: 1,
  pageSize: 10
});

// 字典类型表单相关
const dictTypeFormOpen = ref(false);
const currentDictType = ref({});
const isEdit = ref(false);

// 字典数据相关
const dictDataOpen = ref(false);

// 生命周期钩子
onMounted(() => {
  getDictTypeList();
});

// 获取字典类型列表
const getDictTypeList = async () => {
  loading.value = true;
  try {
    const params = {
      ...queryParams,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    };

    if (params.dateRange && params.dateRange.length === 2) {
      params.beginTime = params.dateRange[0];
      params.endTime = params.dateRange[1];
      delete params.dateRange;
    }

    const data = await getDictTypePage(params);
    dictTypeList.value = data.records;
    pagination.total = data.total;
  } catch (error) {
    message.error('获取字典类型列表失败');
  } finally {
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

  getDictTypeList();
};

// 搜索
const handleQuery = () => {
  pagination.current = 1;
  getDictTypeList();
};

// 重置查询
const resetQuery = () => {
  queryParams.dictName = '';
  queryParams.dictType = '';
  queryParams.status = undefined;
  queryParams.dateRange = [];
  handleQuery();
};

// 新增字典类型
const handleAdd = () => {
  currentDictType.value = {
    dictId: undefined,
    dictName: '',
    dictType: '',
    status: '0',
    remark: ''
  };
  isEdit.value = false;
  dictTypeFormOpen.value = true;
};

// 编辑字典类型
const handleEdit = (record) => {
  currentDictType.value = {...record};
  isEdit.value = true;
  dictTypeFormOpen.value = true;
};

// 查看字典数据
const handleData = (record) => {
  currentDictType.value = {...record};
  dictDataOpen.value = true;
};

// 删除字典类型
const handleDelete = async (record) => {
  try {
    await deleteDictType(record.dictId);
    message.success('删除成功');
    handleSingleDeletePagination(dictTypeList.value, pagination, getDictTypeList);
    getDictTypeList();
  } catch (error) {
    message.error('删除失败');
  }
};

// 批量删除字典类型
const handleBatchDelete = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要删除的字典类型');
    return;
  }

  Modal.confirm({
    title: '确认删除',
    icon: h(ExclamationCircleOutlined),
    content: `确定删除已选中的 ${selectedRowKeys.value.length} 个字典类型吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await batchDeleteDictType(selectedRowKeys.value);
        message.success('批量删除成功');
        selectedRowKeys.value = [];
        handleDeletePagination(dictTypeList.value, pagination, getDictTypeList);
        getDictTypeList();
      } catch (error) {
        message.error('批量删除失败');
      }
    }
  });
};

// 刷新字典缓存
const handleRefreshCache = async () => {
  try {
    await refreshDictCache();
    message.success('刷新字典缓存成功');
  } catch (error) {
    message.error('刷新字典缓存失败');
  }
};

// 字典类型表单提交成功回调
const handleDictTypeSuccess = () => {
  getDictTypeList();
};
</script>

<style lang="scss" scoped>
.search-form {
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style> 