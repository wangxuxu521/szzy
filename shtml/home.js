document.addEventListener("DOMContentLoaded", function () {
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
  const userRole = localStorage.getItem("userRole");
  const username = localStorage.getItem("username");

  function goToLogin() {
    window.location.href = "/login.html";
  }

  function goToUserCenter() {
    if (userRole === "teacher") {
      window.location.href = "/teacher/center.html";
    } else if (userRole === "student") {
      window.location.href = "/student/center.html";
    }
  }

  function goToAdmin() {
    if (userRole === "admin") {
      window.location.href = "/admin.html";
    }
  }

  function logout() {
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("userRole");
    localStorage.removeItem("username");
    window.location.reload();
  }

  function handleSearch() {
    const searchQuery = document.querySelector(".hero-search input").value;
    console.log("搜索:", searchQuery);
    // TODO: 实现搜索功能
  }

  function viewResource(resource) {
    console.log("查看资源:", resource);
    // TODO: 实现资源查看功能
  }

  // 绑定事件
  document
    .querySelector(".hero-search button")
    .addEventListener("click", handleSearch);
  document.querySelector(".login-btn").addEventListener("click", goToLogin);
  document.querySelector(".logout-btn").addEventListener("click", logout);

  // 根据登录状态显示用户信息
  if (isLoggedIn) {
    document.querySelector(".username").textContent = username;
    if (userRole === "teacher") {
      document.querySelector(".icon-btn").textContent = "👤 教师空间";
      document
        .querySelector(".icon-btn")
        .addEventListener("click", goToUserCenter);
    } else if (userRole === "student") {
      document.querySelector(".icon-btn").textContent = "👤 学生空间";
      document
        .querySelector(".icon-btn")
        .addEventListener("click", goToUserCenter);
    } else if (userRole === "admin") {
      document.querySelector(".icon-btn").textContent = "⚙️ 系统管理";
      document.querySelector(".icon-btn").addEventListener("click", goToAdmin);
    }
  }
});
