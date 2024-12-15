# 湫寻

## 项目介绍
湫寻是一个初级的移动端交友项目，可以根据标签爱好匹配同伴和同伴组队，提供用户队伍搜索、个人信息管理等功能，总体来说是一个不错的移动端练手项目。

![](https://github.com/QiuHaimengjing/ImageStorage/blob/main/repositories/qiu-xun/qiuxun01.jpg)

## 项目技术
### 后端
![JDK 1.8](https://img.shields.io/badge/JDK-1.8-007396?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.13-green?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9.9-C71A36?logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-blue)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)
![Redisson](https://img.shields.io/badge/Redisson-3.13.6-red)  
项目具有缓存预热功能并以`Redisson`实现分布式锁，自带`Knif4j`接口文档，提供了阿里云OSS用户头像上传接口。

### 前端
![Node.js](https://img.shields.io/badge/Node.js-%3E%3D18-339933?logo=nodedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-3.4.15-4FC08D?logo=vue.js&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5.0.11-646CFF?logo=vite&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?logo=axios&logoColor=white)  
采用`Vant UI`组件库，`Pinia`状态管理，`Vue Router`路由管理。

## 项目结构
```
qiu-xun
├── backend -- 后端
└── frontend -- 前端
```

## 项目启动
### 后端
1. 打开 idea 根据`pom.xml`使用 Maven 安装依赖；  
2. 创建数据库导入`backend/sql/data_dump.sql`并修改`application.yml`配置文件。

### 前端
进入`frontend`目录，使用`pnpm`安装依赖，执行`pnpm install`安装依赖，执行`pnpm run dev`启动项目。或者使用`npm`安装依赖，执行`npm install`安装依赖，执行`npm run dev`启动项目。

## 项目部署
可以参考我的另一个项目的[项目部署](https://github.com/QiuHaimengjing/qiu-user-center?tab=readme-ov-file#%E9%83%A8%E7%BD%B2)

## 🌟 支持项目
项目仅供学习参考，很多功能尚未完善，期待你的二次开发，如果你觉得这个项目对你有帮助，可以点个`star`支持一下我，谢谢🥰！