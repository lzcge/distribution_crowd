package com.lzcge.crowd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: lzcge
 * @create: 2020-03-26
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrowdTest {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Test
	public void testSaveValueToRedisByRedisTemplate(){

		//获取Redis操作
		ValueOperations<Object,Object> operator = redisTemplate.opsForValue();

		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		operator.set("list",list);

		List<Integer> value = (List)operator.get("list");

		System.out.println(value.toString());

		List<Integer> list2 = new ArrayList<>();
		list2.add(1);
		list2.add(2);

		operator.set("list",list2);

		List<Integer> value2 = (List)operator.get("list");

		System.out.println(value2.toString());

		//设置值
		//operator.set("keyone","valueone");

		//获取值
		//Object value = operator.get("keyone");

	}

	@Test
	public void testRedisTemplate() {
		System.out.println(redisTemplate);
		redisTemplate.opsForValue().set("pig", "red");
	}

	@Test
	public void testStringRedisTemplate() {
		stringRedisTemplate.opsForValue().set("MailVerifyCode_2961136411@qq.com", "123456");
	}

	@Test
	public void testStringRedisTemplate2() {
		System.out.println(stringRedisTemplate.opsForValue().get("MailVerifyCode_2961136411@qq.com"));
	}
}
