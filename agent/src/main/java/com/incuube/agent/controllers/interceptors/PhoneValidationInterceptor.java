package com.incuube.agent.controllers.interceptors;

import com.incuube.agent.util.RestValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Log4j2
public class PhoneValidationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String phone = request.getParameter("phone");

        if (phone == null) {
            return true;
        }

        Optional<String> validationResult = RestValidator.validatePhoneNumber(phone);

        if (validationResult.isPresent()) {
            log.error("Error on request for path {}. Param 'phone' is not valid! - {}", request.getServletPath(), validationResult.get());
            prepareResponse(response, validationResult.get());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    private void prepareResponse(HttpServletResponse response, String text) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        servletOutputStream.println(text);
    }
}
