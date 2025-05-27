import request from "@/utils/request";

/**
 * 获取资源列表
 * @param {Object} params 查询参数
 * @returns {Promise} 资源列表
 */
export function getResourceList(params) {
  return request({
    url: "/resources",
    method: "get",
    params,
  });
}

/**
 * 获取资源详情
 * @param {Number} id 资源ID
 * @returns {Promise} 资源详情
 */
export function getResourceDetail(id) {
  return request({
    url: `/resources/${id}`,
    method: "get",
  });
}

/**
 * 上传资源
 * @param {FormData} data 表单数据
 * @param {Function} onUploadProgress 上传进度回调
 * @param {CancelToken} cancelToken 取消令牌
 * @returns {Promise} 上传结果
 */
export function uploadResource(data, onUploadProgress, cancelToken) {
  // 如果typeId是字符串类型但需要是数字，进行转换
  if (data instanceof FormData && data.has("typeId")) {
    const typeIdValue = data.get("typeId");
    if (typeIdValue && !isNaN(typeIdValue)) {
      // 移除旧值并添加数字类型的typeId
      data.delete("typeId");
      data.append("typeId", Number(typeIdValue));
    }
  }

  return request({
    url: "/resources/upload",
    method: "post",
    data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
    onUploadProgress,
    cancelToken,
  })
    .then((response) => {
      // 确保返回数据格式一致
      if (response && response.code === 200) {
        return response;
      } else if (
        response &&
        typeof response === "object" &&
        !("code" in response)
      ) {
        return { code: 200, data: response };
      } else if (response === true) {
        return { code: 200, message: "上传成功" };
      }
      return response;
    })
    .catch((error) => {
      console.error("上传资源失败:", error);
      return { code: 500, message: error.message || "上传失败，请稍后重试" };
    });
}

/**
 * 更新资源
 * @param {Number} id 资源ID
 * @param {FormData} data 表单数据
 * @returns {Promise} 更新结果
 */
export function updateResource(id, data) {
  return request({
    url: `/resources/${id}`,
    method: "put",
    data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 删除资源
 * @param {Number} id 资源ID
 * @returns {Promise} 删除结果
 */
export function deleteResource(id) {
  return request({
    url: `/resources/${id}`,
    method: "delete",
  });
}

/**
 * 下载资源
 * @param {Number} id 资源ID
 * @returns {Promise<Blob>} 文件Blob对象
 */
export function downloadResource(id) {
  return request({
    url: `/resources/download/${id}`,
    method: "get",
    responseType: "blob",
  });
}

/**
 * 增加资源下载次数
 * @param {Number} id 资源ID
 * @returns {Promise} 增加结果
 */
export function increaseDownloadCount(id) {
  return request({
    url: `/resources/${id}/download-count`,
    method: "put",
  });
}

/**
 * 增加资源浏览次数
 * @param {Number} id 资源ID
 * @returns {Promise} 增加结果
 */
export function increaseViewCount(id) {
  return request({
    url: `/resources/${id}/view-count`,
    method: "put",
  });
}

/**
 * 搜索资源
 * @param {Object} params 搜索参数
 * @returns {Promise} 搜索结果
 */
export function searchResources(params) {
  // 如果typeId是字符串类型但实际是数字，进行转换
  if (
    params &&
    params.typeId &&
    typeof params.typeId === "string" &&
    !isNaN(params.typeId)
  ) {
    params = { ...params, typeId: Number(params.typeId) };
  }

  return request({
    url: "/resources/search",
    method: "get",
    params,
  });
}

/**
 * 获取资源类型列表
 * @returns {Promise<Array>} 资源类型列表
 */
export function getResourceTypes() {
  return import("./type")
    .then((module) => {
      return module.getAllTypes();
    })
    .then((response) => {
      // 处理不同格式的响应，确保返回包含typeId和typeName的对象数组
      if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else if (Array.isArray(response)) {
        return response;
      } else if (response && typeof response === "object") {
        // 可能是封装在data字段中
        return response.data || [];
      }
      // 返回空数组，避免错误
      return [];
    });
}

/**
 * 获取资源预览URL
 * @param {Number} id 资源ID
 * @returns {String} 预览URL
 */
export function getResourcePreviewUrl(id) {
  return `/resources/preview/${id}`;
}

/**
 * 检查资源是否支持预览
 * @param {Number} id 资源ID
 * @param {String} fileName 文件名
 * @returns {Promise<Object>} 检查结果
 */
export function checkPreviewSupport(id, fileName) {
  return request({
    url: `/resources/preview-support`,
    method: "get",
    params: { id, fileName },
  });
}

/**
 * 获取热门资源
 * @param {Number} limit 限制数量
 * @returns {Promise} 热门资源列表
 */
export function getHotResources(limit = 5) {
  return request({
    url: "/resources/hot",
    method: "get",
    params: { limit },
  });
}

/**
 * 更新资源审核状态
 * @param {Number} id 资源ID
 * @param {String} status 审核状态 (approved, rejected, pending)
 * @returns {Promise} 更新结果
 */
export function updateResourceReviewStatus(id, status) {
  return request({
    url: `/resources/${id}/review-status`,
    method: "put",
    data: { reviewStatus: status },
    headers: {
      "Content-Type": "application/json",
    },
  });
}

/**
 * 对资源进行评分
 * @param {Number} resourceId 资源ID
 * @param {Number} rating 评分值 (0-5)
 * @returns {Promise}
 */
export function rateResource(resourceId, rating) {
  return request({
    url: `/resources/${resourceId}/rate`,
    method: "post",
    data: { rating },
  })
    .then((response) => {
      if (response && response.code === 200) {
        return response;
      } else if (response && !response.code) {
        return { code: 200, data: response };
      }
      return response;
    })
    .catch((error) => {
      console.error("评分失败:", error);
      return { code: 500, message: error.message || "评分失败，请稍后重试" };
    });
}

/**
 * 获取用户对资源的评分
 * @param {Number} resourceId 资源ID
 * @returns {Promise} 评分值
 */
export function getUserResourceRating(resourceId) {
  return request({
    url: `/resources/${resourceId}/user-rating`,
    method: "get",
  })
    .then((response) => {
      if (response && response.code === 200) {
        return response.data;
      } else if (typeof response === "number") {
        return response;
      }
      return 0;
    })
    .catch((error) => {
      console.error("获取用户评分失败:", error);
      return 0;
    });
}

/**
 * 获取资源的所有评分
 * @param {Number} resourceId 资源ID
 * @returns {Promise} 评分列表
 */
export function getResourceRatings(resourceId) {
  return request({
    url: `/resources/${resourceId}/ratings`,
    method: "get",
  })
    .then((response) => {
      if (response && response.code === 200 && Array.isArray(response.data)) {
        return response.data;
      } else if (Array.isArray(response)) {
        return response;
      }
      return [];
    })
    .catch((error) => {
      console.error("获取资源评分列表失败:", error);
      return [];
    });
}

export default {
  getResourceList,
  getResourceDetail,
  uploadResource,
  updateResource,
  deleteResource,
  downloadResource,
  increaseDownloadCount,
  increaseViewCount,
  searchResources,
  getResourceTypes,
  getResourcePreviewUrl,
  checkPreviewSupport,
  getHotResources,
  updateResourceReviewStatus,
  rateResource,
  getUserResourceRating,
  getResourceRatings,
};
