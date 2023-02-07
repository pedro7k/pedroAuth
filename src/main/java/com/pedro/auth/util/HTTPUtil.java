package com.pedro.auth.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 网络工具包
 */
public class HTTPUtil {

    /**
     * 重定向到无权限页
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {

        // 1.默认path
        if (path == null) {
            // 未配置path，跳转到默认roleDenied
            path = "/webjars/roleDenied.html";
        }

        // 2.获取当前请求的路径
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // 3.如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理 否则直接重定向就可以了
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            // 告诉ajax本次拦截为重定向
            response.setHeader("REDIRECT", "REDIRECT");
            // 告诉ajax重定向路径
            response.setHeader("CONTENTPATH", basePath + path);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendRedirect(basePath + path);
        }
    }
}
