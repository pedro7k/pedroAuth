package com.pedro.auth.model;

import java.util.List;
import java.util.Set;

/**
 * 配置文件规则映射
 */
public class Rule {

    /**
     * 路径
     */
    private String path;

    /**
     * 规则等级
     */
    private String level;

    /**
     * 所需角色
     */
    private List<String> roles;

    /**
     * 角色匹配规则
     */
    private String roleRule;

    public Rule() {
    }

    public Rule(String path, String level) {
        this.path = path;
        this.level = level;
    }

    public Rule(String path, String level, List<String> roles, String roleRule) {
        this.path = path;
        this.level = level;
        this.roles = roles;
        this.roleRule = roleRule;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getRoleRule() {
        return roleRule;
    }

    public void setRoleRule(String roleRule) {
        this.roleRule = roleRule;
    }
}
