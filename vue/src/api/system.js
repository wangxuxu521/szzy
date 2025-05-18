import { apiGet, apiPost, apiPut, apiDelete } from "./request";

// 系统配置API
export const getSystemConfigs = () => {
  return apiGet("/system/configs");
};

export const getSystemConfigMap = () => {
  return apiGet("/system-config/map");
};

export const getSystemConfigByKey = (key) => {
  return apiGet(`/system/configs/${key}`);
};

export const saveSystemConfig = (config) => {
  if (config.id) {
    return apiPut(`/system/configs/${config.id}`, config);
  } else {
    return apiPost("/system/configs", config);
  }
};

export const updateSystemConfig = (config) => {
  return apiPut("/system-config", config);
};

export const updateSystemConfigValue = (configKey, configValue) => {
  return apiPut(`/system-config/${configKey}`, { configValue });
};

export const deleteSystemConfig = (id) => {
  return apiDelete(`/system/configs/${id}`);
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
