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

	static hasMany = [employees: Employee,users:User]

    static constraints = {
		cardTerminal nullable:true	
		name blank: false
		name unique:true
		//latitude blank: true
		//longitude blank:true
		address blank:true
		town blank:true
		//postCode blank:true
		
    }
	
	String toString(){
		return this.name
	}
}
