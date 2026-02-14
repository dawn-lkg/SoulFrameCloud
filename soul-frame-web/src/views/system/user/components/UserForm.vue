<template>
  <a-modal
    :open="open"
    :title="title"
    :confirm-loading="submitLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    width="500px"
    :maskClosable="false"
    :destroyOnClose="true"
  >
    <a-form
      :model="form"
      :rules="rules"
      ref="formRef"
      :label-col="{ span: 5 }"
      :wrapper-col="{ span: 18 }"
    >
      <a-form-item label="用户名" name="userName">
        <a-input v-model:value="form.userName" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item label="用户昵称" name="nickName">
        <a-input v-model:value="form.nickName" placeholder="请输入用户昵称" />
      </a-form-item>
      <a-form-item label="手机号码" name="phone">
        <a-input v-model:value="form.phone" placeholder="请输入手机号码" />
      </a-form-item>
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="form.email" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item label="性别" name="sex">
        <DictRadio v-model:value="form.sex" dict-type="sys_user_sex"/>
      </a-form-item>
      <a-form-item label="用户角色" name="roleIds">
        <a-select
          v-model:value="form.roleIds"
          mode="multiple"
          placeholder="请选择角色"
          style="width: 100%"
        >
          <a-select-option
            v-for="role in roleOptions"
            :key="role.roleId"
            :value="role.roleId"
          >
            {{ role.roleName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="部门" name="deptId">
        <a-tree-select
            v-model:value="form.deptId"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :fieldNames="{ label: 'title', value: 'id', key: 'id' }"
            :tree-data="deptList"
            placeholder="请选择部门"
        />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <DictRadio v-model:value="form.status" dict-type="sys_user_status"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import {message} from "ant-design-vue";
import {addUser, checkUsername, updateUser} from "@/api/modules/user";

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  userData: {
    type: Object,
    default: () => ({}),
  },
  roleOptions: {
    type: Array,
    default: () => [],
  },
  isEdit: {
    type: Boolean,
    default: false,
  },
  deptList: {
    type: Array,
    default: () => [],
  }
});

const emit = defineEmits(["update:open", "success"]);

// 计算弹窗标题
const title = computed(() => {
  return props.isEdit ? '修改用户' : '添加用户';
});

// 表单相关
const submitLoading = ref(false);
const formRef = ref(null);
const form = reactive({
  userId: undefined,
  userName: "",
  phone: "",
  email: "",
  roleIds: [],
  status: "0",
  sex: "0",
});

// 计算是否为编辑模式
const isEdit = computed(() => {
  return props.isEdit;
});

// 表单验证规则 - 根据是否编辑模式调整规则
const rules = computed(() => ({
  userName: [
    { required: true, message: "请输入用户名称", trigger: "blur" },
    // 新增时验证用户名唯一性，编辑时不验证
    ...(isEdit.value ? [] : [{ validator: validateUsername, trigger: "blur" }]),
  ],
  phone: [
    { required: true, message: "请输入手机号码", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "手机号码格式不正确",
      trigger: "blur",
    },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "邮箱格式不正确", trigger: "blur" },
  ],
  roleIds: [{ required: true, message: "请选择角色", trigger: "change" }],
}));

// 用户名唯一性验证
const validateUsername = (rule, value) => {
  if (!value) {
    return Promise.resolve();
  }
  checkUsername(value).then((res) => {
    if (res.data) {
      return Promise.reject("该用户名已存在");
    }
  });
  return Promise.resolve();
};

// 重置表单
const resetForm = () => {
  form.userId = undefined;
  form.userName = "";
  form.phone = "";
  form.email = "";
  form.roleIds = [];
  (form.status = "0"), (form.sex = "0"), (form.nickName = "");
  setTimeout(() => {
    formRef.value?.resetFields();
  }, 0);
};

// 监听userData变化，填充表单
watch(
  () => props.userData,
  (userData) => {
    if (userData && Object.keys(userData).length > 0) {
      resetForm();
      Object.assign(form, userData);
      console.log("表单数据已更新:", form);
    } else {
      resetForm();
    }
  },
  { immediate: true, deep: true }
);

// 监听visible变化，当关闭时重置表单
watch(
  () => props.open,
  (val) => {
    if (!val) {
      resetForm();
    }
  }
);

// 处理取消
const handleCancel = () => {
  emit("update:open", false);
};

// 提交表单
const handleSubmit = () => {
  formRef.value
    .validate()
    .then(async () => {
      try {
        submitLoading.value = true;

        // 调用实际API
        if (isEdit.value) {
          // 编辑用户
          const res = await updateUser(form);
          message.success("修改成功");

          // 构造返回数据，包含是否为编辑模式的标志
          const returnData = {
            ...form,
            isEdit: isEdit.value,
          };

          emit("success", returnData);
          emit("update:open", false);
        } else {
          // 新增用户
          const res = await addUser(form);
          message.success("新增成功");

          // 构造返回数据，包含是否为编辑模式的标志
          const returnData = {
            ...form,
            userId: res.data?.userId, // 使用后端返回的用户ID
            isEdit: false,
          };

          emit("success", returnData);
          emit("update:open", false);
        }
      } catch (error) {
        console.error("提交表单失败:", error);
        message.error(error.message || "操作失败，请重试");
      } finally {
        submitLoading.value = false;
      }
    })
    .catch((error) => {
      console.log("验证失败:", error);
    });
};
</script>

<style lang="sass" scoped>
</style>