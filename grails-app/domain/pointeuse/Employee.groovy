package pointeuse

class Employee{

	static final float Pentecote=7/12
	static final float legalWeekTime=35
	static final float WeekOpenedDays=6
	static final int entryPerDay=6
	String firstName
	String lastName
	String userName
	String matricule
	Function function
	Site site
	Service service
	float weeklyContractTime //35H par défaut. 
	boolean hasError
	Date arrivalDate
	Status status
	
	static hasMany = [contracts:Contract,vacationsCounters:Vacation,absenceCounters:AbsenceCounter,inAndOuts: InAndOut,dailyTotals:DailyTotal,weeklyTotals:WeeklyTotal,monthlyTotals:MonthlyTotal,absences:Absence]	
	static searchable = true	
	static constraints = {
		matricule (blank: true,nullable:true)
		firstName blank: false
		lastName blank: false 
		userName (unique: true,blank: false)
	}

	String toString(){
		return 'employee: '+ this.lastName+' '+this.firstName
	}
	
}
