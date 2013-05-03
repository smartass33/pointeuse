package pointeuse

import grails.util.*;
import org.apache.log4j.Logger

class ErrorsController {
    def serverError = {
        def env = GrailsUtil.environment;
		def exception = request.exception
		def exceptionLogger = new ExceptionLogger(exception)
		exceptionLogger.save(flush: true)
		
		Logger log = Logger.getInstance(ErrorsController.class)
		log.error('error with application: '+exception.toString())
		
        if(env == "production"){
			//render(view:'/error')
            render(view:'/serverError')
        }
        else {
           render(view:'/error')
			//render(view:'/serverError')
        }
    }

    def notFound = {
        render(view:'/notFound')
    }
}