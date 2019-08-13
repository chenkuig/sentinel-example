/*
 * Copyright © 2015-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shsc.basic.goods.retrieve.utils;

import java.util.Map;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MapBuilderUtil<K, V> {
	
	private Map<K, V> map;

    private MapBuilderUtil() {

    }

    /**
     * builder
     * @param <K> K
     * @param <V> V
     * @return build
     */
    public static <K, V> MapBuilderUtil<K, V> builder() {
    	MapBuilderUtil<K, V> build = new MapBuilderUtil<>();
        build.map = Maps.newHashMap();
        return build;
    }
    /**
     * 
     * @param map map类型
     * @return
     */
    public static <K, V> MapBuilderUtil<K, V> builder(Map<K, V> map) {
    	MapBuilderUtil<K, V> build = new MapBuilderUtil<>();
        if (map==null) {
        	log.error("Map is not null !");
        	return null;
        }
        build.map = map;
        return build;
    }
    /**
     * put
     * @param key key
     * @param value value
     * @return map
     */
    public MapBuilderUtil<K, V> put(final K key, final V value) {
        map.put(key, value);
        return this;
    }

    /**
     * build
     * @return Map
     */
    public Map<K, V> build() {
        return map;
    }
}
