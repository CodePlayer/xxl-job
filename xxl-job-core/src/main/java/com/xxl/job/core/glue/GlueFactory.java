package com.xxl.job.core.glue;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.xxl.job.core.glue.impl.SpringGlueFactory;
import com.xxl.job.core.handler.IJobHandler;
import groovy.lang.GroovyClassLoader;
import org.springframework.util.StringUtils;

/**
 * glue factory, product class/object by name
 *
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory {

	private static GlueFactory glueFactory = new GlueFactory();

	public static GlueFactory getInstance() {
		return glueFactory;
	}

	public static void refreshInstance(int type) {
		if (type == 0) {
			glueFactory = new GlueFactory();
		} else if (type == 1) {
			glueFactory = new SpringGlueFactory();
		}
	}

	/**
	 * groovy class loader
	 */
	private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
	private ConcurrentMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

	/**
	 * load new instance, prototype
	 */
	public IJobHandler loadNewInstance(String codeSource) throws Exception {
		if (StringUtils.hasText(codeSource)) {
			Class<?> clazz = getCodeSourceClass(codeSource);
			if (clazz != null) {
				Object instance = clazz.newInstance(); // instance 不会为 null
				if (instance instanceof IJobHandler) {
					this.injectService(instance);
					return (IJobHandler) instance;
				} else {
					throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, " + "cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
				}
			}
		}
		throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
	}

	private Class<?> getCodeSourceClass(String codeSource) {
		try {
			// md5
			byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
			String md5Str = new BigInteger(1, md5).toString(16);

			Class<?> clazz = CLASS_CACHE.get(md5Str);
			if (clazz == null) {
				clazz = groovyClassLoader.parseClass(codeSource);
				CLASS_CACHE.putIfAbsent(md5Str, clazz);
			}
			return clazz;
		} catch (Exception e) {
			return groovyClassLoader.parseClass(codeSource);
		}
	}

	/**
	 * inject service of bean field
	 */
	public void injectService(Object instance) {
		// do something
	}

}
