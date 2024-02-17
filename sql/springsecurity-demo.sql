/* 表结构与shiro-demo中的一致，只不过这里不需要字段：sys_user.salt、sys_user.hash_iterations */

CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(50) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `enabled` int(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用；0-禁用',
  `salt` varchar(100) NOT NULL DEFAULT '' COMMENT '密码盐',
  `hash_iterations` int(5) NOT NULL DEFAULT 0 COMMENT '密码hash次数',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_username`(`username`) USING BTREE,
  INDEX `idx_enabled`(`enabled`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

/* 密码均是123456 */
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$pzLFUlz0ciz7X7PPWfQnVe/K3XIKFc23prIqPtnbByUQJIh99p6m2', 1, '', 0, '', '', '2020-05-03 16:38:31', '2020-09-05 10:43:31');
INSERT INTO `sys_user` VALUES (2, 'test', '$2a$10$pzLFUlz0ciz7X7PPWfQnVe/K3XIKFc23prIqPtnbByUQJIh99p6m2', 1, '', 0, '', '', '2020-05-03 16:38:31', '2020-09-05 10:43:31');

CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(50) NOT NULL DEFAULT '' COMMENT '标题，用于角色展示',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称，用于角色判断',
  `level` int(1) NOT NULL DEFAULT 1 COMMENT '角色级别，这里分3级，高级别的角色除了有当前角色的权限，还有低级别的所有角色的权限',
  `description` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_title`(`title`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表';

INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 3, '', '', '', '2018-11-23 11:04:37', '2020-08-06 16:10:24');
INSERT INTO `sys_role` VALUES (2, '开发人员', 'developer', 2, '', '', '', '2018-11-23 13:09:06', '2020-09-05 10:45:12');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'normal', 1, '', '', '', '2021-09-02 08:04:08', '2021-09-02 08:20:49');
INSERT INTO `sys_role` VALUES (4, '人才管理部', 'rencai', 1, '', '', '', '2021-09-02 08:04:08', '2021-09-02 08:20:49');

CREATE TABLE `sys_users_roles` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `idx_roleId`(`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表';

INSERT INTO `sys_users_roles` VALUES (1, 1);
INSERT INTO `sys_users_roles` VALUES (2, 3);

CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pid` bigint(20) NOT NULL DEFAULT 0 COMMENT '上级菜单ID，0-无父级菜单',
  `title` varchar(50) NOT NULL DEFAULT '' COMMENT '菜单标题',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '路由名称，前端菜单显示的依据',
  `sort` int(5) NOT NULL DEFAULT 0 COMMENT '排序',
  `permission` varchar(50) NOT NULL DEFAULT '' COMMENT '权限，前端按钮显示的依据',
  `create_by` varchar(50) NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by` varchar(50) NOT NULL DEFAULT '' COMMENT '更新者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pid`(`pid`) USING BTREE,
  UNIQUE INDEX `uniq_title`(`title`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表';

INSERT INTO `sys_menu` VALUES (2, 1, '用户管理', 'User', 2, 'user:list', '', '', '2018-12-18 15:14:44', '2018-12-18 15:14:44');
INSERT INTO `sys_menu` VALUES (44, 2, '用户新增', '', 2, 'user:add', '', '', '2019-10-29 10:59:46', '2019-10-29 10:59:46');
INSERT INTO `sys_menu` VALUES (45, 2, '用户编辑', '', 3, 'user:edit', '', '', '2019-10-29 11:00:08', '2019-10-29 11:00:08');
INSERT INTO `sys_menu` VALUES (46, 2, '用户删除', '', 4, 'user:del', '', '', '2019-10-29 11:00:23', '2019-10-29 11:00:23');

CREATE TABLE `sys_roles_menus` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关联表';

INSERT INTO `sys_roles_menus` VALUES (3, 2);
INSERT INTO `sys_roles_menus` VALUES (4, 2);
INSERT INTO `sys_roles_menus` VALUES (4, 44);
INSERT INTO `sys_roles_menus` VALUES (4, 45);
INSERT INTO `sys_roles_menus` VALUES (4, 46);
