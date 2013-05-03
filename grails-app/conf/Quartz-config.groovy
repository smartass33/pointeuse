import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;
import grails.plugin.quartz2.InvokeMethodJob

grails.plugin.quartz2.autoStartup = true

org{
	quartz{
		autoStartup = true
		//anything here will get merged into the quartz.properties so you don't need another file
		scheduler.instanceName = 'MyAppScheduler'
		threadPool.class = 'org.quartz.simpl.SimpleThreadPool'
		threadPool.threadCount = 20
		threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
		jobStore.class = 'org.quartz.simpl.RAMJobStore'
	}
}

//you can drive the setup. just give them a unique key like "buyTheTicket" below.
//the quartzScheduler bean and application context are passed to your closure

