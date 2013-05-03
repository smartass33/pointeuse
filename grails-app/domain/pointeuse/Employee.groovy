package pointeuse

class Employee{

	static final float Pentecote=7/12
	static final float WeekOpenedDays=6
	static final int entryPerDay=6
	String firstName
	String lastName
	String userName
	String matricule
	//static searchable = true
	//static embedded = ['site']
	Site site
	Service service
	float weeklyContractTime //35H par défaut. 
	boolean status //1: in, 0: out
	boolean hasError
	Date arrivalDate
	
	static hasMany = [inAndOuts: InAndOut,dailyTotals:DailyTotal,weeklyTotals:WeeklyTotal,monthlyTotals:MonthlyTotal,absences:Absence]
	

	
	static searchable = true
	/*
	 * 
	 * static searchable ={
		except = ['id', 'version']
	}
	static searchable = { 
		//tags component: true 
		site reference: true 
	} 
*/	
	static constraints = {
		firstName blank: false
		lastName blank: false 
		userName (unique: true,blank: false)
	}

}
