package pointeuse
class InAndOut {

	Date loggingTime
	Date time
	int day
	int month
	int year
	int week
	String type //In or Out
	Employee employee
	boolean pointed
	boolean systemGenerated
	int regularizationType
	boolean regularization
	DailyTotal dailyTotal
	Reason reason
	User modifyingUser
	Date modifyingTime
	static final int INIT=0
	static final int INITIALE_SALARIE=1
	static final int INITIALE_ADMIN=2
	static final int MODIFIEE_ADMIN=3
	static final int MODIFIEE_SALARIE=4
	static belongsTo = [dailyTotal:DailyTotal,employee:Employee]
		
	
	InAndOut(Employee employee,Date currentDate,String type){
		this.employee=employee
		this.time=currentDate
		this.loggingTime=new Date()
		this.week=currentDate.getAt(Calendar.WEEK_OF_YEAR)
		this.day=currentDate.getAt(Calendar.DAY_OF_MONTH)
		this.month=currentDate.getAt(Calendar.MONTH)+1
		this.year=currentDate.getAt(Calendar.YEAR)
		this.type=type	
		this.regularizationType=this.INIT	
	}
	
	String toString(){
		return 'inAndOut: '+ this.employee.lastName+' '+this.year+'-'+this.month+'-'+this.day+' '+this.time + ' '+this.type
	}
	
    static constraints = {
		type nullable:false
		time  nullable: true
		reason  nullable: true
		modifyingUser  nullable: true
		modifyingTime  nullable: true
    }
}
