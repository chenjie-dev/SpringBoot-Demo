springboot启动类中加上System.setProperty("jasypt.encryptor.password", "${JASYPT_PASSWORD}");
本地测试的话将"${JASYPT_PASSWORD}"改为"gangtise"（若修改了密钥则用修改后的）

application.yaml中加入如下配置：
```yaml
spring:
  elasticSearch:
    host: ${ES_HOST:127.0.0.1}
    port: ${ES_PORT:9200}
    scheme: http
    #  cluster-name: ${ES_CLUSTER_NAME:elasticsearch}
    auth-enable: true
    user:
      name: ${ES_USER:admin}
      password: ${ES_PASSWORD:password}

spring.elasticsearch.auth-enable用于控制是否开启认证，不配置则默认为false