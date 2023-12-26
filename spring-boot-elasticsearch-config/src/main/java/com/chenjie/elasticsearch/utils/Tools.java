package com.chenjie.elasticsearch.utils;


import com.alibaba.fastjson.JSONObject;
import com.chenjie.elasticsearch.constant.ESConstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class Tools {

    /**
     * 获取流中的内容
     */
    public static String getFileContent(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static JSONObject getMappingObject(String index, ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappingMap) {
        try {
            ImmutableOpenMap<String, MappingMetaData> typeMap = mappingMap.get(index);
            MappingMetaData propertieMap = typeMap.get(ESConstant.TYPE);
            Map<String, Object> m = propertieMap.getSourceAsMap();
            String json = JSONObject.toJSONString(m);
            JSONObject jsonObject = JSONObject.parseObject(json);
            return jsonObject;
        } catch (Exception e) {
            log.error("获取Mapping对象时出现异常...{}", e.getMessage());
            return null;
        }
    }

    public static JSONObject getSettingObject(String index, ImmutableOpenMap<String, Settings> settingMap) {
        try {

            Settings settings = settingMap.get(index);
            log.info(settings.toString());
            return null;
        } catch (Exception e) {
            log.error("获取Setting对象时出现异常...{}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化字符串
     */
    public static String formatField(String field) {
        //去除下划线
        String str = field.replaceAll("_", "");
        //转换成小写
        String column = str.toLowerCase();
        return column;
    }

    /**
     * 格式化字符串
     */
    public static String formatValue(String value) {

        String valueStr = "";
        if (!StringUtils.isEmpty(value)) {
            valueStr = value.replaceAll("-", "/");
        }
        return valueStr;
    }

    public static JSONObject getFieldType(String mapping) {
        JSONObject mappingJson = JSONObject.parseObject(mapping);
        return mappingJson.getJSONObject("properties");
    }


}
