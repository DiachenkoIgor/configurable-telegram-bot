package com.incuube.agent.controllers.interceptors;

import com.incuube.rcs.datamodel.util.ValidationConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j2
public class SizeValidationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String text = request.getParameter("text");
        String fileName = request.getParameter("file");

        if (text != null && (text.length() > ValidationConstants.TEXT_FIELD_MAX_LENGTH || text.length() == 0)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            log.error("Error on request for path {}.'text' param size is from 1 to {}.", request.getServletPath(), ValidationConstants.TEXT_FIELD_MAX_LENGTH);

            servletOutputStream.println(String.format("'text' param size is from 1 to %s.", ValidationConstants.TEXT_FIELD_MAX_LENGTH));
            return false;
        }

        if (fileName != null && (fileName.length() > ValidationConstants.FILE_NAME_FIELD_MAX_LENGTH || fileName.length() == 0)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            log.error("Error on request for path {}.'fileName' param size is from 1 to {}", request.getServletPath(), ValidationConstants.FILE_NAME_FIELD_MAX_LENGTH);
            servletOutputStream.println(String.format("'filename' param size is from 1 to %s.", ValidationConstants.FILE_NAME_FIELD_MAX_LENGTH));
            return false;
        }

        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        //Not implemented
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //Not implemented
    }

}
