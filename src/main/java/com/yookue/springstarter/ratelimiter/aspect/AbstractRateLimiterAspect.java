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


import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import com.yookue.commonplexus.javaseutil.constant.AssertMessageConst;
import com.yookue.commonplexus.javaseutil.constant.CharVariantConst;
import com.yookue.commonplexus.javaseutil.util.StringUtilsWraps;
import com.yookue.commonplexus.springutil.util.AspectUtilsWraps;
import com.yookue.commonplexus.springutil.util.BeanFactoryWraps;
import com.yookue.commonplexus.springutil.util.WebUtilsWraps;
import com.yookue.springstarter.ratelimiter.annotation.RateLimited;
import com.yookue.springstarter.ratelimiter.enumeration.LimiterKeyType;
import com.yookue.springstarter.ratelimiter.facade.RateLimiterCallback;
import com.yookue.springstarter.ratelimiter.facade.RateLimiterInformant;
import com.yookue.springstarter.ratelimiter.property.RateLimiterProperties;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Abstract aspect for rate limiter
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
public abstract class AbstractRateLimiterAspect implements ApplicationContextAware {
    protected final RateLimiterProperties limiterProperties;
    protected RateLimiterCallback limiterCallback;

    @Setter
    protected ApplicationContext applicationContext;

    public AbstractRateLimiterAspect(@Nonnull RateLimiterProperties properties, @Nullable RateLimiterCallback callback) {
        this.limiterProperties = properties;
        this.limiterCallback = callback;
    }

    @Around(value = "@annotation(com.yookue.springstarter.ratelimiter.annotation.RateLimited)")
    public Object aroundPoint(@Nonnull ProceedingJoinPoint point) throws Throwable {
        Method method = AspectUtilsWraps.getMethod(point);
        Assert.notNull(method, AssertMessageConst.NOT_NULL);
        RateLimited annotation = AnnotationUtils.getAnnotation(method, RateLimited.class);
        if (annotation == null || annotation.ttl() <= 0L || annotation.unit() == null) {
            return point.proceed();
        }
        String identifier = determineIdentifier(method, annotation);
        if (StringUtils.isBlank(identifier)) {
            return point.proceed();
        }
        return processPoint(point, identifier, annotation);
    }

    @Nullable
    protected String determineIdentifier(@Nonnull Method method, @Nonnull RateLimited annotation) throws Exception {
        String name = StringUtils.join(limiterProperties.getNamePrefix(), ClassUtils.getQualifiedMethodName(method), limiterProperties.getNameSuffix());
        StringBuilder builder = new StringBuilder(annotation.keyType().getValue());
        builder.append(CharVariantConst.SQUARE_BRACKET_LEFT);
        if (annotation.keyType() == LimiterKeyType.IP_ADDRESS || annotation.keyType() == LimiterKeyType.SESSION) {
            HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
            if (request == null) {
                return null;
            }
            if (annotation.keyType() == LimiterKeyType.IP_ADDRESS) {
                builder.append(WebUtilsWraps.getRemoteAddress(request));
            } else if (annotation.keyType() == LimiterKeyType.SESSION) {
                builder.append(WebUtilsWraps.getSessionId(request));
            }
        } else if (annotation.keyType() == LimiterKeyType.USERNAME) {
            RateLimiterInformant informant = BeanFactoryWraps.getBean(applicationContext, RateLimiterInformant.class);
            if (informant == null) {
                throw new NoSuchBeanDefinitionException(RateLimiterInformant.class);
            }
            builder.append(informant.getUsername());
        }
        builder.append(CharVariantConst.SQUARE_BRACKET_RIGHT);
        return StringUtilsWraps.joinWithColon(name, builder.toString());
    }

    protected abstract Object processPoint(@Nonnull ProceedingJoinPoint point, @Nonnull String identifier, @Nonnull RateLimited annotation) throws Throwable;
}
