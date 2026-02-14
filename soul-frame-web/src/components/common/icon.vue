<template>
    <div class="icon-component" :style="styles" :class="className">
      <!-- Ant Design 图标 -->
      <component 
        v-if="isAntIcon" 
        :is="antIcon" 
        :spin="spin"
        :rotate="rotate"
        :style="{
          fontSize: `${size}px`,
          transform: props.rotate ? `rotate(${props.rotate}deg)` : '',
        }"
      />
      
      <!-- SVG 图标 -->
      <svg 
        v-else-if="isSvgIcon" 
        class="svg-icon" 
        aria-hidden="true"
        :style="iconStyles"
      >
        <use :xlink:href="svgIconName"></use>
      </svg>
      
      <!-- 图片图标 -->
      <img 
        v-else-if="isImageIcon" 
        :src="name" 
        :alt="alt" 
        class="img-icon"
        :style="iconStyles"
      />
      
      <!-- 图标字体 -->
      <i 
        v-else 
        :class="[customIconPrefix, name]"
        :style="iconStyles"
      ></i>
    </div>
  </template>
  
  <script setup>
  import { computed, h } from 'vue';
  import * as AntIcons from '@ant-design/icons-vue';
  
  const props = defineProps({
    // 图标名称
    name: {
      type: String,
      required: true
    },
    // 图标大小
    size: {
      type: [Number, String],
      default: 16
    },
    // 图标颜色
    color: {
      type: String,
      default: ''
    },
    // 是否旋转
    spin: {
      type: Boolean,
      default: false
    },
    // 旋转角度
    rotate: {
      type: Number,
      default: 0
    },
    // 自定义样式
    customStyle: {
      type: Object,
      default: () => ({})
    },
    // 自定义类名
    className: {
      type: String,
      default: ''
    },
    // 图标类型：ant, svg, img, custom
    type: {
      type: String,
      default: 'ant'
    },
    // 自定义图标前缀
    customIconPrefix: {
      type: String,
      default: 'iconfont'
    },
    // 图片alt属性
    alt: {
      type: String,
      default: 'icon'
    }
  });
  if(props.name.startsWith('svg-')){
    props.type = 'svg';
  } else if(props.name.startsWith('img-')||/\.(png|jpg|jpeg|gif|webp|svg)$/.test(props.name)){
    props.type = 'img';
  }
  
  
  // 判断图标类型
  const isAntIcon = computed(() => props.type === 'ant');
  const isSvgIcon = computed(() => props.type === 'svg');
  const isImageIcon = computed(() => props.type === 'img' || /\.(png|jpg|jpeg|gif|webp|svg)$/.test(props.name));
  
  // 获取Ant Design图标
  const antIcon = computed(() => {
    if (!isAntIcon.value) return null;
    
    // 确保图标名称符合规范
    let iconName = props.name;
    if (!iconName.endsWith('Outlined') && 
        !iconName.endsWith('Filled') && 
        !iconName.endsWith('TwoTone')) {
      iconName = iconName + 'Outlined';
    }
    
    // 查找对应的图标组件
    const icon = AntIcons[iconName];
    return icon ? () => h(icon) : null;
  });
  
  // 构建SVG图标名称
  const svgIconName = computed(() => {
    if (!isSvgIcon.value) return '';
    return `#icon-${props.name}`;
  });
  
  // 图标样式
  const styles = computed(() => ({
    fontSize: typeof props.size === 'number' ? `${props.size}px` : props.size,
    color: props.color,
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    ...props.customStyle
  }));
  
  // 内部图标样式
  const iconStyles = computed(() => ({
    transform: props.rotate ? `rotate(${props.rotate}deg)` : '',
    fontSize: typeof props.size === 'number' ? `${props.size}px` : props.size,

  }));
  </script>
  
  <style scoped>
  .icon-component {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    line-height: 0;
  }
  
  .svg-icon {
    width: 1em;
    height: 1em;
    fill: currentColor;
    vertical-align: -0.125em;
    overflow: hidden;
  }
  
  .img-icon {
    height: 1em;
    width: auto;
  }
  </style>