package com.chenjie.kafka.annotation;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.log.LogAccessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.chenjie.kafka.constants.KafkaPropertyConstants.DEFAULT_SPRING_KAFKA_NAMESPACE;
import static com.chenjie.kafka.constants.KafkaPropertyConstants.SPRING_KAFKA_CONSUMER_NAMESPACE_KEY;


/**
 * 修改kafkaListener注解中指定的 topic名，为其加上前缀
 */
public class MyKafkaListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered {
    private final Environment env;
    private final LogAccessor logger = new LogAccessor(LogFactory.getLog(getClass()));
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    private String prefix;
    private final String DEFAULT_SEPARATOR = "_";
    private final String EXPRESSION_PREFIX = "$";
    private final char EXPRESSION_SEPARATOR = ':';
    private KubernetesClient kubernetesClient;

    public MyKafkaListenerAnnotationBeanPostProcessor(Environment env, KubernetesClient client) {
        this.env = env;
        this.kubernetesClient = client;
        prefix = this.env.getProperty("spring.kafka.consumer.namespace", DEFAULT_SPRING_KAFKA_NAMESPACE);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE-1;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Map<Method, Set<KafkaListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set<KafkaListener>>) method -> {
                        Set<KafkaListener> listenerMethods = findListenerAnnotations(method);
                        return (!listenerMethods.isEmpty() ? listenerMethods : null);
                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(bean.getClass());
                this.logger.trace(() -> "No @KafkaListener annotations found on bean type: " + bean.getClass());
            }else {
                String prefix = getDefaultNamespace();
                for (Map.Entry<Method, Set<KafkaListener>> entry : annotatedMethods.entrySet()) {
                    Method method = entry.getKey();
                    for (KafkaListener listener : entry.getValue()) {
                        addPrefix(prefix,method);
                    }
                }
            }
        }
        return bean;
    }

    /*
     * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
     */
    private Set<KafkaListener> findListenerAnnotations(Method method) {
        Set<KafkaListener> listeners = new HashSet<>();
        KafkaListener ann = AnnotatedElementUtils.findMergedAnnotation(method, KafkaListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        KafkaListeners anns = AnnotationUtils.findAnnotation(method, KafkaListeners.class);
        if (anns != null) {
            listeners.addAll(Arrays.asList(anns.value()));
        }
        return listeners;
    }

    @SuppressWarnings("unchecked")
    private void addPrefix(String prefix, Method method){
        try {
            KafkaListener kafkaListener = method.getAnnotation(KafkaListener.class);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(kafkaListener);
            Field value = invocationHandler.getClass().getDeclaredField("memberValues");
            value.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
            String[] topics = (String[])memberValues.get("topics");
            String[] prefixed = Arrays.stream(topics).map(new Function<String, String>() {
                @Override
                public String apply(String s) {
                    if (s.startsWith(EXPRESSION_PREFIX)){
                        String realTopic = resolveTopicFromExpression(s);
                        if (Objects.nonNull(realTopic)){
                            return "".equals(prefix) ? realTopic : prefix + DEFAULT_SEPARATOR + realTopic;
                        }else {
                            throw new IllegalArgumentException("cannot resolve placeholder: "+s);
                        }
                    }else {
                        return "".equals(prefix) ? s : prefix + DEFAULT_SEPARATOR + s;
                    }
                }
            }).toArray(val -> new String[topics.length]);
            logger.info("prefixed consumer topics: "+Arrays.asList(prefixed));
            memberValues.put("topics", prefixed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取默认kafka命名空间名
     * @return
     */
    private String getDefaultNamespace() {
        String consumerTopicPrefix = this.env.getProperty(SPRING_KAFKA_CONSUMER_NAMESPACE_KEY);
        if (Objects.nonNull(consumerTopicPrefix)){
            return consumerTopicPrefix;
        }
        String k8sEnabled = this.env.getProperty("spring.cloud.kubernetes.enabled");
        if ((Boolean.TRUE + "").equalsIgnoreCase(k8sEnabled)) {
            return kubernetesClient.getNamespace()==null?DEFAULT_SPRING_KAFKA_NAMESPACE:kubernetesClient.getNamespace();
        } else {
            return DEFAULT_SPRING_KAFKA_NAMESPACE;
        }
    }

    /**
     * 获取真正的topic名字
     * @param expression
     * @return
     */
    private String resolveTopicFromExpression(String expression){
        String ph = expression.substring(2, expression.length() - 1);
        int index = ph.indexOf(EXPRESSION_SEPARATOR);
        String resolved;
        if (index == -1){
            resolved =  env.containsProperty(ph)?env.getProperty(ph):System.getProperty(ph);
        }else {
            String propertyName = ph.substring(0, index);
            String value = env.containsProperty(propertyName) ? env.getProperty(propertyName) : System.getProperty(propertyName);
            resolved =  value == null ? expression.substring(index+1, ph.length()) : value;
        }
        return resolved;
    }
}
