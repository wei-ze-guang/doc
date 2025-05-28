package com.im.springsecurity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class loginFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!Objects.equals(request.getMethod(), "POST")) {
            //判断是否是post请求
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if(Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            try {
                Map<String,String> map = new ObjectMapper().readValue(request.getInputStream(),Map.class);
                //TODO  这里需要处理
                return new UsernamePasswordAuthenticationToken(map.get("username"),map.get("password"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return super.attemptAuthentication(request, response);
    }
}
