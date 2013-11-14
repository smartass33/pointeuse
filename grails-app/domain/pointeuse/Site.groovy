package pointeuse

import java.util.Date;

class Site {
	
	String name
	User user
	Date loggingDate
	double longitude
	double latitude
	String address
	String town
	int postCode
	
	
	static hasMany = [employees: Employee]
	
	//static searchable = true//{ only: ['name'] } 
	
    static constraints = {
		name blank: false
		name unique:true
		latitude blank: true
		longitude blank:true
		address blank:true
		town blank:true
		postCode blank:true
		
    }
}
