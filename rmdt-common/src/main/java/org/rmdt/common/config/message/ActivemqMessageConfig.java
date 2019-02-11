package org.rmdt.common.config.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luohaipeng
 */
@Setter@Getter
public class ActivemqMessageConfig extends BaseMessageConfig{

    private String url;
    private String userName;
    private String password;
}
