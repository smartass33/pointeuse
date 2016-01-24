package pointeuse

import java.util.Date;


class Authorization {

	Employee employee
	Date startDate
	Date endDate
	Boolean isAuthorized
	Category category
	SubCategory subCategory
	static belongsTo = [employee:Employee]

    static constraints = {
		category (blank: true, nullable: true)
		subCategory (blank: true, nullable: true)	
		startDate (blank:true, nullable:true)
		endDate (blank:true, nullable:true)
    }
	
	Authorization(Date startDate,Date endDate,Employee employee){
		this.startDate = startDate
		this.endDate = endDate
		this.employee = employee
		this.isAuthorized = true
	}
	

}
