<template>
  <div class="resource-detail">
    <div class="container" v-loading="loading">
      <div class="back-link">
        <router-link to="/resources">
          <i class="el-icon-arrow-left"></i> 返回资源列表
        </router-link>
      </div>

      <!-- 资源信息部分 -->
      <div class="resource-info" v-if="resource">
        <div class="resource-header">
          <h1 class="resource-title">{{ resource.title }}</h1>
          <div class="resource-meta">
            <span class="meta-item">
              <i class="el-icon-user"></i>
              {{ resource.uploaderName || "未知用户" }}
            </span>
            <span class="meta-item">
              <i class="el-icon-date"></i> {{ formatDate(resource.uploadTime) }}
            </span>
            <span class="meta-item">
              <i class="el-icon-view"></i> {{ resource.viewCount || 0 }}
            </span>
            <span class="meta-item">
              <i class="el-icon-download"></i> {{ resource.downloadCount || 0 }}
            </span>
            <span class="meta-item">
              <el-tag size="small" :type="getTypeTagType(resource.type)">{{
                resource.type
              }}</el-tag>
            </span>
          </div>
        </div>

        <div class="resource-actions">
          <el-button type="primary" @click="handleDownload">
            <i class="el-icon-download"></i> 下载资源
          </el-button>
          <el-button type="success" @click="handlePreview">
            <i class="el-icon-view"></i> 在线预览
          </el-button>
          <favorite-button
            :resource-id="resourceId"
            :is-favorite="isFavorite"
            @favorite-changed="onFavoriteChanged"
          />
          <el-button type="info" @click="showShareDialog">
            <i class="el-icon-share"></i> 分享
          </el-button>
        </div>

        <div class="resource-rating card">
          <div class="card-header">
            <h3>资源评分</h3>
          </div>
          <div class="card-body">
            <ResourceRating
              :resourceId="resourceId"
              :initialRating="resource ? resource.rating || 0 : 0"
              :initialRatingCount="resource ? resource.ratingCount || 0 : 0"
              @rating-changed="onRatingChanged"
            />
          </div>
        </div>

        <div class="resource-description card">
          <div class="card-header">
            <h3>资源描述</h3>
          </div>
          <div class="card-body">
            <p v-if="resource.description">{{ resource.description }}</p>
            <p v-else class="empty-text">暂无描述</p>
          </div>
        </div>

        <div
          class="resource-tags"
          v-if="resource.tags && resource.tags.length > 0"
        >
          <span class="tag-label">标签:</span>
          <el-tag
            v-for="(tag, index) in parseTags(resource.tags)"
            :key="index"
            class="resource-tag"
            :type="getTagType(tag)"
            size="small"
          >
            {{ tag }}
          </el-tag>
        </div>

        <div class="resource-file-info card">
          <div class="card-header">
            <h3>文件信息</h3>
          </div>
          <div class="card-body">
            <p><strong>文件名:</strong> {{ resource.fileName }}</p>
            <p><strong>文件格式:</strong> {{ resource.format }}</p>
            <p>
              <strong>文件大小:</strong> {{ formatFileSize(resource.fileSize) }}
            </p>
          </div>
        </div>
      </div>

      <!-- 评论部分 -->
      <div class="resource-comments card">
        <div class="card-header">
          <h3>评论 ({{ comments.length }})</h3>
        </div>
        <div class="card-body">
          <comment-form
            v-if="isLoggedIn"
            :resource-id="resourceId"
            @comment-added="onCommentAdded"
          />
          <div v-else class="login-to-comment">
            <router-link to="/login">登录</router-link> 后才能发表评论
          </div>

          <comment-list
            :comments="comments"
            :loading="commentsLoading"
            :resource-id="resourceId"
            @comment-deleted="onCommentDeleted"
            @reply-added="onReplyAdded"
          />
        </div>
      </div>

      <!-- 相关资源推荐 -->
      <div class="related-resources card">
        <div class="card-header">
          <h3>相关资源</h3>
        </div>
        <div class="card-body">
          <div
            v-if="relatedResources.length > 0"
            class="related-resources-list"
          >
            <div
              v-for="item in relatedResources"
              :key="item.resourceId"
              class="related-resource-item"
              @click="goToResource(item.resourceId)"
            >
              <div class="related-resource-title">{{ item.title }}</div>
              <div class="related-resource-meta">
                <span class="related-meta-item">
                  <i class="el-icon-view"></i> {{ item.viewCount || 0 }}
                </span>
                <span class="related-meta-item">
                  <i class="el-icon-download"></i> {{ item.downloadCount || 0 }}
                </span>
              </div>
            </div>
          </div>
          <p v-else class="empty-text">暂无相关资源</p>
        </div>
      </div>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog
      v-if="resource"
      v-model="showPreviewDialog"
      :title="resource.fileName || '文件预览'"
      width="80%"
      destroy-on-close
    >
      <div class="preview-container" v-loading="previewLoading">
        <div
          v-if="!previewSupported && !previewLoading"
          class="preview-not-supported"
        >
          <el-alert
            title="该文件类型不支持在线预览"
            type="warning"
            :closable="false"
            show-icon
          />
          <p class="mt-3">文件名: {{ resource.fileName }}</p>
          <el-button type="primary" @click="handleDownload" class="mt-3">
            下载文件
          </el-button>
        </div>

        <!-- PDF预览 -->
        <div v-if="previewType === 'pdf'" class="pdf-container">
          <object
            :data="previewUrl"
            type="application/pdf"
            width="100%"
            height="600"
          >
            <iframe
              :src="previewUrl"
              width="100%"
              height="600"
              frameborder="0"
              sandbox="allow-same-origin allow-scripts allow-forms"
            >
              当前浏览器不支持PDF预览，请
              <a :href="previewUrl" target="_blank">点击下载</a>
              后查看
            </iframe>
          </object>
        </div>

        <!-- 图片预览 -->
        <div v-if="previewType === 'image'" class="image-container">
          <img :src="previewUrl" alt="图片预览" />
        </div>

        <!-- 视频预览 -->
        <div v-if="previewType === 'video'" class="video-container">
          <video controls width="100%" autoplay>
            <source
              :src="previewUrl"
              :type="`video/${getFileExtension(resource.fileName)}`"
            />
            您的浏览器不支持视频播放
          </video>
        </div>

        <!-- 音频预览 -->
        <div v-if="previewType === 'audio'" class="audio-container">
          <audio controls autoplay>
            <source
              :src="previewUrl"
              :type="`audio/${getFileExtension(resource.fileName)}`"
            />
            您的浏览器不支持音频播放
          </audio>
        </div>

        <!-- 文本预览 -->
        <div v-if="previewType === 'text'" class="text-container">
          <pre>{{ textContent }}</pre>
        </div>

        <!-- Office预览 -->
        <div v-if="previewType === 'office'" class="office-container">
          <iframe
            :src="officePreviewUrl"
            width="100%"
            height="600"
            frameborder="0"
            sandbox="allow-same-origin allow-scripts allow-forms allow-popups"
          ></iframe>
        </div>
      </div>
    </el-dialog>

    <!-- 分享弹窗 -->
    <el-dialog
      v-if="resource"
      v-model="shareDialogVisible"
      title="分享资源"
      width="400px"
    >
      <div class="share-dialog-content">
        <div class="resource-share-info">
          <h4>{{ resource.title }}</h4>
          <p>分享这个资源给其他人</p>
        </div>

        <div class="share-link-container">
          <el-input v-model="shareLink" readonly>
            <template #append>
              <el-button @click="copyShareLink">复制</el-button>
            </template>
          </el-input>
        </div>

        <div class="share-methods">
          <h4>分享到</h4>
          <div class="share-buttons">
            <el-button class="share-btn wechat" circle>
              <i class="el-icon-chat-dot-square"></i>
            </el-button>
            <el-button class="share-btn weibo" circle>
              <i class="el-icon-s-promotion"></i>
            </el-button>
            <el-button class="share-btn qq" circle>
              <i class="el-icon-message"></i>
            </el-button>
            <el-button class="share-btn link" circle @click="copyShareLink">
              <i class="el-icon-link"></i>
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useStore } from "vuex";
import { ElMessage, ElLoading } from "element-plus";
import axios from "axios";
import {
  getResourceDetail,
  downloadResource,
  searchResources,
  getResourcePreviewUrl,
  checkPreviewSupport,
  rateResource,
  getUserResourceRating,
  getResourceRatings,
} from "@/api/resource";
import { getResourceComments, addComment, deleteComment } from "@/api/comment";
import {
  checkFavorite,
  addFavorite,
  cancelFavorite,
  countResourceFavorites,
} from "@/api/favorite";
import CommentList from "@/components/resource/CommentList.vue";
import CommentForm from "@/components/resource/CommentForm.vue";
import FavoriteButton from "@/components/resource/FavoriteButton.vue";
import ResourceRating from "@/components/resource/ResourceRating.vue";

export default {
  name: "ResourceDetail",
  components: {
    CommentList,
    CommentForm,
    FavoriteButton,
    ResourceRating,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const store = useStore();

    // 用户信息
    const isLoggedIn = computed(() => store.getters["user/isLoggedIn"]);
    const userId = computed(() => store.getters["user/userId"]);

    // 资源ID
    const resourceId = ref(parseInt(route.params.id));

    // 资源数据
    const resource = ref(null);
    const loading = ref(true);

    // 收藏状态
    const isFavorite = ref(false);
    const favoriteCount = ref(0);

    // 评论数据
    const comments = ref([]);
    const commentsLoading = ref(true);

    // 相关资源
    const relatedResources = ref([]);

    // 预览相关
    const showPreviewDialog = ref(false);
    const previewLoading = ref(false);
    const previewUrl = ref("");
    const previewType = ref("");
    const previewSupported = ref(false);
    const textContent = ref("");
    const officePreviewUrl = ref("");

    // 评分相关状态由ResourceRating组件管理

    // 分享相关
    const shareDialogVisible = ref(false);
    const shareLink = ref("");

    // 加载资源详情
    const loadResourceDetail = async () => {
      loading.value = true;
      try {
        const response = await getResourceDetail(resourceId.value);
        if (response && response.code === 200) {
          resource.value = response.data;
        } else if (response && !response.code) {
          // 直接返回数据的情况
          resource.value = response;
        } else {
          ElMessage.error("获取资源详情失败");
        }
      } catch (error) {
        console.error("获取资源详情失败:", error);
        ElMessage.error("获取资源详情失败");
      } finally {
        loading.value = false;
      }
    };

    // 加载评论
    const loadComments = async () => {
      commentsLoading.value = true;
      try {
        const response = await getResourceComments(resourceId.value);

        // 处理嵌套结构：识别出父评论和子评论
        const parentComments = [];
        const childComments = {};

        // 先遍历所有评论，区分父评论和子评论
        response.forEach((comment) => {
          if (!comment.parentId) {
            // 父评论
            comment.replies = [];
            parentComments.push(comment);
          } else {
            // 子评论（回复）
            if (!childComments[comment.parentId]) {
              childComments[comment.parentId] = [];
            }
            childComments[comment.parentId].push(comment);
          }
        });

        // 将子评论添加到对应的父评论中
        parentComments.forEach((parent) => {
          if (childComments[parent.commentId]) {
            parent.replies = childComments[parent.commentId];
          }
        });

        // 按时间倒序排序父评论
        parentComments.sort((a, b) => {
          return new Date(b.createTime) - new Date(a.createTime);
        });

        comments.value = parentComments;
      } catch (error) {
        console.error("获取评论失败:", error);
        ElMessage.error("获取评论失败");
      } finally {
        commentsLoading.value = false;
      }
    };

    // 检查收藏状态
    const checkFavoriteStatus = async () => {
      if (!isLoggedIn.value) return;

      try {
        const response = await checkFavorite(resourceId.value);
        isFavorite.value = response;
      } catch (error) {
        console.error("检查收藏状态失败:", error);
      }
    };

    // 获取收藏数量
    const loadFavoriteCount = async () => {
      try {
        const count = await countResourceFavorites(resourceId.value);
        favoriteCount.value = count;
      } catch (error) {
        console.error("获取收藏数量失败:", error);
      }
    };

    // 加载相关资源
    const loadRelatedResources = async () => {
      if (!resource.value) return;

      try {
        // 根据资源类型查找相关资源
        const params = {};
        if (resource.value.type) {
          params.type = resource.value.type;
        }
        if (resource.value.typeId) {
          params.typeId = resource.value.typeId;
        }

        const response = await searchResources(params);
        if (response && response.length > 0) {
          // 过滤掉当前资源，最多显示5个相关资源
          relatedResources.value = response
            .filter((item) => item.resourceId !== resourceId.value)
            .slice(0, 5);
        }
      } catch (error) {
        console.error("获取相关资源失败:", error);
      }
    };

    // 评论添加成功回调
    const onCommentAdded = (newComment) => {
      // 确保新评论有必要的字段
      if (!newComment.replies) {
        newComment.replies = [];
      }

      comments.value.unshift(newComment);
    };

    // 评论删除回调
    const onCommentDeleted = (commentId) => {
      // 如果是父评论，直接从列表中移除
      comments.value = comments.value.filter((c) => c.commentId !== commentId);

      // 如果是子评论，从父评论的replies中移除
      comments.value.forEach((parent) => {
        if (parent.replies && parent.replies.length) {
          parent.replies = parent.replies.filter(
            (reply) => reply.commentId !== commentId
          );
        }
      });
    };

    // 回复添加回调
    const onReplyAdded = (newReply) => {
      // 找到对应的父评论
      const parentComment = comments.value.find(
        (c) => c.commentId === newReply.parentId
      );
      if (parentComment) {
        if (!parentComment.replies) {
          parentComment.replies = [];
        }
        parentComment.replies.push(newReply);
      }
    };

    // 收藏状态变化回调
    const onFavoriteChanged = (newStatus) => {
      isFavorite.value = newStatus;
      favoriteCount.value = newStatus
        ? favoriteCount.value + 1
        : favoriteCount.value - 1;
    };

    // 下载资源
    const handleDownload = async () => {
      if (!resource.value) {
        ElMessage.warning("资源信息加载失败，无法下载");
        return;
      }

      try {
        const loading = ElLoading.service({
          text: "下载中...",
          background: "rgba(255, 255, 255, 0.7)",
        });

        try {
          const response = await downloadResource(resourceId.value);
          const blob = new Blob([response], { type: response.type });
          const link = document.createElement("a");
          const fileName = resource.value.fileName || "download.file";

          link.href = URL.createObjectURL(blob);
          link.download = fileName;
          link.click();

          ElMessage.success("下载成功");
        } catch (error) {
          console.error("下载失败:", error);
          ElMessage.error("下载失败: " + (error.message || "未知错误"));
        } finally {
          loading.close();
        }
      } catch (error) {
        console.error("下载操作失败:", error);
        ElMessage.error("下载操作失败");
      }
    };

    // 处理预览
    const handlePreview = async () => {
      if (!resource.value) {
        ElMessage.warning("资源信息加载失败，无法预览");
        return;
      }

      showPreviewDialog.value = true;
      previewLoading.value = true;
      previewSupported.value = false;
      textContent.value = "";
      officePreviewUrl.value = "";

      try {
        // 获取文件扩展名
        const fileName = resource.value.fileName || "";
        const fileExt = getFileExtension(fileName).toLowerCase();
        console.log("预览文件:", fileName, "扩展名:", fileExt);

        // 获取认证信息
        const token =
          localStorage.getItem("token") ||
          localStorage.getItem("accessToken") ||
          "";

        // 构建预览URL - 确保URL带有/api前缀
        const baseUrl = `/api/resources/preview/${resourceId.value}`;
        // 添加随机参数避免缓存
        const timestamp = new Date().getTime();
        previewUrl.value = `${baseUrl}?t=${timestamp}`;

        // 检查MinIO预览URL是否可用
        try {
          const minioPreviewResponse = await axios.get(
            `/api/minio/url/${resource.value.filePath}`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );

          if (
            minioPreviewResponse.data &&
            minioPreviewResponse.data.code === 200
          ) {
            // 使用MinIO URL进行预览
            previewUrl.value = minioPreviewResponse.data.data;
            console.log("使用MinIO预览URL:", previewUrl.value);
          } else {
            console.log("使用后端预览URL:", previewUrl.value);
          }
        } catch (error) {
          console.log("获取MinIO URL失败，使用后端预览URL:", previewUrl.value);
        }

        // 根据文件类型设置预览方式
        if (["pdf"].includes(fileExt)) {
          previewType.value = "pdf";
          previewSupported.value = true;
          console.log("PDF预览模式");
        } else if (
          ["jpg", "jpeg", "png", "gif", "bmp", "webp"].includes(fileExt)
        ) {
          previewType.value = "image";
          previewSupported.value = true;
          console.log("图片预览模式");
        } else if (["mp4", "webm", "ogg", "mov"].includes(fileExt)) {
          previewType.value = "video";
          previewSupported.value = true;
          console.log("视频预览模式");
        } else if (["mp3", "wav", "ogg", "aac"].includes(fileExt)) {
          previewType.value = "audio";
          previewSupported.value = true;
          console.log("音频预览模式");
        } else if (
          [
            "txt",
            "md",
            "json",
            "xml",
            "html",
            "css",
            "js",
            "java",
            "py",
            "c",
            "cpp",
          ].includes(fileExt)
        ) {
          previewType.value = "text";
          previewSupported.value = true;
          console.log("文本预览模式");

          // 获取文本内容
          try {
            console.log("正在加载文本内容...");
            const response = await axios.get(`${baseUrl}?t=${timestamp}`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
              responseType: "text",
            });
            console.log("文本内容加载成功");
            textContent.value = response.data;
          } catch (error) {
            console.error("获取文本内容失败:", error);
            if (error.response) {
              console.error(
                "错误响应:",
                error.response.status,
                error.response.data
              );
            }
            textContent.value =
              "无法加载文本内容: " + (error.message || "未知错误");
            previewSupported.value = true; // 仍然显示预览容器，但内容为错误信息
          }
        } else if (
          ["doc", "docx", "xls", "xlsx", "ppt", "pptx"].includes(fileExt)
        ) {
          previewType.value = "office";
          previewSupported.value = true;

          // 尝试获取MinIO的URL用于Office预览
          try {
            const minioResponse = await axios.get(
              `/api/minio/url/${resource.value.filePath}`,
              {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              }
            );

            if (minioResponse.data && minioResponse.data.code === 200) {
              // 使用Microsoft Office Online Viewer直接预览MinIO URL
              const minioUrl = minioResponse.data.data;
              const encodedUrl = encodeURIComponent(minioUrl);
              officePreviewUrl.value = `https://view.officeapps.live.com/op/view.aspx?src=${encodedUrl}`;
              console.log(
                "使用MinIO URL的Office预览模式:",
                officePreviewUrl.value
              );
            } else {
              // 回退到后端的Office预览
              officePreviewUrl.value = `/api/resources/office-preview/${resourceId.value}?t=${timestamp}`;
              console.log("使用后端的Office预览模式:", officePreviewUrl.value);
            }
          } catch (error) {
            // 出错时回退到后端的Office预览
            officePreviewUrl.value = `/api/resources/office-preview/${resourceId.value}?t=${timestamp}`;
            console.log(
              "获取MinIO URL失败，使用后端Office预览:",
              officePreviewUrl.value
            );
          }
        } else {
          previewSupported.value = false;
          console.log("不支持的文件类型:", fileExt);
        }
      } catch (error) {
        console.error("预览文件失败:", error);
        if (error.response) {
          console.error(
            "错误响应:",
            error.response.status,
            error.response.data
          );
        }
        ElMessage.error("预览文件失败: " + (error.message || "未知错误"));
        previewSupported.value = false;
      } finally {
        previewLoading.value = false;
      }
    };

    // 获取文件扩展名
    const getFileExtension = (filename) => {
      if (!filename) return "";
      return filename.split(".").pop() || "";
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "-";
      const date = new Date(dateString);
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      });
    };

    // 格式化文件大小
    const formatFileSize = (bytes) => {
      if (!bytes) return "0 Bytes";
      const k = 1024;
      const sizes = ["Bytes", "KB", "MB", "GB", "TB"];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
    };

    // 获取标签类型
    const getTagType = (tag) => {
      if (!tag) return "";

      // 主题相关标签
      if (
        tag.includes("主义") ||
        tag.includes("精神") ||
        tag.includes("价值观") ||
        tag.includes("伦理")
      ) {
        return "success";
      }
      // 学科相关标签
      else if (
        tag.includes("计算机") ||
        tag.includes("网络") ||
        tag.includes("人工智能") ||
        tag.includes("数据") ||
        tag.includes("结构") ||
        tag.includes("工程")
      ) {
        return "primary";
      }
      // 格式相关标签
      else if (
        tag.includes("PDF") ||
        tag.includes("PPT") ||
        tag.includes("Word") ||
        tag.includes("Excel") ||
        tag.includes("视频")
      ) {
        return "warning";
      }

      return "info";
    };

    // 获取资源类型标签样式
    const getTypeTagType = (type) => {
      const typeMap = {
        计算机: "primary",
        通信: "success",
        人工智能: "warning",
        嵌入式: "danger",
      };
      return typeMap[type] || "info";
    };

    // 解析标签
    const parseTags = (tags) => {
      if (!tags) return [];

      // 如果已经是数组格式
      if (Array.isArray(tags)) {
        return tags
          .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
          .filter((tag) => tag);
      }

      // 如果是JSON格式字符串，解析为数组
      try {
        const parsedTags = JSON.parse(tags);
        if (Array.isArray(parsedTags)) {
          return parsedTags
            .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
            .filter((tag) => tag);
        }
      } catch (e) {
        // 解析JSON失败，尝试其他方式
      }

      // 如果是逗号分隔的字符串，转换为数组
      if (typeof tags === "string") {
        return tags
          .split(",")
          .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
          .filter((tag) => tag);
      }

      // 其他情况返回空数组
      return [];
    };

    // 跳转到相关资源
    const goToResource = (id) => {
      router.push(`/resources/${id}`);
    };

    // 处理评分变化回调
    const onRatingChanged = (ratingData) => {
      try {
        if (resource.value) {
          resource.value.rating = ratingData.rating;
          resource.value.ratingCount = ratingData.ratingCount;
        }
      } catch (error) {
        console.error("更新评分信息失败:", error);
      }
    };

    // 显示分享对话框
    const showShareDialog = () => {
      if (!resource.value) {
        ElMessage.warning("资源信息加载失败，无法分享");
        return;
      }

      shareDialogVisible.value = true;
      // 生成分享链接
      try {
        shareLink.value =
          window.location.origin +
          router.resolve({
            name: "ResourceDetail",
            params: { id: resourceId.value },
          }).href;
      } catch (error) {
        console.error("生成分享链接失败:", error);
        // 使用当前URL作为备选
        shareLink.value = window.location.href;
      }
    };

    // 复制分享链接
    const copyShareLink = () => {
      navigator.clipboard
        .writeText(shareLink.value)
        .then(() => {
          ElMessage.success("链接已复制到剪贴板");
        })
        .catch(() => {
          ElMessage.error("复制失败，请手动复制");
        });
    };

    onMounted(async () => {
      await loadResourceDetail();
      loadComments();
      checkFavoriteStatus();
      loadFavoriteCount();
      loadRelatedResources();
    });

    return {
      resource,
      loading,
      comments,
      commentsLoading,
      isLoggedIn,
      isFavorite,
      favoriteCount,
      relatedResources,
      resourceId,
      showPreviewDialog,
      previewLoading,
      previewUrl,
      previewType,
      previewSupported,
      textContent,
      officePreviewUrl,

      shareDialogVisible,
      shareLink,

      handleDownload,
      handlePreview,
      onCommentAdded,
      onCommentDeleted,
      onReplyAdded,
      onFavoriteChanged,
      formatDate,
      formatFileSize,
      getTagType,
      getTypeTagType,
      parseTags,
      getFileExtension,
      goToResource,
      onRatingChanged,
      showShareDialog,
      copyShareLink,
    };
  },
};
</script>

<style scoped>
.resource-detail {
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-link {
  margin-bottom: 20px;
}

.back-link a {
  display: inline-flex;
  align-items: center;
  color: #409eff;
  text-decoration: none;
  font-size: 16px;
}

.resource-header {
  margin-bottom: 20px;
}

.resource-title {
  font-size: 24px;
  margin-bottom: 10px;
  color: #333;
}

.resource-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item i {
  margin-right: 5px;
}

.resource-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.card-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.card-body {
  padding: 20px;
}

.resource-description p {
  line-height: 1.6;
  color: #333;
  white-space: pre-line;
}

.resource-tags {
  margin: 15px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.tag-label {
  font-size: 14px;
  color: #666;
}

.resource-tag {
  margin-right: 8px;
}

.resource-file-info p {
  margin: 8px 0;
  color: #333;
}

.empty-text {
  color: #999;
  font-style: italic;
}

.login-to-comment {
  text-align: center;
  padding: 15px;
  background: #f8f8f8;
  border-radius: 4px;
  margin-bottom: 20px;
}

.login-to-comment a {
  color: #409eff;
  text-decoration: none;
  font-weight: bold;
}

.related-resources-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
}

.related-resource-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-resource-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.related-resource-title {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-resource-meta {
  display: flex;
  gap: 15px;
  color: #999;
  font-size: 12px;
}

.preview-container {
  min-height: 400px;
}

.preview-not-supported {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.pdf-container,
.image-container,
.video-container,
.text-container,
.office-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-container img {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
}

.text-container {
  width: 100%;
  height: 600px;
  overflow-y: auto;
  background-color: #f5f5f5;
  padding: 16px;
}

.text-container pre {
  white-space: pre-wrap;
  font-family: monospace;
  width: 100%;
  font-size: 14px;
  line-height: 1.6;
}

.mt-3 {
  margin-top: 15px;
}

.resource-rating {
  margin-bottom: 20px;
}

.rating-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.current-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-value {
  font-size: 24px;
  font-weight: bold;
  color: #ff9900;
}

.rating-count {
  color: #999;
  font-size: 14px;
}

.user-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.rating-label {
  color: #666;
}

.login-to-rate {
  text-align: center;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.login-to-rate a {
  color: #409eff;
  text-decoration: none;
  font-weight: bold;
}

.share-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.resource-share-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.resource-share-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.share-link-container {
  margin: 15px 0;
}

.share-methods h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.share-buttons {
  display: flex;
  gap: 15px;
}

.share-btn {
  font-size: 18px;
}

.share-btn.wechat {
  background-color: #07c160;
  border-color: #07c160;
  color: white;
}

.share-btn.weibo {
  background-color: #e6162d;
  border-color: #e6162d;
  color: white;
}

.share-btn.qq {
  background-color: #12b7f5;
  border-color: #12b7f5;
  color: white;
}

.share-btn.link {
  background-color: #909399;
  border-color: #909399;
  color: white;
}

/* 移动端适配优化 */
@media (max-width: 768px) {
  .resource-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .resource-actions .el-button {
    margin-bottom: 10px;
    margin-left: 0 !important;
  }

  .resource-meta {
    flex-direction: column;
    gap: 8px;
  }

  .current-rating {
    flex-direction: column;
    align-items: flex-start;
  }

  .user-rating {
    flex-direction: column;
    align-items: flex-start;
  }

  .related-resources-list {
    grid-template-columns: 1fr;
  }
}
</style>
