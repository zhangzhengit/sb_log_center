package com.vo.conf;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日志中心配置项
 *
 * @author zhangzhen
 * @date 2023年5月17日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "conf")
@Validated
public class Conf {

	@NotEmpty(message = "conf.secretKey 不能配置为空")
	private String secretKey;

}
