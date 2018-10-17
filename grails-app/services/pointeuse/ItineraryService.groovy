package pointeuse

import java.util.Date;



class ItineraryService {


	def getMonthlyActions(){
		
	}
	
	def getWeeklyActions(){
		
	}
	
	
	def getActionList(def viewType, def itinerary,def currentCalendar){
		
		def actionsList = []
		def monthCalendar = Calendar.instance
		def criteria = Action.createCriteria()
		def actionListMap = [:]
		
		switch (viewType) {
			case 'dailyView':
				actionsList = criteria.list {
					and {
						eq('itinerary',itinerary)
						eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
						eq('month',currentCalendar.get(Calendar.MONTH) + 1)
						eq('year',currentCalendar.get(Calendar.YEAR))
						eq('isTheoritical',false)
						order('date','asc')
					}
				}
				break
			case 'monthlyView':
				monthCalendar = currentCalendar
				monthCalendar.set(Calendar.DAY_OF_MONTH,1)
				def lastDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				for (int j = 1;j < lastDay + 1;j++){
					criteria = Action.createCriteria()
					actionsList = criteria.list {
						and {
							eq('itinerary',itinerary)
							eq('day',monthCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',monthCalendar.get(Calendar.MONTH) + 1)
							eq('year',monthCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
							order('date','asc')
						}
					}
					actionListMap.put(monthCalendar.time,actionsList)
					monthCalendar.roll(Calendar.DAY_OF_MONTH,1)
				}		
				break
			default:
				actionsList = criteria.list {
					and {
						eq('itinerary',itinerary)
						eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
						eq('month',currentCalendar.get(Calendar.MONTH) + 1)
						eq('year',currentCalendar.get(Calendar.YEAR))
						eq('isTheoritical',false)
						order('date','asc')
					}
				}
				break
			}
		
		return [actionsList:actionsList,actionListMap:actionListMap]
		}
	
}
