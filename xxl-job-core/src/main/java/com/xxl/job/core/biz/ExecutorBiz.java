package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.*;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

	/**
	 * beat
	 */
	public ReturnT<String> beat();

	/**
	 * idle beat
	 */
	public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

	/**
	 * run
	 */
	public ReturnT<String> run(TriggerParam triggerParam);

	/**
	 * kill
	 */
	public ReturnT<String> kill(KillParam killParam);

	/**
	 * log
	 */
	public ReturnT<LogResult> log(LogParam logParam);

}
