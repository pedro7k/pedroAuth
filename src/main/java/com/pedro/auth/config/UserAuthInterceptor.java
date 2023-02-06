package com.pedro.auth.config;

import com.pedro.auth.common.EncryptionEnum;
import com.pedro.auth.common.RuleLevelEnum;
import com.pedro.auth.model.Rule;
import com.pedro.auth.model.User;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.subject.impl.DefaultAuthSubject;
import com.pedro.auth.util.CookieUtil;
import com.pedro.auth.util.RoleCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求拦截器
 */
@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthInterceptor.class);

    @Resource
    private AuthInfoAutoConfig authInfoAutoConfig;

    // TODO 优化为更好的，可淘汰的缓存；假如缓存淘汰了，可以重新直接取（可能需要用户提供username查找info的方法，来在缓存过期的时候重新查询）
    public ConcurrentHashMap<String, AuthSubject> cache = new ConcurrentHashMap<>();

    /**
     * 前置拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.info("前置拦截");

        // 0.无权限页
        if (request.getRequestURI().equals("/roleDenied.html")){
            return true;
        }

        String username = null;
        // 1.尝试从session中获得username
        Object sessionObject = request.getSession().getAttribute("username");
        if (sessionObject != null) {
            username = (String) sessionObject;
        }

        // 2.session中没有，尝试从Cookie中获取
        if (username == null) {
            username = CookieUtil.getValue(request, "username");
        }

        // 3.从session或cookie中获得了username，并且可以在缓存中找到User信息
        if (username != null) {
            if (cache.containsKey(username) && cache.get(username).beAuthed()) {
                // 3.1 已认证，设置到当前ThreadLocal内
                AuthSubject authSubject = cache.get(username);
                UserContextHolder.setUserContext(authSubject);
                // 3.2 权限验证-获得本条的权限要求
                String requestURI = request.getRequestURI();
                Rule rule = authInfoAutoConfig.getAuthRuleMap().get(requestURI);
                if (null != rule) {
                    // 3.2.1 从配置文件能拿到Rule
                    if (rule.getLevel().equals(RuleLevelEnum.NO_AUTH.getLevel()) || rule.getLevel().equals(RuleLevelEnum.NEED_AUTH.getLevel())) {
                        // 3.2.1.1 无需认证或只需认证，直接通过
                        return true;
                    } else {
                        // 3.2.1.2 校验权限
                        boolean roleCheckResult = RoleCheckUtil.checkRole(authSubject.getUser().getRoleList(), rule.getRoles(), rule.getRoleRule());
                        if (!roleCheckResult) {
                            redirect(request, response);
                            return false;
                        }

                        return true;
                    }
                }

                // 3.3 无论是配置文件没配置，还是配置了默认的，要求等级一定小于认证，所以一定可以通过
                return true;
            }
        }

        // 4.没拿到username或者缓存中找不到user信息：未认证
        if (authInfoAutoConfig.getDefaultAuthInfo() != null
                && !authInfoAutoConfig.getDefaultAuthInfo().equals(RuleLevelEnum.NO_AUTH.getLevel())
            ) {
            // 有配置默认权限，且默认权限不为NO_AUTH，说明不能通过
            redirect(request, response);
            return false;
        }
        // TODO 第四步，假如默认是noauth，但是配置文件有auth，就给过了？？？？

        // 5.确实是一个未认证请求，创建空AuthSubject供可能的认证操作
        AuthSubject emptyAuthSubject = new DefaultAuthSubject();
        UserContextHolder.setUserContext(emptyAuthSubject);

        return true;
    }

    /**
     * 后置拦截
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        if (UserContextHolder.getUserContext().beAuthed()) {
            User user = UserContextHolder.getUserContext().getUser();
            // 1.将username,authSubject存入缓存
            cache.put(user.getUsername(), UserContextHolder.getUserContext());

            // 2.将username存入session
            request.getSession().setAttribute("username", user.getUsername());

            // 3.是否需要将username存入cookie
            if (UserContextHolder.getUserContext().rememberMe()) {
                CookieUtil.setUserNameCookie(response, user.getUsername());
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
        //获取当前请求的路径
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        //如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理 否则直接重定向就可以了
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            //告诉ajax本次拦截为重定向
            response.setHeader("REDIRECT", "REDIRECT");
            //告诉ajax重定向路径
            response.setHeader("CONTENTPATH", basePath + "/roleDenied.html");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendRedirect(basePath + "/roleDenied.html");
        }
    }
}
