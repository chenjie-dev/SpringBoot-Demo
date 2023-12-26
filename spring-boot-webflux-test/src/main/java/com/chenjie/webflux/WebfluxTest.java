package com.chenjie.webflux;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class WebfluxTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String rpcUrlGet = "https://tool.lu/article/report/";
    private static final String rpcUrlPost = "localhost:10001/test/post";

    @Test
    public void testWebClientGet() {
        Mono<String> mono = webClientBuilder
                .baseUrl(rpcUrlGet)
                .build()
                .get()
                .retrieve()
                .bodyToMono(String.class);

        String result = mono.block();

        if (result == null) {
            log.error("No result!!!");
        }
        log.info(result);
    }

    @Test
    public void testWebClientPost() {

        Mono<String > mono = webClientBuilder
                .baseUrl(rpcUrlPost)
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(typeDto)
                .retrieve()
                .bodyToMono(String.class);

        String result = mono.block();

        System.out.println("执行了！！！！！");
    }
}
