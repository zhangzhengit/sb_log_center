package com.vo.api;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.valueextraction.Unwrapping.Skip;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vo.conf.Conf;
import com.vo.entity.AddDTO;
import com.vo.entity.AddRequest;
import com.vo.entity.AppEntity;
import com.vo.entity.AppService;
import com.vo.entity.LogEntity;
import com.vo.entity.LogRepository;
import com.vo.entity.LogService;
import com.votool.common.CR;
import com.votool.common.ZPU;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月24日
 *
 */
@Controller
public class API implements InitializingBean {

	@Autowired
	private Conf conf;
	@Autowired
	private AppService appService;
	@Autowired
	private LogRepository logRepository;
	@Autowired
	private LogService logService;

	@GetMapping
	public String index(final Model model, @RequestParam(required = false) final String k,
			@RequestParam(required = false,defaultValue = "-1") final Integer app) {

		this.initAppList(model);

		final boolean done = LogService.indexDone.get();
		if (!done) {
			model.addAttribute("message", "提示：正在建立索引，请等待完成后重试【搜索】");
			model.addAttribute("list", Collections.emptyList());
			model.addAttribute("k", k);
			model.addAttribute("t", 1);
			return "index";
		}

		if (StringUtils.isEmpty(k)) {
			model.addAttribute("list", Collections.emptyList());
			model.addAttribute("t", 1);
			return "index";
		}

		// FIXME 2022年12月15日 下午10:27:57 zhanghen: 对命中k的关键字修改css
		final List<LogEntity> list = this.logService.search(k, app);

		model.addAttribute("list", list);
		model.addAttribute("k", k);
		model.addAttribute("t", 2);

		return "index";
	}



	private void initAppList(final Model model) {
		final List<AppEntity> appList = this.appService.findAll();
		model.addAttribute("appList", appList);
	}


	@PostMapping
	@ResponseBody
	public CR<?> add(@RequestBody final AddRequest addRequest,@RequestHeader final String secretKey) {

		if (!Objects.equals(this.conf.getSecretKey(), secretKey)) {
			return CR.error("secretKey错误");
		}

		final List<byte[]> l = addRequest.getList();
		for (final byte[] bs : l) {
			final LogEntity one = one(bs);
			this.logRepository.save(one);
		}

		return CR.ok();
	}

	private static LogEntity one(final byte[] bs) {
		final AddDTO addDTO = ZPU.deserialize(bs, AddDTO.class);
		final LogEntity entity = new LogEntity();
		entity.setCreateTime(new Date());
		entity.setContent(addDTO.getK());
		entity.setAppId(addDTO.getAppId());
		entity.setAppName(addDTO.getAppName());
		return entity;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "API.afterPropertiesSet()");

	}

}
