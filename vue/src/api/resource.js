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

// 获取资源预览URL
export function getResourcePreviewUrl(id) {
  // 使用后端提供的预览API
  return `/api/resources/preview/${id}`;
}

// 检查资源是否支持在线预览
export function checkPreviewSupport(id, fileName) {
  // 使用后端提供的预览支持检查API
  return request({
    url: `/resources/preview-support/${id}`,
    method: "get",
  }).then((response) => {
    // 如果后端返回的是标准格式，直接返回data
    if (response && response.code === 0) {
      return response.data;
    }
    // 否则直接返回响应
    return response;
  });
}

// 根据扩展名获取文件类型 - 仅作为备用方案
function getFileTypeFromExtension(extension) {
  if (["pdf"].includes(extension)) {
    return "pdf";
  } else if (["jpg", "jpeg", "png", "gif", "bmp", "webp"].includes(extension)) {
    return "image";
  } else if (
    ["txt", "log", "md", "json", "xml", "html", "css", "js"].includes(extension)
  ) {
    return "text";
  } else if (
    ["doc", "docx", "xls", "xlsx", "ppt", "pptx"].includes(extension)
  ) {
    return "office";
  } else if (
    ["mp4", "webm", "ogg", "avi", "mov", "wmv", "flv", "mkv"].includes(
      extension
    )
  ) {
    return "video";
  }
  return "other";
}
