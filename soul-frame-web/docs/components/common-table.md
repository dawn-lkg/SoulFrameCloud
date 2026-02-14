# CommonTable 通用表格组件

## 组件概述

`CommonTable` 是一个功能完整的通用表格组件，集成了搜索表单、操作按钮、表格显示、分页、列设置等功能。该组件基于 Ant Design Vue 的 Table 组件构建，提供了更丰富的功能和更好的用户体验。

## ✨ 新特性

- 🎯 **智能分页**: 支持快速跳转、页码选择器
- 📱 **响应式设计**: 移动端友好的布局
- 🎨 **主题定制**: 支持边框、粘性表头等样式配置
- 🔄 **行操作**: 支持行点击、展开行等交互
- 📊 **数据统计**: 显示数据总数和选中数量
- 🎭 **空状态**: 自定义空数据展示
- 🚀 **性能优化**: 计算属性缓存、深度监听优化

## 基本用法

```vue
<template>
  <CommonTable 
    v-model:columns="columns" 
    v-model:dataSource="data"
    :loading="loading"
    v-model:selectedRowKeys="selectedRowKeys"
    v-model:pagination="tablePagination"
    rowKey="userId"
    @search="handleSearch"
    @reset="handleReset"
    @change="handleTableChange">
    
    <!-- 搜索表单插槽 -->
    <template #search-form>
      <a-form-item label="用户名称" name="userName">
        <a-input
          v-model:value="queryParams.userName"
          placeholder="请输入用户名称"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-select
          v-model:value="queryParams.status"
          placeholder="请选择状态"
          allow-clear
        >
          <a-select-option value="1">启用</a-select-option>
          <a-select-option value="0">禁用</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" @click="handleSearch">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button @click="handleReset">
            <template #icon><RedoOutlined /></template>
            重置
          </a-button>
        </a-space>
      </a-form-item>
    </template>

    <!-- 操作按钮插槽 -->
    <template #action-buttons>
      <a-button type="primary" size="small" @click="handleAdd">新增</a-button>
      <a-button 
        type="danger" 
        size="small" 
        :disabled="!selectedRowKeys.length" 
        @click="handleBatchDelete"
      >批量删除</a-button>
    </template>

    <!-- 表格单元格自定义插槽 -->
    <template #bodyCell="{ column, record, index }">
      <span v-if="column.dataIndex === 'action'">
        <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
        <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
      </span>
      <span v-else-if="column.dataIndex === 'status'">
        <a-tag :color="record.status === '1' ? 'green' : 'red'">
          {{ record.status === '1' ? '启用' : '禁用' }}
        </span>
    </template>

    <!-- 展开行内容插槽 -->
    <template #expandedRowRender="{ record, index, indent, expanded }">
      <div class="expanded-content">
        <p>用户ID: {{ record.userId }}</p>
        <p>创建时间: {{ record.createTime }}</p>
      </div>
    </template>

    <!-- 自定义空状态 -->
    <template #emptyText>
      <a-empty description="暂无用户数据" />
    </template>
  </CommonTable>
</template>
```

## Props 属性

### 数据相关

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `dataSource` | `Array` | `[]` | 表格数据源 |
| `loading` | `Boolean` | `false` | 表格加载状态 |
| `columns` | `Array` | `[]` | 表格列配置 |

### 分页相关

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `pagination` | `Object \| Boolean` | 见下方默认值 | 分页配置，设为 `false` 可隐藏分页 |

### 行相关

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `rowKey` | `String \| Function` | `"id"` | 表格行数据的 key，支持字符串或函数 |
| `selectedRowKeys` | `Array` | `[]` | 选中的行 key 数组 |
| `rowSelection` | `Object \| Boolean` | `{}` | 行选择配置，设为 `false` 可隐藏选择框 |

### 表格配置

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `scroll` | `Object` | `{ x: 'max-content' }` | 表格滚动配置 |
| `bordered` | `Boolean` | `false` | 是否显示边框 |
| `sticky` | `Boolean \| Object` | `false` | 是否使用粘性表头 |

### 功能开关

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `showTableInfo` | `Boolean` | `true` | 是否显示表格底部信息 |
| `showSelection` | `Boolean` | `true` | 是否显示行选择框 |

### 空状态

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `emptyText` | `String` | `'暂无数据'` | 空数据提示文本 |
| `emptyImage` | `String` | 默认图片 | 空数据图片地址 |

### pagination 默认值

```javascript
{
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
  pageSizeOptions: ['10', '20', '50', '100']
}
```

## Events 事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `search` | - | 搜索按钮点击事件 |
| `reset` | - | 重置按钮点击事件 |
| `refresh` | - | 刷新按钮点击事件 |
| `change` | `{ pagination, filters, sorter, extra }` | 表格变化事件（分页、筛选、排序） |
| `row` | `{ type, record, index, selectedRowKeys?, selectedRows?, info? }` | 行相关事件（点击、选择） |
| `expand` | `expanded, record` | 展开行事件 |
| `expandedRowsChange` | `expandedRows` | 展开行变化事件 |
| `update:loading` | `Boolean` | 更新加载状态 |
| `update:selectedRowKeys` | `Array` | 更新选中的行 keys |
| `update:scroll` | `Object` | 更新滚动配置 |
| `update:pagination` | `Object` | 更新分页配置 |
| `update:columns` | `Array` | 更新列配置 |

## Slots 插槽

### `search-form`
搜索表单插槽，用于放置搜索条件表单。

**注意：** 搜索和重置按钮需要在此插槽中手动添加，因为组件本身不再提供这些按钮。

### `action-buttons`
操作按钮插槽，用于放置表格上方的操作按钮（如新增、批量删除等）。

### `bodyCell`
表格单元格自定义插槽，用于自定义特定列的显示内容。

**参数：**
- `column`: 列配置对象
- `record`: 当前行数据
- `index`: 行索引

### `expandedRowRender`
展开行内容插槽，用于自定义展开行的显示内容。

**参数：**
- `record`: 当前行数据
- `index`: 行索引
- `indent`: 缩进层级
- `expanded`: 是否展开

### `emptyText`
空数据状态插槽，用于自定义空数据的显示内容。

## 列配置 (columns)

每列支持以下配置：

```javascript
const columns = [
  {
    title: "用户名称",           // 列标题
    dataIndex: "userName",      // 数据字段名
    key: "userName",            // 列的唯一标识
    width: 150,                 // 列宽度
    visible: true,              // 是否可见（用于列设置）
    ellipsis: true,             // 是否显示省略号
    fixed: "right",             // 固定列位置（left/right）
    sorter: true,               // 是否可排序
    filters: [],                // 筛选选项
    customRender: ({ text, record, index }) => {}, // 自定义渲染函数
    children: [],               // 子列配置（用于表头分组）
    onCell: (record, index) => {}, // 单元格属性
    onHeaderCell: (column) => {}, // 表头单元格属性
  }
];
```

## 完整示例

### 1. 基础表格

```vue
<template>
  <CommonTable 
    :columns="columns"
    :dataSource="tableData"
    :loading="loading"
    :pagination="pagination"
    @change="handleTableChange">
  </CommonTable>
</template>

<script setup>
import { ref, reactive } from 'vue';

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '年龄', dataIndex: 'age', key: 'age' },
  { title: '地址', dataIndex: 'address', key: 'address' }
];

const tableData = ref([
  { key: '1', name: '张三', age: 32, address: '北京市朝阳区' },
  { key: '2', name: '李四', age: 42, address: '上海市浦东新区' }
]);

const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 2
});

const handleTableChange = ({ pagination: pag }) => {
  console.log('分页变化:', pag);
};
</script>
```

### 2. 带搜索和操作的表格

```vue
<template>
  <CommonTable 
    v-model:columns="columns"
    v-model:dataSource="tableData"
    :loading="loading"
    v-model:selectedRowKeys="selectedRowKeys"
    v-model:pagination="pagination"
    rowKey="id"
    :bordered="true"
    :sticky="true"
    @search="handleSearch"
    @reset="handleReset"
    @change="handleTableChange"
    @row="handleRowClick">
    
    <template #search-form>
      <a-form-item label="姓名" name="name">
        <a-input v-model:value="searchForm.name" placeholder="请输入姓名" />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button @click="handleReset">重置</a-button>
        </a-space>
      </a-form-item>
    </template>

    <template #action-buttons>
      <a-button type="primary" @click="handleAdd">新增</a-button>
      <a-button 
        type="danger" 
        :disabled="!selectedRowKeys.length"
        @click="handleBatchDelete"
      >批量删除</a-button>
    </template>

    <template #bodyCell="{ column, record, index }">
      <span v-if="column.dataIndex === 'action'">
        <a-button type="link" @click="handleEdit(record)">编辑</a-button>
        <a-button type="link" danger @click="handleDelete(record)">删除</a-button>
      </span>
    </template>
  </CommonTable>
</template>

<script setup>
import { ref, reactive } from 'vue';

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '年龄', dataIndex: 'age', key: 'age' },
  { title: '地址', dataIndex: 'address', key: 'address' },
  { title: '操作', dataIndex: 'action', key: 'action', width: 150 }
];

const tableData = ref([]);
const loading = ref(false);
const selectedRowKeys = ref([]);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});

const searchForm = reactive({
  name: ''
});

const handleSearch = () => {
  pagination.current = 1;
  fetchData();
};

const handleReset = () => {
  searchForm.name = '';
  pagination.current = 1;
  fetchData();
};

const handleTableChange = ({ pagination: pag }) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchData();
};

const handleRowClick = ({ type, record, index }) => {
  if (type === 'click') {
    console.log('点击行:', record, index);
  }
};

const fetchData = () => {
  // 实现数据获取逻辑
};

const handleAdd = () => {
  // 实现新增逻辑
};

const handleEdit = (record) => {
  // 实现编辑逻辑
};

const handleDelete = (record) => {
  // 实现删除逻辑
};

const handleBatchDelete = () => {
  // 实现批量删除逻辑
};
</script>
```

### 3. 高级功能表格

```vue
<template>
  <CommonTable 
    v-model:columns="columns"
    :dataSource="tableData"
    :loading="loading"
    :pagination="pagination"
    :bordered="true"
    :sticky="true"
    :scroll="{ x: 1200, y: 400 }"
    @change="handleTableChange"
    @expand="handleExpand"
    @expandedRowsChange="handleExpandedRowsChange">
    
    <template #expandedRowRender="{ record, index, indent, expanded }">
      <div class="expanded-content">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="用户ID">{{ record.id }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ record.createTime }}</a-descriptions-item>
          <a-descriptions-item label="最后登录">{{ record.lastLogin }}</a-descriptions-item>
          <a-descriptions-item label="状态">{{ record.status }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </template>

    <template #bodyCell="{ column, record, index }">
      <span v-if="column.dataIndex === 'status'">
        <a-tag :color="getStatusColor(record.status)">
          {{ getStatusText(record.status) }}
        </a-tag>
      </span>
      <span v-else-if="column.dataIndex === 'action'">
        <a-space>
          <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
          <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
          <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
        </a-space>
      </span>
    </template>
  </CommonTable>
</template>

<script setup>
import { ref, reactive } from 'vue';

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name', width: 120, fixed: 'left' },
  { title: '年龄', dataIndex: 'age', key: 'age', width: 80 },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 200, ellipsis: true },
  { title: '地址', dataIndex: 'address', key: 'address', width: 300, ellipsis: true },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', dataIndex: 'action', key: 'action', width: 200, fixed: 'right' }
];

const tableData = ref([
  { 
    id: 1, 
    name: '张三', 
    age: 32, 
    email: 'zhangsan@example.com',
    address: '北京市朝阳区建国门外大街1号',
    status: 'active',
    createTime: '2024-01-01',
    lastLogin: '2024-01-15'
  }
]);

const loading = ref(false);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 1
});

const handleTableChange = ({ pagination: pag, filters, sorter }) => {
  console.log('表格变化:', { pagination: pag, filters, sorter });
};

const handleExpand = (expanded, record) => {
  console.log('展开行:', expanded, record);
};

const handleExpandedRowsChange = (expandedRows) => {
  console.log('展开行变化:', expandedRows);
};

const getStatusColor = (status) => {
  const colors = {
    active: 'green',
    inactive: 'red',
    pending: 'orange'
  };
  return colors[status] || 'default';
};

const getStatusText = (status) => {
  const texts = {
    active: '活跃',
    inactive: '非活跃',
    pending: '待审核'
  };
  return texts[status] || status;
};

const handleView = (record) => {
  console.log('查看:', record);
};

const handleEdit = (record) => {
  console.log('编辑:', record);
};

const handleDelete = (record) => {
  console.log('删除:', record);
};
</script>

<style scoped>
.expanded-content {
  padding: 16px;
  background-color: #fafafa;
}
</style>
```

## 组件方法

通过 `ref` 可以调用以下方法：

```javascript
const tableRef = ref();

// 刷新表格
tableRef.value.refresh();

// 获取表格DOM引用
const tableElement = tableRef.value.getTableRef();

// 获取可见列
const visibleColumns = tableRef.value.getVisibleColumns();

// 更新列配置
tableRef.value.updateColumns(newColumns);
```

## 注意事项

1. **搜索表单**: 组件不再自动提供搜索和重置按钮，需要在 `search-form` 插槽中手动添加
2. **列可见性**: 通过 `visible` 属性控制列的显示/隐藏，配合 `TableTool` 组件实现列设置功能
3. **行选择**: 支持单选和多选，通过 `selectedRowKeys` 和 `rowSelection` 配置
4. **分页**: 分页配置完全可定制，支持服务端分页，可设置为 `false` 隐藏分页
5. **响应式**: 组件支持响应式设计，列配置变化会自动更新表格显示
6. **性能**: 使用计算属性优化渲染性能，支持大数据量表格
7. **可访问性**: 支持键盘导航和屏幕阅读器

## 样式定制

组件提供了丰富的样式类，可以通过 CSS 进行定制：

```css
.common-table-container {
  /* 容器样式 */
}

.search-form-wrapper {
  /* 搜索表单区域样式 */
}

.table-wrapper {
  /* 表格包装器样式 */
}

.table-header {
  /* 表格头部样式 */
}

.action-buttons {
  /* 操作按钮区域样式 */
}

.main-table {
  /* 主表格样式 */
}

.table-info {
  /* 表格信息区域样式 */
}

/* 响应式样式 */
@media (max-width: 768px) {
  /* 移动端样式 */
}

/* 全屏模式样式 */
.fullscreen-active {
  /* 全屏状态样式 */
}
```

## 更新日志

- **v3.0.0**: 全面重构，新增响应式设计、高级功能、性能优化
  - 新增响应式布局支持
  - 新增表格边框、粘性表头等样式配置
  - 新增行点击、展开行等交互功能
  - 新增表格信息显示
  - 优化分页配置，支持快速跳转
  - 新增组件方法暴露
  - 优化样式系统，使用SCSS
- **v2.0.0**: 重构搜索表单，移除内置搜索按钮，改为插槽方式
- **v1.0.0**: 初始版本，包含完整的表格功能 