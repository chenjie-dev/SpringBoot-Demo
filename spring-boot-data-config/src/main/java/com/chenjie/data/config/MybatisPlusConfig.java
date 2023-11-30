package com.chenjie.data.config;

import com.baomidou.mybatisplus.MybatisConfiguration;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.mapper.LogicSqlInjector;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * mybatis-plus配置
 */
@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan(basePackages = {"com.chenjie.**.mapper*"})
public class MybatisPlusConfig {

    private static final int TX_METHOD_TIMEOUT = 10000;
    private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.gangtise.*.service.**.*(..))";

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setLocalPage(true);
        return interceptor;
    }

    /**
     * SQL执行效率插件
     */
    @Bean
//	@Profile({"dev","sit","uat"})// 设置 dev test 环境开启
    public SqlInterceptor performanceInterceptor() {
        SqlInterceptor interceptor = new SqlInterceptor();
        interceptor.setWriteInLog(true);
        interceptor.setMaxTime(5000L);
        return interceptor;
    }

    @Bean
    public GlobalConfiguration globalConfiguration() {
        GlobalConfiguration conf = new GlobalConfiguration();
        conf.setIdType(IdType.AUTO.getKey());
        conf.setFieldStrategy(FieldStrategy.NOT_EMPTY.getKey());
        conf.setDbColumnUnderline(true);
        conf.setRefresh(true);
        conf.setLogicDeleteValue("-1");// 逻辑删除全局值
        conf.setLogicNotDeleteValue("0");// 逻辑未删除全局值

        conf.setSqlInjector(new LogicSqlInjector());

        conf.setKeyGenerator(new IKeyGenerator() {

            @Override
            public String executeSql(String incrementerName) {
                StringBuilder sql = new StringBuilder();
                sql.append("select ");
                sql.append(incrementerName);
                sql.append(".nextval");
                return sql.toString();
            }

        });
        return conf;
    }

    @Bean
    public MybatisConfiguration mybatisConfiguration() {
        MybatisConfiguration conf = new MybatisConfiguration();
        conf.setMapUnderscoreToCamelCase(true);//开启下划线转驼峰
        conf.setCacheEnabled(false);
        conf.setCallSettersOnNulls(true);
        return conf;
    }

    //########################################### 事务配置 #####################################################

    @Bean("txManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionInterceptor txAdvice(@Qualifier("txManager") PlatformTransactionManager m) {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTx.setTimeout(TX_METHOD_TIMEOUT);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("insert*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        txMap.put("select*", readOnlyTx);
        source.setNameMap(txMap);
        TransactionInterceptor txAdvice = new TransactionInterceptor(m, source);
        return txAdvice;
    }

    @Bean
    public Advisor txAdviceAdvisor(@Qualifier("txAdvice") TransactionInterceptor txAdvice) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }
}
