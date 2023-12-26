package com.chenjie.elasticsearch.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AnnouncementVo implements Serializable {
    private List<Long> id;

    private Float score;
    /**
     * 证券代码 带后缀
     */
    private List<String> secucodearray;
    /**
     * 证券简称
     */
    private String secuabbr;
    /**
     * 证券匹配字段 证券代码_证券代码（带后缀）_证券简称
     */
    private String secusearch;

    /**
     * 区分正文和 标题
     * 1：正文
     * 2：标题
     */
    private Integer type = 0;

    /**
     * 公告标题
     */
    private String announcementtitle;
    /**
     * 公告内容
     */
    private String announcementcontent;
    /**
     * 公告时间
     */
    private List<String> announcementdate;
    /**
     * 公告PDF地址
     */
    private String announcementlink;
    /**
     * 证券类型编号
     */
    private String categorycode;
    /**
     * 证券类型名称
     */
    private String categoryname;
    /**
     * 市场代码
     */
    private String marketcode;
    /**
     * + 上市板块
     */
    private String listedsector;
    /**
     * 市场名称
     */
    private String marketname;
    /**
     * 行业编号
     */
    private String industrycode;
    /**
     * 行业名称
     */
    private String industryname;
    /**
     * 省编号
     */
    private String provincecode;
    /**
     * 省名称
     */
    private String provincename;
    /**
     * 城市编号
     */
    private String citycode;
    /**
     * 城市名称
     */
    private String cityname;

    private Date syncTime;

    /**
     * 页数
     */
    private Integer pageNum;
    /**
     * 页数大小
     */
    private Integer pageSize;
}
