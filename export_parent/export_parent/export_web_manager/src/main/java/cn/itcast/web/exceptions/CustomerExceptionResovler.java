package cn.itcast.web.exceptions;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义异常处理器，当控制器方法出现异常自动来到这里。
 */
public class CustomerExceptionResovler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        // 打印异常
        ex.printStackTrace();

        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("errorMsg","对不起，我错了！" + ex.getMessage());
        mv.setViewName("error");
        return mv;
    }
}
