<template>
    <a-modal :open="visible" :title="isEdit ? '编辑待办' : '新建待办'" @ok="handleModalOk" @cancel="handleCancel"
        :confirmLoading="loading">
        <a-form ref="formRef" :model="formState" :rules="formRules" :label-col="{ span: 4 }"
            :wrapper-col="{ span: 20 }">
            <a-form-item label="标题" name="title">
                <a-input v-model:value="formState.title" placeholder="请输入待办标题" />
            </a-form-item>
            <a-form-item label="内容" name="content">
                <a-textarea v-model:value="formState.content" placeholder="请输入待办内容" :rows="4" />
            </a-form-item>
            <a-form-item label="截止时间" name="deadline">
                <a-date-picker v-model:value="formState.deadline" show-time format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" placeholder="请选择截止时间" />
            </a-form-item>
            <a-form-item label="优先级" name="priority">
                <a-radio-group v-model:value="formState.priority">
                    <a-radio value="high">高</a-radio>
                    <a-radio value="medium">中</a-radio>
                    <a-radio value="low">低</a-radio>
                </a-radio-group>
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<script setup>
import { message } from 'ant-design-vue'
import { useTodoStore } from '@/stores';

const todoStore = useTodoStore()
const formRef = ref()
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    data: {
        type: Object,
        default: () => ({})
    },
    isEdit: {
        type: Boolean,
        default: false
    }
})

const isEdit = computed(() => {
    return props.isEdit
})

// 表单验证规则
const formRules = {
    title: [
        { required: true, message: '请输入待办标题', trigger: 'blur' },
        { max: 50, message: '标题不能超过50个字符', trigger: 'blur' }
    ],
    content: [
        { required: true, message: '请输入待办内容', trigger: 'blur' },
        { max: 500, message: '内容不能超过500个字符', trigger: 'blur' }
    ],
    deadline: [
        { required: true, message: '请选择截止时间', trigger: 'change' }
    ],
    priority: [
        { required: true, message: '请选择优先级', trigger: 'change' }
    ]
};

const loading = ref(false)

const emit = defineEmits(['update:visible', 'success', 'cancel'])

const formState = reactive({
    id: undefined,
    title: '',
    content: '',
    deadline: null,
    priority: 'medium'
})

watch(() => props.data, (newVal) => {
    if (newVal && Object.keys(newVal).length > 0) {
        Object.assign(formState, newVal)
    }
}, {
    immediate: true,
})

const handleCancel = () => {
    emit('update:visible', false)
    formRef.value?.resetFields()
}

const handleModalOk = async () => {
    try {
        await formRef.value.validate()
        loading.value = true
        if (props.isEdit) {
            await todoStore.updateTodo(formState.id, formState)
            message.success('修改成功')
        } else {
            await todoStore.createTodo(formState)
            message.success('新增成功')
        }
        emit('success', formState)
        handleCancel()
    } catch (error) {
        console.error('表单验证失败:', error)
    } finally {
        loading.value = false
    }
}

</script>