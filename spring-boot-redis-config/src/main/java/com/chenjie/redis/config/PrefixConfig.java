package com.chenjie.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis.prefix.namespace")
public class PrefixConfig {

    private String localNamespace;
    private String publicNamespace;
    private Boolean namespaceSwitch;

    public String getLocalNamespace() {
        return localNamespace;
    }

    public String getPublicNamespace() {
        return publicNamespace;
    }

    public Boolean getNamespaceSwitch() {
        return namespaceSwitch;
    }

    public void setLocalNamespace(String localNamespace) {
        this.localNamespace = localNamespace;
    }

    public void setPublicNamespace(String publicNamespace) {
        this.publicNamespace = publicNamespace;
    }

    public void setNamespaceSwitch(Boolean namespaceSwitch) {
        this.namespaceSwitch = namespaceSwitch;
    }

}
