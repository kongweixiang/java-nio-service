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

    public static int KB = 1024;
    public static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL  =   4  * KB;
    private static final int CAPACITY_MEDIUM = 128  * KB;
    private static final int CAPACITY_LARGE  = 1024 * KB;


}
