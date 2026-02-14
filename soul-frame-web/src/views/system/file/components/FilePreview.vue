<template>
  <div class="file-preview">
    <div class="preview-header">
      <div class="file-title">
        <file-type-icon
            :file-extension="file.fileExtension"
            :file-type="fileType"
        />
        <span class="file-name">{{ file.originalName }}</span>
      </div>
      <a-space>
        <a-tooltip title="全屏预览">
          <a-button
              v-if="canFullscreen"
              type="primary"
              @click="toggleFullscreen"
          >
            <template #icon>
              <FullscreenOutlined v-if="!isFullscreen"/>
              <FullscreenExitOutlined v-else/>
            </template>
          </a-button>
        </a-tooltip>
        <a-tooltip title="下载文件">
          <a-button type="primary" @click="handleDownload">
            <template #icon>
              <DownloadOutlined/>
            </template>
          </a-button>
        </a-tooltip>
      </a-space>
    </div>

    <div
        ref="previewContainerRef"
        :class="['preview-container', { fullscreen: isFullscreen }]"
        @mousemove="handleMouseMove"
    >
      <!-- 全屏模式下的悬浮控制按钮 -->
      <transition name="fade">
        <div
            v-if="isFullscreen && showFloatingControls"
            class="floating-controls"
        >
          <a-tooltip placement="left" title="退出全屏">
            <a-button shape="circle" type="primary" @click="toggleFullscreen">
              <template #icon>
                <FullscreenExitOutlined/>
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip v-if="isImage" placement="left" title="放大">
            <a-button shape="circle" type="primary" @click="zoomIn">
              <template #icon>
                <PlusOutlined/>
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip v-if="isImage" placement="left" title="缩小">
            <a-button shape="circle" type="primary" @click="zoomOut">
              <template #icon>
                <MinusOutlined/>
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip v-if="isImage" placement="left" title="重置">
            <a-button shape="circle" type="primary" @click="resetZoom">
              <template #icon>
                <ReloadOutlined/>
              </template>
            </a-button>
          </a-tooltip>
          <a-tooltip placement="left" title="下载">
            <a-button shape="circle" type="primary" @click="handleDownload">
              <template #icon>
                <DownloadOutlined/>
              </template>
            </a-button>
          </a-tooltip>
        </div>
      </transition>

      <a-spin :spinning="loading" size="large" tip="加载中...">
        <template v-if="isImage">
          <div class="image-preview-wrapper" @wheel.prevent="handleWheel">
            <img
                :src="previewUrl"
                :style="imageStyle"
                alt="图片预览"
                class="preview-image"
                @load="handleContentLoaded"
                @mousedown="startDrag"
                @mouseleave="stopDrag"
                @mousemove="dragImage"
                @mouseup="stopDrag"
            />
            <div v-if="isImage && !isFullscreen" class="image-controls">
              <a-button-group>
                <a-tooltip title="放大">
                  <a-button @click="zoomIn">
                    <PlusOutlined/>
                  </a-button>
                </a-tooltip>
                <a-tooltip title="缩小">
                  <a-button @click="zoomOut">
                    <MinusOutlined/>
                  </a-button>
                </a-tooltip>
                <a-tooltip title="重置">
                  <a-button @click="resetZoom">
                    <ReloadOutlined/>
                  </a-button>
                </a-tooltip>
              </a-button-group>
            </div>
          </div>
        </template>

        <template v-else-if="isVideo">
          <div class="video-preview-wrapper">
            <video
                ref="videoRef"
                :src="previewUrl"
                class="preview-video"
                controls
                @loadeddata="handleContentLoaded"
            >
              您的浏览器不支持视频播放
            </video>
          </div>
        </template>

        <template v-else-if="isAudio">
          <div class="audio-preview-wrapper">
            <div class="audio-cover">
              <SoundOutlined/>
            </div>
            <audio
                :src="previewUrl"
                class="preview-audio"
                controls
                @loadeddata="handleContentLoaded"
            >
              您的浏览器不支持音频播放
            </audio>
          </div>
        </template>

        <template v-else-if="isPdf">
          <div class="pdf-preview-wrapper">
            <iframe
                :src="previewUrl"
                class="preview-pdf"
                frameborder="0"
                @load="handleContentLoaded"
            ></iframe>
          </div>
        </template>

        <template v-else-if="isOffice">
          <div v-if="useLocalPreview" class="local-preview-container">
            <div class="local-preview">
              <div v-if="isExcel" class="excel-preview">
                <a-table
                    :columns="excelColumns"
                    :data-source="excelData"
                    :scroll="{ x: 'max-content' }"
                    bordered
                    size="small"
                />
              </div>
              <div v-else-if="isWord" class="word-preview">
                <div class="word-content" v-html="wordContent"></div>
              </div>
              <div v-else class="unsupported-preview">
                <file-type-icon
                    :file-extension="file.fileExtension"
                    :file-type="fileType"
                />
                <p>{{ file.originalName }}</p>
                <p class="hint">当前文件类型不支持预览，请下载后查看</p>
                <a-button type="primary" @click="handleDownload">
                  <template #icon>
                    <DownloadOutlined/>
                  </template>
                  下载文件
                </a-button>
              </div>
            </div>
          </div>
          <iframe
              v-else
              :src="officePreviewUrl"
              class="preview-office"
              frameborder="0"
              @load="handleContentLoaded"
          ></iframe>
        </template>

        <template v-else-if="isText">
          <div class="preview-text">
            <pre>{{ textContent }}</pre>
          </div>
        </template>

        <template v-else>
          <div class="unsupported-preview">
            <file-type-icon
                :file-extension="file.fileExtension"
                :file-type="fileType"
            />
            <p>{{ file.originalName }}</p>
            <p class="hint">当前文件类型不支持预览，请下载后查看</p>
            <a-button type="primary" @click="handleDownload">
              <template #icon>
                <DownloadOutlined/>
              </template>
              下载文件
            </a-button>
          </div>
        </template>
      </a-spin>
    </div>

    <!-- 文件信息 -->
    <div v-if="!isFullscreen" class="file-info">
      <a-card :bordered="false" size="small">
        <a-descriptions :column="2" size="small">
          <a-descriptions-item label="文件名"
          >{{ file.originalName }}
          </a-descriptions-item>
          <a-descriptions-item label="文件类型"
          >{{ fileTypeText }}
          </a-descriptions-item>
          <a-descriptions-item label="文件大小"
          >{{ formatFileSize(file.fileSize) }}
          </a-descriptions-item>
          <a-descriptions-item label="上传时间"
          >{{ file.createTime }}
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </div>
  </div>
</template>

<script setup>
import {
  DownloadOutlined,
  FullscreenExitOutlined,
  FullscreenOutlined,
  MinusOutlined,
  PlusOutlined,
  ReloadOutlined,
  SoundOutlined,
} from "@ant-design/icons-vue";
import FileTypeIcon from "./FileTypeIcon.vue";
import {getFileUrl} from "@/api/modules/file";
import {message} from "ant-design-vue";
import axios from "axios";
import * as XLSX from "xlsx";
import * as mammoth from "mammoth";
import {watch} from "vue";

const props = defineProps({
  file: {
    type: Object,
    required: true,
  },
  visible: {
    type: Boolean,
    required: true,
  },
});

// 视频引用
const videoRef = ref(null);

// 文本内容和加载状态
const textContent = ref("");
const loading = ref(false);

// 预览方法选择
const useLocalPreview = ref(true); // 默认使用本地预览选择界面

// Excel预览数据
const excelColumns = ref([]);
const excelData = ref([]);

// Word预览内容
const wordContent = ref("");

// 图片拖动相关
const isDragging = ref(false);
const dragStartX = ref(0);
const dragStartY = ref(0);
const imageOffsetX = ref(0);
const imageOffsetY = ref(0);

// 全屏相关
const isFullscreen = ref(false);
const previewContainerRef = ref(null);
const canFullscreen = computed(() => {
  return (
      isImage.value ||
      isVideo.value ||
      isPdf.value ||
      isOffice.value ||
      isText.value
  );
});

// 控制悬浮按钮显示
const showFloatingControls = ref(true);
let hideControlsTimer = null;

// 显示悬浮控制按钮
const showControls = () => {
  showFloatingControls.value = true;

  // 清除之前的定时器
  if (hideControlsTimer) {
    clearTimeout(hideControlsTimer);
  }

  // 设置新的定时器，3秒后自动隐藏
  hideControlsTimer = setTimeout(() => {
    showFloatingControls.value = false;
  }, 3000);
};

// 鼠标移动时显示控制按钮
const handleMouseMove = () => {
  if (isFullscreen.value) {
    showControls();
  }
};

// 图片缩放相关
const imageScale = ref(1);
const zoomIn = () => {
  const oldScale = imageScale.value;
  imageScale.value = Math.min(imageScale.value + 0.2, 5);

  // 保持缩放中心点
  maintainZoomCenter(oldScale, imageScale.value);
  applyImageScale();
  showControls();
};

const zoomOut = () => {
  const oldScale = imageScale.value;
  imageScale.value = Math.max(imageScale.value - 0.2, 0.2);

  // 保持缩放中心点
  maintainZoomCenter(oldScale, imageScale.value);
  applyImageScale();
  showControls();
};

const resetZoom = () => {
  imageScale.value = 1;
  imageOffsetX.value = 0;
  imageOffsetY.value = 0;
  applyImageScale();
  showControls();
};

// 保持缩放中心点（按钮缩放使用）
const maintainZoomCenter = (oldScale, newScale) => {
  if (oldScale === newScale) return;

  // 获取图片容器
  const container = document.querySelector(".image-preview-wrapper");
  if (!container) return;

  // 获取容器的中心点
  const containerRect = container.getBoundingClientRect();
  const centerX = containerRect.width / 2;
  const centerY = containerRect.height / 2;

  // 使用统一的中心点缩放逻辑
  maintainZoomCenterAtPoint(
      oldScale,
      newScale,
      centerX,
      centerY,
      containerRect.width,
      containerRect.height
  );
};

// 统一的中心点缩放逻辑
const maintainZoomCenterAtPoint = (
    oldScale,
    newScale,
    centerX,
    centerY,
    containerWidth,
    containerHeight
) => {
  if (oldScale === newScale) return;

  // 计算缩放比例变化
  const scaleFactor = newScale / oldScale;

  // 计算中心点相对于图片中心的偏移
  const relativeX = centerX - containerWidth / 2;
  const relativeY = centerY - containerHeight / 2;

  // 调整偏移量，使指定中心点保持不变
  imageOffsetX.value =
      imageOffsetX.value * scaleFactor -
      (relativeX * (scaleFactor - 1)) / oldScale;
  imageOffsetY.value =
      imageOffsetY.value * scaleFactor -
      (relativeY * (scaleFactor - 1)) / oldScale;
};

const applyImageScale = () => {
  // 添加缩放比例提示
  message.info(`缩放比例: ${Math.round(imageScale.value * 100)}%`, 0.5);

  // 如果缩放比例等于1，重置偏移量
  if (imageScale.value === 1) {
    imageOffsetX.value = 0;
    imageOffsetY.value = 0;
  }

  // 更新鼠标样式
  const img = document.querySelector(".preview-image");
  if (img) {
    img.style.cursor = imageScale.value > 1 ? "move" : "default";
  }
};

// 内容加载完成处理
const handleContentLoaded = () => {
  loading.value = false;
};

// 获取文件类型
const fileType = computed(() => {
  const suffix = props.file.fileExtension?.toLowerCase();

  // 图片类型后缀
  const imageSuffix = ["jpg", "jpeg", "png", "gif", "bmp", "webp"];
  // PDF类型
  const pdfSuffix = ["pdf"];
  // Office文档类型后缀
  const officeSuffix = ["doc", "docx", "xls", "xlsx", "ppt", "pptx"];
  // 文本类型后缀
  const textSuffix = [
    "txt",
    "md",
    "json",
    "xml",
    "html",
    "css",
    "js",
    "ts",
    "java",
    "py",
    "c",
    "cpp",
    "cs",
    "go",
    "php",
    "rb",
    "swift",
  ];
  // 视频类型后缀
  const videoSuffix = [
    "mp4",
    "avi",
    "mov",
    "flv",
    "wmv",
    "mpeg",
    "mpg",
    "m4v",
    "webm",
    "mkv",
  ];
  // 音频类型后缀
  const audioSuffix = ["mp3", "wav", "ogg", "flac", "aac", "m4a"];

  if (imageSuffix.includes(suffix)) {
    return "image";
  }
  if (pdfSuffix.includes(suffix)) {
    return "pdf";
  }
  if (officeSuffix.includes(suffix)) {
    return "office";
  }
  if (textSuffix.includes(suffix)) {
    return "text";
  }
  if (videoSuffix.includes(suffix)) {
    return "video";
  }
  if (audioSuffix.includes(suffix)) {
    return "audio";
  }
  return "other";
});

const isExcel = computed(() => {
  const suffix = props.file.fileExtension?.toLowerCase();
  return ["xls", "xlsx", "csv"].includes(suffix);
});

const isWord = computed(() => {
  const suffix = props.file.fileExtension?.toLowerCase();
  return ["doc", "docx"].includes(suffix);
});

const previewUrl = computed(() => {
  return props.file.fileUrl || "";
});

// 判断文件类型
const isImage = computed(() => fileType.value === "image");
const isVideo = computed(() => fileType.value === "video");
const isAudio = computed(() => fileType.value === "audio");
const isPdf = computed(() => fileType.value === "pdf");
const isOffice = computed(() => fileType.value === "office");
const isText = computed(() => fileType.value === "text");

// 文件类型文本
const fileTypeText = computed(() => {
  const typeMap = {
    image: "图片",
    document: "文档",
    pdf: "PDF文档",
    office: "Office文档",
    text: "文本文件",
    video: "视频",
    audio: "音频",
    other: "其他",
  };
  return typeMap[fileType.value] || "未知类型";
});

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return "0 B";

  if (size < 1024) {
    return size + " B";
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + " KB";
  } else if (size < 1024 * 1024 * 1024) {
    return (size / 1024 / 1024).toFixed(2) + " MB";
  } else {
    return (size / 1024 / 1024 / 1024).toFixed(2) + " GB";
  }
};

// 下载文件
const handleDownload = async () => {
  try {
    message.success(`正在下载: ${props.file.originalName}`);
    const url = await getFileUrl(props.file.fileId);
    const link = document.createElement("a");
    link.href = url;
    link.download = props.file.originalName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    message.error(error.message || "获取文件url失败");
  }
};

// 全屏预览
const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value;

  if (isFullscreen.value) {
    // 进入全屏模式
    const elem = previewContainerRef.value;
    if (elem.requestFullscreen) {
      elem.requestFullscreen();
    } else if (elem.mozRequestFullScreen) {
      // Firefox
      elem.mozRequestFullScreen();
    } else if (elem.webkitRequestFullscreen) {
      // Chrome, Safari, Opera
      elem.webkitRequestFullscreen();
    } else if (elem.msRequestFullscreen) {
      // IE/Edge
      elem.msRequestFullscreen();
    }

    // 确保全屏内容可滚动
    setTimeout(() => {
      if (elem) {
        elem.style.overflowY = "auto";
        const pdfFrame = elem.querySelector(".preview-pdf");
        if (pdfFrame) {
          pdfFrame.style.height = "100vh";
        }

        const officeFrame = elem.querySelector(".preview-office");
        if (officeFrame) {
          officeFrame.style.height = "100vh";
        }
      }
    }, 300);

    // 显示控制按钮，并设置自动隐藏
    showControls();
  } else {
    // 退出全屏模式
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.mozCancelFullScreen) {
      // Firefox
      document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) {
      // Chrome, Safari, Opera
      document.webkitExitFullscreen();
    } else if (document.msExitFullscreen) {
      // IE/Edge
      document.msExitFullscreen();
    }

    // 恢复正常滚动
    setTimeout(() => {
      const elem = previewContainerRef.value;
      if (elem) {
        elem.style.overflowY = "";

        // 恢复PDF和Office文档的高度
        const pdfFrame = elem.querySelector(".preview-pdf");
        if (pdfFrame) {
          pdfFrame.style.height = "600px";
        }

        const officeFrame = elem.querySelector(".preview-office");
        if (officeFrame) {
          officeFrame.style.height = "600px";
        }
      }
    }, 300);

    // 清除隐藏定时器
    if (hideControlsTimer) {
      clearTimeout(hideControlsTimer);
      hideControlsTimer = null;
    }
  }
};

// 监听全屏变化事件
const handleFullscreenChange = () => {
  isFullscreen.value = !!(
      document.fullscreenElement ||
      document.mozFullScreenElement ||
      document.webkitFullscreenElement ||
      document.msFullscreenElement
  );
};

// 添加键盘事件处理
const handleKeyDown = (event) => {
  if (isFullscreen.value) {
    // Esc键退出全屏
    if (event.key === "Escape") {
      toggleFullscreen();
    }

    // 如果是图片预览，支持方向键控制缩放
    if (isImage.value) {
      if (event.key === "ArrowUp" || event.key === "+") {
        zoomIn();
      } else if (event.key === "ArrowDown" || event.key === "-") {
        zoomOut();
      } else if (event.key === "r" || event.key === "0") {
        resetZoom();
      }
    }
  }
};

// 鼠标滚轮缩放处理
const handleWheel = (event) => {
  if (isImage.value) {
    const delta = event.deltaY || event.detail; // 获取滚轮方向

    // 防止滚动过快
    event.preventDefault();

    // 记录当前缩放比例
    const oldScale = imageScale.value;

    // 根据滚轮方向缩放，但使用更小的步长
    if (delta > 0) {
      // 向下滚动，缩小
      imageScale.value = Math.max(imageScale.value - 0.1, 0.2);
    } else {
      // 向上滚动，放大
      imageScale.value = Math.min(imageScale.value + 0.1, 5);
    }

    // 如果缩放比例发生变化，按照鼠标位置进行缩放
    if (oldScale !== imageScale.value) {
      // 获取鼠标相对于图片容器的位置
      const rect = event.currentTarget.getBoundingClientRect();
      const mouseX = event.clientX - rect.left;
      const mouseY = event.clientY - rect.top;

      // 使用统一的中心点缩放逻辑
      maintainZoomCenterAtPoint(
          oldScale,
          imageScale.value,
          mouseX,
          mouseY,
          rect.width,
          rect.height
      );
    }

    applyImageScale();
    showControls(); // 重置隐藏计时器
  }
};

// 加载Excel文件内容
const loadExcelContent = async () => {
  if (!isExcel.value || !props.file.fileUrl) return;

  loading.value = true;
  try {
    const response = await axios.get(props.file.fileUrl, {
      responseType: "arraybuffer",
    });
    const data = new Uint8Array(response.data);
    const workbook = XLSX.read(data, {type: "array"});

    // 获取第一个工作表
    const firstSheetName = workbook.SheetNames[0];
    const worksheet = workbook.Sheets[firstSheetName];

    // 转换为JSON
    const jsonData = XLSX.utils.sheet_to_json(worksheet, {header: 1});

    // 设置表格列
    if (jsonData.length > 0) {
      const headers = jsonData[0];
      excelColumns.value = headers.map((header, index) => ({
        title: header || `列 ${index + 1}`,
        dataIndex: `col${index}`,
        key: `col${index}`,
        width: 120,
      }));

      // 设置表格数据
      excelData.value = jsonData.slice(1).map((row, rowIndex) => {
        const rowData = {key: rowIndex};
        headers.forEach((_, colIndex) => {
          rowData[`col${colIndex}`] = row[colIndex] || "";
        });
        return rowData;
      });
    }

    message.success("Excel文件加载成功");
  } catch (error) {
    message.error("加载Excel内容失败");
    excelData.value = [];
    excelColumns.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载Word文件内容
const loadWordContent = async () => {
  if (!isWord.value || !props.file.fileUrl) return;

  loading.value = true;
  try {
    const response = await axios.get(props.file.fileUrl, {
      responseType: "arraybuffer",
    });
    const arrayBuffer = response.data;

    const result = await mammoth.convertToHtml({arrayBuffer});
    wordContent.value = result.value;

    message.success("Word文件加载成功");
  } catch (error) {
    message.error("加载Word内容失败");
    wordContent.value = "<p>无法加载Word文件内容，请下载后查看</p>";
  } finally {
    loading.value = false;
  }
};

// 加载文本文件内容
const loadTextContent = async () => {
  if (!isText.value || !props.file.fileUrl) return;

  loading.value = true;
  try {
    const response = await axios.get(props.file.fileUrl);
    textContent.value = response.data;
  } catch (error) {
    message.error("加载文本内容失败");
    textContent.value = "无法加载文件内容，请下载后查看";
  } finally {
    loading.value = false;
  }
};

// 监听可见性变化
watch(() => props.visible, (newVisible) => {
  if (!newVisible) {
    if (isVideo.value) {
      videoRef.value.pause();
      videoRef.value.currentTime = 0;
    }
  }
});

// 监听文件变化和预览方法变化
watch(
    () => props.file,
    (newFile) => {
      if (newFile) {
        loading.value = true; // 开始加载
        if (isText.value) {
          loadTextContent();
        } else if (isOffice.value) {
          if (isExcel.value) {
            loadExcelContent();
          } else if (isWord.value) {
            loadWordContent();
          } else {
            loading.value = false;
          }
        } else if (
            isImage.value ||
            isVideo.value ||
            isAudio.value ||
            isPdf.value
        ) {
          setTimeout(() => {
            if (loading.value) loading.value = false;
          }, 5000);
        } else {
          loading.value = false;
        }
        if (isImage.value) {
          resetZoom();
        }
      } else {
        console.log(isVideo.value);

        if (isVideo.value) {
          console.log(isVideo);

          videoRef.value.pause();
          videoRef.value.currentTime = 0;
        }
      }
    },
    {immediate: true}
);

// 添加全屏事件监听
onMounted(() => {
  document.addEventListener("fullscreenchange", handleFullscreenChange);
  document.addEventListener("webkitfullscreenchange", handleFullscreenChange);
  document.addEventListener("mozfullscreenchange", handleFullscreenChange);
  document.addEventListener("MSFullscreenChange", handleFullscreenChange);

  // 添加键盘事件监听
  window.addEventListener("keydown", handleKeyDown);

  // 初始化显示控制按钮
  showControls();
});

// 移除全屏事件监听
onBeforeUnmount(() => {
  document.removeEventListener("fullscreenchange", handleFullscreenChange);
  document.removeEventListener(
      "webkitfullscreenchange",
      handleFullscreenChange
  );
  document.removeEventListener("mozfullscreenchange", handleFullscreenChange);
  document.removeEventListener("MSFullscreenChange", handleFullscreenChange);

  // 移除键盘事件监听
  window.removeEventListener("keydown", handleKeyDown);

  // 清除定时器
  if (hideControlsTimer) {
    clearTimeout(hideControlsTimer);
    hideControlsTimer = null;
  }

  // 如果退出组件时处于全屏状态，则退出全屏
  if (isFullscreen.value) {
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen();
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen();
    }
  }
});

// 图片样式计算属性
const imageStyle = computed(() => {
  return {
    transform: `scale(${imageScale.value}) translate(${imageOffsetX.value}px, ${imageOffsetY.value}px)`,
    cursor: imageScale.value > 1 ? "move" : "default",
  };
});

// 开始拖动
const startDrag = (event) => {
  if (imageScale.value > 1) {
    isDragging.value = true;
    dragStartX.value = event.clientX;
    dragStartY.value = event.clientY;
    event.target.style.cursor = "grabbing";
  }
};

// 拖动图片
const dragImage = (event) => {
  if (isDragging.value && imageScale.value > 1) {
    const deltaX = event.clientX - dragStartX.value;
    const deltaY = event.clientY - dragStartY.value;

    // 更新偏移量
    imageOffsetX.value += deltaX / imageScale.value;
    imageOffsetY.value += deltaY / imageScale.value;

    // 更新拖动起点
    dragStartX.value = event.clientX;
    dragStartY.value = event.clientY;
  }
};

// 停止拖动
const stopDrag = (event) => {
  if (isDragging.value) {
    isDragging.value = false;
    if (event && event.target) {
      event.target.style.cursor = imageScale.value > 1 ? "move" : "default";
    }
  }
};
</script>

<style lang="scss" scoped>
.file-preview {
  .preview-header {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background-color: #f5f5f5;
    border-radius: 4px;

    .file-title {
      display: flex;
      align-items: center;

      .file-name {
        margin-left: 8px;
        font-weight: 500;
        font-size: 16px;
        max-width: 500px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .preview-container {
    position: relative;
    transition: all 0.3s;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    padding: 20px;
    background-color: #fafafa;
    min-height: 400px;

    &.fullscreen {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 1000;
      background: #fff;
      padding: 20px;
      display: flex;
      flex-direction: column;
      border: none;
      overflow-y: auto; /* 添加垂直滚动 */

      .image-preview-wrapper {
        height: auto;
        min-height: 85vh;
        max-height: none; /* 移除最大高度限制 */
        overflow: auto; /* 保持滚动 */

        .preview-image {
          max-height: 85vh;
          /* 放大时可能超出容器，但容器有滚动条 */
        }
      }

      .video-preview-wrapper,
      .audio-preview-wrapper {
        height: auto;
        min-height: 85vh;
      }

      .pdf-preview-wrapper {
        height: 90vh;

        .preview-pdf {
          height: 100%;
        }
      }

      .preview-video,
      .preview-audio {
        height: 85vh;
      }

      .microsoft-preview {
        height: 90vh !important;

        .preview-office {
          height: 100% !important;
        }
      }

      .preview-text {
        height: 85vh;
        overflow: auto;
      }

      .local-preview {
        height: auto;
        min-height: 85vh;
        overflow: auto;
      }
    }

    .image-preview-wrapper {
      position: relative;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      overflow: auto; /* 添加溢出滚动 */
      max-height: 500px; /* 限制最大高度 */

      .preview-image {
        max-width: 100%;
        max-height: 500px;
        margin: 0 auto;
        display: block;
        transition: transform 0.3s ease;
        transform-origin: center center; /* 确保从中心缩放 */
      }

      .image-controls {
        margin-top: 16px;
        position: sticky; /* 使控制按钮保持在视图中 */
        bottom: 10px;
        z-index: 5;
      }
    }

    .video-preview-wrapper {
      display: flex;
      justify-content: center;

      .preview-video {
        width: 100%;
        max-height: 500px;
      }
    }

    .audio-preview-wrapper {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 40px 0;

      .audio-cover {
        width: 120px;
        height: 120px;
        background-color: #1677ff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 24px;

        .anticon {
          font-size: 60px;
          color: #fff;
        }
      }

      .preview-audio {
        width: 80%;
      }
    }

    .pdf-preview-wrapper {
      height: 100%;

      .preview-pdf {
        width: 100%;
        height: 600px;
        border: none;
      }
    }
  }

  .preview-text {
    width: 100%;
    height: 500px;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    padding: 16px;
    overflow: auto;
    background-color: #fff;

    pre {
      margin: 0;
      white-space: pre-wrap;
      word-wrap: break-word;
      font-family: "Courier New", Courier, monospace;
    }
  }

  .local-preview-container {
    width: 100%;

    .microsoft-preview {
      width: 100%;
      height: 600px;

      .preview-office {
        width: 100%;
        height: 100%;
        border: none;
      }
    }

    .local-preview {
      width: 100%;
      min-height: 400px;
      border: 1px solid #e8e8e8;
      border-radius: 4px;
      padding: 16px;
      background-color: #fff;

      .excel-preview {
        width: 100%;
        overflow: auto;
      }

      .word-preview {
        width: 100%;

        .word-content {
          padding: 20px;
          background-color: #fff;
        }
      }
    }
  }

  .unsupported-preview {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0;
    background-color: #f5f5f5;
    border-radius: 4px;

    .hint {
      color: #999;
      margin: 16px 0;
    }
  }

  .floating-controls {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 1001;
    display: flex;
    flex-direction: column;
    gap: 10px;

    .ant-btn {
      width: 40px;
      height: 40px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      opacity: 0.8;
      transition: all 0.3s;

      &:hover {
        opacity: 1;
        transform: scale(1.1);
      }
    }
  }

  .file-info {
    margin-top: 20px;
  }
}

// 淡入淡出动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 
