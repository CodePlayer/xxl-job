package com.xxl.job.core.enums;

import com.xxl.job.core.util.XxlJobTool;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorBlockStrategyEnum {

	SERIAL_EXECUTION("Serial execution"),
	/*CONCURRENT_EXECUTION("并行"),*/
	DISCARD_LATER("Discard Later"),
	COVER_EARLY("Cover Early");

	private String title;

	ExecutorBlockStrategyEnum(String title) {
		this.title = title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
		return XxlJobTool.getEnum(ExecutorBlockStrategyEnum.class, name, defaultItem);
	}
}