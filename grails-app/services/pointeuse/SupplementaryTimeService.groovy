package pointeuse

import java.util.Date;

class SupplementaryTimeService {

	def timeManagerService
	
	public getAllSupAndCompTime(Employee employee,Period period){
		
		
		def orderedSupTimeMap = [:]
		def orderedCompTimeMap = [:]
		def criteria
		def result
		def periodSupTime = 0
		def payableSupTime = 0
		
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

			Calendar calendar = Calendar.instance
			int month = calendar.get(Calendar.MONTH)
			calendar.set(Calendar.YEAR,period.year)
			calendar.set(Calendar.HOUR_OF_DAY,00)
			calendar.set(Calendar.MINUTE,00)
			calendar.set(Calendar.SECOND,01)
			calendar.set(Calendar.DATE,1)
			calendar.set(Calendar.MONTH,5)
		//	log.error('period: '+calendar.time)
			
			// if month> 5, 1 loop is necessary: from june to dec
			// if month < 6, 2 loops: from june to dec with year, then from 1 to 4 with year+1
			if (month>=5){
				for (int rollingMonth = 5 ;rollingMonth < month ; rollingMonth++){
					calendar.set(Calendar.MONTH,rollingMonth)
					log.error('period: '+calendar.time)
					result = timeManagerService.getReportData(null, employee, null,rollingMonth+1,period.year)
					//computeSecondsFromHumanTime(def table)
					periodSupTime += timeManagerService.computeSecondsFromHumanTime(result.get('payableSupTime'))
					
					log.error('sup time= '+periodSupTime)
					//periodSupTime += result.get('payableSupTime')!=null?Math.round(result.get('monthlySupTime')):0
				}
			}else{
				for (int rollingMonth = 5 ;rollingMonth < 12 ; rollingMonth++){
					calendar.set(Calendar.MONTH,rollingMonth)
					log.error('period: '+calendar.time)
					//result = timeManagerService.computeMonthlyTime(calendar,employee)
					result = timeManagerService.getReportData(null, employee, null,rollingMonth+1,period.year)
					
					periodSupTime  += timeManagerService.computeSecondsFromHumanTime(result.get('payableSupTime'))
					log.error('sup time= '+periodSupTime)				
				}
			
			
				for (int rollingMonth = 0 ;rollingMonth <5 ; rollingMonth++){
					calendar.set(Calendar.MONTH,rollingMonth)
					calendar.set(Calendar.YEAR,period.year+1)
					log.error('period: '+calendar.time)
					result = timeManagerService.getReportData(null, employee, null,rollingMonth+1,period.year)
					periodSupTime  += timeManagerService.computeSecondsFromHumanTime(result.get('payableSupTime'))
					log.error('sup time= '+periodSupTime)
				}
			}
	//	}
			orderedSupTimeMap.put(period, timeManagerService.computeHumanTime(periodSupTime))
		// compute supplementary Service over the period
		
		return [orderedSupTimeMap:orderedSupTimeMap,orderedCompTimeMap:orderedCompTimeMap,periodSupTime:periodSupTime]
	}
	
}