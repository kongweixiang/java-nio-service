/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.context;

import lombok.Data;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
@Data
public class WorkContext {

    /**
     * 单个任务处理的io连接数
     */
    public static int socketProcessNum = 10;


}
