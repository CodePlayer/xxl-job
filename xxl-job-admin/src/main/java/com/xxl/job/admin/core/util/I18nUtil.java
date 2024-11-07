package com.xxl.job.admin.core.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * i18n util
 *
 * @author xuxueli 2018-01-17 20:39:06
 */
public class I18nUtil {

	private static final Logger logger = LoggerFactory.getLogger(I18nUtil.class);

	private static Properties prop = null;

	public static Properties loadI18nProp() {
		if (prop != null) {
			return prop;
		}
		try {
			// build i18n prop
			String i18n = XxlJobAdminConfig.getAdminConfig().getI18n();
			String i18nFile = MessageFormat.format("i18n/message_{0}.properties", i18n);

			// load prop
			Resource resource = new ClassPathResource(i18nFile);
			EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
			prop = PropertiesLoaderUtils.loadProperties(encodedResource);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return prop;
	}

	/**
	 * get val of i18n key
	 */
	public static String getString(String key) {
		return loadI18nProp().getProperty(key);
	}

	/**
	 * get multi val of i18n multi key, as json
	 */
	public static String getMultiString(String... keys) {
		Map<String, String> map = new HashMap<>();

		Properties prop = loadI18nProp();
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				map.put(key, prop.getProperty(key));
			}
		} else {
			for (String key : prop.stringPropertyNames()) {
				map.put(key, prop.getProperty(key));
			}
		}
		return JacksonUtil.writeValueAsString(map);
	}

}
