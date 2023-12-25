package com.chenjie.mysql.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.chenjie.mysql.entity.Worker;
import com.chenjie.mysql.mapper.DepartmentMapper;
import com.chenjie.mysql.mapper.WorkerMapper;
import com.chenjie.mysql.service.WokerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class WokerServiceImpl extends ServiceImpl<WorkerMapper, Worker> implements WokerService {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    private final ArrayBlockingQueue queue = new ArrayBlockingQueue(8, true);

    private final ThreadPoolExecutor.CallerRunsPolicy policy = new ThreadPoolExecutor.CallerRunsPolicy();

    //1、创建核心线程为10的线程池
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 10, TimeUnit.SECONDS
            , queue, policy);

    private static final AtomicInteger atomicId = new AtomicInteger(1);


    /**
     * 使用线程池，控制主线程和子线程的事务例子
     *
     * @throws SQLException
     * @throws InterruptedException
     */
    @Override
    public void inserWorkerBatch() throws SQLException, InterruptedException {

        //2、根据sqlSessionTemplate获取SqlSession工厂
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //3、获取Connection来手动控制事务
        Connection connection = sqlSession.getConnection();
        try {
            //4、设置手动提交
            connection.setAutoCommit(false);
            //5、获取Mapper
            WorkerMapper workerMapper = sqlSession.getMapper(WorkerMapper.class);
            DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
            //6、主线程去删除department表中id为1的数据
            departmentMapper.deleteById(1);
            //新建任务列表
            List<Callable<Integer>> callableList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                List<Worker> workerListPart = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    int id = atomicId.getAndIncrement();
                    if (id == 10) {
                        id = 5;
                    }
                    Worker worker = new Worker();
                    worker.setId(id);
                    worker.setDepartmentId(id);
                    worker.setName("test" + id);
                    workerListPart.add(worker);
                }

                Callable<Integer> callable = () -> {
                    try {
                        workerMapper.inserWorkerBatch(workerListPart);
                    } catch (Exception e) {
                        return 0;
                    }
                    return 1;
                };
                callableList.add(callable);
            }

            //10、任务放入线程池开始执行
            List<Future<Integer>> futures = executor.invokeAll(callableList);
            //11、对比每个任务的返回值 <= 0 代表执行失败
            for (Future<Integer> future : futures) {
                if (future.get() <= 0) {
                    //12、只要有一组任务失败回滚整个connection
                    System.out.println("有一组任务失败回滚整个connection");
                    connection.rollback();
                    return;
                }
            }
            //13、主线程和子线程都执行成功 直接提交
            connection.commit();
            System.out.println("添加成功！");

        } catch (Exception e) {
            //14、主线程报错回滚
            connection.rollback();
            log.error(e.toString());
            throw new SQLException("出现异常！");
        }

        Thread.sleep(999 * 1000);
    }
}
