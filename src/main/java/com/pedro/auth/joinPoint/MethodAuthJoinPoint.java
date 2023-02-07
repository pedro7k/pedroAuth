package com.pedro.auth.joinPoint;

import com.pedro.auth.annotation.MethodAuth;
import com.pedro.auth.common.enums.PedroAuthExceptionEnum;
import com.pedro.auth.common.enums.RuleLevelEnum;
import com.pedro.auth.common.exceptions.PedroAuthException;
import com.pedro.auth.config.AuthInfoAutoConfig;
import com.pedro.auth.context.UserContextHolder;
import com.pedro.auth.subject.AuthSubject;
import com.pedro.auth.util.HTTPUtil;
import com.pedro.auth.util.RoleCheckUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Aspect
public class MethodAuthJoinPoint {

    private Logger logger = LoggerFactory.getLogger(MethodAuthJoinPoint.class);

    /**
     * 逗号
     */
    private static final String COMMA = ",";

    @Pointcut("@annotation(com.pedro.auth.annotation.MethodAuth)")
    public void aopPoint() {
    }

    @Resource
    private AuthInfoAutoConfig authInfoAutoConfig;

    @Around("aopPoint() && @annotation(methodAuth)")
    public Object doAuth(ProceedingJoinPoint joinPoint, MethodAuth methodAuth) throws Throwable {

        // 1.获得所需对象
        AuthSubject authSubject = UserContextHolder.getUserContext();
        RuleLevelEnum level = methodAuth.level();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new PedroAuthException(PedroAuthExceptionEnum.GET_REQUEST_INFO_ERROR);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();

        // 2.配置文件中配置了，跳过注解的校验
        if (authInfoAutoConfig.getAuthRuleMap().get(request.getRequestURI()) != null){
            return joinPoint.proceed();
        }

        // 3.noAuth, 成功
        if (level == RuleLevelEnum.NO_AUTH) {
            return joinPoint.proceed();
        }

        // 4.未认证，且不是noAuth，失败跳转
        if (!authSubject.beAuthed()) {
            HTTPUtil.redirect(request, response, authInfoAutoConfig.getNoAuthPath());
            return null;
        }

        // 5.已认证，且level是needAuth，成功
        if (level == RuleLevelEnum.NEED_AUTH) {
            return joinPoint.proceed();
        }

        // 6.权限校验不通过
        List<String> roleList = authSubject.getUser().getRoleList();
        List<String> needRoles = new ArrayList<>(Arrays.asList(methodAuth.roles().trim().split(COMMA)));
        boolean checkRet = RoleCheckUtil.checkRole(roleList, needRoles, methodAuth.roleRule().getType());
        if (!checkRet) {
            HTTPUtil.redirect(request, response, authInfoAutoConfig.getNoRolePath());
            return null;
        }

        // 7.返回，成功
        return joinPoint.proceed();

    }

}
