<template>
  <a-drawer
      :destroyOnClose="true"
      :open="open"
      :title="`字典数据 - ${dictType.dictName}`"
      placement="right"
      width="900"
      @close="handleClose"
  >
    <template #extra>
      <a-button type="primary" @click="handleAdd">
        <template #icon>
          <plus-outlined/>
        </template>
        新增
      </a-button>
    </template>

    <!-- 字典数据列表 -->
    <a-table
        :columns="columns"
        :data-source="dictDataList"
        :loading="loading"
        :pagination="pagination"
        bordered
        size="middle"
        @change="handleTableChange"
    >
      <!-- 状态列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '0' ? 'green' : 'red'">
            {{ record.status === '0' ? '正常' : '停用' }}
          </a-tag>
        </template>

        <!-- 默认列 -->
        <template v-if="column.key === 'isDefault'">
          <a-tag :color="record.isDefault === 'Y' ? 'blue' : ''">
            {{ record.isDefault === 'Y' ? '是' : '否' }}
          </a-tag>
        </template>

        <!-- 样式列 -->
        <template v-if="column.key === 'listClass'">
          <a-tag v-if="record.listClass" :color="getTagColor(record.listClass)">
            {{ record.listClass }}
          </a-tag>
          <span v-else>-</span>
        </template>

        <!-- 操作列 -->
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button size="small" style="color: #1890ff;" type="link" @click="handleEdit(record)">
              <template #icon>
                <edit-outlined/>
              </template>
              修改
            </a-button>
            <a-popconfirm
                cancel-text="取消"
                ok-text="确定"
                title="确定删除该字典数据吗？"
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

    <!-- 字典数据表单 -->
    <dict-data-form
        v-model:open="dictDataFormOpen"
        :dict-data="currentDictData"
        :dict-type="dictType.dictType"
        :is-edit="isEdit"
        @success="handleDictDataSuccess"
    />
  </a-drawer>
</template>

<script setup>
import {reactive, ref, watch} from 'vue';
import {message} from 'ant-design-vue';
import {DeleteOutlined, EditOutlined, PlusOutlined} from '@ant-design/icons-vue';
import {deleteDictData, getDictDataPage} from '@/api/modules/dict';
import DictDataForm from './DictDataForm.vue';
import {handleSingleDeletePagination} from '@/utils/pagination';

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  dictType: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:open']);

// 表格列定义
const columns = [
  {
    title: '字典编码',
    dataIndex: 'dictCode',
    key: 'dictCode',
    width: 100
  },
  {
    title: '字典标签',
    dataIndex: 'dictLabel',
    key: 'dictLabel',
    width: 100
  },
  {
    title: '字典键值',
    dataIndex: 'dictValue',
    key: 'dictValue',
    width: 100
  },
  {
    title: '字典排序',
    dataIndex: 'dictSort',
    key: 'dictSort',
    width: 100,
    sorter: true
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100
  },
  {
    title: '是否默认',
    dataIndex: 'isDefault',
    key: 'isDefault',
    width: 100
  },
  {
    title: '样式属性',
    dataIndex: 'cssClass',
    key: 'cssClass',
    width: 120
  },
  {
    title: '回显样式',
    dataIndex: 'listClass',
    key: 'listClass',
    width: 120
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    ellipsis: true
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right'
  }
];

// 状态
const loading = ref(false);
const dictDataList = ref([]);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条记录`
});

// 字典数据表单相关
const dictDataFormOpen = ref(false);
const currentDictData = ref({});
const isEdit = ref(false);

// 监听dictType变化
watch(
    () => props.dictType,
    (val) => {
      if (val && val.dictType) {
        pagination.current = 1;
        getDictDataList();
      }
    },
    {immediate: true}
);

// 监听open变化
watch(
    () => props.open,
    (val) => {
      if (val && props.dictType.dictType) {
        pagination.current = 1;
        getDictDataList();
      }
    }
);

// 获取字典数据列表
const getDictDataList = async () => {
  if (!props.dictType.dictType) return;

  loading.value = true;
  try {
    const params = {
      dictType: props.dictType.dictType,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    };

    const data = await getDictDataPage(params);
    dictDataList.value = data.records;
    pagination.total = data.total;
  } catch (error) {
    message.error('获取字典数据列表失败');
  } finally {
    loading.value = false;
  }
};

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;

  // 处理排序
  if (sorter.field) {
    // 后端排序
  }

  getDictDataList();
};

// 新增字典数据
const handleAdd = () => {
  currentDictData.value = {
    dictCode: undefined,
    dictSort: 0,
    dictLabel: '',
    dictValue: '',
    dictType: props.dictType.dictType,
    cssClass: '',
    listClass: '',
    isDefault: 'N',
    status: '0',
    remark: ''
  };
  isEdit.value = false;
  dictDataFormOpen.value = true;
};

// 编辑字典数据
const handleEdit = (record) => {
  currentDictData.value = {...record};
  isEdit.value = true;
  dictDataFormOpen.value = true;
};

// 删除字典数据
const handleDelete = async (record) => {
  try {
    await deleteDictData(record.dictCode);
    message.success('删除成功');
    handleSingleDeletePagination(dictDataList.value, pagination, getDictDataList);
    getDictDataList();
  } catch (error) {
    console.log(error);

    message.error('删除失败');
  }
};

// 字典数据表单提交成功回调
const handleDictDataSuccess = () => {
  getDictDataList();
};

// 关闭抽屉
const handleClose = () => {
  emit('update:open', false);
};

// 获取标签颜色
const getTagColor = (listClass) => {
  switch (listClass) {
    case 'default':
      return '';
    case 'primary':
      return 'blue';
    case 'success':
      return 'green';
    case 'info':
      return 'cyan';
    case 'warning':
      return 'orange';
    case 'danger':
      return 'red';
    default:
      return '';
  }
};
</script>

<style lang="scss" scoped>
.action-buttons {
  margin-bottom: 16px;
}
</style> 