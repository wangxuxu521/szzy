<!-- 资源管理相关导航项 -->
<template v-if="isLoggedIn">
  <li class="nav-item">
    <router-link to="/upload-resource" class="nav-link">
      <i class="nav-icon el-icon-upload"></i>
      上传资源
    </router-link>
  </li>
</template>

<ul class="navbar-nav">
  <li class="nav-item">
    <router-link to="/" class="nav-link">
      <i class="nav-icon el-icon-house"></i>
      首页
    </router-link>
  </li>
  <li class="nav-item">
    <router-link to="/resources" class="nav-link">
      <i class="nav-icon el-icon-folder"></i>
      资源库
    </router-link>
  </li>
  <!-- 资源管理相关导航项 -->
  <template v-if="isLoggedIn">
    <li class="nav-item">
      <router-link to="/upload-resource" class="nav-link">
        <i class="nav-icon el-icon-upload"></i>
        上传资源
      </router-link>
    </li>
  </template>
  <li class="nav-item" v-if="isAdmin">
    <router-link to="/admin" class="nav-link">
      <i class="nav-icon el-icon-setting"></i>
      管理后台
    </router-link>
  </li>
</ul>
