package pointeuse

class SupplementaryTime {
	Employee employee
	Date loggingTime
	User user
	double value
	SupplementaryType type
	Period period
	
    static constraints = {
    }
	
	String toString(){
		return (this.period).toString() +' - ' + this.type+' : '+this.value
	}
}
