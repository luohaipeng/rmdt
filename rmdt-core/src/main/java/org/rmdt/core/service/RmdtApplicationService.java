package org.rmdt.core.service;

/**
 * @author luohaipeng
 * 提供应用相关的服务,具体实现有各个RPC框架实现
 */
public interface RmdtApplicationService {

    /**
     * 获取应用名
     */
    String appName();
}
