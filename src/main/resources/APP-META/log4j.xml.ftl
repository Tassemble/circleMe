<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

	<appender name="CONSOLE" class="org.apache.log4j.ConsoleAppender">
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%p [%t] %c{1}.%M(%L) | %m%n" />
		</layout>
	</appender>
	
	<appender name="DailyRollingFile" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="threshold" value="info" />
		<param name="file" value="${log_dir_root}/default.log" />
		<param name="append" value="true" />
		<param name="datePattern" value="'.'yyyy-MM-dd" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p  [%c] %L %m%n" />
		</layout>
	</appender>
	
	<appender name="swf_module_log_appender" class="org.apache.log4j.DailyRollingFileAppender">
		<#if dev_mode=='product'>
		      <param name="threshold" value="error" />
		<#else>
		     <param name="threshold" value="info" />
		</#if>
		<param name="file" value="${log_dir_root}/pdf/pdf.log" />
		<param name="append" value="true" />
		<param name="datePattern" value="'.'yyyy-MM-dd" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p  [%c] %L %m%n" />
		</layout>
	</appender>
	
	<appender name="message_log_name_appender" class="org.apache.log4j.DailyRollingFileAppender">
		<#if dev_mode=='product'>
		      <param name="threshold" value="error" />
		<#else>
		     <param name="threshold" value="info" />
		</#if>
		<param name="file" value="${log_dir_root}/message/message.log" />
		<param name="append" value="true" />
		<param name="datePattern" value="'.'yyyy-MM-dd" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p  [%c] %L %m%n" />
		</layout>
	</appender>
	
	<appender name="video_log_name_appender" class="org.apache.log4j.DailyRollingFileAppender">
		<#if dev_mode=='product'>
		      <param name="threshold" value="error" />
		<#else>
		     <param name="threshold" value="info" />
		</#if>
		<param name="file" value="${log_dir_root}/video/video.log" />
		<param name="append" value="true" />
		<param name="datePattern" value="'.'yyyy-MM-dd" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p  [%c] %L %m%n" />
		</layout>
	</appender>
	
		<appender name="ask_module_log_appender" class="org.apache.log4j.DailyRollingFileAppender">
		<#if dev_mode=='product'>
		      <param name="threshold" value="error" />
		<#else>
		     <param name="threshold" value="info" />
		</#if>
		<param name="file" value="${log_dir_root}/ask/ask.log" />
		<param name="append" value="true" />
		<param name="datePattern" value="'.'yyyy-MM-dd" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p  [%c] %L %m%n" />
		</layout>
	</appender>
	
	<appender name="mail_task_log_appender" class="org.apache.log4j.DailyRollingFileAppender">
		<#if dev_mode=='product'>
		      <param name="threshold" value="error" />
		<#else>
		     <param name="threshold" value="info" />
		</#if>
		<param name="file" value="${log_dir_root}/mailtask/mailtask.log" />
		<param name="append" value="true" />
		<param name="datePattern" value="'.'yyyy-MM-dd" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p  [%c] %L %m%n" />
		</layout>
	</appender>
	
		<category name="com.netease.edu.ask" additivity="false">
		<#if dev_mode=='product'>
		     <level value="error" />
		<#else>
		     <level value="info" />
		     <appender-ref ref="CONSOLE" />     
		</#if>
		<appender-ref ref="ask_module_log_appender" />
	</category>
	
	<category name="com.netease.edu.mail" additivity="false">
		<#if dev_mode=='product'>
		     <level value="error" />
		<#else>
		     <level value="info" />
		     <appender-ref ref="CONSOLE" />     
		</#if>
		<appender-ref ref="mail_task_log_appender" />
	</category>
	
	<category name="com.netease.edu.swf" additivity="false">
		<#if dev_mode=='product'>
		     <level value="error" />
		<#else>
		     <level value="info" />
		     <appender-ref ref="CONSOLE" />     
		</#if>
		<appender-ref ref="swf_module_log_appender" />
	</category>
	
	<logger name="message_log_name" additivity="false">
		<#if dev_mode=='product'>
		     <level value="error" />
		<#else>
		     <level value="info" />
		     <appender-ref ref="CONSOLE" />     
		</#if>
		<appender-ref ref="message_log_name_appender" />
	</logger>
	

	
	<logger name="video_log_name" additivity="false">
		<#if dev_mode=='product'>
		     <level value="error" />
		<#else>
		     <level value="info" />
		     <appender-ref ref="CONSOLE" />     
		</#if>
		<appender-ref ref="video_log_name_appender" />
	</logger>
	

	

	
	<root>
		<level value="WARN" />
		<appender-ref ref="CONSOLE" />
		<appender-ref ref="DailyRollingFile" />
	</root>

</log4j:configuration>
