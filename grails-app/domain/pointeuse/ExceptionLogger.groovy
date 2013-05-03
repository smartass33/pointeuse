package pointeuse

class ExceptionLogger {

	Date time
	String stacktrace
	//String detailedMessage
	//String fileName
	//String className
	
	ExceptionLogger(Exception exception){	
		this.time= new Date()
		this.stacktrace = exception.stackTrace.toString()
		//this.detailedMessage = exception.detailedMessage.toString()
		//this.fileName = exception.fileName.toString()
		//this.className = exception.className.toString()
	}
	
    static constraints = {
		stacktrace size: 1..5000
    }
}
