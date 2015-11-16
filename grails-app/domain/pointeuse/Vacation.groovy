package pointeuse


class Vacation {
	Employee employee
	Date loggingTime
	User user
	int counter
	VacationType type
	Period period
	
	static belongsTo = [employee:Employee]
	
	String toString(){
		return (this.period).toString() +' - ' + this.type+' : '+this.counter
	}
}
