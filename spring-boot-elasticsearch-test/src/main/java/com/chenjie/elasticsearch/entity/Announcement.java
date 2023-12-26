package com.chenjie.elasticsearch.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 证券主表映射出的-按键精灵表
 */
@Data
public class Announcement implements Serializable {

    private Long id;
    private Float score;
    /** 证券代码 带后缀 */
    private String secucode;
    /** 证券简称  SecuMain */
    private String secuabbr;
    /** 证券匹配字段 证券代码_证券代码（带后缀）_证券简称 */
    private String secusearch;
    /**  公告标题 LC_NotTextAnnouncement  */
    private String infotitle;
     /** 公告内容 LC_TextAnnounce */
    private String content;
     /**  信息发布日期 LC_NotTextAnnouncement */
    private Date infopubldate;
     /** 公告PDF地址 LC_NotTextAnnouncement  */
    private String announcementlink;
     /** 公告类型编号 gangtise_db.annc_level */
    private String anncclscode;
     /** 公告类型名称 gangtise_db.annc_level */
    private String anncclsname;
     /** 公告市场 select blockid from gangtise_db.block_level where Block_Standard = 999 and Block_Category=01 */
    private String secumarket;
    /**   */
    private String listedsector;
    /** 公告市场名称  gangtise_db.block_level blockname */
    private String marketname;
     /** 行业编号 gangtise_db.block_level */
    private String blockid;
     /** 行业名称 gangtise_db.block_level */
    private String blockname;
    /**  省编号 jydb.LC_AreaCode ParentNode*/
    private String parentnode;
    /** 省名称 */
    private String provincename;
    /** 城市编号 jydb.LC_AreaCode AreaInnerCode*/
    private String areainnercode;
    /** 城市名称*/
    private String cityname;

    private String synctime;

}
