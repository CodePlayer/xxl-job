package com.xxl.job.core.executor.impl;

import java.lang.reflect.Method;
import java.util.Map;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * xxl-job executor (for spring)
 *
 * @author xuxueli 2018-11-01 09:24:52
 */
public class XxlJobSpringExecutor extends XxlJobExecutor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(XxlJobSpringExecutor.class);
	// 将该变量放在一个与 GlueFactory 更密切的类中，会有更合理的层次体系结构，不过需要额外更多的代码量，在此也无伤大雅
	public static volatile boolean groovyRefreshRequired;

	// start
	@Override
	public void afterSingletonsInstantiated() {

		// init JobHandler Repository
		/*initJobHandlerRepository(applicationContext);*/

		// init JobHandler Repository (for method)
		initJobHandlerMethodRepository(applicationContext);

		// refresh GlueFactory
		groovyRefreshRequired = true;

		// super start
		try {
			super.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// destroy
	@Override
	public void destroy() {
		super.destroy();
	}


    /*private void initJobHandlerRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }

        // init job handler action
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(JobHandler.class);

        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof IJobHandler) {
                    String name = serviceBean.getClass().getAnnotation(JobHandler.class).value();
                    IJobHandler handler = (IJobHandler) serviceBean;
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
                    }
                    registJobHandler(name, handler);
                }
            }
        }
    }*/

	private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
		if (applicationContext == null) {
			return;
		}
		// init job handler from method
		String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
		for (String beanDefinitionName : beanDefinitionNames) {

			// get bean
			Lazy onBean = applicationContext.findAnnotationOnBean(beanDefinitionName, Lazy.class);
			if (onBean != null) {
				logger.debug("xxl-job annotation scan, skip @Lazy Bean:{}", beanDefinitionName);
				continue;
			}
			Object bean = applicationContext.getBean(beanDefinitionName);

			// filter method
			Map<Method, XxlJob> annotatedMethods = null;   // referred to ：org.springframework.context.event.EventListenerMethodProcessor.processBean
			try {
				annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
						(MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
			} catch (Throwable ex) {
				logger.error("xxl-job method-jobhandler resolve error for bean[" + beanDefinitionName + "].", ex);
			}
			if (annotatedMethods == null || annotatedMethods.isEmpty()) {
				continue;
			}

			// generate and regist method job handler
			for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
				Method executeMethod = methodXxlJobEntry.getKey();
				XxlJob xxlJob = methodXxlJobEntry.getValue();
				// regist
				registJobHandler(xxlJob, bean, executeMethod);
			}

		}
	}

	// ---------------------- applicationContext ----------------------
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		XxlJobSpringExecutor.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

    /*
    BeanDefinitionRegistryPostProcessor
    registry.getBeanDefine()
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }
    * */

}