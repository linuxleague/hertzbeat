package org.dromara.hertzbeat.common.queue.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * queue0 start ---offer---> data1 ---> data2 ---> data3 ---poll---
 *         -------------------------------------------------------'
 * queue1 '---offer---> data3 ---> data4 ---> data5 ---poll--------
 *         -------------------------------------------------------'
 * queue2 '---offer---> data5 ---> data6 ---> data7 ---poll---> end
 *
 * @author ceilzcx
 * @since 23/5/2023
 */
public class MultiCommonDataQueue<T> {

    /**
     * 数据存储的Queue集合
     */
    private final List<LinkedBlockingQueue<T>> dataList;

    /**
     * 消费者消费偏移量
     */
    private final int[] offsetList;

    public MultiCommonDataQueue(int size) {
        this.dataList = new ArrayList<>(size);
        this.offsetList = new int[size];

        for (int i = 0; i < size; i++) {
            offsetList[i] = 0;
            dataList.add(new LinkedBlockingQueue<>());
        }
    }

    /**
     * 向Queue中添加数据, 只往第一个Queue添加
     * @param data 采集数据
     */
    public void offer(T data) {
        dataList.get(0).offer(data);
    }

    /**
     * Queue消费数据
     * 思路: 先确认应该消费哪个Queue, 根据偏移量计算; 成功获取数据后, 修改偏移量, 并将数据放入下一个Queue
     * @param i 消费者index
     * @return 采集数据
     * @throws InterruptedException 中断异常
     */
    public synchronized T poll(int i) throws InterruptedException {
        // 消费第几个Queue, offset越小代表消费的数据越少, 更应该消费靠下的Queue
        int position = 0;
        int presentOffset = this.offsetList[i];
        for (int offset : this.offsetList) {
            if (offset > presentOffset) {
                position++;
            }
        }
        LinkedBlockingQueue<T> queue = this.dataList.get(position);
        T data = queue.poll(2, TimeUnit.SECONDS);
        if (data != null) {
            // 消费数据不为null, 偏移量+1
            this.offsetList[i]++;
            if (position + 1 < this.dataList.size()) {
                // 将消费的数据放入下一个Queue
                this.dataList.get(position + 1).offer(data);
            }
        }
        return data;
    }

    public int[] getQueueSizeMetricsInfo() {
        int[] queueSizeInfo = new int[dataList.size()];
        int minOffset = offsetList[0];
        for (int i = 1; i < offsetList.length; i++) {
            minOffset = Math.min(minOffset, offsetList[i]);
        }
        int sumSize = dataList.stream().mapToInt(LinkedBlockingQueue::size).sum();
        for (int i = 0; i < offsetList.length; i++) {
            queueSizeInfo[i] = sumSize - offsetList[i] + minOffset;
        }
        return queueSizeInfo;
    }
}