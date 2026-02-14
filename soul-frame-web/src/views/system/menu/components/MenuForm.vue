<template>
  <a-modal :open="open" :title="title" :confirm-loading="submitLoading" @ok="handleSubmit" @cancel="handleCancel"
    width="550px" :maskClosable="false" :destroyOnClose="true">
    <a-form :model="form" :rules="rules" ref="formRef" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="上级菜单" name="parentId">
        <a-tree-select v-model:value="form.parentId" :tree-data="menuOptions" placeholder="请选择上级菜单"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }" allow-clear tree-default-expand-all />
      </a-form-item>
      <a-form-item label="菜单类型" name="menuType">
        <a-radio-group v-model:value="form.menuType" @change="handleMenuTypeChange">
          <a-radio value="M">目录</a-radio>
          <a-radio value="C">菜单</a-radio>
          <a-radio value="F">按钮</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="菜单名称" name="menuName">
        <a-input v-model:value="form.menuName" placeholder="请输入菜单名称" />
      </a-form-item>
      <a-form-item v-if="form.menuType !== 'F'" label="菜单图标" name="icon">
        <a-input-group compact style="display: flex;">
          <a-input
              v-model:value="form.icon"
              placeholder="点击选择图标"
              readonly
              style="flex: 1 1 auto;"
          />
          <a-button
              style="flex-shrink: 0; min-width: 40px;"
              type="default"
              @click="handleIconSelect"
          >
            <template #icon>
              <component :is="getIcon(form.icon)" v-if="form.icon" />
              <SearchOutlined v-else />
            </template>
          </a-button>
        </a-input-group>
      </a-form-item>
      <a-form-item label="显示排序" name="orderNum">
        <a-input-number v-model:value="form.orderNum" :min="-1" style="width: 100%" />
      </a-form-item>
      <a-form-item v-if="form.menuType !== 'F'" label="路由地址" name="path">
        <a-input v-model:value="form.path" placeholder="请输入路由地址" />
      </a-form-item>
      <a-form-item v-if="form.menuType !== 'F'" label="路由参数" name="query">
        <a-input v-model:value="form.query" placeholder="访问路由的默认参数，如：{&quot;id&quot;: 1}" />
      </a-form-item>
      <a-form-item v-if="form.menuType === 'C'" label="打开方式" name="isFrame">
        <a-radio-group v-model:value="form.isFrame">
          <a-radio :value="0">组件</a-radio>
          <a-radio :value="1">外链</a-radio>
          <a-radio :value="2">内链</a-radio>
        </a-radio-group>
        <template #help v-if="form.isFrame === 2">
          <span class="help-text">内链使用iframe嵌入目标页面，可填写完整URL或应用内路径</span>
        </template>
      </a-form-item>
      <a-form-item v-if="form.menuType === 'C' && form.isFrame === 0" label="组件路径" name="component">
        <a-input v-model:value="form.component" placeholder="请输入组件路径" />
      </a-form-item>
      <a-form-item v-if="form.menuType === 'F'" label="权限标识" name="perms">
        <a-input v-model:value="form.perms" placeholder="请输入权限标识" />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <dict-radio
            v-model:value="form.status"
            dict-type="sys_menu_status"
        />

      </a-form-item>
      <a-form-item v-if="form.menuType !== 'F'" label="是否显示" name="visible">
        <a-radio-group v-model:value="form.visible">
          <a-radio value="0">显示</a-radio>
          <a-radio value="1">隐藏</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item v-if="form.menuType === 'C'" label="是否缓存" name="isCache">
        <a-radio-group v-model:value="form.isCache">
          <a-radio :value="0">缓存</a-radio>
          <a-radio :value="1">不缓存</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>

    <!-- 图标选择弹窗 -->
    <a-modal v-model:open="iconSelectVisible" title="选择图标" @ok="handleIconSelectOk" width="800px">
      <icon-select v-model="selectedIcon" />
    </a-modal>
  </a-modal>
</template>

<script setup>
import {message} from 'ant-design-vue'
import * as Icons from '@ant-design/icons-vue'
import {SearchOutlined} from '@ant-design/icons-vue'
import {addMenu, updateMenu} from '@/api/modules/menu'
import IconSelect from './IconSelect.vue'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  menuData: {
    type: Object,
    default: () => ({})
  },
  menuOptions: {
    type: Array,
    default: () => []
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:open', 'success'])

// 计算弹窗标题
const title = computed(() => {
  return props.isEdit ? '修改菜单' : '新增菜单';
})

// 表单相关
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  menuId: undefined,
  parentId: 0,
  menuName: '',
  icon: '',
  orderNum: 0,
  path: '',
  query: '',
  component: '',
  menuType: 'M',
  perms: '',
  status: '0',
  isCache: 1,
  isFrame: 0
})

// 计算是否为编辑模式
const isEdit = computed(() => {
  return props.isEdit
})

// 表单验证规则
const rules = computed(() => ({
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  orderNum: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
  path: [
    {
      required: true,
      message: '请输入路由地址',
      trigger: 'blur',
      validator: (rule, value) => {
        if (form.menuType === 'F') return Promise.resolve()
        return value ? Promise.resolve() : Promise.reject(rule.message)
      }
    }
  ]
}))

// 重置表单
const resetForm = () => {
  form.menuId = undefined
  form.parentId = 0
  form.menuName = ''
  form.icon = ''
  form.orderNum = 0
  form.path = ''
  form.query = ''
  form.component = ''
  form.menuType = 'M'
  form.perms = ''
  form.status = '0'
  form.isCache = 1
  form.isFrame = 0

  // 延迟清除验证，确保在表单可见后进行
  setTimeout(() => {
    formRef.value?.resetFields()
  }, 0)
}

// 监听menuData变化，填充表单
watch(
  () => props.menuData,
  (menuData) => {
    if (menuData && Object.keys(menuData).length > 0) {
      resetForm()
      Object.assign(form, menuData)
      selectedIcon.value = menuData.icon || '' // 同步图标选择器的值
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 监听open变化，当关闭时重置表单
watch(
  () => props.open,
  (val) => {
    if (!val) {
      resetForm()
    }
  }
)

// 处理取消
const handleCancel = () => {
  emit('update:open', false)
  resetForm()
}

// 菜单类型变化
const handleMenuTypeChange = (e) => {
  const menuType = e.target.value
  if (menuType === 'F') {
    form.icon = ''
    form.path = ''
    form.query = ''
    form.component = ''
  }
}

// 监听外链选项变化
watch(
  () => form.isFrame,
  (val) => {
    if (val === 1 || val === 2) {
      // 如果是外链或内链，清空组件路径
      form.component = ''
    }
  }
)

// 提交表单
const handleSubmit = () => {
  formRef.value
    .validate()
    .then(async () => {
      try {
        submitLoading.value = true

        // 不再自动添加/前缀，保持外链URL原样
        // 只处理内链的相对路径
        if (form.isFrame === 2 && form.path) {
          // 如果不是以http或https开头的完整URL
          if (!form.path.startsWith('http://') && !form.path.startsWith('https://')) {
            // 如果不是以/开头，添加/
            if (!form.path.startsWith('/')) {
              form.path = '/' + form.path
            }
          }
        }

        // 调用实际API
        if (isEdit.value) {
          // 编辑菜单
          const res = await updateMenu(form)
          message.success('修改成功')
          const returnData = {
            ...form,
            isEdit: true
          }
          emit('success', returnData)
          emit('update:open', false)
        } else {
          // 新增菜单
          const res = await addMenu(form)
          message.success('新增成功')

          // 构造返回数据，包含是否为编辑模式的标志
          const returnData = {
            ...form,
            menuId: res.data?.menuId, // 使用后端返回的菜单ID
            isEdit: false
          }

          emit('success', returnData)
          emit('update:open', false)
        }
      } catch (error) {
        message.error(error.message || '新增失败' || '修改失败')
      } finally {
        submitLoading.value = false
      }
    })
    .catch(error => {
      console.log('验证失败:', error)
    })
}

// 图标选择相关
const iconSelectVisible = ref(false)
const selectedIcon = ref('')

// 获取图标组件
const getIcon = (iconName) => {
  if (!iconName) return null
  const componentName = iconName;
  return Icons[componentName] || null
}

// 处理图标选择
const handleIconSelect = () => {
  selectedIcon.value = form.icon // 设置当前选中的图标
  iconSelectVisible.value = true
}

// 确认图标选择
const handleIconSelectOk = () => {
  form.icon = selectedIcon.value
  iconSelectVisible.value = false
}
</script>

<style lang="scss" scoped>
:deep(.ant-modal-body) {
  padding: 24px 24px 10px;
}

:deep(.ant-form-item) {
  margin-bottom: 20px;
}

:deep(.ant-form-item-label) {
  text-align: right;

  label {
    color: rgba(0, 0, 0, 0.85);
  }
}

.help-text {
  color: #ff4d4f;
  font-size: 12px;
}
</style>