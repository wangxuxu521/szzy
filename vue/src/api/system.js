import { apiGet, apiPost, apiPut, apiDelete } from "./request";

// 系统配置API
export const getSystemConfigs = () => {
  return apiGet("/system-config");
};

export const getSystemConfigMap = () => {
  return apiGet("/system-config/map");
};

export const getSystemConfigByKey = (key) => {
  return apiGet(`/system-config/key/${key}`);
};

export const saveSystemConfig = (config) => {
  return apiPost("/system-config", config);
};

export const updateSystemConfig = (config) => {
  return apiPut("/system-config", config);
};

export const updateSystemConfigValue = (configKey, configValue) => {
  return apiPut(`/system-config/${configKey}`, { configValue });
};

export const deleteSystemConfig = (id) => {
  return apiDelete(`/system-config/${id}`);
};

export const deleteSystemConfigByKey = (configKey) => {
  return apiDelete(`/system-config/key/${configKey}`);
};

export const getSystemConfigsByCategory = (category) => {
  return apiGet(`/system/configs/category/${category}`);
};

export const batchSaveSystemConfigs = (configs) => {
  return apiPost("/system/configs/batch", configs);
};

// 统计分析API
export const getResourceCount = () => {
  return apiGet("/statistics/resource-count");
};

export const getResourceTrend = (days) => {
  return apiGet("/statistics/resource-trend", { days });
};

export const getUserActions = () => {
  return apiGet("/statistics/user-actions");
};

export const getDownloadStatistics = () => {
  return apiGet("/statistics/download-statistics");
};

// 首页API
export const getAnnouncements = (limit = 3) => {
  return apiGet("/announcements", { limit });
};

export const getSystemSummary = () => {
  return apiGet("/statistics/summary");
};

export const getResourceTypeCount = () => {
  return apiGet("/statistics/resource-type-count");
};

export const getResourceTypeTrend = () => {
  return apiGet("/statistics/resource-type-trend");
};

// 统计相关API
export const getResourceStatistics = () => {
  return apiGet("/statistics/resources");
};

export const getUserStatistics = () => {
  return apiGet("/statistics/users");
};

export const getActivityStatistics = (days = 7) => {
  return apiGet(`/statistics/activity?days=${days}`);
};
