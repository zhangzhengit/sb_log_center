package com.vo.entity;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年12月15日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class AddDTO {

	private Integer appId;

	private String appName;

	private String k;

}
