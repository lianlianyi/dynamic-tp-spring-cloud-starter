package io.lyh.dtp.monitor.collector;

import io.lyh.dtp.common.em.CollectorTypeEnum;
import io.lyh.dtp.core.DtpExecutor;
import io.lyh.dtp.monitor.MetricsHelper;
import io.lyh.dtp.monitor.ThreadPoolMetrics;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * MicroMeterCollector related
 *
 * @author: yanhom
 * @since 1.0.0
 */
@Slf4j
public class MicroMeterCollector extends AbstractCollector {

    @Override
    public void collect(DtpExecutor executor) {
        gauge(MetricsHelper.getMetrics(executor));
    }

    @Override
    public String type() {
        return CollectorTypeEnum.MICROMETER.name();
    }

    public void gauge(ThreadPoolMetrics metrics) {

        Iterable<Tag> TAG = Collections.singletonList(Tag.of("thread.pool.name", metrics.getDtpName()));
        Metrics.gauge("thread.pool.core.size", TAG, metrics, ThreadPoolMetrics::getCorePoolSize);
        Metrics.gauge("thread.pool.maximum.size", TAG, metrics, ThreadPoolMetrics::getMaximumPoolSize);
        Metrics.gauge("thread.pool.current.size", TAG, metrics, ThreadPoolMetrics::getPoolSize);
        Metrics.gauge("thread.pool.largest.size", TAG, metrics, ThreadPoolMetrics::getLargestPoolSize);
        Metrics.gauge("thread.pool.active.count", TAG, metrics, ThreadPoolMetrics::getActiveCount);

        Metrics.gauge("thread.pool.task.count", TAG, metrics, ThreadPoolMetrics::getTaskCount);
        Metrics.gauge("thread.pool.completed.task.count", TAG, metrics, ThreadPoolMetrics::getCompletedTaskCount);
        Metrics.gauge("thread.pool.wait.task.count", TAG, metrics, ThreadPoolMetrics::getWaitTaskCount);

        Metrics.gauge("thread.pool.queue.size", TAG, metrics, ThreadPoolMetrics::getQueueSize);
        Metrics.gauge("thread.pool.queue.capacity", TAG, metrics, ThreadPoolMetrics::getQueueCapacity);
        Metrics.gauge("thread.pool.queue.remaining.capacity", TAG, metrics, ThreadPoolMetrics::getQueueRemainingCapacity);

        Metrics.gauge("thread.pool.reject.count", TAG, metrics, ThreadPoolMetrics::getRejectCount);
    }
}
