package com.video.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {
    @Bean
    public Jedis getJedis(){
        Jedis jedis=new Jedis("47.116.41.66",6379);
        jedis.auth("Zhaorongqing.1688");
        if(!"PONG".equals(jedis.ping())){
            return null;
        }
        jedis.select(1);
        return jedis;
    }
}

