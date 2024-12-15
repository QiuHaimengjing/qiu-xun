package com.qiu.qiuxun.model.enums;

import java.util.Objects;

/**
 * @description: 队伍状态枚举
 * @className: TeamStatus.java
 * @author: qiu
 * @createTime: 2024/3/1 11:54
 */
public enum TeamStatusEnum {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密")
    ;

    private final Integer value;
    private final String text;

    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (Objects.equals(teamStatusEnum.getValue(), value)) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
