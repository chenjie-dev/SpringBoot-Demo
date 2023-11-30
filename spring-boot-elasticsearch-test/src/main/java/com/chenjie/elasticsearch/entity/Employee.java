package com.chenjie.elasticsearch.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description:Book实体 加上了@Document注解之后，默认情况下这个实体中所有的属性都会被建立索引、并且分词
 */
@Data
@ToString
@NoArgsConstructor
public class Employee {
    String firstName;
    String lastName;
    String age;
    String[] interests;
    private String id;
    private Long version;
}