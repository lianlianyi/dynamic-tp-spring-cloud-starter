package io.lyh.dtp.monitor;

import com.google.common.collect.Lists;
import io.lyh.dtp.common.em.NotifyTypeEnum;
import io.lyh.dtp.config.DtpProperties;
import io.lyh.dtp.core.DtpExecutor;
import io.lyh.dtp.core.DtpRegistry;
import io.lyh.dtp.core.NamedThreadFactory;
import io.lyh.dtp.handler.CollectorHandler;
import io.lyh.dtp.notify.AlarmManager;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DtpMonitor related
 *
 * @author: yanhom
 * @since 1.0.0
 **/
@Slf4j
public class DtpMonitor implements ApplicationRunner {

    private static final ScheduledExecutorService MONITOR_EXECUTOR = new ScheduledThreadPoolExecutor(
            1,
            new NamedThreadFactory("dtp-monitor", true));

    @Resource
    private DtpProperties dtpProperties;

    @Override
    public void run(ApplicationArguments args) {
        MONITOR_EXECUTOR.scheduleWithFixedDelay(this::run,
                0, dtpProperties.getMonitorInterval(), TimeUnit.SECONDS);
    }

    private void run() {
        try {
            val names = DtpRegistry.listAllDtpNames();
            names.forEach(x -> {
                DtpExecutor executor = DtpRegistry.getExecutor(x);
                AlarmManager.triggerAlarm(
                        () -> AlarmManager.doAlarm(executor,
                                Lists.newArrayList(NotifyTypeEnum.LIVENESS, NotifyTypeEnum.CAPACITY)));

                if (dtpProperties.isEnabledCollect()) {
                    CollectorHandler.getInstance().collect(executor, dtpProperties.getCollectorType());
                }
            });
        } catch (Exception e) {
            log.error("DynamicTp monitor, run error...", e);
        }
    }
}