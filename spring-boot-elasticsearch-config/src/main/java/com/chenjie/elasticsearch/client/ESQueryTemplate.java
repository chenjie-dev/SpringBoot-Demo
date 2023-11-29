package com.chenjie.elasticsearch.client;

import com.chenjie.elasticsearch.constant.EsQueryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: ES查询操作模板类
 */
@Slf4j
@Component
public class ESQueryTemplate {
    @Autowired
    @Qualifier("AutoCheckWhetherAuthRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    public SearchResponse searchResponse(EsQueryBean queryBean) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            //设置查询超时
            if (queryBean.getTimeOut() != null) {
                sourceBuilder.timeout(new TimeValue(queryBean.getTimeOut(), TimeUnit.SECONDS));
            } else {
                sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
            }

            //查询条件
            if (queryBean.getQueryMap() != null && (!queryBean.getQueryMap().isEmpty())) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                if (queryBean.getQueryType().equals("1")) {
                    queryBean.getQueryMap().forEach((k, v) -> {
                        boolQueryBuilder.must(QueryBuilders.termQuery(k, v));
                    });
                } else {
                    queryBean.getQueryMap().forEach((k, v) -> {
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, v.toString()));
                    });
                }
                sourceBuilder.query(boolQueryBuilder);
            }


            //排序
            if (queryBean.getSort() != null && (!queryBean.getSort().isEmpty())) {
                queryBean.getSort().forEach((k, v) -> {
                    sourceBuilder.sort(new FieldSortBuilder(k).order(v ? SortOrder.ASC : SortOrder.DESC));
                });
            } else {
                sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
            }

            // 设置页数 页大小，必须满足 from + pageSize <= 10000，pageNum从0开始
            if (queryBean.getPageNum() != null && queryBean.getSize() != null) {
                int pageNum = queryBean.getPageNum() < 0 ? 0 : queryBean.getPageNum();
                int pageSize = queryBean.getSize() < 0 ? 10 : queryBean.getSize();
                int from = pageNum * pageSize;
                while (from + pageSize > 10000 && from > 0) {
                    from--;
                }
                if (from == 0 && from + pageSize > 10000) {
                    pageSize = 10000;
                }
                sourceBuilder.from(from).size(pageSize);
            } else {
                sourceBuilder.from(0).size(10);
            }

            //返回和排除列
            String[] includes = CollectionUtils.isEmpty(queryBean.getIncludeFields()) ? null : queryBean.getIncludeFields();
            String[] excludeFields = CollectionUtils.isEmpty(queryBean.getExcludeFields()) ? null : queryBean.getExcludeFields();
            sourceBuilder.fetchSource(includes, excludeFields);

            //增加子类扩展入口
            boolSearch(queryBean, sourceBuilder);

            //组装查询请求
            SearchRequest searchRequest = new SearchRequest();
            //索引
            if (!StringUtils.isEmpty(queryBean.getIndex())) {
                searchRequest.indices(queryBean.getIndex());
            }
            //组合条件
            searchRequest.source(sourceBuilder);
            SearchResponse rp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return rp;
        } catch (Exception e) {
            log.error("查询ES出现异常：{}|{}", e.getMessage(), queryBean);
            return null;
        }
    }

    public List<Map<String, Object>> search(EsQueryBean queryBean) {
        try {

            SearchResponse rp = searchResponse(queryBean);

            //解析返回
            if (rp.status() != RestStatus.OK) {
                return Collections.emptyList();
            }
            //获取source
            return Arrays.stream(rp.getHits().getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("查询ES出现异常：{}|{}", e.getMessage(), queryBean);
            return null;
        }
    }

    /**
     * 供子类重载
     */
    protected void boolSearch(EsQueryBean esQueryBean, SearchSourceBuilder sourceBuilder) {

    }
}
