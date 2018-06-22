# 1. XKP-V2.0-API

<!-- TOC -->

- [1. XKP-V2.0-API](#1-xkp-v20-api)
  - [1.1. tips](#11-tips)
  - [1.2. 学院管理](#12-学院管理)
    - [1.2.1. 添加学院](#121-添加学院)
    - [1.2.2. 查询学院](#122-查询学院)
    - [1.2.3. 查询学院名称](#123-查询学院名称)
    - [1.2.4. 删除学院](#124-删除学院)
  - [1.3. 管理员设置](#13-管理员设置)
    - [1.3.1. 添加管理员](#131-添加管理员)
    - [1.3.2. 查询管理员](#132-查询管理员)
    - [1.3.3. 重置密码](#133-重置密码)
    - [1.3.4. 修改密码](#134-修改密码)
    - [1.3.5. 删除管理员](#135-删除管理员)
  - [1.4. 登录设置](#14-登录设置)
    - [1.4.1. 登录](#141-登录)
    - [1.4.2. 登录控制](#142-登录控制)
  - [1.5. 数据管理](#15-数据管理)
    - [1.5.1. 添加年级](#151-添加年级)
    - [1.5.2. 查询年级](#152-查询年级)
    - [1.5.3. 删除年级](#153-删除年级)
    - [1.5.4. 开启新学期](#154-开启新学期)
  - [1.6. 大表查询](#16-大表查询)
    - [1.6.1. 查看大表](#161-查看大表)
    - [1.6.2. 下载 Word 大表](#162-下载-word-大表)
    - [1.6.3. 下载 Excel 大表](#163-下载-excel-大表)
  - [1.7. 专业管理](#17-专业管理)
    - [1.7.1. 添加专业](#171-添加专业)
    - [1.7.2. 查询专业](#172-查询专业)
    - [1.7.3. 查询专业名称](#173-查询专业名称)
    - [1.7.4. 删除专业](#174-删除专业)
  - [1.8. 班级管理](#18-班级管理)
    - [1.8.1. 添加班级](#181-添加班级)
    - [1.8.2. 查询班级](#182-查询班级)
    - [1.8.3. 查询班级名称](#183-查询班级名称)
    - [1.8.4. 删除班级](#184-删除班级)
  - [1.9. 班级成员](#19-班级成员)
    - [1.9.1. 添加学生](#191-添加学生)
    - [1.9.2. 查询学生](#192-查询学生)
    - [1.9.3. 查询学生的学院专业班级](#193-查询学生的学院专业班级)
    - [1.9.4. 删除学生](#194-删除学生)
  - [1.10. 基本加分](#110-基本加分)
    - [1.10.1. 查询基本加分](#1101-查询基本加分)
    - [1.10.2. 修改基本加分](#1102-修改基本加分)
  - [1.11. 课程管理](#111-课程管理)
    - [1.11.1. 添加课程](#1111-添加课程)
    - [1.11.2. 查询课程](#1112-查询课程)
    - [1.11.3. 删除课程](#1113-删除课程)
  - [1.12. 成绩录入](#112-成绩录入)
    - [1.12.1. 添加成绩](#1121-添加成绩)
    - [1.12.2. 查询成绩](#1122-查询成绩)
    - [1.12.3. 修改成绩](#1123-修改成绩)

<!-- /TOC -->

## 1.1. tips

- 单用户登录
- 德育,智育,综合,班级,专业排名
- code :
  - 0 : 一切正常
  - 1 : 直接将 message 展示给用户
  - 2 : 验证用户无效,跳转至登录页

---

## 1.2. 学院管理

### 1.2.1. 添加学院

- POST /xkp/academy
- payload :

```json
{
    "name": "信息"
}
```

- return:
  - data : 学院 id

```json
{
    "code": 0,
    "message": "",
    "data": 23
}
```

---

### 1.2.2. 查询学院

- GET /xkp/academy
- return :
  - id : 学院的唯一键, 序号由前端生成

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "id": 23,
            "name": "信息"
        },
        {
            "id": 24,
            "name": "文法"
        }
    ]
}
```

---

### 1.2.3. 查询学院名称

- GET /xkp/academy/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": "信息学院"
}
```

---

### 1.2.4. 删除学院

- DELETE /xkp/academy/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.3. 管理员设置

### 1.3.1. 添加管理员

- POST /xkp/manager
- payload :

```json
{
    "username": "xxxykpzx",
    "academyId": 2,
    "specialtyId": 2,
    "classId": 2,
    "grade": "2015",
    "type": "C"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": {
        "username": "xxxykpzx",
        "password": "qazwsxed"
    }
}
```

---

### 1.3.2. 查询管理员

- GET /xkp/manager?
  - academyId : 学院 id (必须)
  - specialtyId : 专业 id
  - classId : 班级 id
  - grade : 年级
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "systemId": 23,
            "username": "xxxy"
        },
        {
            "systemId": 24,
            "username": "wexy"
        }
    ]
}
```

---

### 1.3.3. 重置密码

- PUT /xkp/manager/reset/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": {
        "username": "xxxykpzx",
        "password": "qazwsxed"
    }
}
```

---

### 1.3.4. 修改密码

- PUT /xkp/manager/change
- payload :

```json
{
    "systemId": 2,
    "oldPassword": "asdfg",
    "newPassword": "asdfg"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

---

### 1.3.5. 删除管理员

- DELETE /xkp/manager/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.4. 登录设置

### 1.4.1. 登录

- POST /xkp/login
- payload :

```json
{
    "username": "2015111363",
    "password": "123456789"
}
```

- return :
  - type : A 管理员 | B 学院 | C 学生

```json
{
    "code": 0,
    "message": "",
    "data": {
        "systemId": 12,
        "token": "asdfgh",
        "type": "C",
        "grade": "2017",
        "academyId": 12,
        "specialtyId": 3,
        "classId": 13,
    }
}
```

---

### 1.4.2. 登录控制

- PUT /xkp/login
- payload :
  - academyId : 学院 id
  - grade : 年级
  - enable : true 允许登录 | false 禁止登录

```json
{
    "academyIds": [
        12,
        32
    ],
    "grades": [
        "2014",
        "2015"
    ],
    "status": true
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.5. 数据管理

### 1.5.1. 添加年级

- POST /xkp/grade
- payload :

```json
{
    "grade": "2015"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": "2015"
}
```

---

### 1.5.2. 查询年级

- GET /xkp/grade
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        "2015",
        "2016"
    ]
}
```

---

### 1.5.3. 删除年级

- DELETE /xkp/grade/{grade}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

### 1.5.4. 开启新学期

- GET /xkp/data
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.6. 大表查询

### 1.6.1. 查看大表

- GET /xkp/benchmark/{classId}
  - classId : 班级 id
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "studentNumber": 2017111363,
            "name": "zhang",
            "marks": [
                {
                    "courseId": 1234,
                    "type": true,
                    "examination": 70,
                    "inspection": null
                }
            ],
            "academic": 3.5,
            "point": 2.333,
            "behavior": "优",
            "moral": 4.000,
            "activity": 5.000,
            "other": 2.750,
            "dutyDesc": "班长",
            "score": 71.4830,
            "total": 82.5500,
            "complexRank": 1,
            "scoreRank": 2
        }
    ]
}
```

---

### 1.6.2. 下载 Word 大表

- GET /xkp/benchmark/download-docx/{classId}

---

### 1.6.3. 下载 Excel 大表

- GET /xkp/benchmark/download-xlsx/{classId}

---

## 1.7. 专业管理

### 1.7.1. 添加专业

- POST /xkp/specialty
- payload :

```json
{
    "academyId": 213,
    "name": "电气自动化"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": 23
}
```

---

### 1.7.2. 查询专业

- GET /xkp/specialty/{academyId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "systemId": 23,
            "name": "电气自动化"
        },
        {
            "systemId": 24,
            "name": "机械电子"
        }
    ]
}
```

---

### 1.7.3. 查询专业名称

- GET /xkp/specialty-name/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": "机械电子"
}
```

---

### 1.7.4. 删除专业

- DELETE /xkp/specialty/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.8. 班级管理

### 1.8.1. 添加班级

- POST /xkp/class
- payload :

```json
{
    "specialtyId": 2345,
    "grade": "2014",
    "name": "一班"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": 23
}
```

---

### 1.8.2. 查询班级

- GET /xkp/class?
  - specialtyId : 专业 id
  - grade : 年级
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "systemId": 23,
            "name": "一班"
        },
        {
            "systemId": 24,
            "name": "二班"
        }
    ]
}
```

---

### 1.8.3. 查询班级名称

- GET /xkp/class/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": "一班"
}
```

---

### 1.8.4. 删除班级

- DELETE /xkp/class/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.9. 班级成员

### 1.9.1. 添加学生

- POST /xkp/student
- paylaod :

```json
{
    "classId": 22,
    "studentNumber": 2015113633,
    "name": "zhang"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": 123456
}
```

---

### 1.9.2. 查询学生

- GET /xkp/student/{classId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "systemId": 123,
            "name": "zhang",
            "studentNumber": "2015113633"
        }
    ]
}
```

### 1.9.3. 查询学生的学院专业班级

- GET /xkp/student?
  - studentNumber : 学生学号
- return :

```json
{
    "code": 0,
    "message": "",
    "data": "信息学院-信息安全-二班"
}
```

---

### 1.9.4. 删除学生

- DELETE /xkp/student/{systemId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.10. 基本加分

### 1.10.1. 查询基本加分

- GET /xkp/base-score/{classId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "systemId": 231,
            "studentNumber": "2015111363",
            "name": "张三",
            "moral": 20.3,
            "activity": 23.5,
            "duty": 12.0,
            "academic": 30.0,
            "behavior": "优",
            "academicDesc": "论文一篇",
            "dutyDesc": "班长"
        }
    ]
}
```

---

### 1.10.2. 修改基本加分

- PUT /xkp/base-score
- payload :
  - moral : 德育
  - activity : 文体
  - duty : 职务
  - academic : 学术
  - behavior : 操行评等 优 | 良 | 中 | 差

```json
{
    "systemId": 231,
    "moral": 20.3,
    "activity": 23.5,
    "duty": 12.0,
    "academic": 30.0,
    "behavior": "优",
    "academicDesc": "论文一篇",
    "dutyDesc": "班长"
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.11. 课程管理

### 1.11.1. 添加课程

- POST /xkp/course
- payload :
  - credit : 学分
  - type : true 考试 | false 考察

```json
{
    "classId": 1234,
    "name": "高数",
    "credit": 4.5,
    "type": true
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": 123456
}
```

---

### 1.11.2. 查询课程

- GET /xkp/course/{classId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "systemId": 12,
            "courseId": 1234,
            "name": "高数",
            "credit": 4.5,
            "type": true
        }
    ]
}
```

---

### 1.11.3. 删除课程

- DELETE /xkp/course/{courseId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

## 1.12. 成绩录入

### 1.12.1. 添加成绩

- POST /xkp/score
- payload :
  - studentId : 学生 id
  - courseId : 课程 id
  - type : true 考试 | false 考察
  - examination : 分数 (考试)
  - inspection  : 优秀 | 良好 | 中等 | 及格 | 不及格 (考察)

```json
{
    "studentId": 231,
    "marks": [
        {
            "courseId": 1234,
            "type": true,
            "examination": 70,
            "inspection": null
        }
    ]
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---

### 1.12.2. 查询成绩

- GET /xkp/score/{classId}
- return :

```json
{
    "code": 0,
    "message": "",
    "data": [
        {
            "studentId": 231,
            "studentNumber": "2015111363",
            "name": "张三",
            "marks": [
                {
                    "courseId": 1234,
                    "type": true,
                    "examination": 70,
                    "inspection": null
                }
            ]
        }
    ]
}
```

---

### 1.12.3. 修改成绩

- PUT /xkp/score
- payload :
  - studentId : 学生 id
  - courseId : 课程 id
  - type : true 考试 | false 考察
  - examination : 分数 (考试)
  - inspection  : 优秀 | 良好 | 中等 | 及格 | 不及格 (考察)

```json
{
    "studentId": 231,
    "marks": [
        {
            "courseId": 1234,
            "type": true,
            "examination": 70,
            "inspection": null
        }
    ]
}
```

- return :

```json
{
    "code": 0,
    "message": "",
    "data": true
}
```

---