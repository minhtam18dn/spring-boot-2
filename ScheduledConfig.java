package com.dsoft.m2u.config;

import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import com.dsoft.m2u.push.notification.service.PushNotifiService;
import com.dsoft.m2u.service.TokenService;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {
	
	private static final Logger logger = LogManager.getLogger(ScheduledConfig.class);
	
	@Autowired
	private PushNotifiService pushNotificationService;
	@Autowired
	private TokenService tokenService;

	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(100);
		threadPoolTaskScheduler.setThreadNamePrefix("scheduled-task-pool-");
		threadPoolTaskScheduler.initialize();

		scheduledTaskRegistrar.afterPropertiesSet();
		scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

		Runnable runnable = () -> {
			logger.info("Start Push Notification"+LocalDateTime.now());
			pushNotificationService.scheduleTaskWithFixedRate();
			tokenService.deleteTokenPeriodic();
		};
		CronTask task = new CronTask(runnable,
				new CronTrigger("0 0 16 * * *", TimeZone.getTimeZone("Asia/Ho_Chi_Minh")));

		scheduledTaskRegistrar.addCronTask(task);
	}

	public void destroy() throws Exception {
		if (executor != null) {
			executor.shutdownNow();
		}
	}
}
