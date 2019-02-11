package org.rmdt.dubbo.interceptor;

import org.rmdt.core.interceptor.AbstractRmdtTransactionAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luohaipeng
 * dubbo框架的aop切面
 */
@Component
public class DubboRmdtTransactionAspect extends AbstractRmdtTransactionAspect {

    @Autowired
    public DubboRmdtTransactionAspect(DubboRmdtTransactionInterceptor dubboRmdtTransactionInterceptor){
        super.setRmdtTransactonInterceptor(dubboRmdtTransactionInterceptor);
    }

}
