package com.infuq.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
public class OssConfig {

    @Value("${oss.access-key}")
    private String accessKey;
    @Value("${oss.secretKey}")
    private String secretKey;
    @Value("${oss.endpoint}")
    public String endpoint;
    @Value("${oss.bucket-names}")
    public String bucketNames;

    @Bean
    public OSS oss() {
        return new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }


}
