package com.shsc.basic.goods.retrieve.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.shsc.basic.goods.retrieve.entity.Goods;

public interface GoodsSearchRepository extends ElasticsearchRepository<Goods, String>{
	
}
