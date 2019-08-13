package com.shsc.basic.goods.retrieve.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
@Document(indexName = "goods_retriver_store", type = "goods_info")
public class Goods {
	@Id
	private String goodsCode;
	@Field(searchAnalyzer = "ik_max_word",analyzer = "ik_smart" ,type=FieldType.Text)
	private String goodsOfficalName;
	private String specificationsCode;
	private String specificationsDesc;
	private String placeOfProduction;
	private String price;
	private String priceUnit;
	private String productDate;
	private String createDate;
	private String isSold;
	@Field(searchAnalyzer = "ik_max_word",analyzer = "ik_smart" ,type=FieldType.Text)
	private List<String> tags;
	private String goodsUrl;
}
