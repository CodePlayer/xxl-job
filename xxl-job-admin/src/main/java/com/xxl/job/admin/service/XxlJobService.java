package com.xxl.job.admin.service;

import java.util.Date;
import java.util.Map;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * core job action for xxl-job
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
public interface XxlJobService {

	/**
	 * page list
	 */
	public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

	/**
	 * add job
	 */
	public ReturnT<String> add(XxlJobInfo jobInfo);

	/**
	 * update job
	 */
	public ReturnT<String> update(XxlJobInfo jobInfo);

	/**
	 * remove job
	 * *
	 */
	public ReturnT<String> remove(int id);

	/**
	 * start job
	 */
	public ReturnT<String> start(int id);

	/**
	 * stop job
	 */
	public ReturnT<String> stop(int id);

	/**
	 * trigger
	 */
	public ReturnT<String> trigger(XxlJobUser loginUser, int jobId, String executorParam, String addressList);

	/**
	 * dashboard info
	 */
	public Map<String, Object> dashboardInfo();

	/**
	 * chart info
	 */
	public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);

}
