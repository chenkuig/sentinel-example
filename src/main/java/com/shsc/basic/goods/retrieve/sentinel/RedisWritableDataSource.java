package com.shsc.basic.goods.retrieve.sentinel;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisWritableDataSource <T,S> implements WritableDataSource<T> {

	private final String ruleKey;
	
	private final RedisClient redisClient;
	
	protected final Converter<T, S> parser;
	private final Lock lock = new ReentrantLock(true);
	public RedisWritableDataSource(RedisConnectionConfig connectionConfig, String ruleKey,Converter<T, S> parser) {
			AssertUtil.notNull(connectionConfig, "Redis connection config can not be null");
			AssertUtil.notEmpty(ruleKey, "Redis ruleKey can not be empty");
			this.redisClient = getRedisClient(connectionConfig);
			this.ruleKey = ruleKey;
			this.parser = parser;
	}
	 /**
     * Build Redis client fromm {@code RedisConnectionConfig}.
     *
     * @return a new {@link RedisClient}
     */
    private RedisClient getRedisClient(RedisConnectionConfig connectionConfig) {
        if (connectionConfig.getRedisSentinels().size() == 0) {
            RecordLog.info("[RedisDataSource] Creating stand-alone mode Redis client");
            return getRedisStandaloneClient(connectionConfig);
        } else {
            RecordLog.info("[RedisDataSource] Creating Redis Sentinel mode Redis client");
            return getRedisSentinelClient(connectionConfig);
        }
    }

    private RedisClient getRedisStandaloneClient(RedisConnectionConfig connectionConfig) {
        char[] password = connectionConfig.getPassword();
        String clientName = connectionConfig.getClientName();
        RedisURI.Builder redisUriBuilder = RedisURI.builder();
        redisUriBuilder.withHost(connectionConfig.getHost())
            .withPort(connectionConfig.getPort())
            .withDatabase(connectionConfig.getDatabase())
            .withTimeout(Duration.ofMillis(connectionConfig.getTimeout()));
        if (password != null) {
            redisUriBuilder.withPassword(connectionConfig.getPassword());
        }
        if (StringUtil.isNotEmpty(connectionConfig.getClientName())) {
            redisUriBuilder.withClientName(clientName);
        }
        return RedisClient.create(redisUriBuilder.build());
    }

    private RedisClient getRedisSentinelClient(RedisConnectionConfig connectionConfig) {
        char[] password = connectionConfig.getPassword();
        String clientName = connectionConfig.getClientName();
        RedisURI.Builder sentinelRedisUriBuilder = RedisURI.builder();
        for (RedisConnectionConfig config : connectionConfig.getRedisSentinels()) {
            sentinelRedisUriBuilder.withSentinel(config.getHost(), config.getPort());
        }
        if (password != null) {
            sentinelRedisUriBuilder.withPassword(connectionConfig.getPassword());
        }
        if (StringUtil.isNotEmpty(connectionConfig.getClientName())) {
            sentinelRedisUriBuilder.withClientName(clientName);
        }
        sentinelRedisUriBuilder.withSentinelMasterId(connectionConfig.getRedisSentinelMasterId())
            .withTimeout(connectionConfig.getTimeout(), TimeUnit.MILLISECONDS);
        return RedisClient.create(sentinelRedisUriBuilder.build());
    }
	@Override
	public void write(T value) throws Exception {
		 lock.lock();
         try {
        	 RedisCommands<String, String> stringRedisCommands = redisClient.connect().sync();
        	 if (stringRedisCommands.exists(ruleKey) == 1) {
        		 stringRedisCommands.del(ruleKey);
        	 }
        	 stringRedisCommands.set(ruleKey, parser.convert(value).toString());
        } finally {
            lock.unlock();
        }
	}

	@Override
	public void close() throws Exception {
		
	}

}
