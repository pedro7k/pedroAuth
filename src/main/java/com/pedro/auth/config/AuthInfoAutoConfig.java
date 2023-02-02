package com.pedro.auth.config;

import com.pedro.auth.common.RoleRuleEnum;
import com.pedro.auth.common.RuleLevelEnum;
import com.pedro.auth.model.Rule;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.subject.impl.DefaultAuthSubject;
import com.pedro.auth.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.*;

/**
 * 权限配置解析
 */
@Configuration
public class AuthInfoAutoConfig implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(AuthInfoAutoConfig.class);

    /**
     * 权限配置组
     * key：path
     * value：RuleInfo
     */
    private Map<String, Rule> authRuleMap = new HashMap<>();

    /**
     * 默认权限
     */
    private String defaultAuthInfo;

    /**
     * 未认证跳转路径
     */
    private String noAuthPath;

    /**
     * 无权限跳转路径
     */
    private String noRolePath;

    /**
     * 读取配置文件
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {

        logger.info("[pedroAuth]配置文件读取");

        // 1.前缀
        String prefix = "pedro-auth.";
        String authLevelPrefix = "auth-level.";

        // 2.基本参数
        defaultAuthInfo = environment.getProperty(prefix + authLevelPrefix + "default");
        noAuthPath = environment.getProperty(prefix + "no-auth-path");
        noRolePath = environment.getProperty(prefix + "no-role-path");

        // 3.权限信息
        String ruleList = environment.getProperty(prefix + authLevelPrefix + "rule-list");
        assert ruleList != null;
        for (String ruleKey : ruleList.split(",")) {
            // 解析属性
            Map<String, Object> pathAuthInfoProps = PropertyUtil.handle(environment, prefix + authLevelPrefix + ruleKey, Map.class);
            loadRule(pathAuthInfoProps);
        }
    }

    /**
     * 加载单挑规则
     *
     * @param pathAuthInfoProps
     */
    private void loadRule(Map<String, Object> pathAuthInfoProps) {
        Rule rule = new Rule();
        // 路径
        Object path = pathAuthInfoProps.get("path");
        if (path == null || path.toString().isEmpty()) {
            throw new RuntimeException("[pedroAuth]配置文件错误");
        }
        rule.setPath(path.toString());
        // 级别
        Object level = pathAuthInfoProps.get("level");
        if (level == null || level.toString().isEmpty() || RuleLevelEnum.isRuleLevelLegal(level.toString())) {
            throw new RuntimeException("[pedroAuth]配置文件错误");
        }
        rule.setLevel(level.toString());

        if (rule.getLevel().equals(RuleLevelEnum.NEED_ROLE.getLevel())) {
            // 角色
            Object roles = pathAuthInfoProps.get("roles");
            if (roles == null || roles.toString().isEmpty() || RoleRuleEnum.isRoleRuleLegal(roles.toString())) {
                throw new RuntimeException("[pedroAuth]配置文件错误");
            }
            String roleString = roles.toString();
            List<String> roleSet = new ArrayList<>(Arrays.asList(roleString.trim().split(",")));
            rule.setRoles(roleSet);
            // 角色匹配规则
            Object roleRule = pathAuthInfoProps.get("roleRule");
            if (roleRule == null || roleRule.toString().isEmpty()) {
                rule.setRoleRule(RoleRuleEnum.NEED_ONE.getType());
            } else {
                rule.setRoleRule(roleRule.toString());
            }
        }

        // 加入map
        authRuleMap.put(rule.getPath(), rule);
    }

    public Map<String, Rule> getAuthRuleMap() {
        return authRuleMap;
    }

    public String getDefaultAuthInfo() {
        return defaultAuthInfo;
    }

    public String getNoAuthPath() {
        return noAuthPath;
    }

    public String getNoRolePath() {
        return noRolePath;
    }
}
