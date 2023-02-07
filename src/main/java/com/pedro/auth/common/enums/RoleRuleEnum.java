package com.pedro.auth.common.enums;

/**
 * 角色匹配规则枚举
 */
public enum RoleRuleEnum {

    NEED_ALL("needAll"),
    NEED_ONE("needOne");

    private String type;

    RoleRuleEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * 校验合法性
     */
    public static boolean isRoleRuleLegal(String arg) {
        for (RoleRuleEnum value : RoleRuleEnum.values()) {
            if (value.getType().equals(arg)) {
                return true;
            }
        }

        return false;
    }
}
