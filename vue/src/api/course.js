import request from "@/utils/request";

/**
 * 获取课程列表
 * @param {Object} params 查询参数
 * @returns {Promise} 课程列表
 */
export function getCourseList(params) {
  return request({
    url: "/courses",
    method: "get",
    params,
  });
}

/**
 * 获取课程详情
 * @param {Number} id 课程ID
 * @returns {Promise} 课程详情
 */
export function getCourseById(id) {
  return request({
    url: `/courses/${id}`,
    method: "get",
  });
}

/**
 * 添加课程
 * @param {Object} data 课程数据
 * @returns {Promise} 添加结果
 */
export function addCourse(data) {
  return request({
    url: "/courses",
    method: "post",
    data,
  });
}

/**
 * 更新课程
 * @param {Number} id 课程ID
 * @param {Object} data 课程数据
 * @returns {Promise} 更新结果
 */
export function updateCourse(id, data) {
  return request({
    url: `/courses/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除课程
 * @param {Number} id 课程ID
 * @returns {Promise} 删除结果
 */
export function deleteCourse(id) {
  return request({
    url: `/courses/${id}`,
    method: "delete",
  });
}

/**
 * 获取课程统计数据
 * @returns {Promise} 统计数据
 */
export function getCourseStats() {
  return request({
    url: "/courses/stats",
    method: "get",
  });
}

export default {
  getCourseList,
  getCourseById,
  addCourse,
  updateCourse,
  deleteCourse,
  getCourseStats,
};
