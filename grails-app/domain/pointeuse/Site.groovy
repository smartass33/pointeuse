package pointeuse


class Site {
	
	String name
	User user
	Date loggingDate
	double longitude
	double latitude
	String address
	String town
	int postCode
	CardTerminal cardTerminal
	
	
	static hasMany = [employees: Employee]
	
	//static searchable = true//{ only: ['name'] } 
	
    static constraints = {
		cardTerminal blank:true
		name blank: false
		name unique:true
		//latitude blank: true
		//longitude blank:true
		address blank:true
		town blank:true
		//postCode blank:true
		
    }
}
