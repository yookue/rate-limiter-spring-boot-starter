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

package com.yookue.springstarter.ratelimit.property;


import java.io.Serializable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.yookue.springstarter.ratelimit.config.RateLimitAutoConfiguration;
import com.yookue.springstarter.ratelimit.enumeration.LimitStorageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for Rate Limit starter
 *
 * @author David Hsing
 */
@ConfigurationProperties(prefix = RateLimitAutoConfiguration.PROPERTIES_PREFIX)
@Getter
@Setter
@ToString
public class RateLimitProperties implements Serializable {
    /**
     * Indicates whether to enable this starter or not
     * <p>
     * Default is {@code true}
     */
    private Boolean enabled = true;

    /**
     * The order of {@link org.springframework.beans.factory.config.BeanPostProcessor} for registering aspect bean
     */
    private Integer processorOrder;

    /**
     * The prefix of the limit name
     */
    private String namePrefix;

    /**
     * The suffix of the limit name
     */
    private String nameSuffix;

    /**
     * Throws {@link com.yookue.springstarter.ratelimit.exception.RateLimitException} instead of redirection
     */
    private Boolean throwsException = true;

    /**
     * The url to redirect, handled in html model, if exceed the limitation
     */
    private String deniedHtmlUrl;

    /**
     * The url to redirect, handled in rest model, if exceed the limitation
     */
    private String deniedRestUrl;

    /**
     * The storage type of the limier, default is {@code REDIS}
     */
    private LimitStorageType storageType = LimitStorageType.REDIS;
}
