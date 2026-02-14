<template>
  <div class="common-container">
    <!-- 搜索区域 -->
    <a-form
      layout="inline"
      :model="queryParams"
      @finish="handleQuery"
      class="search-form"
    >
      <a-form-item label="菜单名称" name="menuName">
        <a-input
          v-model:value="queryParams.menuName"
          placeholder="请输入菜单名称"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <DictSelect
            dictType="sys_menu_status"
          v-model:value="queryParams.status"
          placeholder="菜单状态"
          allow-clear
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
          :fullScreenElement="tableConfig.fullScreenElement"
        />
      </div>

      <!-- 表格区域 -->
      <a-table
        :columns="tableColumns"
        :data-source="menuList"
        :size="tableConfig.size"
        :row-key="(record) => record.menuId"
        :pagination="false"
        :loading="loading"
        :scroll="{ x: 1300 }"
        v-model:expandedRowKeys="expandedRowKeys"
        :defaultExpandAllRows="true"
        :defaultExpandedRowKeys="expandedRowKeys"
        :expandable="{
          getRecordKey: (record) => record.menuId,
          getChildrenColumnName: 'children',
          
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'icon'">
            <Icon :name="record.icon" />
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === "0" ? "正常" : "停用" }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-space>
              <a-button size="small" type="link" @click="handleAdd(record)">
                <template #icon>
                  <plus-outlined/>
                </template>
                新增
              </a-button>
              <a-button size="small" type="link" @click="handleEdit(record)">
                <template #icon>
                  <edit-outlined/>
                </template>
                修改
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

    <!-- 使用MenuForm组件 -->
    <menu-form
      :open="open"
      :is-edit="isEdit"
      :menu-data="currentMenu"
      :menu-options="menuOptions"
      @update:open="open = $event"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import {message, Modal} from "ant-design-vue";
import {
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  MinusOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  UnorderedListOutlined
} from "@ant-design/icons-vue";
import MenuForm from "./components/MenuForm.vue";
import {getMenuTreeList, removeMenu,} from "@/api/modules/menu";

const appStore = useAppStore();

// 获取dom
const tableContainerRef = ref(null);

// 查询参数
const queryParams = reactive({
  menuName: "",
  status: undefined,
});

// 展开行
const expandedRowKeys = ref([]);

// 表格列定义
const columns = [
  {
    title: "菜单名称",
    dataIndex: "menuName",
    key: "menuName",
    width:150,
    visible: true,
  },
  {
    title: "图标",
    dataIndex: "icon",
    key: "icon",
    width: 80,
    visible: true,
  },
  {
    title: "排序",
    dataIndex: "orderNum",
    key: "orderNum",
    width: 80,
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
    title: "路由地址",
    dataIndex: "path",
    key: "path",
    ellipsis: true,
    visible: true,
  },
  {
    title: "组件路径",
    dataIndex: "component",
    key: "component",
    ellipsis: true,
    visible: true,
  },
  {
    title: "权限标识",
    dataIndex: "perms",
    key: "perms",
    ellipsis: true,
    visible: true,
  },


  {
    title: "操作",
    dataIndex: "action",
    key: "action",
    width: 240,
    visible: true,
    fixed: 'right',
  },
];

// 表格配置
const tableConfig = reactive({
  size: appStore.tableSize,
  columns: columns,
  fullScreenElement: tableContainerRef,
});

// 表格列定义
const tableColumns = computed(() => {
  return tableConfig.columns.filter((column) => column.visible);
});

// 菜单列表数据
const menuList = ref([]);

// 菜单选项
const menuOptions = ref([]);

// 加载状态
const loading = ref(false);

// 表单相关
const open = ref(false);
const currentMenu = ref({});
const isEdit = ref(false);

// 组件挂载时获取数据
onMounted(async () => {
  await getList();
  expandedRowKeys.value = getAllMenuIds(menuList.value);
});

// 将菜单数据转换为树形选择器选项格式
const convertToTreeOptions = (menuItems) => {
  if (!menuItems) return [];

  return menuItems.map((item) => ({
    title: item.menuName,
    value: item.menuId,
    key: item.menuId,
    children: item.children ? convertToTreeOptions(item.children) : undefined,
  }));
};

// 更新菜单选项
const updateMenuOptions = (menuData) => {
  menuOptions.value = [
    {
      title: "主目录",
      value: 0,
      key: 0,
      children: convertToTreeOptions(menuData),
    },
  ];
};

// 获取菜单列表
const getList = async () => {
  loading.value = true;

  await getMenuTreeList(queryParams).then((data) => {
    menuList.value = data;
    updateMenuOptions(menuList.value);
    loading.value = false;
  });
};

// 处理查询
const handleQuery = () => {
  getList();
};

// 重置查询
const resetQuery = () => {
  queryParams.menuName = "";
  queryParams.status = undefined;
  handleQuery();
};

// 新增菜单
const handleAdd = (record) => {
  // 准备新菜单数据
  currentMenu.value = {
    menuId: undefined,
    parentId: record?.menuId || 0,
    menuName: "",
    icon: "",
    orderNum: 0,
    path: "",
    component: "",
    menuType: "M",
    perms: "",
    status: "0",
  };

  isEdit.value = false;

  // 延迟打开弹窗
  setTimeout(() => {
    open.value = true;
  }, 100);
};

// 编辑菜单
const handleEdit = (record) => {
  currentMenu.value = { ...record };
  isEdit.value = true;

  // 延迟打开弹窗
  setTimeout(() => {
    open.value = true;
  }, 100);
};

// 删除菜单
const handleDelete = (record) => {
  Modal.confirm({
    title: "确认删除",
    icon: () => h(ExclamationCircleOutlined),
    content: `是否确认删除菜单 "${record.menuName}" ？此操作将同时删除所有子菜单！`,
    okText: "确定",
    cancelText: "取消",
    okType: "danger",
    onOk() {
      return removeMenu(record.menuId)
        .then((res) => {
          message.success("删除成功");
          getList();
        })
        .catch((e) => {
          message.error(e.message || "删除失败");
        });
    },
  });
};

// 表单提交成功
const handleFormSuccess = (formData) => {
  getList();
};

//获取所有menuId
const getAllMenuIds = (data) => {
  const menuIds = [];
  data.forEach((item) => {
    menuIds.push(item.menuId);
    if (item.children) {
      menuIds.push(...getAllMenuIds(item.children));
    }
  });
  return menuIds;
};

// 展开所有行
const expandAllRows = () => {
  expandedRowKeys.value = getAllMenuIds(menuList.value);
};

// 折叠所有行
const collapseAllRows = () => {
  expandedRowKeys.value = [];
};
</script>

<style lang="scss" scoped>
</style>