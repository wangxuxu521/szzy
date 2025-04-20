import request from "@/utils/request";

// 获取资源列表
export function getResourceList(params) {
  return request({
    url: "/resources",
    method: "get",
    params,
  });
}

// 获取资源详情
export function getResourceDetail(id) {
  return request({
    url: `/resources/${id}`,
    method: "get",
  });
}

// 上传资源
export function uploadResource(data) {
  return request({
    url: "/resources/upload",
    method: "post",
    data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

// 更新资源信息
export function updateResource(id, data) {
  return request({
    url: `/resources/${id}`,
    method: "put",
    data,
  });
}

// 删除资源
export function deleteResource(id) {
  return request({
    url: `/resources/${id}`,
    method: "delete",
  });
}

// 搜索资源
export function searchResources(params) {
  return request({
    url: "/resources/search",
    method: "get",
    params,
  });
}

// 获取热门资源
export function getHotResources(limit = 5) {
  return request({
    url: "/resources/hot",
    method: "get",
    params: { limit },
  });
}

// 下载资源
export function downloadResource(id) {
  return request({
    url: `/resources/download/${id}`,
    method: "get",
    responseType: "blob",
  });
}
