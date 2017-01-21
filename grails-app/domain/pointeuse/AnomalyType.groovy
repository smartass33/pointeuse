package pointeuse

class AnomalyType {

	String name
	Date creatingDate
	User creatingUser
	
    static constraints = {
		creatingDate (blank: true,nullable:true)
		creatingUser (blank: true,nullable:true)
		name nullable: false
    }
	
	
	String toString(){
			return 'Anomaly: name:'+ this.name+' and employee: '+this.creatingUser.lastName
		
	}
}
