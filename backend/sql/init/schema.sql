-- ============================================================================
-- 数据分析仪表板系统 - 业务数据库初始化脚本
-- Database: dad_business
-- Version: 1.0.0
-- MySQL Version: 8.0.35+
-- Author: Data Analysis Dashboard Team
-- Description: 核心业务表结构，包含仪表板、图表、数据源、任务等核心功能
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `dad_business`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `dad_business`;

-- ============================================================================
-- 1. 用户表 (users)
-- ============================================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `guid` VARCHAR(64) NOT NULL COMMENT '用户GUID（业务唯一标识）',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` ENUM('ACTIVE', 'INACTIVE', 'LOCKED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    `role` ENUM('ADMIN', 'USER', 'GUEST') DEFAULT 'USER' COMMENT '用户角色',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '部门',
    `position` VARCHAR(100) DEFAULT NULL COMMENT '职位',
    `last_login_at` TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间（软删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_guid` (`guid`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`),
    KEY `idx_department` (`department`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================================
-- 2. 数据源表 (data_sources)
-- ============================================================================
DROP TABLE IF EXISTS `data_sources`;
CREATE TABLE `data_sources` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `guid` VARCHAR(64) NOT NULL COMMENT '数据源GUID（业务唯一标识）',
    `name` VARCHAR(100) NOT NULL COMMENT '数据源名称',
    `description` TEXT DEFAULT NULL COMMENT '数据源描述',
    `source_type` ENUM('MYSQL', 'POSTGRESQL', 'ORACLE', 'SQLSERVER', 'MONGODB', 'REDIS', 'ELASTICSEARCH', 'KAFKA', 'API', 'EXCEL', 'CSV', 'JSON') NOT NULL COMMENT '数据源类型',
    `connection_config` JSON NOT NULL COMMENT '连接配置（host, port, username, database等）',
    `connection_params` JSON DEFAULT NULL COMMENT '连接参数（连接池配置、超时等）',
    `status` ENUM('ACTIVE', 'INACTIVE', 'ERROR', 'TESTING') DEFAULT 'ACTIVE' COMMENT '数据源状态',
    `is_tested` TINYINT(1) DEFAULT 0 COMMENT '是否已测试连接',
    `last_test_at` TIMESTAMP NULL DEFAULT NULL COMMENT '最后测试时间',
    `last_test_result` JSON DEFAULT NULL COMMENT '最后测试结果',
    `owner_id` BIGINT UNSIGNED NOT NULL COMMENT '所有者ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间（软删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_guid` (`guid`),
    KEY `idx_name` (`name`),
    KEY `idx_source_type` (`source_type`),
    KEY `idx_status` (`status`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_datasource_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源表';

-- ============================================================================
-- 3. 仪表板表 (dashboards)
-- ============================================================================
DROP TABLE IF EXISTS `dashboards`;
CREATE TABLE `dashboards` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `guid` VARCHAR(64) NOT NULL COMMENT '仪表板GUID（业务唯一标识）',
    `name` VARCHAR(100) NOT NULL COMMENT '仪表板名称',
    `description` TEXT DEFAULT NULL COMMENT '仪表板描述',
    `thumbnail` VARCHAR(512) DEFAULT NULL COMMENT '缩略图URL',
    `layout_config` JSON NOT NULL COMMENT '布局配置（grid layout）',
    `style_config` JSON DEFAULT NULL COMMENT '样式配置（背景、主题等）',
    `filter_config` JSON DEFAULT NULL COMMENT '筛选器配置',
    `refresh_interval` INT DEFAULT 0 COMMENT '自动刷新间隔（秒，0表示不刷新）',
    `is_public` TINYINT(1) DEFAULT 0 COMMENT '是否公开',
    `is_favorite` TINYINT(1) DEFAULT 0 COMMENT '是否收藏',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `tags` JSON DEFAULT NULL COMMENT '标签数组',
    `status` ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT' COMMENT '状态',
    `owner_id` BIGINT UNSIGNED NOT NULL COMMENT '所有者ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `published_at` TIMESTAMP NULL DEFAULT NULL COMMENT '发布时间',
    `deleted_at` TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间（软删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_guid` (`guid`),
    KEY `idx_name` (`name`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_is_public` (`is_public`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_published_at` (`published_at`),
    CONSTRAINT `fk_dashboard_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仪表板表';

-- ============================================================================
-- 4. 图表表 (charts)
-- ============================================================================
DROP TABLE IF EXISTS `charts`;
CREATE TABLE `charts` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `guid` VARCHAR(64) NOT NULL COMMENT '图表GUID（业务唯一标识）',
    `dashboard_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '所属仪表板ID（null表示独立图表）',
    `name` VARCHAR(100) NOT NULL COMMENT '图表名称',
    `description` TEXT DEFAULT NULL COMMENT '图表描述',
    `chart_type` ENUM('LINE', 'BAR', 'PIE', 'SCATTER', 'MAP', 'TABLE', 'CARD', 'GAUGE', 'FUNNEL', 'RADAR', 'HEATMAP', 'TREE', 'SANKEY', 'WORDCLOUD') NOT NULL COMMENT '图表类型',
    `data_source_id` BIGINT UNSIGNED NOT NULL COMMENT '数据源ID',
    `query_config` JSON NOT NULL COMMENT '查询配置（SQL、API路径等）',
    `chart_config` JSON NOT NULL COMMENT '图表配置（颜色、坐标轴、图例等）',
    `style_config` JSON DEFAULT NULL COMMENT '样式配置（尺寸、边框等）',
    `interaction_config` JSON DEFAULT NULL COMMENT '交互配置（钻取、联动等）',
    `data_cache_seconds` INT DEFAULT 300 COMMENT '数据缓存时长（秒）',
    `position_x` INT DEFAULT 0 COMMENT '在仪表板中的X坐标',
    `position_y` INT DEFAULT 0 COMMENT '在仪表板中的Y坐标',
    `width` INT DEFAULT 6 COMMENT '宽度（grid单位）',
    `height` INT DEFAULT 4 COMMENT '高度（grid单位）',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间（软删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_guid` (`guid`),
    KEY `idx_dashboard_id` (`dashboard_id`),
    KEY `idx_data_source_id` (`data_source_id`),
    KEY `idx_chart_type` (`chart_type`),
    KEY `idx_name` (`name`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_chart_dashboard` FOREIGN KEY (`dashboard_id`) REFERENCES `dashboards` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_chart_datasource` FOREIGN KEY (`data_source_id`) REFERENCES `data_sources` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图表表';

-- ============================================================================
-- 5. 任务表 (tasks)
-- ============================================================================
DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` VARCHAR(64) NOT NULL COMMENT '任务GUID（业务唯一标识）',
    `task_type` ENUM('EXPORT', 'IMPORT', 'DATA_REFRESH', 'DATA_SYNC', 'CHART_RENDER', 'REPORT_GENERATE') NOT NULL COMMENT '任务类型',
    `task_key` VARCHAR(128) DEFAULT NULL COMMENT '任务唯一键（用于幂等性判断）',
    `status` ENUM('PENDING', 'QUEUED', 'RUNNING', 'SUCCESS', 'FAILED', 'CANCELLED', 'TIMEOUT') DEFAULT 'PENDING' COMMENT '任务状态',
    `priority` TINYINT DEFAULT 5 COMMENT '优先级（1-10，10最高）',
    `progress` INT DEFAULT 0 COMMENT '进度百分比（0-100）',
    `total_steps` INT DEFAULT 0 COMMENT '总步骤数',
    `current_step` INT DEFAULT 0 COMMENT '当前步骤',
    `current_message` VARCHAR(512) DEFAULT NULL COMMENT '当前步骤消息',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `error_stack` TEXT DEFAULT NULL COMMENT '错误堆栈',
    `retry_count` INT DEFAULT 0 COMMENT '重试次数',
    `max_retries` INT DEFAULT 3 COMMENT '最大重试次数',
    `timeout_seconds` INT DEFAULT 3600 COMMENT '超时时间（秒）',
    `started_at` TIMESTAMP NULL DEFAULT NULL COMMENT '开始时间',
    `completed_at` TIMESTAMP NULL DEFAULT NULL COMMENT '完成时间',
    `result_data` JSON DEFAULT NULL COMMENT '任务结果数据',
    `input_params` JSON DEFAULT NULL COMMENT '任务输入参数',
    `created_by` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    UNIQUE KEY `uk_task_key` (`task_key`),
    KEY `idx_status` (`status`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_priority` (`priority`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_started_at` (`started_at`),
    CONSTRAINT `fk_task_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- ============================================================================
-- 6. 资源表 (resources)
-- ============================================================================
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `guid` VARCHAR(64) NOT NULL COMMENT '资源GUID（业务唯一标识）',
    `resource_type` ENUM('DASHBOARD', 'CHART', 'DATASOURCE', 'THEME', 'MAP', 'FILTER', 'CALCULATED_FIELD') NOT NULL COMMENT '资源类型',
    `resource_name` VARCHAR(255) NOT NULL COMMENT '资源名称',
    `resource_path` VARCHAR(512) DEFAULT NULL COMMENT '资源路径（层级结构）',
    `content` LONGTEXT NOT NULL COMMENT '资源内容（JSON格式）',
    `content_hash` VARCHAR(64) DEFAULT NULL COMMENT '内容哈希（SHA-256，用于幂等性）',
    `dependencies` JSON DEFAULT NULL COMMENT '依赖的资源GUID列表',
    `metadata` JSON DEFAULT NULL COMMENT '元数据（版本、标签等）',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `is_template` TINYINT(1) DEFAULT 0 COMMENT '是否为模板',
    `created_by` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间（软删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_guid` (`guid`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_resource_name` (`resource_name`),
    KEY `idx_content_hash` (`content_hash`),
    KEY `idx_is_template` (`is_template`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_resource_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

-- ============================================================================
-- 7. 导入导出记录表 (import_exports)
-- ============================================================================
DROP TABLE IF EXISTS `import_exports`;
CREATE TABLE `import_exports` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` VARCHAR(64) NOT NULL COMMENT '关联任务ID',
    `operation_type` ENUM('EXPORT', 'IMPORT') NOT NULL COMMENT '操作类型',
    `resource_type` ENUM('DASHBOARD', 'CHART', 'DATASOURCE', 'ALL') NOT NULL COMMENT '资源类型',
    `file_name` VARCHAR(255) DEFAULT NULL COMMENT '文件名',
    `file_path` VARCHAR(512) DEFAULT NULL COMMENT '文件存储路径',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    `file_format` ENUM('JSON', 'ZIP', 'EXCEL') DEFAULT 'ZIP' COMMENT '文件格式',
    `resource_count` INT DEFAULT 0 COMMENT '资源数量',
    `included_resources` JSON DEFAULT NULL COMMENT '包含的资源GUID列表',
    `conflicts` JSON DEFAULT NULL COMMENT '冲突列表',
    `resolution_strategy` ENUM('AUTO_RENAME', 'SKIP', 'OVERRIDE', 'USE_EXISTING', 'MANUAL') DEFAULT NULL COMMENT '冲突解决策略',
    `status` ENUM('PENDING', 'VALIDATING', 'PROCESSING', 'SUCCESS', 'FAILED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '状态',
    `progress` INT DEFAULT 0 COMMENT '进度百分比',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `summary` JSON DEFAULT NULL COMMENT '操作摘要（导入/导出统计）',
    `download_url` VARCHAR(512) DEFAULT NULL COMMENT '下载链接',
    `download_expires_at` TIMESTAMP NULL DEFAULT NULL COMMENT '下载链接过期时间',
    `created_by` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `completed_at` TIMESTAMP NULL DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_status` (`status`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_import_export_task` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`task_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_import_export_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入导出记录表';

-- ============================================================================
-- 8. AI分析报告表 (ai_analysis_reports)
-- ============================================================================
DROP TABLE IF EXISTS `ai_analysis_reports`;
CREATE TABLE `ai_analysis_reports` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `guid` VARCHAR(64) NOT NULL COMMENT '报告GUID（业务唯一标识）',
    `report_name` VARCHAR(255) NOT NULL COMMENT '报告名称',
    `dashboard_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联仪表板ID',
    `chart_ids` JSON DEFAULT NULL COMMENT '关联图表ID列表',
    `analysis_type` ENUM('TREND', 'ANOMALY', 'CORRELATION', 'FORECAST', 'INSIGHT', 'SUMMARY', 'CUSTOM') NOT NULL COMMENT '分析类型',
    `data_source_id` BIGINT UNSIGNED NOT NULL COMMENT '数据源ID',
    `query_config` JSON NOT NULL COMMENT '查询配置',
    `analysis_config` JSON DEFAULT NULL COMMENT '分析配置（算法参数等）',
    `prompt_template` TEXT DEFAULT NULL COMMENT 'AI提示词模板',
    `input_data` JSON DEFAULT NULL COMMENT '输入数据',
    `analysis_result` LONGTEXT DEFAULT NULL COMMENT '分析结果（JSON格式）',
    `natural_language_summary` TEXT DEFAULT NULL COMMENT '自然语言总结',
    `recommendations` JSON DEFAULT NULL COMMENT '建议列表',
    `confidence_score` DECIMAL(5,4) DEFAULT NULL COMMENT '置信度分数（0-1）',
    `model_version` VARCHAR(50) DEFAULT NULL COMMENT 'AI模型版本',
    `tokens_used` INT DEFAULT NULL COMMENT '使用的token数量',
    `processing_time_ms` BIGINT DEFAULT NULL COMMENT '处理时间（毫秒）',
    `status` ENUM('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'EXPIRED') DEFAULT 'PENDING' COMMENT '状态',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `is_cached` TINYINT(1) DEFAULT 0 COMMENT '是否使用缓存',
    `created_by` BIGINT UNSIGNED NOT NULL COMMENT '创建者ID',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `expired_at` TIMESTAMP NULL DEFAULT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_guid` (`guid`),
    KEY `idx_dashboard_id` (`dashboard_id`),
    KEY `idx_data_source_id` (`data_source_id`),
    KEY `idx_analysis_type` (`analysis_type`),
    KEY `idx_status` (`status`),
    KEY `idx_created_by` (`created_by`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_expired_at` (`expired_at`),
    CONSTRAINT `fk_ai_report_dashboard` FOREIGN KEY (`dashboard_id`) REFERENCES `dashboards` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_ai_report_datasource` FOREIGN KEY (`data_source_id`) REFERENCES `data_sources` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_ai_report_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI分析报告表';

-- ============================================================================
-- 初始化数据
-- ============================================================================

-- 插入默认管理员用户（密码：admin123，需要在应用层进行加密）
INSERT INTO `users` (`guid`, `username`, `email`, `password`, `nickname`, `role`, `status`) VALUES
('admin-001', 'admin', 'admin@dad.com', '.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 'ACTIVE'),
('user-001', 'demo', 'demo@dad.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '演示用户', 'USER', 'ACTIVE');


-- 1. 修改 users 表
ALTER TABLE `users` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '删除标记(0=正常,1=删除)';
UPDATE `users` SET `deleted` = 0 WHERE `deleted_at` IS NULL;
ALTER TABLE `users` DROP COLUMN `deleted_at`;

-- 2. 修改 data_sources 表
ALTER TABLE `data_sources` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '删除标记(0=正常,1=删除)';
UPDATE `data_sources` SET `deleted` = 0 WHERE `deleted_at` IS NULL;
ALTER TABLE `data_sources` DROP COLUMN `deleted_at`;

-- 3. 修改 dashboards 表
ALTER TABLE `dashboards` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '删除标记(0=正常,1=删除)';
UPDATE `dashboards` SET `deleted` = 0 WHERE `deleted_at` IS NULL;
ALTER TABLE `dashboards` DROP COLUMN `deleted_at`;

-- 4. 修改 charts 表
ALTER TABLE `charts` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '删除标记(0=正常,1=删除)';
UPDATE `charts` SET `deleted` = 0 WHERE `deleted_at` IS NULL;
ALTER TABLE `charts` DROP COLUMN `deleted_at`;
-- ============================================================================
-- 索引优化建议
-- ============================================================================
-- 1. 对于高并发查询场景，考虑添加复合索引：
--    - dashboards: (owner_id, status, created_at)
--    - tasks: (status, priority, created_at)
--    - charts: (dashboard_id, chart_type)
--
-- 2. 对于大表，考虑添加分区：
--    - tasks: 按created_at时间范围分区
--    - ai_analysis_reports: 按created_at时间范围分区
--
-- 3. 对于JSON字段，可以添加生成列和索引：
--    - dashboards.tags: 生成标签数组索引
--    - resources.dependencies: 生成依赖关系索引
-- ============================================================================

-- ============================================================================
-- 权限设置
-- ============================================================================
-- 创建应用数据库用户（根据实际情况修改）
-- CREATE USER IF NOT EXISTS 'dad_app'@'%' IDENTIFIED BY 'your_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON dad_business.* TO 'dad_app'@'%';
-- FLUSH PRIVILEGES;

-- ============================================================================
-- 备注说明
-- ============================================================================
-- 1. 所有表使用utf8mb4字符集，支持emoji等特殊字符
-- 2. 所有表使用InnoDB引擎，支持事务和外键
-- 3. 所有业务表使用GUID作为业务唯一标识，避免暴露数据库自增ID
-- 4. 所有表包含created_at和updated_at字段，便于审计和追踪
-- 5. 重要表支持软删除（deleted_at），便于数据恢复
-- 6. JSON字段用于存储灵活的配置信息，便于扩展
-- 7. 外键约束保证数据完整性，级联删除/更新确保数据一致性
-- 8. 索引优化查询性能，特别是在常用查询字段上
-- ============================================================================
