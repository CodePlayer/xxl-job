package com.xxl.job.admin.core.route;

import java.util.List;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {

	protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

	/**
	 * route address
	 *
	 * @return ReturnT.content=address
	 */
	public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);

}
