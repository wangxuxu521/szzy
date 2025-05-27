import request from "@/utils/request";

/**
 * 获取教师个人信息
 */
export function getTeacherInfo() {
  return request({
    url: "/teacher/info",
    method: "get",
  });
}

/**
 * 更新教师个人信息
 * @param {Object} data 教师信息
 */
export function updateTeacherInfo(data) {
  return request({
    url: "/teacher/info",
    method: "put",
    data,
  });
}

/**
 * 获取教师上传的资源列表
 * @param {Object} params 查询参数
 */
export function getTeacherResources(params) {
  return request({
    url: "/teacher/resources",
    method: "get",
    params,
  }).then((response) => {
    // 确保返回数据格式一致
    if (response && response.code === 200) {
      return response;
    } else if (Array.isArray(response)) {
      return { code: 200, data: response };
    } else if (typeof response === "object" && !("code" in response)) {
      return { code: 200, data: response };
    }
    return response;
  });
}

/**
 * 上传教师资源
 * @param {FormData} formData 资源表单数据
 */
export function uploadTeacherResource(formData) {
  return request({
    url: "/resources/upload",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 更新教师资源
 * @param {Number} resourceId 资源ID
 * @param {FormData} formData 资源表单数据
 */
export function updateTeacherResource(resourceId, formData) {
  return request({
    url: `/resources/${resourceId}`,
    method: "put",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 删除教师资源
 * @param {Number} resourceId 资源ID
 */
export function deleteTeacherResource(resourceId) {
  return request({
    url: `/resources/${resourceId}`,
    method: "delete",
  });
}

/**
 * 获取教师的课程列表
 */
export function getTeacherCourses() {
  return request({
    url: "/teacher/courses",
    method: "get",
  });
}

/**
 * 获取教师活动记录
 * @param {Number} limit 限制数量
 */
export function getTeacherActivities(limit = 10) {
  return request({
    url: "/teacher/activities",
    method: "get",
    params: { limit },
  })
    .then((response) => {
      // 确保返回数据格式一致
      if (response && response.code === 200) {
        return response;
      } else if (Array.isArray(response)) {
        return { code: 200, data: response };
      } else if (typeof response === "object" && !("code" in response)) {
        return { code: 200, data: response };
      }
      return response;
    })
    .catch((error) => {
      console.error("获取教师活动记录失败:", error);
      // 返回空数组作为默认值
      return { code: 200, data: [] };
    });
}

/**
 * 获取教师资源统计数据
 */
export function getTeacherStatistics() {
  return request({
    url: "/teacher/statistics",
    method: "get",
  })
    .then((response) => {
      // 确保返回数据格式一致
      if (response && response.code === 200) {
        return response;
      } else if (typeof response === "object" && !("code" in response)) {
        return { code: 200, data: response };
      }
      return response;
    })
    .catch((error) => {
      console.error("获取教师统计数据失败:", error);
      // 返回默认统计数据
      return {
        code: 200,
        data: {
          resourceCount: 0,
          courseCount: 0,
          resourceTypeCount: {},
        },
      };
    });
}

/**
 * 更新课程信息
 * @param {Number} courseId 课程ID
 * @param {Object} data 课程数据
 */
export function updateCourse(courseId, data) {
  return request({
    url: `/courses/${courseId}`,
    method: "put",
    data,
  });
}

/**
 * 创建新课程
 * @param {Object} data 课程数据
 */
export function createCourse(data) {
  return request({
    url: "/courses",
    method: "post",
    data,
  });
}

/**
 * 删除课程
 * @param {Number} courseId 课程ID
 */
export function deleteCourse(courseId) {
  return request({
    url: `/courses/${courseId}`,
    method: "delete",
  });
}
