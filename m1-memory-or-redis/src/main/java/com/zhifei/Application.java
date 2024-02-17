package com.zhifei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;

import javax.servlet.Filter;
import java.util.List;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)     //动态数据源需要忽略自动配置
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        System.out.println("======================================================");
        List<Filter> filters = ((FilterChainProxy) context.getBean("springSecurityFilterChain")).getFilterChains().get(0).getFilters();
        filters.forEach(System.out::println);
        System.out.println("=========== 本项目用到的SpringSecurity过滤器，共：" + filters.size() + "个 ===========");

        System.out.println("======================= 用户注册时，账号加密方法 =======================");
        PasswordEncoder encoder = context.getBean("bCryptPasswordEncoder", PasswordEncoder.class);
        String encode = encoder.encode("123456");
        System.out.println(encode);
        System.out.println(encoder.matches("123456", encode));
        System.out.println(encoder.matches("123456", "$2a$10$pzLFUlz0ciz7X7PPWfQnVe/K3XIKFc23prIqPtnbByUQJIh99p6m2"));
    }
}
