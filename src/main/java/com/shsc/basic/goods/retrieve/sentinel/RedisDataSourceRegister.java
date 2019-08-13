package com.shsc.basic.goods.retrieve.sentinel;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.util.Assert;

import com.alibaba.csp.sentinel.command.handler.ModifyParamFlowRulesCommandHandler;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.datasource.redis.RedisDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisDataSourceRegister extends ApplicationObjectSupport implements SmartInitializingSingleton , InitFunc{
	private static String paramterRuleKey = "paramterRuleKey";
	private static String paramterRuleChannel = "paramterRuleChannel";
	
	
	private static String flowRuleKey = "flowRuleKey";
	private static String flowRulechannel = "flowRulechannel";
	
	
	private static String degradeRuleKey = "degradeRuleKey";
	private static String degradeRulechannel = "degradeRulechannel";
	
	private static String  systemRuleKey = "systemRuleKey";
	private static String  systemRulechannel = "systemRulechannel";
	
	private static String  authorityRuleKey = "authorityRuleKey";
	private static String  authorityRulechannel = "authorityRulechannel";
	@Override
	public void init() throws Exception {
		try {
			String redisHost = "127.0.0.1";
			int redisPort = 6379;
			
		    RedisConnectionConfig config = RedisConnectionConfig.builder()
		        .withHost(redisHost)
		        .withPort(redisPort)
		        .withDatabase(10)
		        .build();
		    
		    // 限流规则
		    Converter<String, List<FlowRule>> flowRuleParser = source -> JSON.parseObject(source,new TypeReference<List<FlowRule>>() {});
		    ReadableDataSource<String, List<FlowRule>> redisDataSource = new RedisDataSource<>(config, flowRuleKey, flowRulechannel, flowRuleParser);
		    FlowRuleManager.register2Property(redisDataSource.getProperty());
		    
		    Converter<List<FlowRule>, String> writeFlowRuleParser = source -> JSON.toJSONString(source);
		    WritableDataSource<List<FlowRule>> writableDataSource = new RedisWritableDataSource<>(config, flowRuleKey,writeFlowRuleParser);
		    WritableDataSourceRegistry.registerFlowDataSource(writableDataSource);
		    
		    //热点参数规则
		    Converter<String, List<ParamFlowRule>> paramRuleParser = source -> JSON.parseObject(source,new TypeReference<List<ParamFlowRule>>() {});
		    ReadableDataSource<String, List<ParamFlowRule>>  paramRedisDataSource = new RedisDataSource<>(config, paramterRuleKey, paramterRuleChannel, paramRuleParser);
		    ParamFlowRuleManager.register2Property(paramRedisDataSource.getProperty());
		    
		    Converter<List<ParamFlowRule>, String> writeParamRuleParser = source -> JSON.toJSONString(source);
		    WritableDataSource<List<ParamFlowRule>> paramWritableDataSource = new RedisWritableDataSource<>(config, paramterRuleKey,writeParamRuleParser);
		    ModifyParamFlowRulesCommandHandler.setWritableDataSource(paramWritableDataSource);
		    
		    // 降级规则
		    Converter<String, List<DegradeRule>> degradeRuleParser = source -> JSON.parseObject(source,new TypeReference<List<DegradeRule>>() {});
	        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new RedisDataSource<>(config, degradeRuleKey, degradeRulechannel, degradeRuleParser);
	        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
	        
	        Converter<List<DegradeRule>, String> writeDegradeRuleParser = source -> JSON.toJSONString(source);
	        WritableDataSource<List<DegradeRule>> writeDegradeRuleDataSource = new RedisWritableDataSource<>(config, degradeRuleKey, writeDegradeRuleParser);
	        WritableDataSourceRegistry.registerDegradeDataSource(writeDegradeRuleDataSource);
		    
	        // 系统规则
	        Converter<String, List<SystemRule>> systemRuleParser = source -> JSON.parseObject(source,new TypeReference<List<SystemRule>>() {});
	        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource= new RedisDataSource<>(config, systemRuleKey, systemRulechannel, systemRuleParser);
	        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
	        
	        Converter<List<SystemRule>, String> writeSystemRuleParser = source -> JSON.toJSONString(source);
	        WritableDataSource<List<SystemRule>>  writeSystemRuleDataSource =  new RedisWritableDataSource<>(config, systemRuleKey, writeSystemRuleParser);
	        WritableDataSourceRegistry.registerSystemDataSource(writeSystemRuleDataSource);
	        
	        
	        // 授权规则
	        Converter<String, List<AuthorityRule>> authorityRuleParser = source -> JSON.parseObject(source,new TypeReference<List<AuthorityRule>>() {});
	        ReadableDataSource<String, List<AuthorityRule>> authorityRuleDataSource= new RedisDataSource<>(config, authorityRuleKey, authorityRulechannel, authorityRuleParser);
	        AuthorityRuleManager.register2Property(authorityRuleDataSource.getProperty());
	        
	        Converter<List<AuthorityRule>, String> writeAuthorityRuleParser = source -> JSON.toJSONString(source);
	        WritableDataSource<List<AuthorityRule>> writeauthorityRuleDataSource = new RedisWritableDataSource<>(config, authorityRuleKey, writeAuthorityRuleParser);
	        WritableDataSourceRegistry.registerAuthorityDataSource(writeauthorityRuleDataSource);

		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	public void afterSingletonsInstantiated() {
		try {
			init();
		} catch (Exception e) {
			Assert.isTrue(false, e.getMessage());
		}
		
	}
}
