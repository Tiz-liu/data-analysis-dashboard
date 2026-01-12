-- ============================================================================
-- XXL-Job 调度中心数据库初始化脚本
-- Database: dad_xxl_job
-- Version: 2.4.0
-- MySQL Version: 8.0.35+
-- Source: https://github.com/xuxueli/xxl-job
-- Description: XXL-Job分布式任务调度平台的数据库表结构
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `dad_xxl_job`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `dad_xxl_job`;

-- ============================================================================
-- 1. 调度信息表 (xxl_job_info)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `job_group` INT NOT NULL COMMENT '执行器主键ID',
    `job_desc` VARCHAR(255) NOT NULL COMMENT '任务名称',
    `author` VARCHAR(64) DEFAULT NULL COMMENT '作者',
    `alarm_email` VARCHAR(255) DEFAULT NULL COMMENT '报警邮件',
    `schedule_type` VARCHAR(50) NOT NULL DEFAULT 'NONE' COMMENT '调度类型：NONE-不调度,CRON-cron脚本,FIX_RATE-固定频率,FIX_DELAY-固定延迟',
    `schedule_conf` VARCHAR(128) DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    `misfire_strategy` VARCHAR(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '过期处理策略：DO_NOTHING-什么都不做,ONCE-执行一次',
    `executor_route_strategy` VARCHAR(50) NOT NULL DEFAULT 'FIRST' COMMENT '执行器路由策略：FIRST-第一个,LAST-最后一个,ROUND-轮询,RANDOM-随机,CONSISTENT_HASH-一致性哈希,LEAST_FREQUENTLY_USED-最不经常使用,LEAST_RECENTLY_USED-最近最少使用,FAILOVER-故障转移,BUSYOVER-忙碌转移,SHARDING_BROADCAST-分片广播',
    `executor_handler` VARCHAR(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param` VARCHAR(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy` VARCHAR(50) NOT NULL DEFAULT 'SERIAL_EXECUTION' COMMENT '阻塞处理策略：SERIAL_EXECUTION-单机串行,DISCARD_LATER-丢弃后续,COVER_EARLY-覆盖之前',
    `executor_timeout` INT NOT NULL DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` INT NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    `glue_type` VARCHAR(50) NOT NULL COMMENT 'GLUE类型：BEAN-使用bean模式,GLUE_GROOVY-Groovy脚本,GLUE_SHELL-Shell脚本,GLUE_PYTHON-Python脚本,GLUE_PHP-PHP脚本',
    `glue_source` MEDIUMTEXT DEFAULT NULL COMMENT 'GLUE源代码',
    `glue_remark` VARCHAR(255) DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime` DATETIME DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid` VARCHAR(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    `trigger_status` TINYINT NOT NULL DEFAULT 0 COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time` BIGINT NOT NULL DEFAULT 0 COMMENT '上次调度时间',
    `trigger_next_time` BIGINT NOT NULL DEFAULT 0 COMMENT '下次调度时间',
    PRIMARY KEY (`id`),
    KEY `idx_trigger_status` (`trigger_status`),
    KEY `idx_job_group` (`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调度信息表';

-- ============================================================================
-- 2. 调度日志表 (xxl_job_log)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE `xxl_job_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `job_group` INT NOT NULL COMMENT '执行器主键ID',
    `job_id` INT NOT NULL COMMENT '任务，主键ID',
    `executor_address` VARCHAR(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler` VARCHAR(255) DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param` VARCHAR(512) DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param` VARCHAR(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` INT NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    `trigger_time` DATETIME NOT NULL COMMENT '调度-时间',
    `trigger_code` INT NOT NULL COMMENT '调度-结果',
    `trigger_msg` VARCHAR(1024) DEFAULT NULL COMMENT '调度-日志',
    `handle_time` DATETIME DEFAULT NULL COMMENT '执行-时间',
    `handle_code` INT NOT NULL COMMENT '执行-状态',
    `handle_msg` VARCHAR(1024) DEFAULT NULL COMMENT '执行-日志',
    `alarm_status` TINYINT NOT NULL DEFAULT 0 COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`),
    KEY `idx_trigger_time` (`trigger_time`),
    KEY `idx_handle_time` (`handle_time`),
    KEY `idx_job_group` (`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调度日志表';

-- ============================================================================
-- 3. 调度日志报表表 (xxl_job_log_report)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_log_report`;
CREATE TABLE `xxl_job_log_report` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `trigger_date` DATETIME NOT NULL COMMENT '调度-时间',
    `running_count` INT NOT NULL DEFAULT 0 COMMENT '运行中-日志数量',
    `suc_count` INT NOT NULL DEFAULT 0 COMMENT '执行成功-日志数量',
    `fail_count` INT NOT NULL DEFAULT 0 COMMENT '执行失败-日志数量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_trigger_date` (`trigger_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调度日志报表表';

-- ============================================================================
-- 4. 执行器表 (xxl_job_registry)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_registry`;
CREATE TABLE `xxl_job_registry` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `registry_group` VARCHAR(50) NOT NULL COMMENT '执行器组名',
    `registry_key` VARCHAR(255) NOT NULL COMMENT '执行器唯一标识',
    `registry_value` VARCHAR(255) NOT NULL COMMENT '执行器地址',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_g_k_v` (`registry_group`, `registry_key`, `registry_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='执行器表';

-- ============================================================================
-- 5. 执行器组表 (xxl_job_group)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE `xxl_job_group` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `app_name` VARCHAR(64) NOT NULL COMMENT '执行器AppName',
    `title` VARCHAR(64) NOT NULL COMMENT '执行器名称',
    `address_type` TINYINT NOT NULL DEFAULT 0 COMMENT '执行器地址类型：0=自动注册，1=手动录入',
    `address_list` VARCHAR(512) DEFAULT NULL COMMENT '执行器地址列表，多地址逗号分隔',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='执行器组表';

INSERT INTO `xxl_job_group` (`id`, `app_name`, `title`, `address_type`, `address_list`, `update_time`) VALUES
(1, 'dad-executor', '数据分析仪表板执行器', 0, NULL, NOW());

-- ============================================================================
-- 6. 用户表 (xxl_job_user)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '账号',
    `password` VARCHAR(64) NOT NULL COMMENT '密码',
    `role` TINYINT NOT NULL COMMENT '角色：0-普通用户，1-管理员',
    `permission` VARCHAR(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分隔',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化管理员账号（密码：123456）
INSERT INTO `xxl_job_user` (`username`, `password`, `role`, `permission`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);

-- ============================================================================
-- 7. 锁表 (xxl_job_lock)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_lock`;
CREATE TABLE `xxl_job_lock` (
    `lock_name` VARCHAR(50) NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='锁表';

INSERT INTO `xxl_job_lock` (`lock_name`) VALUES ('schedule_lock');

-- ============================================================================
-- 8. 访问令牌表 (xxl_job_access_token)
-- ============================================================================
DROP TABLE IF EXISTS `xxl_job_access_token`;
CREATE TABLE `xxl_job_access_token` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `token` VARCHAR(255) NOT NULL COMMENT '访问令牌',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问令牌表';

-- 初始化默认访问令牌（请在生产环境中修改）
INSERT INTO `xxl_job_access_token` (`token`) VALUES ('xxl-job-default-token');

-- ============================================================================
-- 权限设置
-- ============================================================================
-- 创建XXL-Job专用数据库用户（根据实际情况修改）
-- CREATE USER IF NOT EXISTS 'xxl_job'@'%' IDENTIFIED BY 'your_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON dad_xxl_job.* TO 'xxl_job'@'%';
-- FLUSH PRIVILEGES;

-- ============================================================================
-- 备注说明
-- ============================================================================
-- 1. XXL-Job数据库与业务数据库分离，便于独立管理和维护
-- 2. 所有表使用utf8mb4字符集，支持特殊字符
-- 3. xxl_job_log表数据量较大，建议定期清理历史数据或进行归档
-- 4. xxl_job_registry表存储在线执行器信息，通过心跳自动更新
-- 5. xxl_job_lock表用于集群环境下的调度任务分布式锁
-- 6. 默认管理员账号：admin / 123456（首次登录后请及时修改）
-- 7. 默认访问令牌：xxl-job-default-token（生产环境请使用复杂令牌）
-- 8. 执行器组已预置一个示例组，可根据需要添加更多执行器组
-- ============================================================================
