package pointeuse

import java.util.Date;


class Anomaly {
	String name
	Date creationDate
	Employee employee
	User user
	AnomalyType type
	String description
	Boolean RAS
	
	static belongsTo = [site:Site]	
	
	static constraints = {
		creationDate (blank: true,nullable:true)
		user(blank:true,nullable:true)
		RAS(blank:true,nullable:true)
		type(blank:true,nullable:true)
		description(blank:true,nullable:true)
		name(blank:true,nullable:true)
	}

	Anomaly(Employee employee,Boolean RAS,String name,AnomalyType type,String description,User user){
		this.employee=employee
		this.RAS=RAS
		this.creationDate = new Date()
		if (!RAS){
			this.type = type
			this.description = description
			if (user != null){
				this.user = user
			}
		}
			
	}
	
	String toString(){
		if (this.employee != null)
			return 'Anomaly: name:'+ this.name+' and employee: '+this.employee.lastName
		if (this.user != null)
			return 'Anomaly: name:'+ this.name+' and employee: '+this.user.lastName
		
	}

}
