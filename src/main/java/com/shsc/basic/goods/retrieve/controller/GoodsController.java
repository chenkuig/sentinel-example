package com.shsc.basic.goods.retrieve.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.shsc.basic.goods.retrieve.entity.Goods;
import com.shsc.basic.goods.retrieve.repositories.GoodsSearchRepository;
import com.shsc.basic.goods.retrieve.utils.ExceptionUtil;
import com.shsc.basic.goods.retrieve.utils.MapBuilderUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {
	private static int MAX_REQUEST_SIZE = 30;
	private static String [] MATCH_COLUMN = {"goodOfficalName", "specificationsCode", "specificationsDesc", "tags"};
	
	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;
	@Resource
	private GoodsSearchRepository goodsSearchRepository;
	
	@GetMapping("/input")
    @SentinelResource(value = "goods-input-qps-1000-rule", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
	ResponseEntity<List<Map<String,String>>> searchGoodsBy(@RequestParam("input") String input) {
        try{
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(input, MATCH_COLUMN);
    		Pageable pageable =  PageRequest.of(0, MAX_REQUEST_SIZE);
    		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).withPageable(pageable).build();
    		List<Goods> result = elasticsearchTemplate.queryForList(searchQuery, Goods.class);
    		if (CollectionUtils.isEmpty(result)) {
    			return null;
    		}
    		List<Map<String,String>> list = new LinkedList<Map<String,String>>();
    		result.forEach(goods->{
    			list.add(MapBuilderUtil.<String, String>builder().put("goodName", goods.getGoodsOfficalName()).put("url", goods.getGoodsUrl()).put("goodCode", goods.getGoodsCode()).build());
    		});
    		return new  ResponseEntity<>(list, HttpStatus.OK);
        }catch(Exception e){
            log.error("", e);
        }
        return null;
	}
	
	@RequestMapping("/insert")
	boolean insertGoods() {
		Goods goods =  new Goods().setGoodsCode("shsc-AS102000000001").setGoodsOfficalName("乐事薯片").setSpecificationsCode("0000001").setSpecificationsDesc("500g/袋")
				.setGoodsUrl("https://www.baidu.com").setPlaceOfProduction("中国上海").setIsSold("1").setPrice("7.8").setPriceUnit("元/¥");
		List<String> tags = new LinkedList<String>();
		tags.add("shupian");
		tags.add("薯片");
		tags.add("土豆油炸型食品");
		tags.add("休闲食品");
		tags.add("零食");
		tags.add("乐事");
		goods.setTags(tags);
		goodsSearchRepository.save(goods);
		return true;
	}
}
