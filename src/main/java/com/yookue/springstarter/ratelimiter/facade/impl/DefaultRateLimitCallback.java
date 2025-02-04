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

package com.yookue.springstarter.ratelimiter.facade.impl;


import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.RedirectView;
import com.yookue.commonplexus.javaseutil.constant.AssertMessageConst;
import com.yookue.commonplexus.springutil.structure.RestResponseStruct;
import com.yookue.commonplexus.springutil.util.WebUtilsWraps;
import com.yookue.springstarter.ratelimiter.annotation.RateLimit;
import com.yookue.springstarter.ratelimiter.property.RateLimiterProperties;


/**
 * Facade implementation for rate limit callback
 *
 * @author David Hsing
 */
public class DefaultRateLimitCallback extends AbstractRateLimitCallback {
    public DefaultRateLimitCallback(RateLimiterProperties properties) {
        super(properties);
    }

    @Override
    public Object process(@Nonnull ProceedingJoinPoint point, @Nonnull RateLimit annotation) throws Exception {
        HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
        Assert.notNull(request, AssertMessageConst.NOT_NULL);
        return WebUtilsWraps.isRestRequest(request) ? processRest(point, annotation) : processHtml(point, annotation);
    }

    @SuppressWarnings("unused")
    protected Object processHtml(@Nonnull ProceedingJoinPoint point, @Nonnull RateLimit annotation) throws Exception {
        HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
        HttpServletResponse response = WebUtilsWraps.getContextServletResponse();
        Assert.notNull(request, AssertMessageConst.NOT_NULL);
        Assert.notNull(response, AssertMessageConst.NOT_NULL);
        if (StringUtils.isNotBlank(super.limitProperties.getDeniedHtmlUrl())) {
            WebUtilsWraps.forwardRequest(request, response, super.limitProperties.getDeniedHtmlUrl());
            return null;
        }
        WebUtilsWraps.writeResponse(response, super.resolveMessage(annotation));
        return null;
    }

    @SuppressWarnings("unused")
    protected Object processRest(@Nonnull ProceedingJoinPoint point, @Nonnull RateLimit annotation) throws Exception {
        HttpServletRequest request = WebUtilsWraps.getContextServletRequest();
        HttpServletResponse response = WebUtilsWraps.getContextServletResponse();
        Assert.notNull(request, AssertMessageConst.NOT_NULL);
        Assert.notNull(response, AssertMessageConst.NOT_NULL);
        if (StringUtils.isNotBlank(super.limitProperties.getDeniedRestUrl())) {
            WebUtilsWraps.forwardRequest(request, response, super.limitProperties.getDeniedRestUrl());
            return null;
        }
        return new RestResponseStruct(HttpStatus.FORBIDDEN, super.resolveMessage(annotation));
    }
}
