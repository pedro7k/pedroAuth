package com.pedro.auth.config;

import com.pedro.auth.annotation.MethodAuth;
import com.pedro.auth.common.enums.RuleLevelEnum;
import com.pedro.auth.context.UserAccessFunctionContext;
import com.pedro.auth.context.UserContextHolder;
import com.pedro.auth.model.Rule;
import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.util.CookieUtil;
import com.pedro.auth.util.HTTPUtil;
import com.pedro.auth.util.RoleCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求拦截器
 */
@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthInterceptor.class);

    private static final String TOKEN = "token";

    @Resource
    private AuthInfoAutoConfig authInfoAutoConfig;

    /**
     * 用户信息缓存
     * key: token
     * value: username
     */
    public ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    /**
     * 前置拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.info("前置拦截");

        String token = null;
        // 1.尝试从session中获得username
        Object sessionObject = request.getSession().getAttribute(TOKEN);
        if (sessionObject != null) {
            token = (String) sessionObject;
        }

        // 2.session中没有，尝试从Cookie中获取
        if (token == null) {
            token = CookieUtil.getValue(request, TOKEN);
        }

        String requestURI = request.getRequestURI();
        Rule rule = authInfoAutoConfig.getAuthRuleMap().get(requestURI);
        // 3.从session或cookie中获得了token，并且可以在缓存中找到username，已认证
        if (token != null) {
            if (cache.containsKey(token)) {
                // 3.1 已认证，读取用户数据，设置到ThreadLocal当中去
                String username = cache.get(token);
                // 如果没登陆过，cache中不可能有值，所以一定能拿到一个Function
                User user = UserAccessFunctionContext.getUserAccessFunction().getUserInfo(username);
                UserContextHolder.getUserContext().setUser(user);
                // 3.2 权限验证
                if (null != rule) {
                    // 3.2.1 从配置文件能拿到Rule
                    if (rule.getLevel().equals(RuleLevelEnum.NO_AUTH.getLevel()) || rule.getLevel().equals(RuleLevelEnum.NEED_AUTH.getLevel())) {
                        // 3.2.1.1 无需认证或只需认证，直接通过
                        return true;
                    } else {
                        // 3.2.1.2 校验权限
                        boolean roleCheckResult = RoleCheckUtil.checkRole(user.getRoleList(), rule.getRoles(), rule.getRoleRule());
                        if (!roleCheckResult) {
                            HTTPUtil.redirect(request, response, authInfoAutoConfig.getNoRolePath());
                            return false;
                        }
                    }
                }
                // 3.3 无论是配置文件没配置，还是配置了默认的，要求等级一定小于认证，所以一定可以通过
                return true;
            }
        }

        boolean withAnnotation = false;
        if (handler instanceof HandlerMethod){
            HandlerMethod method = (HandlerMethod) handler;
            withAnnotation = method.getMethodAnnotation(MethodAuth.class) != null;
        }
        // 4.没拿到token或者缓存中找不到username信息：未认证
        if (null != rule && rule.getLevel().equals(RuleLevelEnum.NO_AUTH.getLevel())) {
            // 4.1 rule不是null，且是NO_AUTH，通过
            return true;
        } else if (withAnnotation) {
            // 4.2 虽然配置文件中没有配置，但有注解控制本方法权限，通过
            return true;
        } else if (rule == null
                && authInfoAutoConfig.getDefaultAuthInfo() != null
                && authInfoAutoConfig.getDefaultAuthInfo().equals(RuleLevelEnum.NO_AUTH.getLevel())) {
            // 4.3 有配置默认权限，且默认权限为NO_AUTH，通过
            return true;
        } else if (rule == null && authInfoAutoConfig.getDefaultAuthInfo() == null) {
            // 4.4 没有配置默认权限，通过
            return true;
        }

        // 5.未认证且权限校验不通过
        HTTPUtil.redirect(request, response, authInfoAutoConfig.getNoAuthPath());
        return false;
    }

    /**
     * 后置拦截
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        AuthSubject authSubject = UserContextHolder.getUserContext();

        // 如果已被认证，发生在刚刚得到登录，或者之前就已经有session/cookie时
        if (authSubject.beAuthed()) {

            // 1.获取相关信息
            User user = authSubject.getUser();
            String sessionId = request.getSession().getId();

            // 2.如果当前浏览器并没有得到session, 设置相关数据
            if (request.getSession().getAttribute(TOKEN) == null || authSubject.isLoginReq()) {
                // 2.1 将token到username存入缓存
                cache.put(sessionId, user.getUsername());
                // 2.2 将token存入session
                request.getSession().setAttribute(TOKEN, sessionId);
            }

            // 3.是否需要将token存入cookie，只在登陆时生效
            if ((CookieUtil.getValue(request, TOKEN) == null || authSubject.isLoginReq()) && authSubject.rememberMe()) {
                CookieUtil.setTokenCookie(response, sessionId);
            }
        }
    }

    /**
     * 返回处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 清空
        UserContextHolder.clearUserContext();
    }


}
