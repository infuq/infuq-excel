package com.infuq.consumer.config;

import com.infuq.common.constants.CommonConstant;
import com.infuq.consumer.handler.RedisExportFileErrorHandler;
import com.infuq.consumer.listener.RedisExportListener;
import com.infuq.consumer.service.ExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;


@Configuration
@Slf4j
public class RedisConfig {


    @Resource
    private Environment environment;
    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 序列化工具
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // key 和 hashKey 采用 string 序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        // value 和 hashValue 采用 JSON 序列化
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);

        return redisTemplate;


    }



    /**
     * 配置类中注入连接工厂
     */
    //@Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return new LettuceConnectionFactory(config);
    }

    /**
     * 创建监听器/订阅者
     */
    public RedisExportListener redisExportListener(ExportService exportService) {
        return new RedisExportListener(exportService);
    }

    /**
     * 创建频道
     */
    public ChannelTopic channelTopic() {
        return new ChannelTopic(CommonConstant.REDIS_EXPORT_CHANNEL);
    }

    /**
     * 创建错误监听器
     */
    public RedisExportFileErrorHandler errorHandler() {
        return new RedisExportFileErrorHandler();
    }



    /**
     * 建立频道与监听器绑定关系
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, ExportService exportService) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisExportListener(exportService), channelTopic());
        container.setErrorHandler(errorHandler());
        return container;
    }

}
