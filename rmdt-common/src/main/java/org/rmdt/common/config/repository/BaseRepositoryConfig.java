package org.rmdt.common.config.repository;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luohaipeng
 * 事务日志存储技术支持相关配置信息的父类
 * 具体的子类需要哪些配置属性，由各自存储技术决定
 */
@Setter@Getter
public class BaseRepositoryConfig {

    /**
     * 事务日志数据存储模型名（注意：用户配置的只是后缀，到时候需要在程序中拼上前缀，构成全名）
     * 由于不管使用哪种具体存储技术，都需要模型名，所以该属性放到了该父类中
     */
    protected String reposirotyModelSuffix;
}
