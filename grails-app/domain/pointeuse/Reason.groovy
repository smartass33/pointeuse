package pointeuse

class Reason {

	String name
	Date creatingDate
	User creatingUser
	
    static constraints = {
		creatingDate  nullable: true
		creatingUser  nullable: true
		name nullable: false
    }
}
