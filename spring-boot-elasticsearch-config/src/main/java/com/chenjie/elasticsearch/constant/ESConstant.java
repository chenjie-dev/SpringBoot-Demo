package com.chenjie.elasticsearch.constant;

/**
 * @Description: java类作用描述
 */
public class ESConstant {
    /**
     * ES的类型，为了兼容7.X，固定设置为_doc
     */
    public static final String TYPE = "_doc";
    /**
     * ES的属性字段统一
     */
    public static final String PROPERTIES = "properties";

    //关系标识符
    public static final String MUST = "must";
    public static final String SHOULD = "should";
    public static final String NOT_MUST = "not_must";
    public static final String FILTER = "filter";


    //es 字符匹配条件类型
    public enum ConditionType {
        MATCH, RANGE, TERM
    }
}
