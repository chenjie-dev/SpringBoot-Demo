package com.chenjie.es.client;


import com.chenjie.es.constant.ESBoolQueryBean;
import com.chenjie.es.constant.ESConstant;
import com.chenjie.es.constant.EsQueryBean;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: ES查询条件组装类
 */
@Component
public class ESBoolQueryTemplate extends ESQueryTemplate {

    /**
     * 根据各个查询类型组装查询条件
     */
    @Override
    public void boolSearch(EsQueryBean esQueryBean, SearchSourceBuilder sourceBuilder) {
        //针对ESBoolQueryBean 处理;
        ESBoolQueryBean esBoolQueryBean = (ESBoolQueryBean) esQueryBean;
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (esBoolQueryBean.getBoolMustMap() != null && !esBoolQueryBean.getBoolMustMap().isEmpty()) {
            each(esBoolQueryBean.getBoolMustMap(), boolQueryBuilder, ESConstant.MUST);
        }
        if (esBoolQueryBean.getBoolFilterMap() != null && !esBoolQueryBean.getBoolFilterMap().isEmpty()) {
            each(esBoolQueryBean.getBoolMustMap(), boolQueryBuilder, ESConstant.FILTER);
        }
        if (esBoolQueryBean.getBoolNotMustMap() != null && !esBoolQueryBean.getBoolNotMustMap().isEmpty()) {
            each(esBoolQueryBean.getBoolMustMap(), boolQueryBuilder, ESConstant.NOT_MUST);
        }
        if (esBoolQueryBean.getBoolShouldMap() != null && !esBoolQueryBean.getBoolShouldMap().isEmpty()) {
            each(esBoolQueryBean.getBoolMustMap(), boolQueryBuilder, ESConstant.SHOULD);
        }
        sourceBuilder.query(boolQueryBuilder);
    }

    private void each(Map<String, ESBoolQueryBean.ConditionBean> map, BoolQueryBuilder boolQueryBuilder, String flag) {
        map.forEach((conditionType, conditionBean) -> {
            each(flag, boolQueryBuilder, conditionType, conditionBean);
        });
    }

    private void each(String flag, BoolQueryBuilder boolQueryBuilder, String conditionType, ESBoolQueryBean.ConditionBean conditionBean) {
        if (flag.equals(ESConstant.MUST)) {
            if (conditionType.equals(ESConstant.ConditionType.MATCH.name())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.TERM.name())) {
                boolQueryBuilder.must(QueryBuilders.termQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.RANGE.name())) {
                boolQueryBuilder.must(getRangeQueryBuilder(conditionBean));
            }
        } else if (flag.equals(ESConstant.FILTER)) {
            if (conditionType.equals(ESConstant.ConditionType.MATCH.name())) {
                boolQueryBuilder.filter(QueryBuilders.matchQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.TERM.name())) {
                boolQueryBuilder.filter(QueryBuilders.termQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.RANGE.name())) {
                boolQueryBuilder.filter(getRangeQueryBuilder(conditionBean));
            }
        } else if (flag.equals(ESConstant.SHOULD)) {
            if (conditionType.equals(ESConstant.ConditionType.MATCH.name())) {
                boolQueryBuilder.should(QueryBuilders.matchQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.TERM.name())) {
                boolQueryBuilder.should(QueryBuilders.termQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.RANGE.name())) {
                boolQueryBuilder.should(getRangeQueryBuilder(conditionBean));
            }
        } else if (flag.equals(ESConstant.NOT_MUST)) {
            if (conditionType.equals(ESConstant.ConditionType.MATCH.name())) {
                boolQueryBuilder.mustNot(QueryBuilders.matchQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.TERM.name())) {
                boolQueryBuilder.mustNot(QueryBuilders.termQuery(conditionBean.getKey(), conditionBean.getValue()));
            }
            if (conditionType.equals(ESConstant.ConditionType.RANGE.name())) {
                RangeQueryBuilder rangeQueryBuilder = getRangeQueryBuilder(conditionBean);
                boolQueryBuilder.mustNot(rangeQueryBuilder);
            }
        }

    }

    /**
     * 范围查询条件组装
     */
    private RangeQueryBuilder getRangeQueryBuilder(ESBoolQueryBean.ConditionBean conditionBean) {
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(conditionBean.getKey());
        if (conditionBean.getGte() != null) {
            rangeQueryBuilder.gte(conditionBean.getGte());
        }
        if (conditionBean.getGt() != null) {
            rangeQueryBuilder.gt(conditionBean.getGt());
        }
        if (conditionBean.getLte() != null) {
            rangeQueryBuilder.lte(conditionBean.getLte());
        }
        if (conditionBean.getLt() != null) {
            rangeQueryBuilder.lt(conditionBean.getLt());
        }
        return rangeQueryBuilder;
    }
}
