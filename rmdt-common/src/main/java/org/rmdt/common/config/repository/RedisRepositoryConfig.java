package org.rmdt.common.config.repository;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luohaipeng
 * Redis存储技术支持相关配置
 */
@Setter@Getter
public class RedisRepositoryConfig extends BaseRepositoryConfig {
    private String url;
    private String username;
    private String password;
}
