document.addEventListener("DOMContentLoaded", function () {
  const username = localStorage.getItem("username") || "";
  document.getElementById("username").textContent = username;

  function setActiveMenu(menu) {
    document.querySelectorAll(".menu-item").forEach((item) => {
      item.classList.remove("active");
    });
    const activeItem = Array.from(document.querySelectorAll(".menu-item")).find(
      (item) => item.textContent.trim() === menu
    );
    if (activeItem) {
      activeItem.classList.add("active");
    }
    // TODO: 根据菜单加载相应的组件
    console.log("激活菜单:", menu);
  }

  function logout() {
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("userRole");
    localStorage.removeItem("username");
    window.location.href = "/login.html";
  }

  function handleSearch() {
    const searchQuery = document.getElementById("searchQuery").value;
    console.log("搜索:", searchQuery);
    // TODO: 实现搜索功能
  }

  function goHome() {
    window.location.href = "/home.html";
  }

  document.querySelector(".logout-btn").addEventListener("click", logout);
  document.querySelector(".back-btn").addEventListener("click", goHome);
  document
    .querySelector(".search-bar button")
    .addEventListener("click", handleSearch);

  // 默认激活第一个菜单
  setActiveMenu("资源管理");
});
