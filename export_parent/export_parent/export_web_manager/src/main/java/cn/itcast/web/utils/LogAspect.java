package cn.itcast.web.utils;
import java.util.Date;
import java.util.UUID;

import cn.itcast.domain.system.SysLog;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 切面类。通知类。抽取公用的代码，自动在执行目标对象方法基础上织入公用的代码。
 * 需求： 希望在执行controller方法之后自动记录日志。
 * 关键点： @Aspect、@Around、切入点表达式
 */
@Component  // 创建对象，加入容器
@Aspect     // 指定当前类为切面类
public class LogAspect {

    // 注入日志service
    @Autowired
    private SysLogService sysLogService;
    // 注入request对象
    @Autowired
    private HttpServletRequest request;


    /**
     * 环绕通知： 可以获取当前执行的方法信息
     */
    @Around("execution(* cn.itcast.web.controller.*.*.*(..))")
    public Object insertLog(ProceedingJoinPoint pjp){
        // 创建日志对象
        SysLog log = new SysLog();
         // 封装日志对象
        log.setId(UUID.randomUUID().toString());
        log.setTime(new Date());
        // 获取类全名
        log.setAction(pjp.getTarget().getClass().getName());
        // 获取当前类的执行方法
        log.setMethod(pjp.getSignature().getName());

        // 获取来访者IP地址
        log.setIp(request.getRemoteAddr());

        // 获取session
        // 参数false； 只获取session，不创建新的session
        HttpSession session = request.getSession(false);
        if (session != null){
            // 获取登陆用户
            User user = (User) session.getAttribute("loginUser");
            // 判断
            if (user != null){
                log.setUserName(user.getUserName());
                log.setCompanyId(user.getCompanyId());
                log.setCompanyName(user.getCompanyName());
            }
        }

        try {
            // 执行控制器方法，获取其返回值并返回
            Object retV = pjp.proceed();
            // 记录日志
            sysLogService.save(log);
            return retV;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
