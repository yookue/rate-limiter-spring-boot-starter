/*
 * Copyright (c) 2022 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yookue.springstarter.ratelimiter.config;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.yookue.springstarter.ratelimiter.aspect.RedisRateLimiterAspect;
import com.yookue.springstarter.ratelimiter.facade.RateLimiterCallback;
import com.yookue.springstarter.ratelimiter.facade.impl.DefaultRateLimiterCallback;
import com.yookue.springstarter.ratelimiter.property.RateLimiterProperties;


/**
 * Configuration for rate limiter
 *
 * @author David Hsing
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = RateLimiterAutoConfiguration.PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(value = {RateLimiterAutoConfiguration.Entry.class, RateLimiterAutoConfiguration.Redis.class})
public class RateLimiterAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.rate-limiter";    // $NON-NLS-1$
    public static final String REDIS_TEMPLATE = "rateLimiterRedisTemplate";    // $NON-NLS-1$

    @Order(value = 0)
    @EnableConfigurationProperties(value = RateLimiterProperties.class)
    static class Entry {
        @Bean
        @ConditionalOnProperty(prefix = RateLimiterAutoConfiguration.PROPERTIES_PREFIX, name = "throws-exception", havingValue = "false", matchIfMissing = true)
        @ConditionalOnMissingBean
        public RateLimiterCallback defaultRateLimiterCallback(@Nonnull RateLimiterProperties properties) {
            return new DefaultRateLimiterCallback(properties);
        }
    }


    @Order(value = 1)
    @ConditionalOnProperty(prefix = RateLimiterAutoConfiguration.PROPERTIES_PREFIX, name = "storage-type", havingValue = "redis", matchIfMissing = true)
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisOperations")
    static class Redis {
        @Bean
        @ConditionalOnMissingBean
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        public RedisRateLimiterAspect redisRateLimiterAspect(@Nonnull RateLimiterProperties properties, @Nonnull ObjectProvider<RateLimiterCallback> callback, @Nullable @Qualifier(value = REDIS_TEMPLATE) StringRedisTemplate preferredTemplate, @Nonnull ObjectProvider<StringRedisTemplate> presentTemplate) {
            return new RedisRateLimiterAspect(properties, callback.getIfAvailable(), ObjectUtils.defaultIfNull(preferredTemplate, presentTemplate.getIfAvailable()));
        }
    }
}
