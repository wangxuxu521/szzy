<template>
  <div class="file-viewer">
    <div class="viewer-header">
      <h3>{{ fileName }}</h3>
      <div class="actions">
        <button @click="fullScreen" class="action-btn" title="全屏查看">
          <i class="icon-fullscreen"></i>
        </button>
        <button @click="zoomIn" class="action-btn" title="放大">
          <i class="icon-zoom-in"></i>
        </button>
        <button @click="zoomOut" class="action-btn" title="缩小">
          <i class="icon-zoom-out"></i>
        </button>
        <button @click="download" class="action-btn" title="下载">
          <i class="icon-download"></i>
        </button>
        <button @click="$emit('close')" class="close-btn" title="关闭">
          ×
        </button>
      </div>
    </div>

    <div class="viewer-content" ref="viewerContainer">
      <!-- PDF查看器 -->
      <div v-if="fileType === 'pdf'" class="pdf-viewer">
        <canvas
          v-if="pdfDocument"
          ref="pdfCanvas"
          class="pdf-canvas"
          :style="{ transform: `scale(${zoom})` }"
        ></canvas>
        <div v-else class="loading">
          <div class="loading-spinner"></div>
          <p>加载PDF中...</p>
        </div>
      </div>

      <!-- 图片查看器 -->
      <div v-else-if="isImage" class="image-viewer">
        <img
          v-if="fileUrl"
          :src="fileUrl"
          :style="{ transform: `scale(${zoom})` }"
          @load="imageLoaded = true"
          :class="{ loaded: imageLoaded }"
        />
        <div v-else class="loading">加载中...</div>
      </div>

      <!-- 视频查看器 -->
      <div v-else-if="isVideo" class="video-viewer">
        <video
          v-if="fileUrl"
          :src="fileUrl"
          controls
          autoplay
          class="video-player"
          @loadeddata="videoLoaded = true"
          :class="{ loaded: videoLoaded }"
        ></video>
        <div v-else class="loading">
          <div class="loading-spinner"></div>
          <p>加载视频中...</p>
        </div>
      </div>

      <!-- 文本查看器 -->
      <div v-else-if="isText" class="text-viewer">
        <pre v-if="textContent" :style="{ fontSize: `${14 * zoom}px` }">{{
          textContent
        }}</pre>
        <div v-else class="loading">加载中...</div>
      </div>

      <!-- Office文档查看器 -->
      <div v-else-if="isOffice" class="office-viewer">
        <div
          v-if="officeContent"
          ref="officeContainer"
          class="office-container"
        ></div>
        <div v-else class="loading">
          <div class="loading-spinner"></div>
          <p>加载文档中...</p>
        </div>
      </div>

      <!-- 其他文件类型 -->
      <div v-else class="unsupported-file">
        <div class="message">
          <i class="icon-file-o"></i>
          <p>该文件类型暂不支持在线预览</p>
          <button @click="download" class="download-btn">下载文件</button>
        </div>
      </div>

      <!-- 加载失败显示 -->
      <div v-if="loadError" class="load-error">
        <i class="icon-error"></i>
        <p>{{ loadErrorMessage }}</p>
        <button @click="retry" class="retry-btn">重试</button>
        <button @click="download" class="download-btn">下载查看</button>
      </div>
    </div>

    <div class="viewer-footer">
      <div class="pagination" v-if="totalPages > 1">
        <button
          :disabled="currentPage === 1"
          @click="prevPage"
          class="page-btn"
        >
          上一页
        </button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button
          :disabled="currentPage === totalPages"
          @click="nextPage"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onUnmounted } from "vue";
import * as pdfjsLib from "pdfjs-dist";
import "pdfjs-dist/web/pdf_viewer.css";
import { renderAsync } from "docx-preview";

// 设置PDF.js worker
pdfjsLib.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.js`;

export default {
  name: "FileViewer",
  props: {
    resourceId: {
      type: [Number, String],
      required: true,
    },
    fileName: {
      type: String,
      default: "未命名文件",
    },
    fileUrl: {
      type: String,
      default: "",
    },
    fileType: {
      type: String,
      default: "",
    },
  },
  emits: ["close"],
  setup(props, { emit }) {
    const zoom = ref(1);
    const currentPage = ref(1);
    const totalPages = ref(1);
    const imageLoaded = ref(false);
    const videoLoaded = ref(false);
    const textContent = ref("");
    const viewerContainer = ref(null);
    const loadError = ref(false);
    const loadErrorMessage = ref("加载文件失败");
    const pdfDocument = ref(null);
    const pdfCanvas = ref(null);
    const pdfPage = ref(null);
    const officeContent = ref(null);
    const officeContainer = ref(null);

    // 文件类型处理
    const fileExtension = computed(() => {
      if (!props.fileName) return "";
      const parts = props.fileName.split(".");
      return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : "";
    });

    const isImage = computed(() => {
      const imgExtensions = ["jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"];
      return imgExtensions.includes(fileExtension.value);
    });

    const isVideo = computed(() => {
      const videoExtensions = [
        "mp4",
        "webm",
        "ogg",
        "avi",
        "mov",
        "wmv",
        "flv",
        "mkv",
      ];
      return (
        videoExtensions.includes(fileExtension.value) ||
        props.fileType === "video"
      );
    });

    const isText = computed(() => {
      const textExtensions = [
        "txt",
        "log",
        "json",
        "xml",
        "html",
        "css",
        "js",
        "md",
      ];
      return textExtensions.includes(fileExtension.value);
    });

    const isOffice = computed(() => {
      const officeExtensions = ["doc", "docx", "xls", "xlsx", "ppt", "pptx"];
      return officeExtensions.includes(fileExtension.value);
    });

    // 微软Office在线预览或Google Docs预览URL
    const officeViewerUrl = computed(() => {
      if (!props.fileUrl || !isOffice.value) return "";

      // 使用Google Docs Viewer作为主要预览方式
      const encodedUrl = encodeURIComponent(props.fileUrl);
      return `https://docs.google.com/viewer?url=${encodedUrl}&embedded=true&hl=zh-CN`;

      // 备选方案：Microsoft Office Online Viewer
      // return `https://view.officeapps.live.com/op/view.aspx?src=${encodedUrl}`;
    });

    // 加载文本内容
    const loadTextContent = async () => {
      if (!props.fileUrl || !isText.value) return;

      try {
        const response = await fetch(props.fileUrl);
        if (response.ok) {
          textContent.value = await response.text();
        }
      } catch (error) {
        console.error("加载文本内容失败:", error);
      }
    };

    // 缩放功能
    const zoomIn = () => {
      zoom.value = Math.min(zoom.value + 0.1, 2.5);
    };

    const zoomOut = () => {
      zoom.value = Math.max(zoom.value - 0.1, 0.5);
    };

    // 全屏功能
    const fullScreen = () => {
      const container = viewerContainer.value;
      if (!container) return;

      if (document.fullscreenElement) {
        document.exitFullscreen();
      } else {
        container.requestFullscreen().catch((err) => {
          console.error("无法进入全屏模式:", err);
        });
      }
    };

    // 翻页功能
    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
      }
    };

    // 下载文件
    const download = () => {
      if (props.fileUrl) {
        const a = document.createElement("a");
        a.href = props.fileUrl;
        a.download = props.fileName || "download";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
      } else {
        // 通过API下载
        window.open(`/api/resources/download/${props.resourceId}`, "_blank");
      }
    };

    // 处理iframe加载事件
    const onFrameLoad = (event) => {
      // 移除跨域检查，因为Google Docs Viewer不允许跨域访问
      loadError.value = false;
    };

    // 处理iframe加载错误
    const onFrameError = () => {
      loadError.value = true;
      loadErrorMessage.value = "加载文件失败，请稍后重试或下载后查看";
    };

    // 重试加载
    const retry = () => {
      loadError.value = false;
      // 重新加载iframe
      const iframe = viewerContainer.value?.querySelector("iframe");
      if (iframe) {
        iframe.src = props.fileUrl;
      }
    };

    // 键盘事件处理
    const handleKeyDown = (e) => {
      if (e.key === "Escape") {
        emit("close");
      } else if (e.key === "ArrowRight" || e.key === "PageDown") {
        nextPage();
      } else if (e.key === "ArrowLeft" || e.key === "PageUp") {
        prevPage();
      } else if (e.key === "+" || e.key === "=") {
        zoomIn();
      } else if (e.key === "-") {
        zoomOut();
      } else if (e.key === "f" || e.key === "F") {
        fullScreen();
      }
    };

    // 加载PDF
    const loadPDF = async () => {
      if (!props.fileUrl || props.fileType !== "pdf") return;

      try {
        // 加载PDF文档
        const loadingTask = pdfjsLib.getDocument({
          url: props.fileUrl,
          cMapUrl: "https://cdn.jsdelivr.net/npm/pdfjs-dist@2.16.105/cmaps/",
          cMapPacked: true,
        });
        pdfDocument.value = await loadingTask.promise;

        // 加载第一页
        pdfPage.value = await pdfDocument.value.getPage(1);

        // 渲染PDF页面
        const canvas = pdfCanvas.value;
        const context = canvas.getContext("2d");
        const viewport = pdfPage.value.getViewport({ scale: 1.5 });

        canvas.height = viewport.height;
        canvas.width = viewport.width;

        const renderContext = {
          canvasContext: context,
          viewport: viewport,
        };

        await pdfPage.value.render(renderContext).promise;
        loadError.value = false;
      } catch (error) {
        console.error("加载PDF失败:", error);
        loadError.value = true;
        loadErrorMessage.value = "加载PDF失败，请下载后查看";
      }
    };

    // 加载Office文档
    const loadOfficeDocument = async () => {
      if (!props.fileUrl || !isOffice.value) return;

      try {
        const response = await fetch(props.fileUrl);
        const arrayBuffer = await response.arrayBuffer();

        if (officeContainer.value) {
          await renderAsync(arrayBuffer, officeContainer.value);
          loadError.value = false;
        }
      } catch (error) {
        console.error("加载Office文档失败:", error);
        loadError.value = true;
        loadErrorMessage.value = "加载文档失败，请下载后查看";
      }
    };

    // 监听文件URL变化
    watch(
      () => props.fileUrl,
      () => {
        if (props.fileType === "pdf") {
          loadPDF();
        }
        if (isOffice.value) {
          loadOfficeDocument();
        }
      }
    );

    // 监听缩放变化
    watch(zoom, () => {
      if (pdfPage.value && pdfCanvas.value) {
        const canvas = pdfCanvas.value;
        const context = canvas.getContext("2d");
        const viewport = pdfPage.value.getViewport({ scale: 1.5 * zoom.value });

        canvas.height = viewport.height;
        canvas.width = viewport.width;

        const renderContext = {
          canvasContext: context,
          viewport: viewport,
        };

        pdfPage.value.render(renderContext).promise;
      }
    });

    onMounted(() => {
      document.addEventListener("keydown", handleKeyDown);

      if (isText.value) {
        loadTextContent();
      }

      if (props.fileType === "pdf") {
        loadPDF();
      }

      if (isOffice.value) {
        loadOfficeDocument();
      }
    });

    onUnmounted(() => {
      document.removeEventListener("keydown", handleKeyDown);
    });

    return {
      zoom,
      currentPage,
      totalPages,
      imageLoaded,
      videoLoaded,
      textContent,
      viewerContainer,
      isImage,
      isVideo,
      isText,
      isOffice,
      officeViewerUrl,
      zoomIn,
      zoomOut,
      fullScreen,
      nextPage,
      prevPage,
      download,
      loadError,
      loadErrorMessage,
      onFrameLoad,
      onFrameError,
      retry,
      pdfDocument,
      pdfCanvas,
      pdfPage,
      officeContent,
      officeContainer,
    };
  },
};
</script>

<style scoped>
.file-viewer {
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90vw;
  height: 90vh;
  max-width: 1200px;
  overflow: hidden;
}

.viewer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #eee;
}

.viewer-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

.actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
}

.action-btn:hover {
  background-color: #f5f5f5;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  line-height: 1;
  color: #999;
  cursor: pointer;
  padding: 2px 8px;
}

.close-btn:hover {
  color: #666;
}

.viewer-content {
  flex: 1;
  overflow: auto;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.pdf-viewer,
.image-viewer,
.video-viewer,
.text-viewer,
.office-viewer,
.unsupported-file {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pdf-frame,
.office-frame {
  width: 100%;
  height: 100%;
  border: none;
}

.image-viewer img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  transition: transform 0.2s ease;
  opacity: 0;
}

.image-viewer img.loaded,
.video-viewer video.loaded {
  opacity: 1;
}

.text-viewer pre {
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: white;
  padding: 16px;
  border-radius: 4px;
  white-space: pre-wrap;
  font-family: "Courier New", Courier, monospace;
  margin: 0;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #f0f0f0;
  border-radius: 50%;
  border-top: 3px solid #1890ff;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.unsupported-file .message {
  text-align: center;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.unsupported-file i {
  font-size: 48px;
  color: #999;
  margin-bottom: 16px;
}

.unsupported-file p {
  margin-bottom: 16px;
  color: #666;
}

.download-btn {
  padding: 8px 16px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.viewer-footer {
  padding: 12px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: center;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-btn {
  padding: 4px 12px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

/* 图标字体 */
.icon-fullscreen:before {
  content: "⛶";
}
.icon-zoom-in:before {
  content: "+";
}
.icon-zoom-out:before {
  content: "-";
}
.icon-download:before {
  content: "↓";
}
.icon-file-o:before {
  content: "📄";
}

.load-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.load-error i {
  font-size: 48px;
  color: #ff4d4f;
  margin-bottom: 16px;
}

.load-error p {
  margin-bottom: 16px;
  color: #666;
}

.retry-btn {
  padding: 8px 16px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}

.icon-error:before {
  content: "❌";
}

.video-player {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.pdf-object {
  width: 100%;
  height: 100%;
  border: none;
  background-color: white;
}

.pdf-object p {
  padding: 20px;
  text-align: center;
  color: #666;
}

.pdf-object a {
  color: #1890ff;
  text-decoration: none;
}

.pdf-object a:hover {
  text-decoration: underline;
}

.pdf-canvas {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.office-container {
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: white;
  padding: 20px;
}
</style>
