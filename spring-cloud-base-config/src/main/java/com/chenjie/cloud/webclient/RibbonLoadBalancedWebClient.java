package com.chenjie.cloud.webclient;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URL;
import java.util.Objects;

/**
 * 优化封装 WebClient + Ribbon的调用
 */
@Component
@Slf4j
public class RibbonLoadBalancedWebClient {

    @Autowired
    private SpringClientFactory factory;

    public WebClient createWebClient(){
        return WebClient.builder().build();
    }

//    public WebClient createWebClient(PrometheusMeterRegistry registry){
//        MetricsWebClientFilterFunction metricsFilter = new MetricsWebClientFilterFunction(
//                registry,
//                new CustomWebClientExchangeTagsProvider(),
//                "call_service_metrics",
//                AutoTimer.ENABLED
//        );
//        return WebClient.builder().filter(metricsFilter).build();
//    }



    public WebClient.RequestBodySpec getRibbonLoadBalancedWebClient(WebClient.RequestHeadersUriSpec requestHeadersUriSpec, String url) {
        String remoteUrl = url;
        try {
            URL originUrl = new URL(url);
            String host = originUrl.getHost();
            ILoadBalancer loadBalancer = factory.getLoadBalancer(host);
            Server server = loadBalancer.chooseServer(null);
            if(Objects.nonNull(server)){
                remoteUrl = url.replace(host,server.getHost());
                log.debug("chooseService host : {}",server.getHost());
            }else {
                log.warn("RibbonLoadBalancedWebClient no service to choose");
            }
            requestHeadersUriSpec.attribute("clientName",host);
        }catch (Exception e){
            e.printStackTrace();
        }

        return (WebClient.RequestBodySpec) requestHeadersUriSpec.uri(remoteUrl);
    }


}
