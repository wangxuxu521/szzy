import { login, logout, getUserInfo, refreshToken } from "@/api/user";

const state = {
  token: localStorage.getItem("accessToken") || localStorage.getItem("token"),
  refreshToken: localStorage.getItem("refreshToken"),
  userInfo: null,
  isLoggedIn: localStorage.getItem("isLoggedIn") === "true",
  userRole: localStorage.getItem("userRole"),
  username: localStorage.getItem("username"),
};

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token;
    if (token) {
      localStorage.setItem("accessToken", token);
      localStorage.setItem("token", token); // 为了兼容性同时设置
    } else {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("token");
    }
  },
  SET_REFRESH_TOKEN(state, refreshToken) {
    state.refreshToken = refreshToken;
    if (refreshToken) {
      localStorage.setItem("refreshToken", refreshToken);
    } else {
      localStorage.removeItem("refreshToken");
    }
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo;
  },
  SET_LOGIN_STATE(state, isLoggedIn) {
    state.isLoggedIn = isLoggedIn;
    localStorage.setItem("isLoggedIn", isLoggedIn);
  },
  SET_USER_ROLE(state, role) {
    state.userRole = role;
    if (role) {
      localStorage.setItem("userRole", role);
    } else {
      localStorage.removeItem("userRole");
    }
  },
  SET_USERNAME(state, username) {
    state.username = username;
    if (username) {
      localStorage.setItem("username", username);
    } else {
      localStorage.removeItem("username");
    }
  },
};

const actions = {
  // 用户登录
  login({ commit }, userInfo) {
    const { username, password } = userInfo;
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password })
        .then((response) => {
          // 处理不同可能的响应格式
          let data;

          // 处理直接返回对象的情况
          if (response && typeof response === "object") {
            if (response.code === 200 && response.data) {
              // 标准Response格式：{code: 200, data: {...}}
              data = response.data;
            } else if (response.code === 200) {
              // 简化Response格式：{code: 200, ...其他数据}
              data = response;
            } else if (
              !("code" in response) &&
              (response.token || response.accessToken)
            ) {
              // 直接返回数据对象：{token: "...", ...}
              data = response;
            } else if (response.message && !response.code) {
              // 仅包含消息的情况，可能是错误
              return reject(new Error(response.message));
            } else if (response.code !== 200) {
              // 错误响应
              return reject(new Error(response.message || "登录失败"));
            } else {
              // 其他情况尝试使用整个响应
              data = response;
            }
          } else {
            // 不是对象的情况（字符串、布尔值等）
            return reject(new Error("登录响应格式无效"));
          }

          // 确保数据中包含必要的字段
          if (!data || (!data.accessToken && !data.token)) {
            console.error("登录响应缺少必要的令牌信息:", data);
            return reject(new Error("登录响应格式错误: 缺少令牌信息"));
          }

          // 使用正确的字段值
          const accessToken = data.accessToken || data.token;
          const refreshToken = data.refreshToken || "";
          const userRole = data.role || "";
          const username = data.username || "";

          // 更新状态
          commit("SET_TOKEN", accessToken);
          commit("SET_REFRESH_TOKEN", refreshToken);
          commit("SET_USER_ROLE", userRole);
          commit("SET_USERNAME", username);
          commit("SET_LOGIN_STATE", true);

          resolve();
        })
        .catch((error) => {
          console.error("登录失败:", error);
          reject(error);
        });
    });
  },

  // 刷新令牌
  refreshToken({ commit, state }) {
    return new Promise((resolve, reject) => {
      if (!state.refreshToken) {
        reject(new Error("没有可用的刷新令牌"));
        return;
      }

      refreshToken({ refreshToken: state.refreshToken })
        .then((response) => {
          // 处理不同的响应格式
          let data;
          if (response && response.data) {
            data = response.data;
          } else if (response && response.code === 200) {
            data = response;
          } else {
            data = response;
          }

          // 检查响应中是否包含新的访问令牌
          if (!data || (!data.accessToken && !data.token)) {
            console.error("刷新令牌响应缺少必要的令牌信息:", data);
            reject(new Error("刷新令牌响应格式错误"));
            return;
          }

          const accessToken = data.accessToken || data.token;
          commit("SET_TOKEN", accessToken);

          // 如果响应中包含新的刷新令牌，也一并更新
          if (data.refreshToken) {
            commit("SET_REFRESH_TOKEN", data.refreshToken);
          }

          resolve(accessToken);
        })
        .catch((error) => {
          console.error("刷新令牌失败:", error);
          reject(error);
        });
    });
  },

  // 获取用户信息
  getUserInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then((response) => {
          const { data } = response;
          if (!data) {
            reject("获取用户信息失败，请重新登录。");
          }
          commit("SET_USER_INFO", data);
          resolve(data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },

  // 用户登出
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      // 尝试调用后端登出API
      logout()
        .then(() => {
          // API调用成功，清除状态
          commit("SET_TOKEN", "");
          commit("SET_REFRESH_TOKEN", "");
          commit("SET_USER_INFO", null);
          commit("SET_USER_ROLE", "");
          commit("SET_USERNAME", "");
          commit("SET_LOGIN_STATE", false);
          resolve();
        })
        .catch((error) => {
          console.error("登出API请求失败，使用本地登出:", error);
          // API调用失败，仍然执行本地登出
          commit("SET_TOKEN", "");
          commit("SET_REFRESH_TOKEN", "");
          commit("SET_USER_INFO", null);
          commit("SET_USER_ROLE", "");
          commit("SET_USERNAME", "");
          commit("SET_LOGIN_STATE", false);
          resolve();
        });
    });
  },
};

const getters = {
  token: (state) => state.token,
  refreshToken: (state) => state.refreshToken,
  userInfo: (state) => state.userInfo,
  isLoggedIn: (state) => state.isLoggedIn,
  userRole: (state) => state.userRole,
  username: (state) => state.username,
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
};
