package me.doudan.doc.config;

import me.doudan.doc.annotation.ControllerLayer;
import me.doudan.doc.annotation.DataLayer;
import me.doudan.doc.annotation.ManagementLayer;
import me.doudan.doc.annotation.ServiceLayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "me")

public class AnnotationConfig {
        @Bean
        public Map<Class<?>, String> methodAnnotation() {
            Map<Class<?>, String> map = new LinkedHashMap<>();
            map.put(ControllerLayer.class, "ControllerLayer");
            map.put(ManagementLayer.class, "ManagementLayer");
            map.put(ServiceLayer.class, "ServiceLayer");
            map.put(DataLayer.class, "DataLayer");
            return map;
        }

}
