package com.im.springsecurity.config;

import com.im.springsecurity.handle.HandleLogoutSuccess;
import com.im.springsecurity.handle.HandleSecurityAuthFail;
import com.im.springsecurity.handle.HandleSecurityAuthSuccess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author 86199
 */
@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth").permitAll() // 放行静态资源
                        .anyRequest().authenticated() // 其他请求都要认证
                )

                .formLogin(form -> form
                        // 登录成功处理：方式二：自定义处理器（前后端分离推荐）
                         .successHandler(new HandleSecurityAuthSuccess())
                        // 登录失败处理：方式二：自定义处理器（推荐）
                        .failureHandler(new HandleSecurityAuthFail())
                )

                .logout(logout -> logout
                        .logoutUrl("/logout") // 注销请求地址
                        .invalidateHttpSession(true) // 使 session 失效
                        .clearAuthentication(true) // 清除认证信息
                        // .logoutSuccessUrl("/login.html") // 注销成功后重定向（用于页面式）
                        .logoutSuccessHandler(new HandleLogoutSuccess()) // 注销成功处理（用于前后端分离）
                )

                .csrf(csrf -> csrf.disable()) // 禁用 CSRF（前后端分离通常需要禁用）
                // 5. 跨域支持（CORS，前后端分离必要）
                .cors(Customizer.withDefaults())
                //对于异常处理还需要配置
                .build();
    }
}
