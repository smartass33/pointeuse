package pointeuse

class WeeklyCase {
	//int month
	int year
	int week
	int cases
	long particularity1
	long particularity2
	int home_assistance
	Date loggingDate
	Site site
	static belongsTo = [Site]
	
	static constraints = {
		particularity1 (blank: true,nullable:true)
		particularity2 (blank: true,nullable:true)
		home_assistance (blank: true,nullable:true)
	}
	
	WeeklyCase(int value,Date currentDate,def site){
		this.year=currentDate.getAt(Calendar.YEAR)
		//this.month=currentDate.getAt(Calendar.MONTH + 1)
		this.week=currentDate.getAt(Calendar.WEEK_OF_YEAR)
		this.cases=value
		this.site=site
		this.loggingDate=new Date()
	}
	
	String toString(){
		return 'weeklyCase: site:'+ this.site.name+', week:'+this.week + ', year:'+ this.year+', cases:'+ this.cases + ', particularity1:'+this.particularity1 + ', particularity2:'+this.particularity2 + ', home_assistance:'+this.home_assistance
	}
	
}
