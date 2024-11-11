package com.xxl.job.core.biz.client;

import java.util.List;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.XxlJobRemotingUtil;

/**
 * admin api test
 *
 * @author xuxueli 2017-07-28 22:14:52
 */
public class AdminBizClient implements AdminBiz {

	public AdminBizClient() {
	}

	public AdminBizClient(String addressUrl, String accessToken) {
		this.addressUrl = addressUrl;
		this.accessToken = accessToken;

		// valid
		if (!this.addressUrl.endsWith("/")) {
			this.addressUrl = this.addressUrl + "/";
		}
	}

	private String addressUrl;
	private String accessToken;
	private final int timeout = 3;

	@Override
	public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/callback", accessToken, timeout, callbackParamList, XxlJobRemotingUtil.stringTypeRef);
	}

	@Override
	public ReturnT<String> registry(RegistryParam registryParam) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/registry", accessToken, timeout, registryParam, XxlJobRemotingUtil.stringTypeRef);
	}

	@Override
	public ReturnT<String> registryRemove(RegistryParam registryParam) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/registryRemove", accessToken, timeout, registryParam, XxlJobRemotingUtil.stringTypeRef);
	}

}