package pointeuse

class EventLog {
	final static int SOURCE_MAXSIZE = 255
	final static int MESSAGE_MAXSIZE = 1000
	final static int DETAILS_MAXSIZE = 4000
 
	Date dateCreated
 
	String message
	String details
	String source
 
	// did someone look at this error?
	boolean cleared = false
 
	static constraints = {
		source(blank: false, nullable: false, maxSize: SOURCE_MAXSIZE)
		message(blank: false, nullable: false, maxSize: MESSAGE_MAXSIZE)
		details(blank: true, nullable: true, maxSize: DETAILS_MAXSIZE)
	}
 
	static mapping = {
		sort "dateCreated"
	}

}
