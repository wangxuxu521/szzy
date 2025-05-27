import request from "@/utils/request";

/**
 * 获取标签列表
 * @returns {Promise<Array>} 标签列表
 */
export function getTagList() {
  return request({
    url: "/tags",
    method: "get",
  })
    .then((response) => {
      // 处理不同的响应格式
      if (Array.isArray(response)) {
        return response;
      } else if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else if (
        response &&
        response.code === 200 &&
        Array.isArray(response.data)
      ) {
        return response.data;
      } else {
        console.warn("获取标签列表响应格式不符合预期:", response);
        return [];
      }
    })
    .catch((error) => {
      console.error("获取标签列表失败:", error);
      return [];
    });
}

/**
 * 添加标签
 * @param {Object} tagData 标签数据
 * @returns {Promise<Object>} 添加结果
 */
export function addTag(tagData) {
  return request({
    url: "/tags",
    method: "post",
    data: tagData,
  });
}

/**
 * 删除标签
 * @param {Number} tagId 标签ID
 * @returns {Promise<Boolean>} 操作结果
 */
export function deleteTag(tagId) {
  return request({
    url: `/tags/${tagId}`,
    method: "delete",
  });
}

/**
 * 根据资源ID获取标签列表
 * @param {Number} resourceId 资源ID
 * @returns {Promise<Array>} 标签列表
 */
export function getTagsByResourceId(resourceId) {
  return request({
    url: `/tags/resource/${resourceId}`,
    method: "get",
  });
}

/**
 * 保存标签（新增或更新）
 * @param {Object} tagData 标签数据
 * @returns {Promise<Object>} 操作结果
 */
export function saveTag(tagData) {
  const isUpdate = tagData.tagId != null;
  return request({
    url: isUpdate ? `/tags/${tagData.tagId}` : "/tags",
    method: isUpdate ? "put" : "post",
    data: tagData,
  });
}

export default {
  getTagList,
  addTag,
  deleteTag,
  getTagsByResourceId,
  saveTag,
};
