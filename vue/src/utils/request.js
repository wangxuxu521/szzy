import axios from "axios";

// 创建axios实例
const service = axios.create({
  baseURL: "/api", // API基础URL
  timeout: 10000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在发送请求前做些什么
    // 例如：获取token并设置请求头
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    // 对请求错误做些什么
    console.error("请求错误:", error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 对响应数据做些什么
    const res = response.data;
    // 处理标准格式的响应（带有code字段）
    if (res && typeof res === "object" && "code" in res) {
      if (res.code !== 200) {
        // 根据业务逻辑处理不同错误码
        if (res.code === 401) {
          // 未授权，可能是token过期
          localStorage.removeItem("token");
          localStorage.removeItem("isLoggedIn");
          localStorage.removeItem("userRole");
          localStorage.removeItem("username");
          // 跳转到登录页
          window.location.href = "/login";
        }
        return Promise.reject(new Error(res.message || "未知错误"));
      } else {
        return res.data;
      }
    } else {
      // 直接返回数据（后端没有封装标准响应格式时）
      return res;
    }
  },
  (error) => {
    // 对响应错误做些什么
    console.error("响应错误:", error);
    return Promise.reject(error);
  }
);

export default service;
