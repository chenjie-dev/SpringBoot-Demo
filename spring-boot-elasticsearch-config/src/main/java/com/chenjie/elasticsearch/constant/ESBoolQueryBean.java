package com.chenjie.elasticsearch.constant;

import java.util.Map;

/**
 * @Description: 组装ES bool查询条件
 */
public class ESBoolQueryBean extends EsQueryBean {

    private Map<String, ConditionBean> boolMustMap;
    private Map<String, ConditionBean> boolShouldMap;
    private Map<String, ConditionBean> boolNotMustMap;
    private Map<String, ConditionBean> boolFilterMap;

    public Map<String, ConditionBean> getBoolMustMap() {
        return boolMustMap;
    }

    public void setBoolMustMap(Map<String, ConditionBean> boolMustMap) {
        this.boolMustMap = boolMustMap;
    }

    public Map<String, ConditionBean> getBoolShouldMap() {
        return boolShouldMap;
    }

    public void setBoolShouldMap(Map<String, ConditionBean> boolShouldMap) {
        this.boolShouldMap = boolShouldMap;
    }

    public Map<String, ConditionBean> getBoolNotMustMap() {
        return boolNotMustMap;
    }

    public void setBoolNotMustMap(Map<String, ConditionBean> boolNotMustMap) {
        this.boolNotMustMap = boolNotMustMap;
    }

    public Map<String, ConditionBean> getBoolFilterMap() {
        return boolFilterMap;
    }

    public void setBoolFilterMap(Map<String, ConditionBean> boolFilterMap) {
        this.boolFilterMap = boolFilterMap;
    }

    public static class ConditionBean {
        String key;
        Object value;
        Object gte;
        Object lte;
        Object gt;
        Object lt;

        public ConditionBean() {
        }

        public ConditionBean(String key, Object gte, Object lte) {
            this.key = key;
            this.gte = gte;
            this.lte = lte;
        }

        public ConditionBean(String key, Object gte, Object lte, Object gt, Object lt) {
            this.key = key;
            this.gte = gte;
            this.lte = lte;
            this.gt = gt;
            this.lt = lt;
        }

        public ConditionBean(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public ConditionBean(String key, Object value, Object gte, Object lte) {
            this.key = key;
            this.value = value;
            this.gte = gte;
            this.lte = lte;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getGte() {
            return gte;
        }

        public void setGte(Object gte) {
            this.gte = gte;
        }

        public Object getLte() {
            return lte;
        }

        public void setLte(Object lte) {
            this.lte = lte;
        }

        public Object getGt() {
            return gt;
        }

        public void setGt(Object gt) {
            this.gt = gt;
        }

        public Object getLt() {
            return lt;
        }

        public void setLt(Object lt) {
            this.lt = lt;
        }
    }
}
