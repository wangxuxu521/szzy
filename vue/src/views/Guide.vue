<script>
import { ref } from "vue";
import BaseLayout from "@/layout/BaseLayout.vue";

export default {
  name: "Guide",
  components: {
    BaseLayout,
  },
  setup() {
    // 可以在这里添加需要的响应式数据和方法
    return {};
  },
};
</script>

<template>
  <base-layout>
    <div class="guide-container">
      <h1 class="page-title">使用指南</h1>

      <el-tabs type="border-card" class="guide-tabs">
        <el-tab-pane label="系统简介">
          <div class="guide-section">
            <h2>课程思政资源管理系统简介</h2>
            <p class="guide-description">
              欢迎使用课程思政资源管理系统！本系统旨在为高校教师和学生提供便捷的课程思政教学资源共享平台，
              促进思政元素与专业课程的有机融合，共同构建优质的课程思政教学资源库。
            </p>

            <div class="guide-features">
              <div class="feature-item">
                <div class="feature-icon">📚</div>
                <div class="feature-content">
                  <h3>资源共享</h3>
                  <p>
                    提供多种类型的课程思政教学资源，支持多种格式的文件上传和下载
                  </p>
                </div>
              </div>

              <div class="feature-item">
                <div class="feature-icon">👤</div>
                <div class="feature-content">
                  <h3>角色权限</h3>
                  <p>
                    系统支持管理员、教师和学生三种角色，各角色拥有不同的操作权限
                  </p>
                </div>
              </div>

              <div class="feature-item">
                <div class="feature-icon">🔍</div>
                <div class="feature-content">
                  <h3>智能检索</h3>
                  <p>提供多维度的资源检索功能，方便用户快速找到所需资源</p>
                </div>
              </div>

              <div class="feature-item">
                <div class="feature-icon">🔄</div>
                <div class="feature-content">
                  <h3>资源审核</h3>
                  <p>确保资源质量，管理员可对上传的资源进行审核</p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="学生使用指南">
          <div class="guide-section">
            <h2>学生使用指南</h2>

            <div class="guide-steps">
              <div class="step-item">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h3>注册和登录</h3>
                  <p>
                    访问系统首页，点击"注册"按钮，填写相关信息完成注册。注册成功后，使用账号和密码登录系统。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h3>资源浏览和检索</h3>
                  <p>
                    在首页或资源库页面浏览资源，可使用关键词搜索、分类筛选等方式快速找到所需资源。
                    点击资源卡片可查看资源详情。
                  </p>
                  <div class="step-image">
                    <img
                      src="@/assets/guide/student-browse.png"
                      alt="资源浏览"
                    />
                  </div>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h3>资源下载</h3>
                  <p>
                    在资源详情页，点击"下载"按钮即可下载资源。系统会记录您的下载历史，
                    可在个人中心查看下载记录。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">4</div>
                <div class="step-content">
                  <h3>收藏与评论</h3>
                  <p>
                    您可以收藏喜欢的资源，并对资源进行评论和打分，分享您的使用体验和建议。
                    所有收藏的资源都可以在个人中心的"我的收藏"中查看。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">5</div>
                <div class="step-content">
                  <h3>个人中心</h3>
                  <p>
                    在个人中心，您可以修改个人资料、查看下载历史、管理收藏资源等。
                    点击右上角的用户头像，选择"个人中心"即可进入。
                  </p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="教师使用指南">
          <div class="guide-section">
            <h2>教师使用指南</h2>

            <div class="guide-steps">
              <div class="step-item">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h3>账号注册与认证</h3>
                  <p>
                    教师账号需要管理员进行角色认证。请联系系统管理员获取教师权限，
                    或者在注册时选择"教师"角色，等待管理员审核。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h3>资源上传</h3>
                  <p>
                    点击首页或资源库页面的"上传资源"按钮，填写资源信息，上传文件。
                    资源上传后需经过管理员审核才能公开展示。
                  </p>
                  <div class="step-image">
                    <img
                      src="@/assets/guide/teacher-upload.png"
                      alt="资源上传"
                    />
                  </div>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h3>资源管理</h3>
                  <p>
                    在个人中心的"我的资源"页面，您可以查看、编辑、删除您上传的资源，
                    还可以查看资源的审核状态和使用统计数据。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">4</div>
                <div class="step-content">
                  <h3>资源收藏与分享</h3>
                  <p>
                    与学生一样，教师也可以收藏和评论资源。此外，教师可以将系统中的资源
                    分享给学生，促进教学资源的有效利用。
                  </p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="管理员使用指南">
          <div class="guide-section">
            <h2>管理员使用指南</h2>

            <div class="guide-steps">
              <div class="step-item">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h3>用户管理</h3>
                  <p>
                    在管理后台的"用户管理"页面，可以查看、编辑用户信息，
                    审核用户角色申请，设置用户权限等。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h3>资源审核</h3>
                  <p>
                    在"资源审核"页面，可以查看待审核的资源，进行审核操作，
                    并可对不符合要求的资源提供审核意见。
                  </p>
                  <div class="step-image">
                    <img src="@/assets/guide/admin-review.png" alt="资源审核" />
                  </div>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h3>类型管理</h3>
                  <p>
                    在"类型管理"页面，可以创建、编辑和删除资源类型，
                    对系统的资源分类体系进行管理。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">4</div>
                <div class="step-content">
                  <h3>系统设置</h3>
                  <p>
                    在"系统设置"页面，可以配置系统参数，如是否开放注册、
                    资源上传大小限制、允许的文件类型等。
                  </p>
                </div>
              </div>

              <div class="step-item">
                <div class="step-number">5</div>
                <div class="step-content">
                  <h3>数据统计</h3>
                  <p>
                    在"数据统计"页面，可以查看系统使用情况的统计数据，
                    如用户活跃度、资源使用情况等。
                  </p>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="常见问题">
          <div class="guide-section">
            <h2>常见问题解答</h2>

            <el-collapse accordion>
              <el-collapse-item title="1. 如何注册和登录系统？" name="1">
                <div class="faq-answer">
                  <p>
                    访问系统首页，点击右上角的"注册"按钮，填写用户名、密码等信息完成注册。
                    注册成功后，使用账号和密码在登录页面登录系统。如系统未开放注册，请联系管理员获取账号。
                  </p>
                </div>
              </el-collapse-item>

              <el-collapse-item title="2. 忘记密码怎么办？" name="2">
                <div class="faq-answer">
                  <p>
                    在登录页面点击"忘记密码"，按照提示填写注册邮箱，系统会发送密码重置链接到您的邮箱。
                    如无法收到邮件，请联系管理员处理。
                  </p>
                </div>
              </el-collapse-item>

              <el-collapse-item
                title="3. 为什么我上传的资源需要审核？"
                name="3"
              >
                <div class="faq-answer">
                  <p>
                    为了保证资源质量和内容合规性，系统对所有上传的资源进行审核。
                    审核通过后，资源才会公开展示给其他用户。审核时间通常在1-3个工作日内完成。
                  </p>
                </div>
              </el-collapse-item>

              <el-collapse-item title="4. 支持哪些类型的文件上传？" name="4">
                <div class="faq-answer">
                  <p>系统支持多种文件格式上传，包括但不限于：</p>
                  <ul>
                    <li>文档类：PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, TXT</li>
                    <li>图片类：JPG, JPEG, PNG, GIF</li>
                    <li>音视频类：MP4, MP3</li>
                    <li>压缩文件：ZIP, RAR</li>
                  </ul>
                  <p>
                    单个文件大小限制为50MB，如需上传更大文件，请联系管理员。
                  </p>
                </div>
              </el-collapse-item>

              <el-collapse-item title="5. 如何联系系统管理员？" name="5">
                <div class="faq-answer">
                  <p>您可以通过以下方式联系系统管理员：</p>
                  <ul>
                    <li>发送邮件至：admin@example.com</li>
                    <li>系统内留言：在"关于"页面填写反馈表单</li>
                    <li>电话联系：123-4567-8910（工作日9:00-17:00）</li>
                  </ul>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </base-layout>
</template>

<style scoped>
.guide-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
}

.guide-tabs {
  margin-bottom: 30px;
}

.guide-section {
  padding: 20px;
}

.guide-section h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 22px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}

.guide-description {
  font-size: 16px;
  line-height: 1.6;
  color: #666;
  margin-bottom: 30px;
}

.guide-features {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.feature-item {
  display: flex;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 36px;
  margin-right: 15px;
}

.feature-content h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.feature-content p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.guide-steps {
  margin-top: 30px;
}

.step-item {
  display: flex;
  margin-bottom: 30px;
  position: relative;
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  font-size: 18px;
  font-weight: bold;
  margin-right: 20px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-content h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.step-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.step-image {
  margin-top: 15px;
  margin-bottom: 15px;
  text-align: center;
}

.step-image img {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.faq-answer {
  padding: 10px;
  color: #666;
  line-height: 1.6;
}

.faq-answer ul {
  padding-left: 20px;
}

.faq-answer li {
  margin-bottom: 5px;
}

@media (max-width: 768px) {
  .guide-features {
    grid-template-columns: 1fr;
  }

  .step-item {
    flex-direction: column;
  }

  .step-number {
    margin-bottom: 10px;
  }
}
</style>
