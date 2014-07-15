package pointeuse

class Period {

	int year
	static hasMany = [bankHolidays:BankHoliday]
    static constraints = {
		year (unique: true)
		bankHolidays nullable: true
    }

	String toString(){
		return this.year+'/'+(this.year+1)
	}
}
