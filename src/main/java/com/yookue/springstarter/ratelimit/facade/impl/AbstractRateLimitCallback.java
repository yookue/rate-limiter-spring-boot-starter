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

package com.yookue.springstarter.ratelimit.facade.impl;


import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import com.yookue.commonplexus.springutil.util.MessageSourceWraps;
import com.yookue.springstarter.ratelimit.annotation.RateLimit;
import com.yookue.springstarter.ratelimit.facade.RateLimitCallback;
import com.yookue.springstarter.ratelimit.property.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


/**
 * Facade implementation for rate limit callback
 *
 * @author David Hsing
 */
@RequiredArgsConstructor
public abstract class AbstractRateLimitCallback implements RateLimitCallback, MessageSourceAware {
    private static final String MESSAGE_CODE = "AbstractRateLimitCallback.tooFrequentTry";    // $NON-NLS-1$
    private static final String MESSAGE_TEXT = "Your operation is too frequent, please try again later";    // $NON-NLS-1$
    protected final RateLimitProperties limitProperties;

    @Setter
    protected MessageSource messageSource;

    protected String resolveMessage(@Nonnull RateLimit annotation) {
        if (StringUtils.isNotBlank(annotation.messageText())) {
            return annotation.messageText();
        }
        return MessageSourceWraps.firstMessageLookup(messageSource, new String[]{annotation.messageCode(), MESSAGE_CODE}, null, MESSAGE_TEXT, LocaleContextHolder.getLocale());
    }
}
