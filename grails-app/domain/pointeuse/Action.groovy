package pointeuse

import java.util.Date;
import java.util.Calendar;


class Action {
	Date date
	int day
	int month
	int year
	Site site
	Employee employeeLogger
	boolean isTheoritical
	ItineraryNature nature
	Itinerary itinerary
	boolean isSaturday
	String commentary
	String fnc
	String other
	static belongsTo = [itinerary:Itinerary]
	boolean isNotDone 
	boolean isRelay
	
	public Action(){
	}
	
	static constraints = {
		employeeLogger (blank: true,nullable:true)
		commentary (blank: true,nullable:true)
		other (blank: true,nullable:true)
		fnc (blank: true,nullable:true)
	}
	
	public Action(Itinerary itinerary, Date date, Site site, Employee employeeLogger, ItineraryNature nature) {
		this.itinerary = itinerary
		this.date = date
		this.day = date.getAt(Calendar.DAY_OF_MONTH)
		this.month = date.getAt(Calendar.MONTH) + 1
		this.year = date.getAt(Calendar.YEAR)
		this.site = site
		this.employeeLogger = employeeLogger
		this.isTheoritical = false
		this.isNotDone = false
		this.isRelay = false
 		this.nature = nature
	}
	
}
