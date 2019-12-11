/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk;

import com.kwxyzk.message.BlockQueueIntPointer;
import org.junit.Test;

/**
 * @author kongweixiang
 * @date 2019/11/6
 * @since 1.0.0
 */
public class BlockQueueIntPointerTest {


    @Test
    public void addPoll() {
        BlockQueueIntPointer pointer = new BlockQueueIntPointer(10);
        pointer.add(10);
        pointer.add(10);
        pointer.add(10);
        pointer.add(10);
        for (int i = 0; i < 100; i++) {
            if (!pointer.add(10)) {
                System.out.println("写入失败");
                break;
            }
            if (pointer.poll() == -1) {
                System.out.println("读取失败");
            }

        }
    }
}
