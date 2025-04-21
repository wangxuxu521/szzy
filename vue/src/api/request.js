import axios from "axios";
import { ElMessage } from "element-plus";

// 创建axios实例
const service = axios.create({
  baseURL: "/api", // API基础URL
  timeout: 15000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 获取token并设置请求头
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("请求错误:", error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    return res;
  },
  (error) => {
    console.error("响应错误:", error);
    let message = "请求失败";
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = "未授权，请重新登录";
          // 清除登录信息
          localStorage.removeItem("token");
          localStorage.removeItem("user");
          // 跳转到登录页
          window.location.href = "/login";
          break;
        case 403:
          message = "拒绝访问";
          break;
        case 404:
          message = "请求的资源不存在";
          break;
        case 500:
          message = "服务器错误";
          break;
        default:
          message =
            error.response.data.message || `请求失败(${error.response.status})`;
      }
    } else if (error.request) {
      message = "没有收到响应，请检查网络";
    }

    ElMessage.error(message);
    return Promise.reject(error);
  }
);

// GET请求
export const apiGet = (url, params) => {
  return service.get(url, { params });
};

// POST请求
export const apiPost = (url, data, config) => {
  return service.post(url, data, config);
};

// PUT请求
export const apiPut = (url, data) => {
  return service.put(url, data);
};

// DELETE请求
export const apiDelete = (url, data) => {
  return service.delete(url, { data });
};

export default service;
