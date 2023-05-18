package com.vo.entity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年12月17日
 *
 */
@org.springframework.stereotype.Service
public class AppService {

	@Autowired
	private AppRepository appRepository;

	public List<AppEntity> findAll() {
		return this.appRepository.findAll();
	}

}
