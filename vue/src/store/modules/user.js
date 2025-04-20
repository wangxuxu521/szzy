import { login, logout, getUserInfo } from "@/api/user";

const state = {
  token: localStorage.getItem("token"),
  userInfo: null,
  isLoggedIn: localStorage.getItem("isLoggedIn") === "true",
  userRole: localStorage.getItem("userRole"),
  username: localStorage.getItem("username"),
};

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token;
    if (token) {
      localStorage.setItem("token", token);
    } else {
      localStorage.removeItem("token");
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
          const { data } = response;
          commit("SET_TOKEN", data.token);
          commit("SET_USER_ROLE", data.role);
          commit("SET_USERNAME", data.username);
          commit("SET_LOGIN_STATE", true);
          resolve();
        })
        .catch((error) => {
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
