package com.xxl.job.admin.core.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonUtilTest {

	@Test
	public void shouldWriteValueAsString() {
		//given
		Map<String, String> map = new HashMap<>();
		map.put("aaa", "111");
		map.put("bbb", "222");

		//when
		String json = JSON.toJSONString(map);

		//then
		assertEquals(json, "{\"aaa\":\"111\",\"bbb\":\"222\"}");
	}

	@Test
	public void shouldReadValueAsObject() {
		//given
		String jsonString = "{\"aaa\":\"111\",\"bbb\":\"222\"}";

		//when
		Map result = JSON.parseObject(jsonString, Map.class);

		//then
		assertEquals(result.get("aaa"), "111");
		assertEquals(result.get("bbb"), "222");

	}

}
