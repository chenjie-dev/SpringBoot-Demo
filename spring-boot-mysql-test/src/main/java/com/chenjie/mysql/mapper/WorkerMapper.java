package com.chenjie.mysql.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.chenjie.mysql.entity.Worker;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface WorkerMapper extends BaseMapper<Worker> {
    void inserWorkerBatch(List<Worker> workerList);
}
