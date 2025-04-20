document.addEventListener("DOMContentLoaded", function () {
  function handleLogin() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;

    if (!username || !password) {
      alert("请输入用户名和密码");
      return;
    }

    // TODO: 这里替换为实际的登录API调用
    console.log("登录信息：", { username, password, role });

    // 模拟登录成功
    localStorage.setItem("userRole", role);
    localStorage.setItem("username", username);
    localStorage.setItem("isLoggedIn", "true");

    // 登录成功后统一跳转到首页
    window.location.href = "/home.html";
  }

  document
    .querySelector(".login-button")
    .addEventListener("click", handleLogin);
});
