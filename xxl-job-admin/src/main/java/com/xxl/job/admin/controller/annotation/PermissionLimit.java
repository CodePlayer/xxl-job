package com.xxl.job.admin.controller.annotation;

import java.lang.annotation.*;

/**
 * 权限限制
 *
 * @author xuxueli 2015-12-12 18:29:02
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionLimit {

	/**
	 * 登录拦截 (默认拦截)
	 */
	boolean limit() default true;

	/**
	 * 要求管理员权限
	 */
	boolean adminuser() default false;

}
