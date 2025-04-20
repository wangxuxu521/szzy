import request from "@/utils/request";

// 获取标签列表
export function getTagList() {
  return request({
    url: "/tags",
    method: "get",
  });
}

// 获取标签详情
export function getTagDetail(id) {
  return request({
    url: `/tags/${id}`,
    method: "get",
  });
}

// 创建或更新标签
export function saveTag(data) {
  return request({
    url: "/tags",
    method: "post",
    data,
  });
}

// 删除标签
export function deleteTag(id) {
  return request({
    url: `/tags/${id}`,
    method: "delete",
  });
}

// 根据资源ID获取标签
export function getTagsByResourceId(resourceId) {
  return request({
    url: `/resource-tags/resource/${resourceId}`,
    method: "get",
  });
}

// 保存资源标签关联
export function saveResourceTag(data) {
  return request({
    url: "/resource-tags",
    method: "post",
    data,
  });
}

// 删除资源标签关联
export function deleteResourceTag(id) {
  return request({
    url: `/resource-tags/${id}`,
    method: "delete",
  });
}
