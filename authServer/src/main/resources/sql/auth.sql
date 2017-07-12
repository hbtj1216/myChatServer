-- 创建数据库
CREATE DATABASE myChat;

-- 使用数据库
USE myChat;

-- 创建用户表t_user
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
  userId VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  nickName VARCHAR(100) NOT NULL,
  PRIMARY KEY(userId),
  UNIQUE(nickName)
) ENGINE = InnoDB CHARSET=utf8;


