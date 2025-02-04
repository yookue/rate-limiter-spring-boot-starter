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

package com.yookue.springstarter.ratelimiter.aspect;


import java.time.temporal.ChronoUnit;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import com.yookue.commonplexus.javaseutil.constant.AssertMessageConst;
import com.yookue.commonplexus.javaseutil.util.LocalDateWraps;
import com.yookue.commonplexus.springutil.event.RateLimitedEvent;
import com.yookue.commonplexus.springutil.exception.RateLimitedException;
import com.yookue.commonplexus.springutil.util.RedisTemplateWraps;
import com.yookue.commonplexus.springutil.util.WebUtilsWraps;
import com.yookue.springstarter.ratelimiter.annotation.RateLimit;
import com.yookue.springstarter.ratelimiter.facade.RateLimitCallback;
import com.yookue.springstarter.ratelimiter.property.RateLimiterProperties;
import lombok.Getter;
import lombok.Setter;


/**
 * Redis aspect for rate limit
 *
 * @author David Hsing
 */
@Aspect
@Getter
@Setter
@SuppressWarnings("unused")
public class RedisRateLimitAspect extends AbstractRateLimitAspect {
    private StringRedisTemplate redisTemplate;

    public RedisRateLimitAspect(RateLimiterProperties limitProperties) {
        super(limitProperties);
    }

    public RedisRateLimitAspect(@Nonnull RateLimiterProperties properties, @Nullable RateLimitCallback callback, @Nonnull StringRedisTemplate template) {
        super(properties, callback);
        this.redisTemplate = template;
    }

    protected Object processPoint(@Nonnull ProceedingJoinPoint point, @Nonnull String identifier, @Nonnull RateLimit annotation) throws Throwable {
        Assert.notNull(redisTemplate, AssertMessageConst.NOT_NULL);
        if (RedisTemplateWraps.existsKey(redisTemplate, identifier)) {
            HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
            if (request != null) {
                super.applicationContext.publishEvent(new RateLimitedEvent(request));
            }
            if (BooleanUtils.isTrue(super.limiterProperties.getThrowException())) {
                throw new RateLimitedException();
            }
            Assert.notNull(super.limitCallback, AssertMessageConst.NOT_NULL);
            return super.limitCallback.process(point, annotation);
        } else {
            String dateTime = LocalDateWraps.formatCurrentDateTime();
            Object result = point.proceed();
            if (annotation.ttl() <= 0L || annotation.unit() == ChronoUnit.FOREVER) {
                redisTemplate.boundValueOps(identifier).set(dateTime);
            } else {
                redisTemplate.boundValueOps(identifier).set(dateTime, annotation.unit().getDuration().multipliedBy(annotation.ttl()));
            }
            return result;
        }
    }
}
