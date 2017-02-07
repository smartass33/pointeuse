package pointeuse

import java.util.Date;



class MileageService {


	
	def getSiteAndPeriod(def params,def session){
		def periodId = params['periodId']
		def siteId = params['siteId']
		def sessionSite = session['siteId']
		def sessionPeriod = session['periodId']
		def calendar = Calendar.instance
		def period
		def site
		
		if (periodId != null && !periodId.equals('')){
			period = Period.get(periodId)
		}else{
			if (sessionPeriod != null && !sessionPeriod.equals('')){
				period = Period.get(sessionPeriod)				
			}else{
				def month =  calendar.get(Calendar.MONTH) + 1
				def year = calendar.get(Calendar.YEAR)
				if (month < 6){
					year -=1
				}
				period = Period.findByYear(year)
			}
		}
		session['periodId']=period.id
			
		if (siteId != null && !siteId.equals('')){
			site = Site.get(siteId)
			session['siteId']=site.id
		}
			else{
				if (sessionSite != null && !sessionSite.equals('')){
					site = Site.get(sessionSite)
					session['siteId']=site.id		
				}
		}
		def isSitePresent = (session['siteId'] != null && !session['siteId'].equals('')) ? true : false
		def isPeriodPresent = (session['periodId'] != null && !session['periodId'].equals('')) ? true : false	
		return [site:site,period:period,isSitePresent:isSitePresent,isPeriodPresent:isPeriodPresent]
	}
	
	
	def getReportData(Period period,Site site){
		log.error('calling Mileage getReportData with period: '+period+' and site: '+site)
		def monthList=[6,7,8,9,10,11,12,1,2,3,4,5]
		def mileageMap = [:]
		def mileageIDMap = [:]
		def mileageMapByEmployee = [:]
		def mileageMapByStringByEmployee = [:]
		def mileageMapAsString = [:]
		def mileageIDMapByEmployee = [:]
		def employeeInstanceList
		def criteria
		def mileageList
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site,[sort:'lastName',order:'asc'])
			for (Employee employee:employeeInstanceList){
				mileageMap =[:]
				mileageIDMap= [:]

				for (int month in monthList ){
					Mileage existingMileage = Mileage.find('from Mileage where employee = :employee and period = :period and month = :month',[employee:employee,period:period,month:month])
					if (existingMileage != null){
						log.debug('mileage is created for period: '+period+' and month: '+month)
						mileageIDMap.put(month,existingMileage.id)
						mileageMap.put(month,existingMileage.value)
					}else{
						log.debug('mileage does not exist. will generate an =0 cell')
						mileageIDMap.put(month,0)
						mileageMap.put(month,0)
					}
				}
				mileageMapByEmployee.put(employee,mileageMap)
				mileageIDMapByEmployee.put(employee,mileageIDMap)
			}
		}
		return [
			site:site,
			period:period,
			employeeInstanceList:employeeInstanceList,
			mileageMapByEmployee:mileageMapByEmployee,
			mileageIDMapByEmployee:mileageIDMapByEmployee
		]
	}
	
}
