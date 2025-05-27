import {
  getResourceList,
  getHotResources,
  searchResources,
} from "@/api/resource";

const state = {
  resourceList: [],
  hotResources: [],
  totalCount: 0,
  loading: false,
  searchResults: [],
  searchLoading: false,
};

const mutations = {
  SET_RESOURCE_LIST(state, resources) {
    state.resourceList = resources;
  },
  SET_HOT_RESOURCES(state, resources) {
    state.hotResources = resources;
  },
  SET_TOTAL_COUNT(state, count) {
    state.totalCount = count;
  },
  SET_LOADING(state, loading) {
    state.loading = loading;
  },
  SET_SEARCH_RESULTS(state, results) {
    state.searchResults = results;
  },
  SET_SEARCH_LOADING(state, loading) {
    state.searchLoading = loading;
  },
};

const actions = {
  // 获取资源列表
  getResourceList({ commit }, params) {
    commit("SET_LOADING", true);
    return new Promise((resolve, reject) => {
      getResourceList(params)
        .then((response) => {
          const { data } = response;
          commit("SET_RESOURCE_LIST", data.list || []);
          commit("SET_TOTAL_COUNT", data.total || 0);
          commit("SET_LOADING", false);
          resolve(data);
        })
        .catch((error) => {
          commit("SET_LOADING", false);
          reject(error);
        });
    });
  },

  // 获取热门资源
  getHotResources({ commit }, limit) {
    return new Promise((resolve, reject) => {
      getHotResources(limit)
        .then((response) => {
          console.log("Hot resources API response:", response);
          // 处理标准响应格式
          if (response && response.code === 200) {
            commit("SET_HOT_RESOURCES", response.data || []);
            resolve(response.data);
          } else if (response && response.data) {
            // 处理直接返回数据的情况
            commit("SET_HOT_RESOURCES", response.data || []);
            resolve(response.data);
          } else {
            // 处理其他情况
            commit("SET_HOT_RESOURCES", response || []);
            resolve(response);
          }
        })
        .catch((error) => {
          console.error("获取热门资源失败:", error);
          reject(error);
        });
    });
  },

  // 搜索资源
  searchResources({ commit }, params) {
    commit("SET_SEARCH_LOADING", true);
    return new Promise((resolve, reject) => {
      searchResources(params)
        .then((response) => {
          const { data } = response;
          commit("SET_SEARCH_RESULTS", data.list || []);
          commit("SET_SEARCH_LOADING", false);
          resolve(data);
        })
        .catch((error) => {
          commit("SET_SEARCH_LOADING", false);
          reject(error);
        });
    });
  },
};

const getters = {
  resourceList: (state) => state.resourceList,
  hotResources: (state) => state.hotResources,
  totalCount: (state) => state.totalCount,
  loading: (state) => state.loading,
  searchResults: (state) => state.searchResults,
  searchLoading: (state) => state.searchLoading,
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
};
