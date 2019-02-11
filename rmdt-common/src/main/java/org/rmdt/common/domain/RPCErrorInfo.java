package org.rmdt.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 保存RPC接口调用时的报错信息
 */
@Setter@Getter
public class RPCErrorInfo {

    private Integer code = 0;//1是有错误的情况
    private String errorMsg;
}
