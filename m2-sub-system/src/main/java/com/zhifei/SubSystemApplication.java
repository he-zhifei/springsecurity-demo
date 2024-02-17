package com.zhifei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author He Zhifei
 * @date 2020/7/23 9:18
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)     // 动态数据源需要忽略自动配置
public class SubSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubSystemApplication.class, args);
    }
}
