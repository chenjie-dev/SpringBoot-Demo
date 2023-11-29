package com.chenjie.elasticsearch.constant;


import javax.validation.constraints.NotNull;
import java.util.Map;

public class EsQueryBean {

    /**
     * ES的index名称
     */
    @NotNull(message = "index不能为空")
    String index;

    /**
     * 查询条件
     */
    Map<String, Object> queryMap;


    /**
     * 查询类型 1：完全匹配 2：模糊匹配 3：
     */
    String queryType;

    /**
     * 查询的页数
     */
    Integer pageNum;

    /**
     * 每页大小
     */
    Integer size;

    /**
     * 排序字段，true:asc,false:desc
     */
    Map<String, Boolean> sort;

    /**
     * 需要的字段
     */
    String[] includeFields;

    /**
     * 排除的字段
     */
    String[] excludeFields;

    /**
     * 超时时间 单位秒
     */
    Integer timeOut;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Map<String, Object> getQueryMap() {
        return queryMap;
    }

    public void setQueryMap(Map<String, Object> queryMap) {
        this.queryMap = queryMap;
    }

    public Map<String, Boolean> getSort() {
        return sort;
    }

    public void setSort(Map<String, Boolean> sort) {
        this.sort = sort;
    }

    public String[] getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(String[] includeFields) {
        this.includeFields = includeFields;
    }

    public String[] getExcludeFields() {
        return excludeFields;
    }

    public void setExcludeFields(String[] excludeFields) {
        this.excludeFields = excludeFields;
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

}
