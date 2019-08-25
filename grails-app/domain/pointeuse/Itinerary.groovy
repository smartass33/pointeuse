
package pointeuse

import java.util.Date;


class Itinerary {
	Date creationDate
	String name
	Employee deliveryBoy
	User creationUser	
	boolean isSaturday
	String description

	static hasMany = [actions:Action]
	
	static constraints = {
		description nullable:true
		description blank: true
	}
	
	public Itinerary(String name, Employee deliveryBoy, User creationUser,isSaturday){
		this.creationDate = new Date()
		this.name = name
		this.deliveryBoy = deliveryBoy
		this.creationUser = creationUser
		this.isSaturday = isSaturday
	}
}
