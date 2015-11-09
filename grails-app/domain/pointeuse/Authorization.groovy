package pointeuse

import java.util.Date;


class Authorization {

	Employee employee
	Date startDate
	Date endDate
	Boolean isAuthorized
	AuthorizationType type
	static belongsTo = [employee:Employee]

    static constraints = {
    }
	
	Authorization(Date startDate,Date endDate,Employee employee){
		this.startDate = startDate
		this.endDate = endDate
		this.employee = employee
		this.isAuthorized = true
	}
}
