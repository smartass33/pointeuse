package pointeuse

class DailyTotal {

	static final long maxWorkingTime=10*3600
	Date date
	int day
	int month
	int year
	int week
	long elapsedSeconds
	long supplementarySeconds
	Employee employee
	int entryCount
	int exitCount
	Site site
	WeeklyTotal weeklyTotal
	static hasMany = [inAndOuts:InAndOut]
	static belongsTo = [weeklyTotal:WeeklyTotal]
	
	DailyTotal(Employee employee,Date currentDate){
		this.employee=employee
		this.week=currentDate.getAt(Calendar.WEEK_OF_YEAR)
		this.year=currentDate.getAt(Calendar.YEAR)
		this.month=currentDate.getAt(Calendar.MONTH)+1
		this.day=currentDate.getAt(Calendar.DAY_OF_MONTH)
		this.date=currentDate
		this.elapsedSeconds=0
		this.entryCount=1
		this.inAndOuts=[]
		this.site = null
	}
	static constraints = {
		site nullable:true
		site blank: true
	}
	
	String toString(){
		return 'employee: '+ this.employee.lastName+' '+this.year+'-'+this.month+'-'+this.day+' elapsed seconds: '+this.elapsedSeconds
	}

}
