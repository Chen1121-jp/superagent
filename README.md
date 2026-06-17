# Chen AI Agent 🚀

基于 **Spring AI Alibaba 1.1.2** 的智能 Agent 系统，实现 ReAct（Reasoning + Acting）模式，支持手动工具调用、RAG 知识检索、流式对话和 MCP 多智能体协作。

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 3.5、Spring AI Alibaba 1.1.2 |
| LLM | 阿里云百炼 DashScope（Qwen 系列） |
| 数据库 | PostgreSQL + pgvector（向量存储） |
| ORM | MyBatis-Plus |
| API 文档 | Knife4j / Swagger |
| PDF 生成 | iText 9 |
| 工具库 | Hutool、Jsoup、Jackson |

## 项目结构

```
chen-ai-agent/
├── agent/                 # Agent 架构
│   ├── BaseAgent          # 抽象基类：生命周期、状态机、同步/流式执行
│   ├── ReActAgent         # ReAct 模式：think() → act()
│   ├── ToolCallAgent      # 工具调用 Agent，手动控制工具执行循环
│   └── ChenManus          # 通用 AI 助手 Agent Bean
├── Tools/                 # 工具集（9 个）
│   ├── EmailTool          # 发送邮件
│   ├── FileOperationTool  # 读写文件
│   ├── JdbcTool           # 执行 SQL 查询
│   ├── PDFGenerationTool  # 生成 PDF（支持中文字体）
│   ├── ResourceDownloadTool  # 下载网络资源
│   ├── TerminalOperationTool # 执行终端命令
│   ├── TerminateTool      # 终止 Agent 任务
│   ├── WebScrapingTool    # 网页抓取
│   └── WebSearchTool      # 网络搜索
├── rag/                   # RAG 检索增强生成
│   ├── LoveDocumentLoader           # Markdown 知识库加载
│   ├── LovePgVectorStoreConfig      # PGVector 向量存储
│   ├── LoveRagCloudAdvisorConfig    # 阿里云 RAG 云服务
│   ├── QueryRewriter                # 查询重写优化
│   └── QueryTranslation             # 多语言翻译（阿里云 MT）
├── controller/            # API 接口
│   ├── AiController       # Agent 对话接口
│   └── HealthTestController  # 健康检查
├── app/LoveApp.java       # 恋爱心理咨询应用
├── exception/             # 全局异常处理
├── chatmemory/            # 对话记忆（JDBC / 文件持久化）
└── chen-image-serch-mcp-server/  # MCP 图片搜索子模块
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- PostgreSQL 16+（需安装 **pgvector** 扩展）
- 阿里云百炼 API Key

### 1. 配置文件

创建 `src/main/resources/application-local.yml`：

```yaml
spring:
  ai:
    dashscope:
      api-key: sk-your-dashscope-api-key
  mail:
    host: smtp.qq.com
    username: your-email@qq.com
    password: your-smtp-password
  datasource:
    username: your-db-user
    password: your-db-password

aliyun:
  translate:
    access-key-id: your-aliyun-ak
    access-key-secret: your-aliyun-sk

search-api:
  api-key: your-search-api-key
```

### 2. 初始化数据库

```sql
CREATE DATABASE chen_agent;
CREATE EXTENSION IF NOT EXISTS vector;   -- 启用 pgvector
```

### 3. 启动项目

```bash
mvn spring-boot:run
```

启动后访问：

| 页面 | 地址 |
|------|------|
| Knife4j 接口文档 | http://localhost:8123/api/doc.html |
| 健康检查 | http://localhost:8123/api/health |

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/ai/love_app/chat/sync` | 恋爱咨询（同步返回） |
| GET | `/ai/love_app/chat/sse` | 恋爱咨询（SSE 流式） |
| GET | `/ai/love_app/chat/sse/emitter` | 恋爱咨询（SseEmitter） |
| GET | `/ai/manus/chat` | ChenManus Agent 对话 |
| GET | `/health` | 健康检查 |

### 调用示例

```bash
# 同步对话
curl "http://localhost:8123/api/ai/love_app/chat/sync?message=异地恋怎么维持&chatId=user001"

# Agent 工具调用
curl "http://localhost:8123/api/ai/manus/chat?message=帮我搜索江门情侣约会攻略并生成PDF报告"
```

## Agent 架构

采用 **ReAct（Reasoning + Acting）** 循环：

```
用户输入
  → think() 调用 LLM 分析
  → 模型返回 tool_calls？
     ├─ 是 → act() 手动执行工具 → 结果喂回会话 → 回到 think()
     └─ 否 → 输出最终结果
```

核心设计：
- **手动工具控制**：`internalToolExecutionEnabled = false`，框架不自动循环执行工具，Agent 自行管理每一步
- **状态机**：`IDLE → RUNNING → FINISHED / ERROR`
- **最大步数**：默认 5 步，防止无限循环
- **流式支持**：同步 `run()` 和 SSE 流式 `runStream()` 两种执行方式

## 添加新工具

1. 创建工具类，使用 `@Tool` 注解：

```java
@Tool(description = "获取指定城市的天气信息")
public String getWeather(@ToolParam(description = "城市名称") String city) {
    // 实现逻辑
    return "北京今天晴，25°C";
}
```

2. 在 `ToolRegistration.java` 中注册：

```java
@Bean
public ToolCallback[] allTools() {
    return ToolCallbacks.from(
        new EmailTool(),
        new FileOperationTool(),
        // ... 其他工具 ...
        new MyNewTool()  // 添加到这里
    );
}
```

Agent 会在下一次对话中自动发现并调用新工具。
