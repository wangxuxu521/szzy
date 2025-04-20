document.addEventListener("DOMContentLoaded", function () {
  const tabButtons = document.querySelectorAll(".tab-btn");
  const contentSections = document.querySelectorAll(".content-section");

  tabButtons.forEach((button, index) => {
    button.addEventListener("click", () => {
      tabButtons.forEach((btn) => btn.classList.remove("active"));
      contentSections.forEach((section) => (section.style.display = "none"));

      button.classList.add("active");
      contentSections[index].style.display = "block";
    });
  });

  // 默认激活第一个标签
  if (tabButtons.length > 0) {
    tabButtons[0].classList.add("active");
    contentSections[0].style.display = "block";
  }

  // 通知按钮点击事件
  const notificationButton = document.querySelector(".notification-btn");
  const notificationList = document.querySelector(".notification-list");

  if (notificationButton && notificationList) {
    notificationButton.addEventListener("click", () => {
      notificationList.classList.toggle("show");
    });
  }

  // 标记通知为已读
  const markReadButtons = document.querySelectorAll(".mark-read-btn");

  markReadButtons.forEach((button) => {
    button.addEventListener("click", (event) => {
      const notificationItem = event.target.closest(".notification-item");
      if (notificationItem) {
        notificationItem.classList.remove("unread");
      }
    });
  });
});
