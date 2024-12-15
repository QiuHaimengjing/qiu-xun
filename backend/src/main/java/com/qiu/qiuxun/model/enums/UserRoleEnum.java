package com.qiu.qiuxun.model.enums;

/**
 * @description: 用户角色枚举
 * @className: UserRoleEnum.java
 * @author: qiu
 * @createTime: 2024/3/17 13:15
 */
public enum UserRoleEnum {
    USER(0, "普通用户"),
    ADMIN(1, "管理员");

    private final Integer role;
    private final String description;

    UserRoleEnum(Integer role, String description) {
        this.role = role;
        this.description = description;
    }

    public Integer getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }
}
