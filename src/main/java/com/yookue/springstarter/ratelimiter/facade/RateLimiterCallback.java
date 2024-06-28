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

package com.yookue.springstarter.ratelimiter.facade;


import javax.annotation.Nonnull;
import org.aspectj.lang.ProceedingJoinPoint;
import com.yookue.springstarter.ratelimiter.annotation.RateLimited;


/**
 * Facade for rate limiter callback
 *
 * @author David Hsing
 */
@FunctionalInterface
public interface RateLimiterCallback {
    Object process(@Nonnull ProceedingJoinPoint point, @Nonnull RateLimited annotation) throws Exception;
}
