阅读日志管理系统（Reading Activity Management）
1. 概述
阅读日志管理系统 旨在帮助用户记录和管理他们的阅读活动，包括书籍或文章的标题、作者、阅读时间、笔记等。同时，系统支持管理员删除包含不适当内容的日志。

本系统基于 Spring Boot 进行开发，使用 Spring Data JPA 进行数据持久化，并提供 REST API 供前端调用。

2. 主要组件
本模块主要包含以下核心类：

ReadingLog（实体类）：代表用户的阅读日志数据。

ReadingLogDto（数据传输对象）：用于处理前端请求的数据结构。

ReadingLogRepository（数据访问层）：提供数据库查询功能。

ReadingLogService（业务逻辑层）：处理日志的增删改查逻辑。

ReadingLogController（控制器）：提供 REST API 端点，供前端调用。

ViolationLog（违规日志记录）：记录管理员删除的违规日志。

ViolationLogRepository（违规日志存储库）：存储违规日志。

3. 代码结构
cpp
复制
编辑
com.group20.dailyreadingtracker.readinglog
│── ReadingLog.java           // 阅读日志实体类
│── ReadingLogDto.java        // 阅读日志DTO
│── ReadingLogRepository.java // 阅读日志数据访问层
│── ReadingLogService.java    // 阅读日志业务逻辑层
│── ReadingLogController.java // 阅读日志控制器
│
com.group20.dailyreadingtracker.violationlog
│── ViolationLog.java         // 违规日志实体类
│── ViolationLogRepository.java // 违规日志存储库
│
com.group20.dailyreadingtracker.user
│── User.java                 // 用户实体类
│── UserRepository.java       // 用户存储库
4. 详细功能说明
4.1 阅读日志管理
创建阅读日志
方法: POST /api/reading-logs

描述: 允许用户创建新的阅读日志

请求体 (JSON):

json
复制
编辑
{
  "title": "Spring Boot 入门",
  "author": "John Doe",
  "date": "2024-03-27",
  "timeSpent": 60,
  "notes": "学习了 Spring Boot 基础知识"
}
响应:

json
复制
编辑
{
  "id": 1,
  "message": "Reading log created successfully"
}
获取当前用户的所有日志
方法: GET /api/reading-logs

描述: 获取当前用户的所有阅读日志。

获取指定日志
方法: GET /api/reading-logs/{logId}

描述: 获取某个日志的详情。

更新日志
方法: PUT /api/reading-logs/{logId}

描述: 更新用户的阅读日志内容。

删除日志
方法: DELETE /api/reading-logs/{logId}

描述: 仅允许日志所属用户删除自己的日志。

4.2 管理员功能
获取所有日志
方法: GET /api/reading-logs/all

描述: 允许管理员查看所有日志。

删除违规日志
方法: DELETE /api/admin/reading-logs/{logId}

描述: 允许管理员删除不适当内容的日志，并将日志记录到违规日志数据库中。

流程:

先从数据库查找日志。

记录违规日志（ViolationLog）。

从数据库中删除该日志。

代码逻辑:

java
复制
编辑
public void deleteInappropriateLog(Long logId) {
    ReadingLog log = readingLogRepository.findById(logId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found"));

    // 记录违规日志
    ViolationLog violationLog = new ViolationLog();
    violationLog.setLogId(log.getId());
    violationLog.setReason("Inappropriate content");
    violationLogRepository.save(violationLog);

    // 删除日志
    readingLogRepository.delete(log);
}
5. 异常处理
系统提供全局异常处理，包括：

找不到日志:

json
复制
编辑
{
  "error": "Reading log not found"
}
无权限访问:

json
复制
编辑
{
  "error": "Access denied"
}
无效的日志 ID:

json
复制
编辑
{
  "error": "Invalid log ID format"
}
6. 结论
本系统提供完整的阅读日志管理功能，包括用户创建、查询、更新、删除日志的能力，并提供管理员对违规日志的管理。所有 API 采用 RESTful 设计，数据存储使用 Spring Data JPA，确保数据的持久化与一致性。
