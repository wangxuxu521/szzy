import request from "@/utils/request";

/**
 * 用户登录
 * @param {Object} data 登录参数 {username, password}
 * @returns {Promise} 登录结果
 */
export function login(data) {
  return request({
    url: "/auth/login",
    method: "post",
    data,
  });
}

/**
 * 用户注册
 * @param {Object} data 注册参数
 * @returns {Promise} 注册结果
 */
export function register(data) {
  return request({
    url: "/auth/register",
    method: "post",
    data,
  });
}

/**
 * 退出登录
 * @returns {Promise} 登出结果
 */
export function logout() {
  return request({
    url: "/auth/logout",
    method: "post",
  });
}

/**
 * 获取用户信息
 * @returns {Promise} 用户信息
 */
export function getUserInfo() {
  return request({
    url: "/auth/info",
    method: "get",
  });
}

/**
 * 刷新令牌
 * @param {String} refreshToken 刷新令牌
 * @returns {Promise} 新的访问令牌
 */
export function refreshToken(refreshToken) {
  return request({
    url: "/auth/refresh-token",
    method: "post",
    data: { refreshToken },
  });
}

/**
 * 获取用户列表
 * @param {Object} params 查询参数
 * @returns {Promise} 用户列表
 */
export function getUserList(params) {
  return request({
    url: "/users",
    method: "get",
    params,
  });
}

/**
 * 根据ID获取用户
 * @param {Number} id 用户ID
 * @returns {Promise} 用户信息
 */
export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: "get",
  });
}

/**
 * 根据角色获取用户
 * @param {String} role 角色名称
 * @returns {Promise} 用户列表
 */
export function findByRole(role) {
  return request({
    url: `/users/role/${role}`,
    method: "get",
  });
}

/**
 * 更新用户信息
 * @param {Number} id 用户ID
 * @param {Object} data 用户数据
 * @returns {Promise} 更新结果
 */
export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除用户
 * @param {Number} id 用户ID
 * @returns {Promise} 删除结果
 */
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: "delete",
  });
}

/**
 * 更改密码
 * @param {Object} data 密码数据 {oldPassword, newPassword}
 * @returns {Promise} 操作结果
 */
export function changePassword(data) {
  return request({
    url: "/users/password",
    method: "post",
    data,
  });
}

export default {
  login,
  register,
  logout,
  getUserInfo,
  refreshToken,
  getUserList,
  getUserById,
  findByRole,
  updateUser,
  deleteUser,
  changePassword,
};
