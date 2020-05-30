package com.fangtan.hourse.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 *@desc: redis cash配置类
 *@EnableCaching 支持缓存注解
 *@author:  weiqingeng
 *@date:  2018/7/13 20:10
 */
@EnableCaching
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 配置 redisConnectionFactory
     *
     * @return
     */

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName("redistest.server.com");
        standaloneConfiguration.setPort(6379);
        standaloneConfiguration.setPassword(RedisPassword.of("Hello1234"));
        standaloneConfiguration.setDatabase(0);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(3);
        config.setMinIdle(1);
        config.setMaxTotal(3);
        config.setMaxWaitMillis(3000);

        LettucePoolingClientConfiguration poolingClientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration, poolingClientConfiguration);
        /**
         * spring-date-redis的2.0及以上版本废弃了原来的LettucePool，改为使用LettucePoolingClientConfiguration
         * LettuceConnection 是使用共享连接的，需要关闭，这样redis才能切换仓库，否则切换仓库会报错：Selecting a new database not supported due to shared connection. Use separate ConnectionFactorys to work with multiple databases
         * 对于lettuce其shareNativeConnection参数默认为true，且validateConnection为false，第一次从连接池borrow到连接之后，就一直复用底层的连接，也没有归还。如果要每次获取连接都走连接池获取然后归还，需要设置shareNativeConnection为false
         **/
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }

    /**
     * 配置 redisTemplate key,value都进行序列化
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        //开启redis事务
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }

    /**
     * 设置RedisCacheManager
     * 使用cache注解管理redis缓存
     *
     * @param factory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(5 * 60L))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }



}