package com.vo.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vo.entity.LogEntity;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月24日
 *
 */
public interface LogRepository extends JpaRepository<LogEntity, Long>{

}
