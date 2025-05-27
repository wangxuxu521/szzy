import request from "@/utils/request";

/**
 * 获取资源评论列表
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Array>} 评论列表
 */
export function getResourceComments(resourceId) {
  return request({
    url: `/comments/resource/${resourceId}`,
    method: "get",
  })
    .then((response) => {
      // 处理不同的响应格式
      if (Array.isArray(response)) {
        return response;
      } else if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else {
        console.warn("获取评论列表响应格式不符合预期:", response);
        return [];
      }
    })
    .catch((error) => {
      console.error("获取评论列表失败:", error);
      return [];
    });
}

/**
 * 添加评论
 * @param {Object} commentData 评论数据对象
 * @returns {Promise<Object>} 新创建的评论
 */
export function addComment(commentData) {
  return request({
    url: "/comments",
    method: "post",
    data: commentData,
  });
}

/**
 * 删除评论
 * @param {Number} commentId 评论ID
 * @returns {Promise<Boolean>} 操作结果
 */
export function deleteComment(commentId) {
  return request({
    url: `/comments/${commentId}`,
    method: "delete",
  });
}

/**
 * 获取用户评论列表
 * @param {Number} userId 用户ID
 * @returns {Promise<Array>} 评论列表
 */
export function getUserComments(userId) {
  return request({
    url: `/comments/user/${userId}`,
    method: "get",
  })
    .then((response) => {
      // 处理不同的响应格式
      if (Array.isArray(response)) {
        return response;
      } else if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else {
        console.warn("获取用户评论响应格式不符合预期:", response);
        return [];
      }
    })
    .catch((error) => {
      console.error("获取用户评论失败:", error);
      return [];
    });
}

/**
 * 回复评论
 * @param {Object} replyData 回复数据对象
 * @returns {Promise<Object>} 新创建的回复
 */
export function replyComment(replyData) {
  return request({
    url: "/comments",
    method: "post",
    data: replyData,
  });
}

/**
 * 更新评论状态
 * @param {Number} commentId 评论ID
 * @param {Number} status 状态值
 * @returns {Promise<Boolean>} 操作结果
 */
export function updateCommentStatus(commentId, status) {
  return request({
    url: `/comments/${commentId}/status`,
    method: "put",
    data: { status },
  });
}

/**
 * 获取评论数量
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Number>} 评论数量
 */
export function getCommentCount(resourceId) {
  return request({
    url: `/comments/count/${resourceId}`,
    method: "get",
  });
}

/**
 * 管理员获取所有评论
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 分页评论列表
 */
export function getAllComments(params) {
  return request({
    url: "/comments/admin",
    method: "get",
    params,
  });
}

export default {
  getResourceComments,
  addComment,
  deleteComment,
  getUserComments,
  replyComment,
  updateCommentStatus,
  getCommentCount,
  getAllComments,
};
