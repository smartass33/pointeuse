package pointeuse

import java.util.Date;



class ItineraryService {


	def getMonthlyActions(){
		
	}
	
	def getWeeklyActions(){
		
	}
	
	def getTheoriticalActionMap(def input,boolean isSaturday){
		def site
		def itinerary
		def theoriticalActionsMap = [:]
		def criteria = Action.createCriteria()
		def theoriticalActionsList = []
		def itineraryList = Itinerary.findAll("from Itinerary")
		
		if (input instanceof Itinerary){
			itinerary = input
		}
		if (input instanceof Site){
			site = input
		}
		
		if (site != null){			
			for (def itineraryIter: itineraryList){
				criteria = Action.createCriteria()
				theoriticalActionsList = criteria.list {
					and {
						eq('isTheoritical',true)
						eq('site',site)
						eq('itinerary',itineraryIter)
						eq('isSaturday',isSaturday)
						order('date','asc')
					}
				}				
				if (theoriticalActionsList != null && theoriticalActionsList.size() > 0)
					theoriticalActionsMap.put(itineraryIter,theoriticalActionsList)
			}
		}else{
			theoriticalActionsList= criteria.list {
				and {
					eq('itinerary',itinerary)
					eq('isTheoritical',true)
					eq('isSaturday',isSaturday)
					order('date','asc')
				}
			}
		}
		return theoriticalActionsMap
	}
	
	def getTheoriticalActionList(def input,boolean isSaturday){
		def site
		def itinerary
		def theoriticalActionsMap = [:]
		def criteria = Action.createCriteria()
		def theoriticalActionsList = []
		//def itineraryList = Itinerary.findAll("from Itinerary")	
		
		if (input instanceof Itinerary){
			itinerary = input
		}
		if (input instanceof Site){
			site = input
		}
		
		if (site != null){
			theoriticalActionsList = criteria.list {
				and {
					eq('isTheoritical',true)
					eq('site',site)
					eq('isSaturday',isSaturday)
					order('date','asc')
				}
			}	
		}else{
			theoriticalActionsList= criteria.list {
				and {
					eq('itinerary',itinerary)
					eq('isTheoritical',true)
					eq('isSaturday',isSaturday)
					order('date','asc')
				}
			}
		}
		return theoriticalActionsList
	}
	
	def getActionList(def viewType, def itinerary,def currentCalendar,def site){	
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
		
				case 'dailyViewBySite':
				actionsList = criteria.list {
					and {
						eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
						eq('month',currentCalendar.get(Calendar.MONTH) + 1)
						eq('year',currentCalendar.get(Calendar.YEAR))
						eq('isTheoritical',false)
						eq('site',site)
						order('date','asc')
					}
				}
				break
					
			case 'monthlyViewBySite':
				monthCalendar = currentCalendar
				monthCalendar.set(Calendar.DAY_OF_MONTH,1)
				def lastDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				for (int j = 1;j < lastDay + 1;j++){
					criteria = Action.createCriteria()
					actionsList = criteria.list {
						and {
							eq('day',monthCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',monthCalendar.get(Calendar.MONTH) + 1)
							eq('year',monthCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
							eq('site',site)
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
	
	def getActionMap(def viewType, def itinerary,def currentCalendar,def site){
		def actionsList = []
		def monthCalendar = Calendar.instance
		def criteria = Action.createCriteria()
		def actionListMap = [:]
		def dailyActionMap = [:]
		def actionsThOrderedMap = [:]
		def actionsThOrderedBySiteMap = [:]
		def actionsThOrderedList = []
		def actionsThOrderedListIter = []
		def itineraryList = Itinerary.findAll("from Itinerary")

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
		
				case 'dailyViewBySite':
				for (def itineraryIter: itineraryList){
					criteria = Action.createCriteria()
					actionsList = criteria.list {
						and {
							eq('itinerary',itineraryIter)
							eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',currentCalendar.get(Calendar.MONTH) + 1)
							eq('year',currentCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
							eq('site',site)
							order('date','asc')
						}
					}
					if (actionsList != null && actionsList.size() > 0)
						dailyActionMap.put(itineraryIter,actionsList)
				}
				break
					
			case 'monthlyViewBySite':
				monthCalendar = currentCalendar
				monthCalendar.set(Calendar.DAY_OF_MONTH,1)
				def lastDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				for (int j = 1;j < lastDay + 1;j++){
					log.debug('monthCalendar: '+monthCalendar.time)
					actionsThOrderedList = []
					actionsThOrderedBySiteMap = [:]
					criteria = Action.createCriteria()
					actionsList = criteria.list {
						and {
							eq('day',monthCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',monthCalendar.get(Calendar.MONTH) + 1)
							eq('year',monthCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
							eq('site',site)
							order('date','asc')
						}
					}
					
					for (def itineraryIter: itineraryList){
						criteria = Action.createCriteria()
						actionsThOrderedListIter = criteria.list {
							and {
								eq('itinerary',itineraryIter)
								eq('day',monthCalendar.get(Calendar.DAY_OF_MONTH))
								eq('month',monthCalendar.get(Calendar.MONTH) + 1)
								eq('year',monthCalendar.get(Calendar.YEAR))
								eq('isTheoritical',false)
								eq('site',site)
								order('date','asc')
							}
						}
						log.debug('actionsThOrderedListIter: '+actionsThOrderedListIter)
						actionsThOrderedBySiteMap.put(itineraryIter,actionsThOrderedListIter)
						//actionsThOrderedList.addAll(actionsThOrderedListIter)
					}				
					actionsThOrderedMap.put(monthCalendar.time,actionsThOrderedBySiteMap)
					log.debug('actionsThOrderedList '+monthCalendar.time+' '+actionsThOrderedList)		
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
		
		return [
			actionsList:actionsList,
			actionsThOrderedMap:actionsThOrderedMap,
			actionListMap:actionListMap,
			dailyActionMap:dailyActionMap
		]
	}
}
