/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.context;

import com.kwxyzk.message.Message;
import lombok.Data;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
@Data
public class WorkContext {

    private KSocket socket;

    private Message readMessage;

    private Message writeMessage;


}
