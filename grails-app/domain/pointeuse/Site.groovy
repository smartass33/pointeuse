package pointeuse

class Site {
	
	String name
	
	static hasMany = [employees: Employee]
	
	//static searchable = true//{ only: ['name'] } 
	
    static constraints = {
		name blank: false
		
    }
}
