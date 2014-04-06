package pointeuse


class Status {

	Employee employee
	Date date
	//Boolean isOut
	StatusType type
	Date loggingDate
	static belongsTo = [employee:Employee]
	
	
    static constraints = {
		date(nullable:true)
		loggingDate(nullable:true)
		
		
    }
	
	String toString(){
		return 'status: '+ this.employee.lastName+' '+this.type+'-'+this.date
	}
}
