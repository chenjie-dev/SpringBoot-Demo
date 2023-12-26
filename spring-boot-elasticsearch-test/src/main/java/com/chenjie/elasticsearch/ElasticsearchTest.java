package com.chenjie.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenjie.elasticsearch.entity.Announcement;
import com.chenjie.elasticsearch.vo.AnnouncementVo;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.chenjie.elasticsearch.utils.PageUtil.checkPageNum;
import static com.chenjie.elasticsearch.utils.PageUtil.checkPageSize;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final AnnouncementVo searchContent = new AnnouncementVo();

    static {
        searchContent.setType(2);
        searchContent.setAnnouncementcontent("茅台");
    }

    @Test
    public void testElasticsearch() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 构造查询条件
        // id 查询
        if (searchContent.getId() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("id", searchContent.getId()));
        }
        //证券代码/简称
        if (searchContent.getSecusearch() != null) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("secusearch.kw", "*" + searchContent.getSecusearch() + "*"));
        }
        //type 1:查询单个新闻时内容全部返回 2：查询标题 3：查询内容，内容需要做特殊处理
        if (searchContent.getType() == 3) {
            // 公告内容
            if (StringUtils.isNotBlank(searchContent.getAnnouncementcontent())) {
                boolQueryBuilder.must(QueryBuilders.wildcardQuery("content.kw", "*" + searchContent.getAnnouncementcontent() + "*").boost(3F));
                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("content", searchContent.getAnnouncementcontent()).slop(1));
            }
        }
        if (searchContent.getType() == 2) {
            // 公告标题
            if (StringUtils.isNotBlank(searchContent.getAnnouncementcontent())) {
                boolQueryBuilder.must(QueryBuilders.wildcardQuery("infotitle", "*" + searchContent.getAnnouncementcontent() + "*"));
            }
        }

        // 证券代码集
        if (searchContent.getSecucodearray() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("secucode", searchContent.getSecucodearray()));
        }

        //公告日期
        if (searchContent.getAnnouncementdate() != null) {
            try {
                SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
                boolQueryBuilder.must(QueryBuilders.rangeQuery("infopubldate").gte(yyyyMMdd.parse(searchContent.getAnnouncementdate().get(0)).getTime()).lte(yyyyMMdd.parse(searchContent.getAnnouncementdate().get(1)).getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 类别栏目
        if (searchContent.getCategorycode() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("anncclscode", searchContent.getCategorycode() + ""));
        }
        // 市场栏目
        if (searchContent.getMarketcode() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("secumarket", searchContent.getMarketcode() + ""));
        }
        // 行业栏目
        if (searchContent.getIndustrycode() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("blockid", searchContent.getIndustrycode() + ""));
        }
        // 地域栏目
        if (searchContent.getProvincecode() != null) {
            //区分 省城code
            String provinceCode = searchContent.getProvincecode();
            String cityCode = provinceCode.substring(5);
            // 省
            if ("0000".equals(cityCode)) {
                boolQueryBuilder.must(QueryBuilders.termQuery("parentnode", searchContent.getProvincecode() + ""));
            } else {
                boolQueryBuilder.must(QueryBuilders.termQuery("areainnercode", searchContent.getProvincecode() + ""));
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);

        // 分页
        int pageNum = checkPageNum(searchContent.getPageNum());
        int pageSize = checkPageSize(searchContent.getPageSize());
        int from = pageNum * pageSize;
        while (from + pageSize > 10000 && from > 0) {
            from--;
        }
        if (from == 0 && from + pageSize > 10000) {
            pageSize = 10000;
        }
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(pageSize);

        // 先按发布时间降序排，再按id降序排
        searchSourceBuilder.sort("infopubldate", SortOrder.DESC);
        searchSourceBuilder.sort("id", SortOrder.DESC);

        // 执行查询
        SearchRequest searchRequest = new SearchRequest("announcement");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解出返回结果
        List<Announcement> announcements = new ArrayList<>();
        SearchHit[] searchHitsList = searchResponse.getHits().getHits();
        for (SearchHit searchHit : searchHitsList) {
            Map<String, Object> itemMap = searchHit.getSourceAsMap();
            announcements.add(JSONObject.parseObject(String.valueOf(JSON.toJSON(itemMap)), Announcement.class));
        }

        for (Announcement announcement : announcements) {
            System.out.println(announcement);
        }
    }
}
