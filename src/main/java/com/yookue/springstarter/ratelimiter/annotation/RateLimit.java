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

package com.yookue.springstarter.ratelimiter.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;
import org.apache.commons.lang3.StringUtils;
import com.yookue.springstarter.ratelimiter.enumeration.LimitTriggerType;


/**
 * Annotation for setting Rate Limiter period and key type
 * <p>
 * Needs a predefined bean of {@link com.yookue.springstarter.ratelimiter.aspect.AbstractRateLimitAspect}
 *
 * @author David Hsing
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SuppressWarnings("unused")
public @interface RateLimit {
    /**
     * Returns the time amount for the limit
     *
     * @return the time amount for the limit
     */
    long ttl();

    /**
     * Returns the time unit for the limit
     *
     * @return the time unit for the limit
     */
    ChronoUnit unit() default ChronoUnit.SECONDS;

    /**
     * Returns the trigger type for limit
     *
     * @return the trigger type for limit
     */
    LimitTriggerType triggerType() default LimitTriggerType.IP_ADDRESS;

    /**
     * Returns the message code for {@link org.springframework.context.MessageSource}
     * <p>
     * Only works when {@code messageText} is absent or blank
     *
     * @return the message code for {@link org.springframework.context.MessageSource}
     */
    String messageCode() default StringUtils.EMPTY;

    /**
     * Returns the message text for display directly
     * <p>
     * Ignored {@code messageCode} when this is not blank
     *
     * @return the message text for display directly
     */
    String messageText() default StringUtils.EMPTY;
}
