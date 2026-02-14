<template>
  <a-modal
    :open="visible"
    title="分配权限"
    :confirm-loading="permissionLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    width="500px"
    :maskClosable="false"
    :destroyOnClose="true"
  >
    <div class="content-container">
      <a-tree
        ref="treeRef"
        checkable
        :tree-data="menuTree"
        v-model:checked-keys="checkedKeys"
        v-model:expandedKeys="expandedKeys"
        v-model:default-expanded-keys="expandedKeys"
        :fieldNames="{
          key: 'menuId',
          title: 'menuName',
          children: 'children',
        }"
      />
      
      <div class="spin-overlay" v-if="permissionLoading">
        <a-spin size="large" tip="权限数据加载中..." class="custom-spin" />
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { message } from "ant-design-vue";
import { getMenuTreeList } from "@/api/modules/menu";
import { assignMenuPermissions, getRoleMenuList } from "@/api/modules/role";

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  roleId: {
    type: [Number, String],
    default: null,
  },
});

const emit = defineEmits(["update:visible", "success"]);

// 权限相关数据
const permissionLoading = ref(false);
const menuTree = ref([]);
const checkedKeys = ref([]);
const halfCheckedKeys = ref([]);
const expandedKeys = ref([]);
const childKeys = ref([]);
const treeRef = ref(null);

// 监听roleId变化，加载权限数据
watch(
  () => props.roleId,
  async (roleId) => {
    permissionLoading.value = true;
    if (roleId && props.visible) {
      await loadPermissionData(roleId);
    }
    // permissionLoading.value = false;
  },
  { immediate: true }
);

// 监听visible变化，当打开时加载权限数据
watch(
  () => props.visible,
  async (val) => {
    if (val && props.roleId) {
      loadPermissionData(props.roleId).finally(() => {
        permissionLoading.value = false;
      });
    }
  }
);

// 处理取消
const handleCancel = () => {
  checkedKeys.value = [];
  halfCheckedKeys.value = [];
  expandedKeys.value = [];
  emit("update:visible", false);
};
//获取树结构最下级节点
const getLeftMenuIds = (treeData) => {
  return treeData.reduce((acc, v) => {
    if (v.children && v.children.length > 0) {
      return acc.concat(getLeftMenuIds(v.children));
    } else {
      return acc.concat(v.menuId);
    }
  }, []);
};

// 加载权限数据
const loadPermissionData = async (roleId) => {
  await getMenuTreeList().then((data) => {
    menuTree.value = data;
    childKeys.value = getLeftMenuIds(menuTree.value);
  });
  await getRoleMenuList(roleId).then((data) => {
    expandedKeys.value = data;
    checkedKeys.value = data.filter((d) => childKeys.value.includes(d));
  });
};

// 获取半选中的节点
const getHalfCheckedKeys = () => {
  return treeRef.value.halfCheckedKeys;
};

// 获取选中的节点
const getCheckedKeys = () => {
  return treeRef.value.checkedKeys;
};

// 提交权限分配
const handleSubmit = () => {
  permissionLoading.value = true;
  const checkedKeys = getCheckedKeys();
  const halfCheckedKeys = getHalfCheckedKeys();
  const allSelectedKeys = [...checkedKeys, ...halfCheckedKeys];
  assignMenuPermissions(props.roleId, allSelectedKeys)
    .then((res) => {
      emit("success", {
        roleId: props.roleId,
        menuIds: checkedKeys.value,
      });
      emit("update:visible", false);
      message.success("权限分配成功");
    })
    .catch((e) => {
      message.error(e.message || "分配权限失败");
    })
    .finally(() => {
      permissionLoading.value = false;
    });
};
</script>

<style lang="scss" scoped>
.content-container {
  position: relative;
  min-height: 300px;
}

.spin-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.65);
  z-index: 10;
  backdrop-filter: blur(1px);
}

:deep(.custom-spin) {
  .ant-spin-text {
    margin-top: 12px;
    font-size: 14px;
    color: var(--primary-color, #1890ff);
  }
  
  .ant-spin-dot-item {
    background-color: var(--primary-color, #1890ff);
  }
}

:deep(.ant-modal-body) {
  padding: 24px;
  max-height: 400px;
  overflow-y: auto;
}

:deep(.ant-tree) {
  background: transparent;
}
</style>