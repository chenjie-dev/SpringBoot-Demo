package com.chenjie.mysql;

import com.chenjie.mysql.service.WokerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlTest {

    @Autowired
    private WokerService wokerService;

    @Test
    public void test() throws SQLException, InterruptedException {
        wokerService.inserWorkerBatch();
    }

}