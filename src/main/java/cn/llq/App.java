package cn.llq;

import cn.llq.server.ChineseProverbServer;
import cn.llq.service.ChineseProverbServerHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "cn.llq")
/**
 *启动类：启动tomcat和UDP监听
 */
public class App {
    public static void main(String[] args) {
        ChineseProverbServerHandler.init(100);
        SpringApplication.run(App.class);
    }
    @Bean
    public ChineseProverbServer chineseProverbServer(){
        return  new ChineseProverbServer(9527);
    }
}
