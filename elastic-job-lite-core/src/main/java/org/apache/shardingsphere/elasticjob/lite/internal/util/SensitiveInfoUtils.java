/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.lite.internal.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.shardingsphere.elasticjob.lite.util.env.IpUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sensitive info utility.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SensitiveInfoUtils {
    
    private static final String FAKE_IP_SAMPLE = "ip";
    
    /**
     * Filter sensitive IP addresses.
     * 
     * @param target IP addresses to be filtered
     * @return filtered IP addresses
     */
    public static List<String> filterSensitiveIps(final List<String> target) {
        final Map<String, String> fakeIpMap = new HashMap<>();
        final AtomicInteger step = new AtomicInteger();
        return Lists.transform(target, new Function<String, String>() {
            
            @Override
            public String apply(final String input) {
                Matcher matcher = Pattern.compile(IpUtils.IP_REGEX).matcher(input);
                String result = input;
                while (matcher.find()) {
                    String realIp = matcher.group();
                    String fakeIp;
                    if (fakeIpMap.containsKey(realIp)) {
                        fakeIp = fakeIpMap.get(realIp);
                    } else {
                        fakeIp = Joiner.on("").join(FAKE_IP_SAMPLE, step.incrementAndGet());
                        fakeIpMap.put(realIp, fakeIp);
                    }
                    result = result.replace(realIp, fakeIp);
                }
                return result;
            }
        });
    }
}