package com.chenjie.es.client;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chenjie.core.util.SnowFlakeID;
import com.chenjie.es.constant.ESConstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ES数据操作模板类
 */
@Slf4j
@Component
public class ESDataTemplate {

    @Autowired
    @Qualifier("AutoCheckWhetherAuthRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 添加索引文档
     *
     * @param index 索引名称
     * @param map   Map对象
     */
    public void save(String index, Map<String, Object> map) {
        try {
            IndexRequest request = new IndexRequest();
            //存在id字段则使用id，否则使用雪花算法
            String id = "";
            if (map.containsKey("id")) {
                id = (String) map.get("id");
            } else {
                id = SnowFlakeID.getSnowNumber() + "";
            }

            request.index(index).type(ESConstant.TYPE).id(id).source(map);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("保存成功！{}", response.toString());
        } catch (Exception e) {
            log.error("{}保存单条数据{}失败...{}", index, map.toString(), e.getMessage());
        }
    }

    /**
     * 添加索引文档
     *
     * @param index 索引名称
     * @param obj   JSONObject对象
     */
    public void save(String index, JSONObject obj) {
        try {
            IndexRequest request = new IndexRequest();
            //存在id字段则使用id，否则使用雪花算法
            String id = "";
            if (obj.containsKey("id")) {
                id = obj.getString("id");
            } else {
                id = SnowFlakeID.getSnowNumber() + "";
            }
            request.index(index).type(ESConstant.TYPE).id(id).source(obj.toJSONString(), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("保存成功！{}", response.toString());
        } catch (Exception e) {
            log.error("{}保存单条数据{}失败...{}", index, obj.toJSONString(), e.getMessage());
        }
    }

    /**
     * 批量保存文档
     *
     * @param index 索引名称
     * @param list  List<Map>对象
     */
    public void batchSave(String index, List<Map<String, Object>> list) {
        try {
            log.info("array.size()：[{}]", list.size());
            BulkRequest bulkRequest = new BulkRequest();
            IndexRequest request;
            String id;
            for (int i = 0; i < list.size(); i++) {
                request = new IndexRequest("post");
                Map<String, Object> obj = list.get(i);
                if (obj.containsKey("id")) {
                    id = (String) obj.get("id");
                } else {
                    id = SnowFlakeID.getSnowNumber() + "";
                }
                request.index(index).type(ESConstant.TYPE).id(id).source(obj);
                bulkRequest.add(request);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("批量保存数据成功！{}", response.status().toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}保存批量数据失败...{}", index, e.getMessage());
        }
    }

    /**
     * 批量添加文档
     *
     * @param index 索引名称
     * @param array JSONArray数组对象
     * @throws Exception
     */
    public void batchSave(String index, JSONArray array) {
        try {

            log.info("array.size()：[{}]", array.size());

            BulkRequest bulkRequest = new BulkRequest();
            IndexRequest request;
            String id;
            for (int i = 0; i < array.size(); i++) {
                request = new IndexRequest("post");
                JSONObject obj = array.getJSONObject(i);
                if (obj.containsKey("id")) {
                    id = obj.getString("id");
                } else {
                    id = SnowFlakeID.getSnowNumber() + "";
                }
                request.index(index).type(ESConstant.TYPE).id(id).source(obj.toJSONString(), XContentType.JSON);
                bulkRequest.add(request);
            }
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("批量保存数据成功！{}", response.status().toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}保存批量数据失败...{},{}", index, e.getMessage(), array.toJSONString());
        }
    }

    /**
     * 删除文档
     *
     * @param index
     * @param id
     * @return
     */
    public boolean remove(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, ESConstant.TYPE, id);
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            return response.isFragment();
        } catch (Exception e) {
            log.error("删除数据失败...{},{}", id, e.getMessage());
            return false;
        }
    }

    public boolean batchRemove(String index, List<String> ids) {

        for (String id : ids) {
            try {
                DeleteRequest request = new DeleteRequest(index, ESConstant.TYPE, id);
                DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error("批量删除数据失败...id：{},{}", id, e.getMessage());
                return false;
            }
        }
        return true;

    }
}
