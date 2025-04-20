import request from "@/utils/request";

// 用户登录
export function login(data) {
  return request({
    url: "/auth/login",
    method: "post",
    data,
  });
}

// 用户登出
export function logout() {
  return request({
    url: "/auth/logout",
    method: "post",
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: "/auth/info",
    method: "get",
  });
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: `/user`,
    method: "post",
    data,
  });
}

// 更新用户密码
export function updatePassword(data) {
  return request({
    url: "/user/password",
    method: "put",
    data,
  });
}

// 获取用户列表（仅管理员可用）
export function getUserList(params) {
  return request({
    url: "/user/list",
    method: "get",
    params,
  });
}

// 添加用户（仅管理员可用）
export function addUser(data) {
  return request({
    url: "/user",
    method: "post",
    data,
  });
}

// 删除用户（仅管理员可用）
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: "delete",
  });
}
