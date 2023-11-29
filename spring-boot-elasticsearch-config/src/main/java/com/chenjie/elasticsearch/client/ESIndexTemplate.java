package com.chenjie.elasticsearch.client;

import com.alibaba.fastjson.JSONObject;
import com.chenjie.elasticsearch.constant.ESConstant;
import com.chenjie.elasticsearch.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @Description: ES索引操作模板类
 */
@Slf4j
@Component
public class ESIndexTemplate {

    @Autowired
    @Qualifier("AutoCheckWhetherAuthRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public boolean existsIndex(String index) {
        boolean result = false;
        try {
            GetIndexRequest getRequest = new GetIndexRequest().indices(index);
            getRequest.local(false);
            getRequest.humanReadable(true);
            result = restHighLevelClient.indices().exists(getRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("IndexServiceImpl.existsIndex error...{}", e.getMessage());
        }
        return result;
    }

    /**
     * 获取ES中所有的索引名称
     *
     * @return
     */
    public Set<String> getAllIndexName() {
        Set<String> indices = null;
        try {
            GetAliasesRequest request = new GetAliasesRequest();
            GetAliasesResponse getAliasesResponse = restHighLevelClient.indices().getAlias(request, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetaData>> map = getAliasesResponse.getAliases();
            indices = map.keySet();
        } catch (IOException e) {
            log.error("IndexServiceImpl.getAllIndexName error...{}", e.getMessage());
        }
        return indices;
    }

    /**
     * 创建索引
     *
     * @param indexName 索引名称
     *                  需要在resources目录下json目录下新建两个文件：
     *                  索引名称-setting.json 、
     *                  索引名称-mapping.json
     */
    public void createIndex(String indexName) {

        //根据indexName获取setting.json 、mapping.json
        String setJson = "";
        try {
            ClassPathResource seResource = new ClassPathResource("json/" + indexName + "-setting.json");
            InputStream seInputStream = seResource.getInputStream();
            setJson = Tools.getFileContent(seInputStream);
            seInputStream.close();
        } catch (IOException e) {
            log.error("出现异常！{}", "json/" + indexName + "-setting.json文件不存在！");
        }

        String mappingJson = "";
        try {
            ClassPathResource mpResource = new ClassPathResource("json/" + indexName + "-mapping.json");
            InputStream mpInputStream = mpResource.getInputStream();
            mappingJson = Tools.getFileContent(mpInputStream);
            mpInputStream.close();
        } catch (IOException e) {
            log.error("出现异常！{}", "json/" + indexName + "-mapping.json文件不存在！");
        }

        try {
            CreateIndexRequest cir = new CreateIndexRequest(indexName);
            cir.settings(setJson, XContentType.JSON);
            //type设置为_doc，7.X只能是_doc
            //7.x可以设置为 _doc 或 _create
            cir.mapping(ESConstant.TYPE, mappingJson, XContentType.JSON);
            //设置别名
            //cir.alias(new Alias(index+"_alias"));

            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(cir, RequestOptions.DEFAULT);
            boolean falg = createIndexResponse.isAcknowledged();
            if (falg) {
                log.info("创建索引库:" + indexName + "成功！");
            }
        } catch (IOException e) {
            log.error("创建索引库:" + indexName + "失败！");
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    public boolean removeIndex(String index) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("ESTemplate.removeIndex error...{}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取索引的Mapping信息
     *
     * @param index
     * @return
     */
    public String getIndexMappingInfo(String index) {
        try {
            GetIndexRequest getRequest = new GetIndexRequest().indices(index);
            GetIndexResponse response = restHighLevelClient.indices().get(getRequest, RequestOptions.DEFAULT);
            ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappingMap = response.getMappings();
            JSONObject jsonObject = Tools.getMappingObject(index, mappingMap);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            log.error("IndexServiceImpl.getIndexMappingInfo error...{}", e.getMessage());
            return "";
        }
    }

    /**
     * 获取索引的Setting信息
     *
     * @param index
     * @return
     */
    public String getIndexSettingInfo(String index) {
        try {
            GetIndexRequest getRequest = new GetIndexRequest().indices(index);
            GetIndexResponse response = restHighLevelClient.indices().get(getRequest, RequestOptions.DEFAULT);
            ImmutableOpenMap<String, Settings> settingMap = response.getSettings();
            Settings settings = settingMap.get(index);
            JSONObject jsonObject = JSONObject.parseObject(settings.toString());
            return jsonObject.toJSONString();
        } catch (Exception e) {
            log.error("IndexServiceImpl.getIndexSettingInfo error...{}", e.getMessage());
            return "";
        }
    }

}
