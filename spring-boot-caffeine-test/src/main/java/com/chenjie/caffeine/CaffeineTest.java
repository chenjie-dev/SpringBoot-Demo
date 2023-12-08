package com.chenjie.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CaffeineTest {

    /**
     * 代码侵入性高
     */
    @Autowired
    private Cache<String, Object> caffeineCache;

    @Test
    public void test() throws InterruptedException {
        caffeineCache.put("testKey", "testValue");
        int i = 1;
        while (true) {
            caffeineCache.put("testKey", "testValue");
            System.out.println(i++);
            System.out.println(caffeineCache.getIfPresent("testKey"));
            System.out.println();
            Thread.sleep(1000);
        }
    }

}
