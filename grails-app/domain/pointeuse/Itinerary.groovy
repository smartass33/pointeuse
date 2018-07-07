
package pointeuse

import java.util.Date;


class Itinerary {
	Date creationDate
	String name
	Employee deliveryBoy
	User creationUser	
	static hasMany = [actions:Action]
	
	public Itinerary(String name, Employee deliveryBoy, User creationUser){
		this.creationDate = new Date()
		this.name = name
		this.deliveryBoy = deliveryBoy
		this.creationUser = creationUser
	}
}
