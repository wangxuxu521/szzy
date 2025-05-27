import axios from "axios";
import { ElMessage } from "element-plus";
import store from "@/store";

// 创建axios实例
const service = axios.create({
  baseURL: "/api", // API基础URL
  timeout: 15000, // 请求超时时间
});

// 记录是否正在刷新令牌
let isRefreshing = false;
// 等待刷新令牌的请求队列
let refreshSubscribers = [];

// 将请求添加到队列
const subscribeTokenRefresh = (cb) => {
  refreshSubscribers.push(cb);
};

// 执行队列中的请求
const onRefreshed = (token) => {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
};

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 获取token并设置请求头
    // 先尝试从accessToken获取，这是我们新定义的名称
    let token = localStorage.getItem("accessToken");
    // 如果不存在，再尝试从token获取，这是原来的名称
    if (!token) {
      token = localStorage.getItem("token");
    }

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

    // 处理不同的响应格式
    if (res && typeof res === "object") {
      // 处理util.Result格式 {code: 200, message: "xxx", data: ...}
      if ("code" in res) {
        // 根据后端约定，code为200或0表示成功
        if (res.code === 200 || res.code === 0) {
          return res;
        } else {
          // 业务错误处理
          ElMessage.error(res.message || "请求失败");
          return Promise.reject(new Error(res.message || "请求失败"));
        }
      }

      // 对于没有code字段的对象响应，直接返回
      return res;
    }

    // 对于非对象类型的响应（如字符串、布尔值等），直接返回
    return res;
  },
  async (error) => {
    console.error("响应错误:", error);

    // 处理令牌过期的情况
    if (error.response && error.response.status === 401) {
      const originalRequest = error.config;

      // 避免重复重试已经失败的请求
      if (originalRequest._retry) {
        // 如果已经重试过，直接返回错误
        return Promise.reject(error);
      }

      // 判断是否是刷新令牌请求，避免无限循环
      if (originalRequest.url.includes("/auth/refresh-token")) {
        // 清除登录信息
        await store.dispatch("user/logout");
        // 跳转到登录页
        window.location.href = "/login";
        return Promise.reject(error);
      }

      // 检查是否有刷新令牌
      const refreshToken = store.getters["user/refreshToken"];
      if (!refreshToken) {
        // 如果没有刷新令牌，直接跳转到登录页
        await store.dispatch("user/logout");
        window.location.href = "/login";
        return Promise.reject(error);
      }

      // 如果不是正在刷新令牌，进行刷新
      if (!isRefreshing) {
        isRefreshing = true;
        originalRequest._retry = true;

        try {
          // 尝试使用刷新令牌获取新的访问令牌
          const token = await store.dispatch("user/refreshToken");

          // 刷新令牌成功，更新状态
          isRefreshing = false;
          onRefreshed(token);

          // 重新发送原始请求
          originalRequest.headers["Authorization"] = `Bearer ${token}`;
          return axios(originalRequest);
        } catch (refreshError) {
          // 刷新令牌失败，清除登录信息并跳转到登录页
          isRefreshing = false;
          refreshSubscribers = [];
          await store.dispatch("user/logout");
          window.location.href = "/login";
          return Promise.reject(refreshError);
        }
      } else {
        // 已经在刷新令牌，将请求加入队列
        return new Promise((resolve) => {
          subscribeTokenRefresh((token) => {
            originalRequest.headers["Authorization"] = `Bearer ${token}`;
            resolve(axios(originalRequest));
          });
        });
      }
    }

    let message = "请求失败";
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = "未授权，请重新登录";
          break;
        case 403:
          message = "拒绝访问";
          break;
        case 404:
          message = "请求的资源不存在";
          break;
        case 500:
          message = "服务器错误";
          // 尝试从响应中获取更详细的错误信息
          if (error.response.data) {
            if (typeof error.response.data === "string") {
              message += `: ${error.response.data}`;
            } else if (error.response.data.message) {
              message += `: ${error.response.data.message}`;
            }
          }
          break;
        default:
          message =
            error.response.data && error.response.data.message
              ? error.response.data.message
              : `请求失败(${error.response.status})`;
      }
    } else if (error.request) {
      message = "没有收到响应，请检查网络";
    } else if (error.message) {
      message = error.message;
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
