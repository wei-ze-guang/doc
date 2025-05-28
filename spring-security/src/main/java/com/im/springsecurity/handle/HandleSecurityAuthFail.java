package com.im.springsecurity.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.Map;


/**
 * springSecurity 认证失败的时候，是认证失败，不是未认证
 * @author 86199
 */
@Slf4j
public class HandleSecurityAuthFail implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("Authentication failed");
        ObjectMapper mapper = new ObjectMapper();
        Map map = Map.of("message", "这是认证失败的回调");
        response.getWriter().write(mapper.writeValueAsString(map));
    }
}
