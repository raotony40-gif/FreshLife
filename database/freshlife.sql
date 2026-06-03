-- 乐鲜生活 MySQL 8 数据库脚本
-- 仅包含 MVP 阶段核心表：用户、商品、购物车、订单、订单明细。

CREATE DATABASE IF NOT EXISTS freshlife
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE freshlife;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT 'BCrypt加密后的密码',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  nickname VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
  avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态：1正常，0禁用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username),
  UNIQUE KEY uk_user_phone (phone),
  KEY idx_user_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

CREATE TABLE product (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  name VARCHAR(100) NOT NULL COMMENT '商品名称',
  category VARCHAR(50) NOT NULL COMMENT '商品分类',
  price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  stock INT NOT NULL DEFAULT 0 COMMENT '商品库存',
  image_url VARCHAR(255) DEFAULT NULL COMMENT '商品图片地址',
  description TEXT DEFAULT NULL COMMENT '商品描述',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态：1上架，0下架',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (id),
  KEY idx_product_category_status_deleted (category, status, deleted),
  KEY idx_product_name (name),
  KEY idx_product_status_stock (status, stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

CREATE TABLE cart (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (id),
  KEY idx_cart_user_product_deleted (user_id, product_id, deleted),
  KEY idx_cart_user_id (user_id),
  KEY idx_cart_product_id (product_id),
  CONSTRAINT fk_cart_user_id FOREIGN KEY (user_id) REFERENCES `user` (id),
  CONSTRAINT fk_cart_product_id FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';

CREATE TABLE orders (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
  delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '配送费',
  pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  status ENUM('WAIT_PAY', 'WAIT_DELIVERY', 'DELIVERING', 'FINISHED', 'CANCELLED') NOT NULL DEFAULT 'WAIT_PAY' COMMENT '订单状态',
  receiver_name VARCHAR(50) DEFAULT NULL COMMENT '收货人',
  receiver_phone VARCHAR(20) DEFAULT NULL COMMENT '收货电话',
  receiver_address VARCHAR(255) DEFAULT NULL COMMENT '收货地址',
  remark VARCHAR(255) DEFAULT NULL COMMENT '用户备注',
  pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
  finish_time DATETIME DEFAULT NULL COMMENT '完成时间',
  cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_orders_order_no (order_no),
  KEY idx_orders_user_id (user_id),
  KEY idx_orders_user_status (user_id, status),
  KEY idx_orders_status_create_time (status, create_time),
  CONSTRAINT fk_orders_user_id FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

CREATE TABLE order_item (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  product_name VARCHAR(100) NOT NULL COMMENT '下单时商品名称',
  product_image_url VARCHAR(255) DEFAULT NULL COMMENT '下单时商品图片',
  product_price DECIMAL(10,2) NOT NULL COMMENT '下单时商品单价',
  quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品小计',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (id),
  KEY idx_order_item_order_id (order_id),
  KEY idx_order_item_product_id (product_id),
  CONSTRAINT fk_order_item_order_id FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT fk_order_item_product_id FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';

INSERT INTO product
  (name, category, price, stock, image_url, description, status)
VALUES
  ('西红柿', '蔬菜', 4.90, 200, 'https://example.com/products/tomato.jpg', '新鲜西红柿，适合炒菜、煮汤和凉拌。', 1),
  ('鸡蛋', '蛋奶', 12.80, 300, 'https://example.com/products/egg.jpg', '新鲜鸡蛋，家庭日常烹饪必备。', 1),
  ('牛奶', '蛋奶', 6.90, 180, 'https://example.com/products/milk.jpg', '营养纯牛奶，早餐和日常补充都适合。', 1),
  ('苹果', '水果', 8.80, 160, 'https://example.com/products/apple.jpg', '脆甜多汁苹果，适合家庭日常水果补充。', 1),
  ('鸡胸肉', '肉禽', 15.90, 120, 'https://example.com/products/chicken-breast.jpg', '低脂鸡胸肉，适合煎、炒和健身餐。', 1);
