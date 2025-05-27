import request from "./request";

/**
 * 获取系统摘要统计数据
 */
export function getSystemSummary() {
  return request({
    url: "/statistics/summary",
    method: "get",
  });
}

/**
 * 获取资源类型统计
 */
export function getResourceTypeCount() {
  return request({
    url: "/statistics/resource-type-count",
    method: "get",
  });
}

/**
 * 获取资源统计数据
 */
export function getResourceCount() {
  return request({
    url: "/statistics/resource-count",
    method: "get",
  });
}

/**
 * 获取资源趋势数据
 * @param {number} days 天数
 */
export function getResourceTrend(days = 30) {
  return request({
    url: "/statistics/resource-trend",
    method: "get",
    params: { days },
  });
}

/**
 * 获取用户行为统计
 */
export function getUserActions() {
  return request({
    url: "/statistics/user-actions",
    method: "get",
  });
}

/**
 * 获取下载统计
 */
export function getDownloadStatistics() {
  return request({
    url: "/statistics/download-statistics",
    method: "get",
  });
}

/**
 * 获取用户统计数据
 */
export function getUserStatistics() {
  return request({
    url: "/statistics/user-statistics",
    method: "get",
  });
}

/**
 * 获取最近活动日志
 * @param {number} limit 限制数量
 */
export function getRecentActivities(limit = 10) {
  return request({
    url: "/statistics/recent-activities",
    method: "get",
    params: { limit },
  });
}
