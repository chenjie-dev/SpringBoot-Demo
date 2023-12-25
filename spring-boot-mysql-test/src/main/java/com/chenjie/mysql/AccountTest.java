package com.chenjie.mysql;

import cn.hutool.core.lang.hash.CityHash;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQL = "SELECT account from tableName";
    private static final String ACCOUNT = "account";

    private static final List<String> tableNameList = new ArrayList<>();

    static {
        tableNameList.add("tb_tenant_userinfo_14");
        tableNameList.add("tb_tenant_userinfo_18");
        tableNameList.add("tb_tenant_userinfo_19");
        tableNameList.add("tb_tenant_userinfo_22");
        tableNameList.add("tb_tenant_userinfo_23");
        tableNameList.add("tb_tenant_userinfo_24");
        tableNameList.add("tb_tenant_userinfo_25");
        tableNameList.add("tb_tenant_userinfo_26");
        tableNameList.add("tb_tenant_userinfo_27");
        tableNameList.add("tb_tenant_userinfo_29");
        tableNameList.add("tb_tenant_userinfo_30");
        tableNameList.add("tb_tenant_userinfo_31");
        tableNameList.add("tb_tenant_userinfo_32");
        tableNameList.add("tb_tenant_userinfo_33");
        tableNameList.add("tb_tenant_userinfo_34");
        tableNameList.add("tb_tenant_userinfo_35");
        tableNameList.add("tb_tenant_userinfo_36");
        tableNameList.add("tb_tenant_userinfo_37");
        tableNameList.add("tb_tenant_userinfo_38");
        tableNameList.add("tb_tenant_userinfo_39");
        tableNameList.add("tb_tenant_userinfo_40");
        tableNameList.add("tb_tenant_userinfo_41");
        tableNameList.add("tb_tenant_userinfo_42");
    }

    @Test
    public void test() {
        for (String tableName : tableNameList) {

            String query = SQL.replace("tableName", tableName);
            List<Map<String, Object>> mapList = jdbcTemplate.queryForList(query);
            if (mapList.isEmpty()) {
                System.out.println(tableName + "表为空");
                continue;
            }
            System.out.println(tableName + "表 原始account数量：" + mapList.size());

            Set<Long> userIdSet = new HashSet<>();
            for (Map<String, Object> map : mapList) {
                String account = map.get(ACCOUNT).toString();
                Long userId = Math.abs(CityHash.hash64(account.getBytes()));
                userIdSet.add(userId);
            }
            System.out.println(tableName + "表 转换后用户ID最小值：" + Collections.min(userIdSet));
            System.out.println(tableName + "表 转换后用户ID最大值：" + Collections.max(userIdSet));
            System.out.println(tableName + "表 转换后Long类型用户ID的数量：" + userIdSet.size());
            Assert.assertEquals(mapList.size(), userIdSet.size());
            System.out.println();
            System.out.println();
        }
    }

}