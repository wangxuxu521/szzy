# 数字化教学资源共享平台系统图表

## 1. ER 图（实体关系图）

```plantuml
@startuml ER图

entity "User" as user {
  *user_id : int <<PK>>
  --
  username : varchar
  password : varchar
  name : varchar
  email : varchar
  phone : varchar
  role : varchar
  status : tinyint
  avatar : varchar
  last_login_time : datetime
  create_time : datetime
  update_time : datetime
}

entity "Resource" as resource {
  *resource_id : int <<PK>>
  --
  title : varchar
  description : text
  file_path : varchar
  format : varchar
  type_id : int <<FK>>
  upload_time : datetime
  update_time : datetime
  download_count : int
  rating : float
  uploader_id : int <<FK>>
  review_status : varchar
  file_name : varchar
  file_size : bigint
  view_count : int
  type : varchar
  tags : text
}

entity "Comment" as comment {
  *comment_id : int <<PK>>
  --
  resource_id : int <<FK>>
  user_id : int <<FK>>
  content : text
  parent_id : int <<FK>>
  create_time : datetime
  status : tinyint
}

entity "ResourceRating" as rating {
  *id : int <<PK>>
  --
  resource_id : int <<FK>>
  user_id : int <<FK>>
  rating : double
  create_time : datetime
  update_time : datetime
}

entity "ResourceFavorite" as favorite {
  *id : int <<PK>>
  --
  user_id : int <<FK>>
  resource_id : int <<FK>>
  create_time : datetime
}

entity "Course" as course {
  *course_id : int <<PK>>
  --
  course_name : varchar
  description : text
  type_id : int <<FK>>
  teacher_id : int <<FK>>
  create_time : datetime
  status : tinyint
  cover_image : varchar
}

entity "Type" as type {
  *type_id : int <<PK>>
  --
  type_name : varchar
  description : varchar
  parent_id : int <<FK>>
  sort_order : int
  create_time : datetime
  update_time : datetime
  status : tinyint
}

entity "Tag" as tag {
  *tag_id : int <<PK>>
  --
  tag_name : varchar
  tag_type : varchar
  create_time : datetime
  update_time : datetime
}

entity "ResourceTag" as resourceTag {
  *id : int <<PK>>
  --
  resource_id : int <<FK>>
  tag_id : int <<FK>>
  create_time : datetime
}

entity "Review" as review {
  *review_id : int <<PK>>
  --
  resource_id : int <<FK>>
  reviewer_id : int <<FK>>
  review_time : datetime
  review_opinion : text
  review_result : varchar
  create_time : datetime
}

entity "Announcement" as announcement {
  *id : int <<PK>>
  --
  title : varchar
  content : text
  type : int
  priority : int
  publisher : varchar
  publish_time : datetime
  create_time : datetime
  update_time : datetime
  status : int
}

entity "SystemConfig" as config {
  *id : int <<PK>>
  --
  config_key : varchar
  config_value : text
  description : varchar
  create_time : datetime
  update_time : datetime
}

user ||--o{ resource : 上传
user ||--o{ comment : 评论
user ||--o{ rating : 评分
user ||--o{ favorite : 收藏
user ||--o{ review : 审核
user ||--o{ course : 创建

resource ||--o{ comment : 被评论
resource ||--o{ rating : 被评分
resource ||--o{ favorite : 被收藏
resource }|--|| type : 分类
resource }|--o{ review : 被审核
resource }|--|{ tag : 拥有 > resourceTag

comment }o--o| comment : 回复

course }|--|| type : 分类
course }|--|| user : 教师

@enduml
```

## 2. 类图

```plantuml
@startuml 类图

package "实体层" {
  class User {
    - userId: Integer
    - username: String
    - password: String
    - name: String
    - email: String
    - phone: String
    - role: String
    - status: Boolean
    - avatar: String
    - lastLoginTime: Date
    - createTime: Date
    - updateTime: Date
    + getter/setter方法()
  }

  class Resource {
    - resourceId: Integer
    - title: String
    - description: String
    - filePath: String
    - format: String
    - typeId: Integer
    - uploadTime: Date
    - updateTime: Date
    - downloadCount: Integer
    - rating: Float
    - uploaderId: Integer
    - reviewStatus: String
    - fileName: String
    - fileSize: Long
    - viewCount: Integer
    - type: String
    - tags: String
    + getter/setter方法()
  }

  class Comment {
    - commentId: Integer
    - resourceId: Integer
    - userId: Integer
    - content: String
    - parentId: Integer
    - createTime: Date
    - status: Boolean
    + getter/setter方法()
  }

  class Course {
    - courseId: Integer
    - courseName: String
    - description: String
    - typeId: Integer
    - teacherId: Integer
    - createTime: Date
    - status: Boolean
    - coverImage: String
    + getter/setter方法()
  }

  class Type {
    - typeId: Integer
    - typeName: String
    - description: String
    - parentId: Integer
    - sortOrder: Integer
    - createTime: Date
    - updateTime: Date
    - status: Boolean
    + getter/setter方法()
  }
}

package "服务层" {
  interface UserService {
    + findByUsername(String username): User
    + register(User user): User
    + updateUser(User user): User
    + listUsers(): List<User>
    + getUserById(Integer userId): User
    + deleteUser(Integer userId): void
    + changeUserStatus(Integer userId, Boolean status): void
  }

  interface ResourceService {
    + uploadResource(...): Resource
    + getResourceById(Integer resourceId): Resource
    + listResources(...): List<Resource>
    + updateResource(Resource resource): Resource
    + deleteResource(Integer resourceId): void
    + incrementDownloadCount(Integer resourceId, Integer userId): void
    + incrementViewCount(Integer resourceId): void
    + searchResources(...): List<Resource>
  }

  interface ReviewService {
    + reviewResource(Integer resourceId, Integer reviewerId, String opinion, String result): Review
    + getResourcesForReview(): List<Resource>
    + getReviewHistory(Integer resourceId): List<Review>
  }
}

package "控制层" {
  class AuthController {
    - authService: AuthService
    + login(LoginRequest loginRequest): ResponseEntity<?>
    + register(RegisterRequest registerRequest): ResponseEntity<?>
    + refreshToken(String token): ResponseEntity<?>
  }

  class ResourceController {
    - resourceService: ResourceService
    + uploadResource(...): ResponseEntity<?>
    + getResource(Integer resourceId): ResponseEntity<?>
    + updateResource(...): ResponseEntity<?>
    + deleteResource(Integer resourceId): ResponseEntity<?>
    + downloadResource(Integer resourceId): ResponseEntity<?>
    + searchResources(...): ResponseEntity<?>
  }

  class AdminController {
    - userService: UserService
    - resourceService: ResourceService
    - reviewService: ReviewService
    - systemConfigService: SystemConfigService
    + listUsers(...): ResponseEntity<?>
    + updateUser(...): ResponseEntity<?>
    + deleteUser(Integer userId): ResponseEntity<?>
    + reviewResource(...): ResponseEntity<?>
    + updateSystemConfig(...): ResponseEntity<?>
  }
}

User "1" -- "*" Resource : 上传
User "1" -- "*" Comment : 发表
User "1" -- "*" Course : 创建
Resource "1" -- "*" Comment : 拥有
Resource "*" -- "1" Type : 分类
Course "*" -- "1" Type : 分类

ResourceService ..> Resource
UserService ..> User
ReviewService ..> Review
ReviewService ..> Resource

AuthController ..> UserService
ResourceController ..> ResourceService
AdminController ..> UserService
AdminController ..> ResourceService
AdminController ..> ReviewService

@enduml
```

## 3. 用户登录时序图

```plantuml
@startuml 用户登录时序图

actor 用户
participant "前端界面" as FrontEnd
participant "AuthController" as AuthController
participant "AuthService" as AuthService
participant "UserService" as UserService
database "数据库" as DB

用户 -> FrontEnd: 输入用户名和密码
activate FrontEnd
FrontEnd -> AuthController: 发送登录请求
activate AuthController
AuthController -> AuthService: 验证用户身份
activate AuthService
AuthService -> UserService: 根据用户名查找用户
activate UserService
UserService -> DB: 查询用户数据
activate DB
DB --> UserService: 返回用户数据
deactivate DB
UserService --> AuthService: 返回用户对象
deactivate UserService
AuthService -> AuthService: 验证密码
AuthService -> AuthService: 生成JWT令牌
AuthService --> AuthController: 返回JWT令牌
deactivate AuthService
AuthController --> FrontEnd: 返回JWT令牌和用户信息
deactivate AuthController
FrontEnd -> FrontEnd: 保存令牌到本地存储
FrontEnd --> 用户: 登录成功，跳转到首页
deactivate FrontEnd

@enduml
```

## 4. 资源上传时序图

```plantuml
@startuml 资源上传时序图

actor 用户
participant "前端界面" as FrontEnd
participant "ResourceController" as ResController
participant "ResourceService" as ResService
database "数据库" as DB
database "文件系统" as FileSystem

用户 -> FrontEnd: 选择文件并填写资源信息
activate FrontEnd
FrontEnd -> FrontEnd: 验证文件类型和大小
FrontEnd -> ResController: 提交资源上传请求
activate ResController
ResController -> ResController: 验证用户权限
ResController -> ResService: 上传资源
activate ResService
ResService -> FileSystem: 存储文件
activate FileSystem
FileSystem --> ResService: 返回文件路径
deactivate FileSystem
ResService -> ResService: 生成资源元数据
ResService -> DB: 保存资源记录
activate DB
DB --> ResService: 保存成功
deactivate DB
ResService --> ResController: 返回资源信息
deactivate ResService
ResController --> FrontEnd: 返回上传结果
deactivate ResController
alt 需要审核
    FrontEnd --> 用户: 提示等待审核
else 无需审核
    FrontEnd --> 用户: 提示上传成功
end
deactivate FrontEnd

@enduml
```

## 5. 资源审核时序图

```plantuml
@startuml 资源审核时序图

actor 管理员
participant "前端界面" as FrontEnd
participant "AdminController" as AdminController
participant "ReviewService" as ReviewService
participant "ResourceService" as ResourceService
database "数据库" as DB

管理员 -> FrontEnd: 进入资源审核页面
activate FrontEnd
FrontEnd -> AdminController: 请求待审核资源列表
activate AdminController
AdminController -> ReviewService: 获取待审核资源
activate ReviewService
ReviewService -> DB: 查询待审核资源
activate DB
DB --> ReviewService: 返回资源列表
deactivate DB
ReviewService --> AdminController: 返回资源列表
deactivate ReviewService
AdminController --> FrontEnd: 显示待审核资源
deactivate AdminController
FrontEnd --> 管理员: 展示待审核资源

管理员 -> FrontEnd: 提交审核结果
activate FrontEnd
FrontEnd -> AdminController: 发送审核决定
activate AdminController
AdminController -> ReviewService: 处理审核结果
activate ReviewService
ReviewService -> ResourceService: 更新资源状态
activate ResourceService
ResourceService -> DB: 更新资源记录
activate DB
DB --> ResourceService: 更新成功
deactivate DB
ResourceService --> ReviewService: 返回更新结果
deactivate ResourceService
ReviewService -> DB: 保存审核记录
activate DB
DB --> ReviewService: 保存成功
deactivate DB
ReviewService --> AdminController: 返回审核结果
deactivate ReviewService
AdminController --> FrontEnd: 返回操作结果
deactivate AdminController
FrontEnd --> 管理员: 显示审核完成信息
deactivate FrontEnd

@enduml
```

## 使用说明

1. 复制以上代码到 PlantUML 在线编辑器（如 http://www.plantuml.com/plantuml/uml/）
2. 生成图片后下载保存
3. 在论文中插入这些图片并添加相应说明
