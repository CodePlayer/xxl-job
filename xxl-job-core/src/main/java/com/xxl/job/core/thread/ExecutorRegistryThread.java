package com.xxl.job.core.thread;

import java.util.concurrent.TimeUnit;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by xuxueli on 17/3/2.
 */
public class ExecutorRegistryThread {

	private static final Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

	private static final ExecutorRegistryThread instance = new ExecutorRegistryThread();

	public static ExecutorRegistryThread getInstance() {
		return instance;
	}

	private Thread registryThread;
	private volatile boolean toStop = false;

	public void start(final String appname, final String address) {

		// valid
		if (!StringUtils.hasText(appname)) {
			logger.warn(">>>>>>>>>>> xxl-job, executor registry config fail, appname is null.");
			return;
		}
		if (XxlJobExecutor.getAdminBizList() == null) {
			logger.warn(">>>>>>>>>>> xxl-job, executor registry config fail, adminAddresses is null.");
			return;
		}

		registryThread = new Thread(() -> {

			// registry
			while (!toStop) {
				try {
					RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appname, address);
					for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
						try {
							ReturnT<String> registryResult = adminBiz.registry(registryParam);
							if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
								registryResult = ReturnT.SUCCESS;
								logger.debug(">>>>>>>>>>> xxl-job registry success, registryParam:{}, registryResult:{}", registryParam, registryResult);
								break;
							} else {
								logger.info(">>>>>>>>>>> xxl-job registry fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
							}
						} catch (Throwable e) {
							logger.info(">>>>>>>>>>> xxl-job registry error, registryParam:{}", registryParam, e);
						}

					}
				} catch (Throwable e) {
					if (!toStop) {
						logger.error(e.getMessage(), e);
					}

				}

				try {
					if (!toStop) {
						TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
					}
				} catch (Throwable e) {
					if (!toStop) {
						logger.warn(">>>>>>>>>>> xxl-job, executor registry thread interrupted, error msg:{}", e.getMessage());
					}
				}
			}

			// registry remove
			try {
				RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistType.EXECUTOR.name(), appname, address);
				for (AdminBiz adminBiz : XxlJobExecutor.getAdminBizList()) {
					try {
						ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
						if (registryResult != null && ReturnT.SUCCESS_CODE == registryResult.getCode()) {
							registryResult = ReturnT.SUCCESS;
							logger.info(">>>>>>>>>>> xxl-job registry-remove success, registryParam:{}, registryResult:{}", registryParam, registryResult);
							break;
						} else {
							logger.info(">>>>>>>>>>> xxl-job registry-remove fail, registryParam:{}, registryResult:{}", registryParam, registryResult);
						}
					} catch (Throwable e) {
						if (!toStop) {
							logger.info(">>>>>>>>>>> xxl-job registry-remove error, registryParam:{}", registryParam, e);
						}

					}

				}
			} catch (Throwable e) {
				if (!toStop) {
					logger.error(e.getMessage(), e);
				}
			}
			logger.info(">>>>>>>>>>> xxl-job, executor registry thread destroy.");

		});
		registryThread.setDaemon(true);
		registryThread.setName("xxl-job, executor ExecutorRegistryThread");
		registryThread.start();
	}

	public void toStop() {
		toStop = true;

		// interrupt and wait
		if (registryThread != null) {
			registryThread.interrupt();
			try {
				registryThread.join();
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

}