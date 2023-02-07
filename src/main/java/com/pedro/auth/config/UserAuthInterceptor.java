package com.pedro.auth.config;

import com.pedro.auth.common.enums.RuleLevelEnum;
import com.pedro.auth.context.UserAccessFunctionContext;
import com.pedro.auth.context.UserContextHolder;
import com.pedro.auth.model.Rule;
import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.util.CookieUtil;
import com.pedro.auth.util.RoleCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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

        // 0.无权限页
        if (request.getRequestURI().equals("/roleDenied.html")) {
            return true;
        }

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
                            // TODO 跳转到无权限页
                            redirect(request, response);
                            return false;
                        }
                    }
                }

                // 3.3 无论是配置文件没配置，还是配置了默认的，要求等级一定小于认证，所以一定可以通过
                return true;
            }
        }

        // 4.没拿到token或者缓存中找不到username信息：未认证
        if (null != rule && rule.getLevel().equals(RuleLevelEnum.NO_AUTH.getLevel())) {
            // 4.1 rule不是null，且是NO_AUTH，通过
            return true;
        } else if (rule == null
                && authInfoAutoConfig.getDefaultAuthInfo() != null
                && authInfoAutoConfig.getDefaultAuthInfo().equals(RuleLevelEnum.NO_AUTH.getLevel())) {
            // 4.2 有配置默认权限，且默认权限为NO_AUTH，通过
            return true;
        } else if (rule == null && authInfoAutoConfig.getDefaultAuthInfo() == null) {
            // 4.3 没有配置默认权限，通过
            return true;
        }

        // 5.未认证且权限校验不通过
        // TODO 跳转到未认证页（登陆页
        redirect(request, response);
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

            // 1.注销
            if (authSubject.isLogout()) {
                request.getSession().removeAttribute(TOKEN);
                CookieUtil.removeTokenCookie(response);
                // TODO  跳转到未认证页（登陆页
                redirect(request, response);
                return;
            }

            // 2.获取相关信息
            User user = authSubject.getUser();
            String sessionId = request.getSession().getId();

            // 3.如果当前浏览器并没有得到session, 设置相关数据
            if (request.getSession().getAttribute(TOKEN) == null) {
                // 2.1 将token到username存入缓存
                cache.put(sessionId, user.getUsername());
                // 2.2 将token存入session
                request.getSession().setAttribute(TOKEN, sessionId);
            }

            // 4.是否需要将token存入cookie，只在登陆时生效
            if (CookieUtil.getValue(request, TOKEN) == null && authSubject.rememberMe()) {
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

    /**
     * 重定向到无权限页
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取当前请求的路径
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        //如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理 否则直接重定向就可以了
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            // 告诉ajax本次拦截为重定向
            response.setHeader("REDIRECT", "REDIRECT");
            // 告诉ajax重定向路径
            response.setHeader("CONTENTPATH", basePath + "/roleDenied.html");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendRedirect(basePath + "/roleDenied.html");
        }
    }
}
