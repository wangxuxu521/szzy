import request from "@/utils/request";

/**
 * 获取所有类型
 * @returns {Promise} 类型列表
 */
export function getAllTypes() {
  return request({
    url: "/types",
    method: "get",
  });
}

/**
 * 获取类型详情
 * @param {Number} id 类型ID
 * @returns {Promise} 类型详情
 */
export function getTypeById(id) {
  return request({
    url: `/types/${id}`,
    method: "get",
  });
}

/**
 * 获取类型树结构
 * @returns {Promise} 类型树结构
 */
export function getTypeTree() {
  return request({
    url: "/types/tree",
    method: "get",
  });
}

/**
 * 获取子类型
 * @param {Number} parentId 父类型ID
 * @returns {Promise} 子类型列表
 */
export function getChildrenByParentId(parentId) {
  return request({
    url: `/types/children/${parentId}`,
    method: "get",
  });
}

/**
 * 搜索类型
 * @param {String} keyword 搜索关键词
 * @returns {Promise} 搜索结果
 */
export function searchTypes(keyword) {
  return request({
    url: "/types/search",
    method: "get",
    params: { keyword },
  });
}

/**
 * 添加类型
 * @param {Object} data 类型数据
 * @returns {Promise} 添加结果
 */
export function addType(data) {
  return request({
    url: "/types",
    method: "post",
    data,
  });
}

/**
 * 更新类型
 * @param {Object} data 类型数据（包含typeId）
 * @returns {Promise} 更新结果
 */
export function updateType(data) {
  return request({
    url: "/types",
    method: "post",
    data,
  });
}

/**
 * 更新类型状态
 * @param {Number} id 类型ID
 * @param {Number} status 状态值
 * @returns {Promise} 更新结果
 */
export function updateTypeStatus(id, status) {
  return request({
    url: `/types/${id}/status/${status}`,
    method: "put",
  });
}

/**
 * 删除类型
 * @param {Number} id 类型ID
 * @returns {Promise} 删除结果
 */
export function deleteType(id) {
  return request({
    url: `/types/${id}`,
    method: "delete",
  });
}

/**
 * 批量删除类型
 * @param {Array} ids 类型ID数组
 * @returns {Promise} 删除结果
 */
export function batchDeleteTypes(ids) {
  return request({
    url: "/types/batch",
    method: "delete",
    data: ids,
  });
}

export default {
  getAllTypes,
  getTypeById,
  getTypeTree,
  getChildrenByParentId,
  searchTypes,
  addType,
  updateType,
  updateTypeStatus,
  deleteType,
  batchDeleteTypes,
};
