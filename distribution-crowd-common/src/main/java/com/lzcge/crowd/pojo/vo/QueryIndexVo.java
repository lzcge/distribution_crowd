package com.lzcge.crowd.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: lzcge
 * @create: 2019-08-15
 **/

@Data
public class QueryIndexVo {
	private Integer pageNo;//页号
	private Integer pageSize;//每页的条数
	private String queryText;//模糊查询内容


	private List<Byte> status;//状态
	private List<Integer> typeIds;//类型标签
}
