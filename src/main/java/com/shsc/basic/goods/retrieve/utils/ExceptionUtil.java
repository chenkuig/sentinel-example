/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shsc.basic.goods.retrieve.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author Eric Zhao
 */
public final class ExceptionUtil {

    public static ResponseEntity<String> handleException(String input,BlockException ex) {
        return new ResponseEntity<>("访问量超过限制，过段时间访问!", HttpStatus.OK);
    }
}
