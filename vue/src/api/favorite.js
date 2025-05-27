import request from "@/utils/request";

/**
 * 添加收藏
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Object>} 操作结果
 */
export function addFavorite(resourceId) {
  return request({
    url: "/favorites",
    method: "post",
    data: { resourceId },
  })
    .then((response) => {
      if (response && response.code === 200) {
        return response.data || true;
      } else {
        console.warn("添加收藏响应格式不符合预期:", response);
        return false;
      }
    })
    .catch((error) => {
      console.error("添加收藏失败:", error);
      throw error;
    });
}

/**
 * 取消收藏
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Boolean>} 操作结果
 */
export function cancelFavorite(resourceId) {
  return request({
    url: `/favorites/${resourceId}`,
    method: "delete",
  })
    .then((response) => {
      if (response && response.code === 200) {
        return true;
      } else {
        console.warn("取消收藏响应格式不符合预期:", response);
        return false;
      }
    })
    .catch((error) => {
      console.error("取消收藏失败:", error);
      throw error;
    });
}

/**
 * 检查资源是否被当前用户收藏
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Boolean>} 是否已收藏
 */
export function checkFavorite(resourceId) {
  return request({
    url: `/favorites/check`,
    method: "get",
    params: { resourceId },
  })
    .then((response) => {
      if (typeof response === "boolean") {
        return response;
      } else if (response && typeof response.data === "boolean") {
        return response.data;
      } else if (
        response &&
        response.code === 200 &&
        typeof response.data === "boolean"
      ) {
        return response.data;
      } else {
        console.warn("检查收藏状态响应格式不符合预期:", response);
        return false;
      }
    })
    .catch((error) => {
      console.error("检查收藏状态失败:", error);
      return false;
    });
}

/**
 * 获取当前用户的收藏列表
 * @param {Object} params 查询参数
 * @returns {Promise<Array>} 收藏列表
 */
export function getCurrentUserFavorites(params) {
  return request({
    url: "/favorites/current",
    method: "get",
    params,
  });
}

/**
 * 获取资源的收藏数量
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Number>} 收藏数量
 */
export function countResourceFavorites(resourceId) {
  return request({
    url: `/favorites/count/resource/${resourceId}`,
    method: "get",
  })
    .then((response) => {
      if (typeof response === "number") {
        return response;
      } else if (response && typeof response.data === "number") {
        return response.data;
      } else if (
        response &&
        response.code === 200 &&
        typeof response.data === "number"
      ) {
        return response.data;
      } else {
        console.warn("获取资源收藏数量响应格式不符合预期:", response);
        return 0;
      }
    })
    .catch((error) => {
      console.error("获取资源收藏数量失败:", error);
      return 0;
    });
}

/**
 * 批量取消收藏
 * @param {Array} favoriteIds 收藏ID数组
 * @returns {Promise<Boolean>} 操作结果
 */
export function batchCancelFavorites(favoriteIds) {
  return request({
    url: "/favorites/batch",
    method: "delete",
    data: { favoriteIds },
  });
}

/**
 * 获取资源的收藏用户列表
 * @param {Number} resourceId 资源ID
 * @param {Object} params 查询参数
 * @returns {Promise<Array>} 用户列表
 */
export function getResourceFavoriteUsers(resourceId, params) {
  return request({
    url: `/favorites/users/${resourceId}`,
    method: "get",
    params,
  });
}

// 获取用户收藏列表
export function getUserFavorites(userId) {
  return request({
    url: `/favorites/user/${userId}`,
    method: "get",
  })
    .then((response) => {
      // 处理不同的响应格式
      if (Array.isArray(response)) {
        return response;
      } else if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else {
        console.warn("获取用户收藏响应格式不符合预期:", response);
        return [];
      }
    })
    .catch((error) => {
      console.error("获取用户收藏失败:", error);
      return [];
    });
}

// 获取资源收藏列表
export function getResourceFavorites(resourceId) {
  return request({
    url: `/favorites/resource/${resourceId}`,
    method: "get",
  })
    .then((response) => {
      // 处理不同的响应格式
      if (Array.isArray(response)) {
        return response;
      } else if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else {
        console.warn("获取资源收藏响应格式不符合预期:", response);
        return [];
      }
    })
    .catch((error) => {
      console.error("获取资源收藏失败:", error);
      return [];
    });
}

// 统计用户收藏数量
export function countUserFavorites(userId) {
  return request({
    url: `/favorites/count/user/${userId}`,
    method: "get",
  })
    .then((response) => {
      // 处理不同的响应格式
      if (typeof response === "number") {
        return response;
      } else if (response && typeof response.data === "number") {
        return response.data;
      } else {
        console.warn("统计用户收藏数量响应格式不符合预期:", response);
        return 0;
      }
    })
    .catch((error) => {
      console.error("统计用户收藏数量失败:", error);
      return 0;
    });
}

export default {
  addFavorite,
  cancelFavorite,
  checkFavorite,
  getCurrentUserFavorites,
  countResourceFavorites,
  batchCancelFavorites,
  getResourceFavoriteUsers,
  getUserFavorites,
  getResourceFavorites,
  countUserFavorites,
};
