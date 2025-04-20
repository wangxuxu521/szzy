// 资源管理相关的 JavaScript 代码
// TODO: 实现资源管理功能

document.addEventListener("DOMContentLoaded", function () {
  const resourceList = document.getElementById("resourceList");

  function fetchResources() {
    fetch("/api/resources")
      .then((response) => response.json())
      .then((data) => {
        resourceList.innerHTML = "";
        data.forEach((resource) => {
          const li = document.createElement("li");
          li.textContent = resource.name;
          resourceList.appendChild(li);
        });
      })
      .catch((error) => {
        console.error("获取资源数据出错:", error);
      });
  }

  fetchResources();
});
