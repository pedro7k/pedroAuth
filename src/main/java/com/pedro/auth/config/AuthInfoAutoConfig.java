package com.pedro.auth.config;

import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.subject.impl.DefaultAuthSubject;
import com.pedro.auth.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限配置解析
 */
@Configuration
public class AuthInfoAutoConfig implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(AuthInfoAutoConfig.class);

    /**
     * 权限配置组
     */
    private Map<String, Map<String, Object>> authInfoMap = new HashMap<>();

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
        for (String path : ruleList.split(",")) {
            // 解析属性为Map，存入
            Map<String, Object> pathAuthInfoProps = PropertyUtil.handle(environment, prefix + authLevelPrefix + path, Map.class);
            authInfoMap.put(path, pathAuthInfoProps);
        }
    }
}
