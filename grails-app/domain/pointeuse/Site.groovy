package pointeuse


class Site {
	String name
	Date loggingDate
	double longitude
	double latitude
	String address
	String town
	int postCode
	CardTerminal cardTerminal
	Date lastReportDate
	
	static hasMany = [employees: Employee,users:User,weeklyCases:WeeklyCase]

    static constraints = {
		cardTerminal nullable:true	
		name blank: false
		name unique:true
		address blank:true
		town blank:true	
		lastReportDate nullable:true
    }
	
	String toString(){
		return this.name
	}
}
