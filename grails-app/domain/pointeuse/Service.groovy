package pointeuse

class Service {
	
	String name
	static hasMany = [employees: Employee]
	static constraints = {
		name blank: false
		
    }
}
