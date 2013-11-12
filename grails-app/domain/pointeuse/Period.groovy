package pointeuse

class Period {

	int year
	
	static hasMany = [bankHolidays:BankHoliday]
    static constraints = {
		year (unique: true,blank: false)
		bankHolidays nullable: true
    }
	
	
	String toString(){
		return this.year+'/'+(this.year+1)
	}
}
