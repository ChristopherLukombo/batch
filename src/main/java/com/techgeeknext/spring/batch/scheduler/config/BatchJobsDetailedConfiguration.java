package com.techgeeknext.spring.batch.scheduler.config;


import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;


@Configuration
public class BatchJobsDetailedConfiguration {


	private final String cronDay;


	private final String cronEndDay;


	private final JobLauncher jobLauncher;


	public BatchJobsDetailedConfiguration(
			@Value("${cronDay}") String cronDay,
			@Value("${cronEndDay}") String cronEndDay,
			JobLauncher jobLauncher) {
		this.cronDay = cronDay;
		this.cronEndDay = cronEndDay;
		this.jobLauncher = jobLauncher;
	}


	@Bean(name = "reportsDetailContext")
	ApplicationContextFactory getApplicationContext() {
		return new GenericApplicationContextFactory(JobLauncherDetails.class);

	}


	@Bean(name = "reportsDetailJob")
	JobDetailFactoryBean jobDetailFactoryBean() {

		final JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();

		jobDetailFactoryBean.setJobClass(JobLauncherDetails.class);

		jobDetailFactoryBean.setDurability(true);

		final Map<String, Object> map = new HashMap<>();

		map.put("jobLauncher", jobLauncher);
		map.put("jobName", "Job1");

		jobDetailFactoryBean.setJobDataAsMap(map);

		return jobDetailFactoryBean;
	}


	@Bean(name = "reportsCronJobDay")
	CronTriggerFactoryBean cronTriggerFactoryBeanDay() {

		final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();

		cronTriggerFactoryBean.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean().getObject()));

		cronTriggerFactoryBean.setCronExpression(cronDay);
		cronTriggerFactoryBean.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Paris")));
		return cronTriggerFactoryBean;

	}

	@Bean(name = "reportsCronJobEndDay")
	CronTriggerFactoryBean cronTriggerFactoryBeanEndDay() {
		final CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();

		cronTriggerFactoryBean.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean().getObject()));

		cronTriggerFactoryBean.setCronExpression(cronEndDay);
		cronTriggerFactoryBean.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Europe/Paris")));
		return cronTriggerFactoryBean;

	}


	@Bean
	SchedulerFactoryBean schedulerFactoryBean(JobRegistry jobRegistry) {

		final SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		schedulerFactoryBean.setTriggers(cronTriggerFactoryBeanDay().getObject(), cronTriggerFactoryBeanEndDay().getObject());

		schedulerFactoryBean.setAutoStartup(true);

		final Map<String, Object> map = new HashMap<>();

		map.put("jobLauncher", jobLauncher);

		map.put("jobLocator", jobRegistry);
		schedulerFactoryBean.setSchedulerContextAsMap(map);
		return schedulerFactoryBean;

	}

}