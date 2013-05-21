package pointeuse

import java.util.Date;

class Site {
	
	String name
	User user
	Date loggingDate
	//float longitude
	//float latitude
	
	
	static hasMany = [employees: Employee]
	
	//static searchable = true//{ only: ['name'] } 
	
    static constraints = {
		name blank: false
		name unique:true
	//	latitude blank: true
	//	longitude blank:true
		
    }
}
