package com.pedro.auth.config;

import com.pedro.auth.util.PropertyUtil;
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

    @Bean
    public EnvironmentTestVO environmentTestVO() {
        return new EnvironmentTestVO(noAuthPath);
    }

    /**
     * 读取配置文件
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        // TODO 配置文件不能有反斜杠，这里需要相应的适应和处理

        // 1.前缀
        String prefix = "pedro-auth.";
        String authLevelPrefix = "auth-level.";

        // 2.基本参数
        defaultAuthInfo = environment.getProperty(prefix + authLevelPrefix + "default");
        noAuthPath = environment.getProperty(prefix + "no-auth-path");
        noRolePath = environment.getProperty(prefix + "no-role-path");

        // 3.权限信息
        String pathList = environment.getProperty(prefix + authLevelPrefix + "path-list");
        assert pathList != null;
        for (String path : pathList.split(",")) {
            // 解析属性为Map，存入
            Map<String, Object> pathAuthInfoProps = PropertyUtil.handle(environment, prefix + authLevelPrefix + path, Map.class);
            authInfoMap.put(path, pathAuthInfoProps);
        }
    }
}
