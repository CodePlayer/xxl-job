package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.RegistryConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * job group controller
 * @author xuxueli 2016-10-02 20:52:56
 */
@Controller
@RequestMapping("/jobgroup")
public class JobGroupController {

	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobRegistryDao xxlJobRegistryDao;

	@RequestMapping
	@PermissionLimit(adminuser = true)
	public String index(Model model) {
		return "jobgroup/jobgroup.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int start,
										@RequestParam(required = false, defaultValue = "10") int length,
										String appname, String title) {

		// page query
		List<XxlJobGroup> list = xxlJobGroupDao.pageList(start, length, appname, title);
		int list_count = xxlJobGroupDao.pageListCount(start, length, appname, title);

		// package result
		Map<String, Object> maps = new HashMap<>();
		maps.put("recordsTotal", list_count);		// 总记录数
		maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
		maps.put("data", list);  					// 分页列表
		return maps;
	}

	@RequestMapping("/save")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> save(XxlJobGroup xxlJobGroup){

		// valid
		if (!StringUtils.hasText(xxlJobGroup.getAppname())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + "AppName"));
		}
		int nameLength = xxlJobGroup.getAppname().length();
		if (nameLength < 4 || nameLength > 64) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_appname_length"));
		}
		if (xxlJobGroup.getAppname().contains(">") || xxlJobGroup.getAppname().contains("<")) {
			return new ReturnT<>(500, "AppName" + I18nUtil.getString("system_unvalid"));
		}
		if (!StringUtils.hasText(xxlJobGroup.getTitle())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
		}
		if (xxlJobGroup.getTitle().contains(">") || xxlJobGroup.getTitle().contains("<")) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_title") + I18nUtil.getString("system_unvalid"));
		}
		if (xxlJobGroup.getAddressType()!=0) {
			if (!StringUtils.hasText(xxlJobGroup.getAddressList())) {
				return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
			}
			if (xxlJobGroup.getAddressList().contains(">") || xxlJobGroup.getAddressList().contains("<")) {
				return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList") + I18nUtil.getString("system_unvalid"));
			}

			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (!StringUtils.hasText(item)) {
					return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
				}
			}
		}

		// process
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupDao.save(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/update")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> update(XxlJobGroup xxlJobGroup){
		// valid
		if (!StringUtils.hasText(xxlJobGroup.getAppname())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + "AppName"));
		}
		int nameLength = xxlJobGroup.getAppname().length();
		if (nameLength < 4 || nameLength > 64) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_appname_length"));
		}
		if (!StringUtils.hasText(xxlJobGroup.getTitle())) {
			return new ReturnT<>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobgroup_field_title")));
		}
		if (xxlJobGroup.getAddressType() == 0) {
			// 0=自动注册
			List<String> registryList = findRegistryByAppName(xxlJobGroup.getAppname());
			String addressListStr = null;
			if (registryList!=null && !registryList.isEmpty()) {
				Collections.sort(registryList);
				addressListStr = String.join(",", registryList);
			}
			xxlJobGroup.setAddressList(addressListStr);
		} else {
			// 1=手动录入
			if (!StringUtils.hasText(xxlJobGroup.getAddressList())) {
				return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_addressType_limit"));
			}
			String[] addresss = xxlJobGroup.getAddressList().split(",");
			for (String item: addresss) {
				if (!StringUtils.hasText(item)) {
					return new ReturnT<>(500, I18nUtil.getString("jobgroup_field_registryList_unvalid"));
				}
			}
		}

		// process
		xxlJobGroup.setUpdateTime(new Date());

		int ret = xxlJobGroupDao.update(xxlJobGroup);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	private List<String> findRegistryByAppName(String appnameParam){
		HashMap<String, List<String>> appAddressMap = new HashMap<>();
		List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
		if (list != null) {
			for (XxlJobRegistry item: list) {
				if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
					String appname = item.getRegistryKey();
					List<String> registryList = appAddressMap.get(appname);
					if (registryList == null) {
						registryList = new ArrayList<>();
					}

					if (!registryList.contains(item.getRegistryValue())) {
						registryList.add(item.getRegistryValue());
					}
					appAddressMap.put(appname, registryList);
				}
			}
		}
		return appAddressMap.get(appnameParam);
	}

	@RequestMapping("/remove")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<String> remove(int id){

		// valid
		int count = xxlJobInfoDao.pageListCount(0, 10, id, -1,  null, null, null);
		if (count > 0) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_del_limit_0"));
		}

		List<XxlJobGroup> allList = xxlJobGroupDao.findAll();
		if (allList.size() == 1) {
			return new ReturnT<>(500, I18nUtil.getString("jobgroup_del_limit_1"));
		}

		int ret = xxlJobGroupDao.remove(id);
		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/loadById")
	@ResponseBody
	@PermissionLimit(adminuser = true)
	public ReturnT<XxlJobGroup> loadById(int id){
		XxlJobGroup jobGroup = xxlJobGroupDao.load(id);
		return jobGroup!=null? new ReturnT<>(jobGroup): new ReturnT<>(ReturnT.FAIL_CODE, null);
	}

}
