import { apiGet, apiPost, apiPut, apiDelete } from "./request";

// 系统配置API
export const getSystemConfigs = () => {
  return apiGet("/system-config");
};

export const getSystemConfigMap = () => {
  return apiGet("/system-config/map");
};

export const getSystemConfigByKey = (configKey) => {
  return apiGet(`/system-config/key/${configKey}`);
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

export const deleteSystemConfigByKey = (configKey) => {
  return apiDelete(`/system-config/key/${configKey}`);
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
