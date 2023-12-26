package com.chenjie.mysql.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/post")
    public String saveAnnouncement() {
        System.out.println("请求进来了！！！！");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<String> result = new ArrayList<String>();
        result.add("Hello");
        result.add("world");
        return result.toString();
    }

}
