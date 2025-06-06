package practiceAPIspring.managingUsers.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

//n cau hình caché cho ram

    //Spring mặc định sử dụng storeByReference = true cho ConcurrentMapCacheManager.
    // Điều này có nghĩa là các đối tượng được lưu trong cache sẽ được lưu theo tham chiếu, không phải theo giá trị.
    // Khi bạn lưu một đối tượng vào cache, nó sẽ không tạo bản sao của đối tượng đó mà chỉ lưu tham chiếu đến nó.
//        @Bean
//        public CacheManager cacheManager() {
//            return new ConcurrentMapCacheManager("users");
//        }

        //cau hinh cache cho redis
        @Bean
        public RedisCacheConfiguration cacheConfiguration() {
            return RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10)) // thời gian sống của 1 entry trong cache 10 phut
                    .disableCachingNullValues();       // không lưu giá trị null vào cache
        }
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();

//        return RedisCacheManager
//                .builder(redisConnectionFactory)
//                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
//                .build();
//        RedisConnectionFactory được Spring tự động inject, chứa cấu hình kết nối tới Redis server (host, port, password,...).
//        RedisCacheConfiguration.defaultCacheConfig() tạo cấu hình cache mặc định.

//        .entryTtl(Duration.ofMinutes(30)) đặt thời gian sống (Time To Live - TTL) của cache entry là 30 phút.
//
//        Điều này có nghĩa mỗi cache item sẽ tự động bị xóa khỏi Redis sau 30 phút kể từ khi được ghi vào cache.
//
//        TTL giúp cache không bị lưu mãi, tránh dữ liệu cũ lỗi thời.
    }//


}
