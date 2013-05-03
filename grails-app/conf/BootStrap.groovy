import org.apache.log4j.Logger
import java.util.Calendar
import pointeuse.InAndOutCLosingJob

class BootStrap {

	
    def init = { servletContext ->
		log.error 'executing bootstrap'
		def calendar = Calendar.instance		
		calendar.set(Calendar.HOUR_OF_DAY,22)
		calendar.set(Calendar.MINUTE,0)
		log.error 'registring InAndOutCLosingJob at '+calendar.time
		InAndOutCLosingJob.schedule(calendar.time)
    }
    def destroy = {
    }
}
