package app.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许来自 http://localhost:5173 的请求
        registry.addMapping("/api/**")  // 你可以根据需要调整路径
                .allowedOrigins("http://localhost:5173")  // 前端应用的 URL
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允许的 HTTP 方法
                .allowedHeaders("*")  // 允许所有的请求头
                .allowCredentials(true);  // 允许发送 cookie 等凭证信息
    }
}
