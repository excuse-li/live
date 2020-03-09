package cn.llq.web;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
/**
 * 测试页：测试是否可以启动
 */
public class HomePage {
    @GetMapping("")
    public Object homePage(){
        return "homePage";
    }
}
