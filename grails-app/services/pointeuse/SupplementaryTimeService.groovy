package pointeuse

class SupplementaryTimeService {

	
	
	public getAllSupAndCompTime(Employee employee){
		
		
		def orderedSupTimeMap = [:]
		def orderedCompTimeMap = [:]
		def criteria
		
		for (Period period:Period.findAll(sort:'year',order:'asc')){
			def vacations = Vacation.findAllByEmployeeAndPeriod(employee,period,[sort:'type',order:'asc'])
			
			criteria = SupplementaryTime.createCriteria()
			
			def supTime = criteria.get {
				and {
					eq('employee',employee)
					eq('period',period)
					eq('type',SupplementaryType.HS)
				}
				
			}
			criteria = SupplementaryTime.createCriteria()
			
			def compTime = criteria.get {
				and {
					eq('employee',employee)
					eq('period',period)
					eq('type',SupplementaryType.HC)
				}
			}
			if (supTime != null){
				orderedSupTimeMap.put(period,supTime)
			}else{
				orderedSupTimeMap.put(period,0)
				
			}
			if (compTime != null){
				orderedCompTimeMap.put(period,compTime)
			}else{
				orderedCompTimeMap.put(period,0)
				
			}

		}
		return [orderedSupTimeMap:orderedSupTimeMap,orderedCompTimeMap:orderedCompTimeMap]
	}
	
}