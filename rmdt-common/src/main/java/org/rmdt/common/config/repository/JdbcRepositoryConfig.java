package org.rmdt.common.config.repository;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luohaipeng
 * jdbc存储技术支持相关配置
 */
@Setter@Getter
public class JdbcRepositoryConfig extends BaseRepositoryConfig {
    private String driverClassName = "com.mysql.jdbc.Driver";

    private String url;

    private String username;

    private String password;

    private int initialSize = 10;

    private int maxActive = 100;

    private int minIdle = 20;

    private int maxWait = 60000;

    private int timeBetweenEvictionRunsMillis = 60000;

    private int minEvictableIdleTimeMillis = 300000;

    private String validationQuery = " SELECT 1 ";

    private Boolean testOnBorrow = false;

    private Boolean testOnReturn = false;

    private Boolean testWhileIdle = true;

    private Boolean poolPreparedStatements = false;

    private int maxPoolPreparedStatementPerConnectionSize = 100;
}
