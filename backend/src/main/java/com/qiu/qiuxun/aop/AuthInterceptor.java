package com.qiu.qiuxun.aop;

import com.qiu.qiuxun.annotation.AuthCheck;
import com.qiu.qiuxun.common.ErrorCode;
import com.qiu.qiuxun.exception.BusinessException;
import com.qiu.qiuxun.model.enums.UserRoleEnum;
import com.qiu.qiuxun.model.vo.UserVO;
import com.qiu.qiuxun.service.IUserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: 权限校验AOP
 * @className: AuthInterceptor.java
 * @author: qiu
 * @createTime: 2024/3/17 10:38
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private IUserService userService;

    /**
     * @description: 权限校验的通知方法
     * @params: [point, authCheck]
     * @return: java.lang.Object
     * @author: qiu
     * @dateTime: 2024/3/17 11:08
     * 切入点@AuthCheck的方法，
     */
    @Around("execution(* com.qiu.qiuxun..*(..)) && @annotation(authCheck)")
    public Object doAuthCheck(ProceedingJoinPoint point, AuthCheck authCheck) throws Throwable {
        // 1.拿到权限角色
        UserRoleEnum mustRole = authCheck.mustRole();
        // 2.获取当前登录用户
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        UserVO userVO = userService.getLoginUser(request);
        Integer userRole = userVO.getUserRole();
        // 3.判断当前用户是否有权限
        if (mustRole.equals(UserRoleEnum.ADMIN) && !userRole.equals(UserRoleEnum.ADMIN.getRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 4.权限校验通过，放行
        return point.proceed();
    }
}
