package pointeuse

class Employee{
	static final float Pentecote=7/12
	static final float legalWeekTime=35
	static final float WeekOpenedDays=6
	static final int entryPerDay=6
	String firstName
	String lastName
	String birthName
	String userName
	String matricule
	Function function
	Site site
	Service service
	float weeklyContractTime //35H par défaut. 
	boolean hasError
	boolean hasNightJob
	Date arrivalDate
	Status status
	Map extraData 
	Title title
	
	static belongsTo = [site:Site]
	
	static hasMany = [
		annualEmployeeData:AnnualEmployeeData,
		payments:Payment,
		supplementary_time:SupplementaryTime,
		contracts:Contract,
		vacations:Vacation,
		vacationsCounters:Vacation,
		absenceCounters:AbsenceCounter,
		inAndOuts: InAndOut,
		dailyTotals:DailyTotal,
		weeklyTotals:WeeklyTotal,
		monthlyTotals:MonthlyTotal,
		absences:Absence,
		milages:Mileage
	]	
	static searchable = true	
	static constraints = {
		matricule (blank: true,nullable:true)
		service (blank: true,nullable:true)
		firstName blank: false
		lastName blank: false 
		userName (unique: true,blank: false)
		birthName (blank: true,nullable:true)
	}

	static mapping = {
		site lazy: false
	}
	
	String toString(){
		return 'employee: lastName:'+ this.lastName+' firstName:'+this.firstName + ' username:'+ this.userName+' id:'+ this.id + ' site:'+this.site.name
	}
}
