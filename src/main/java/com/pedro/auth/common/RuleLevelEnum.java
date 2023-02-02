package com.pedro.auth.common;

/**
 * 规则等级枚举
 */
public enum RuleLevelEnum {

    NO_AUTH("noAuth"),
    NEED_AUTH("needAuth"),
    NEED_ROLE("needRule");

    private String level;

    RuleLevelEnum(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    /**
     * 校验合法性
     */
    public static boolean isRuleLevelLegal(String arg) {

        for (RuleLevelEnum value : RuleLevelEnum.values()) {
            if (value.getLevel().equals(arg)) {
                return true;
            }
        }

        return false;
    }
}
