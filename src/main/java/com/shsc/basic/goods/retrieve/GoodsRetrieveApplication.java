package com.shsc.basic.goods.retrieve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.shsc.basic.goods.retrieve.repositories")
public class GoodsRetrieveApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodsRetrieveApplication.class, args);
	}

}
