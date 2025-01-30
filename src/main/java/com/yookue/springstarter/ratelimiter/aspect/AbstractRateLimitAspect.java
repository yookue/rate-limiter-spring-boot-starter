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
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
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
import com.yookue.commonplexus.springutil.util.AopUtilsWraps;
import com.yookue.commonplexus.springutil.util.BeanFactoryWraps;
import com.yookue.commonplexus.springutil.util.WebUtilsWraps;
import com.yookue.springstarter.ratelimiter.annotation.RateLimit;
import com.yookue.springstarter.ratelimiter.enumeration.LimitTriggerType;
import com.yookue.springstarter.ratelimiter.facade.RateLimitCallback;
import com.yookue.springstarter.ratelimiter.facade.RateLimitInformant;
import com.yookue.springstarter.ratelimiter.property.RateLimiterProperties;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Abstract aspect for rate limit
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
public abstract class AbstractRateLimitAspect implements ApplicationContextAware {
    protected final RateLimiterProperties limiterProperties;
    protected RateLimitCallback limitCallback;

    @Setter
    protected ApplicationContext applicationContext;

    public AbstractRateLimitAspect(@Nonnull RateLimiterProperties properties, @Nullable RateLimitCallback callback) {
        this.limiterProperties = properties;
        this.limitCallback = callback;
    }

    @Around(value = "@annotation(com.yookue.springstarter.ratelimiter.annotation.RateLimit)")
    public Object aroundPoint(@Nonnull ProceedingJoinPoint point) throws Throwable {
        Method method = AopUtilsWraps.getTargetMethod(point);
        Assert.notNull(method, AssertMessageConst.NOT_NULL);
        RateLimit annotation = AnnotationUtils.getAnnotation(method, RateLimit.class);
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
    protected String determineIdentifier(@Nonnull Method method, @Nonnull RateLimit annotation) throws Exception {
        String name = StringUtils.join(limiterProperties.getNamePrefix(), ClassUtils.getQualifiedMethodName(method), limiterProperties.getNameSuffix());
        StringBuilder builder = new StringBuilder(annotation.triggerType().getValue());
        builder.append(CharVariantConst.SQUARE_BRACKET_LEFT);
        if (annotation.triggerType() == LimitTriggerType.IP_ADDRESS || annotation.triggerType() == LimitTriggerType.SESSION) {
            HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
            if (request == null) {
                return null;
            }
            if (annotation.triggerType() == LimitTriggerType.IP_ADDRESS) {
                builder.append(WebUtilsWraps.getRemoteAddress(request));
            } else if (annotation.triggerType() == LimitTriggerType.SESSION) {
                builder.append(WebUtilsWraps.getSessionId(request));
            }
        } else if (annotation.triggerType() == LimitTriggerType.USERNAME) {
            RateLimitInformant informant = BeanFactoryWraps.getBean(applicationContext, RateLimitInformant.class);
            if (informant == null) {
                throw new NoSuchBeanDefinitionException(RateLimitInformant.class);
            }
            builder.append(informant.getUsername());
        }
        builder.append(CharVariantConst.SQUARE_BRACKET_RIGHT);
        return StringUtilsWraps.joinWithColon(name, builder.toString());
    }

    protected abstract Object processPoint(@Nonnull ProceedingJoinPoint point, @Nonnull String identifier, @Nonnull RateLimit annotation) throws Throwable;
}
