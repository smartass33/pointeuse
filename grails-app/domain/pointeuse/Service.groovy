package pointeuse

class Service {
	
	String name
	
	static hasMany = [employees: Employee]
	
	//static searchable = true
	
    static constraints = {
		name blank: false
		
    }
}
