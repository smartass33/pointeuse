package pointeuse


class MonthlyTotal {

	
	int month
	int year
	Date loggingTime
	Period period
	long elapsedSeconds
	double supplementarySeconds
	double complementarySeconds
	double timeBefore7
	double timeAfter20
	Employee employee
	static hasMany = [weeklyTotals:WeeklyTotal]
	static belongsTo = [employee:Employee]
	
	MonthlyTotal(Employee employee,Date currentDate){
		this.employee=employee
		this.year=currentDate.getAt(Calendar.YEAR)
		this.month=currentDate.getAt(Calendar.MONTH)+1
		this.elapsedSeconds=0
		this.supplementarySeconds=0
		this.timeBefore7=0
		this.timeAfter20=0
		this.weeklyTotals=[]
		this.loggingTime = new Date()
		this.period = (this.month > 5)?Period.findByYear(this.year):Period.findByYear(this.year - 1)
		
	}
	
   static constraints = {
		month (unique:['employee','period'])
		
    }
}
