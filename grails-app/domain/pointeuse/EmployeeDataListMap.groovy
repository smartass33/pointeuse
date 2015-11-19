package pointeuse

import java.util.Date;


class EmployeeDataListMap {

	Date creationDate
	Date lastModification
	User creationUser
	User modificationUser	
	Map fieldMap
	Map hiddenFieldMap
	
    static constraints = {
		hiddenFieldMap nullable:true
		fieldMap nullable:true	
    }
	
	EmployeeDataListMap(Date currentDate,User creationUser){
		this.creationDate = currentDate
		this.creationUser = creationUser
	}
}
