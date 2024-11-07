package com.xxl.job.core.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jackson Tool
 */
public class JacksonTool {

	static final Logger logger = LoggerFactory.getLogger(JacksonTool.class);

	static final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			// Jackson 底层已确保了线程安全
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	public static ObjectMapper getInstance() {
		return objectMapper;
	}

	/**
	 * bean、array、List、Map --> json
	 *
	 * @return json string
	 */
	public static String writeValueAsString(Object obj) {
		try {
			return getInstance().writeValueAsString(obj);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * string --> bean、Map、List(array)
	 *
	 * @return obj
	 */
	public static <T> T readValue(String jsonStr, Class<T> clazz) {
		try {
			return getInstance().readValue(jsonStr, clazz);
		} catch (IOException e) { // JsonParseException、JsonMappingException 都继承自 IOException
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * string --> List<Bean>...
	 */
	public static <T> T readValue(String jsonStr, Class<?> parametrized, Class<?>... parameterClasses) {
		try {
			JavaType javaType = getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
			return getInstance().readValue(jsonStr, javaType);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
