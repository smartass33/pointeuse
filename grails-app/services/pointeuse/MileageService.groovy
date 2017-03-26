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
		//def mileageIDMap = [:]
		def mileageMapByEmployee = [:]
		//def mileageMapByStringByEmployee = [:]
		//def mileageMapAsString = [:]
		//def mileageIDMapByEmployee = [:]
		def employeeInstanceList
		def criteria
		def mileageList
		def supYear
		def infYear
		def supMonth
		def infMonth
		def monthlyPeriodValue = 0
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site,[sort:'lastName',order:'asc'])
			for (Employee employee:employeeInstanceList){
				mileageMap =[:]
				//mileageIDMap= [:]
				for (int month in monthList ){
					monthlyPeriodValue = 0
					criteria = Mileage.createCriteria()					
					supMonth = month + 1
					infMonth = month
					if (month < 6){
						supYear = period.year + 1
						infYear = period.year + 1						

					}else{					
							supYear = period.year
							infYear = period.year
							if (month == 12){
								supYear = period.year + 1
								infYear = period.year
								supMonth = 1
								infMonth = 12
							}
					}
					mileageList = criteria.list {
						or{
							and {
								eq('employee',employee)
								eq('month',supMonth)
								eq('year',supYear)
								le('day',20)
							}
							and {
								eq('employee',employee)
								eq('month',infMonth)
								eq('year',infYear)
								gt('day',20)
							}
						}
					}
					
					if (mileageList != null){
						for (Mileage mileageIter : mileageList){
							monthlyPeriodValue += mileageIter.value
						}
						mileageMap.put(month,monthlyPeriodValue)
					}else{
						mileageMap.put(month,0)
					
					}
				}
				mileageMapByEmployee.put(employee,mileageMap)
			}
		}
		return [
			site:site,
			period:period,
			employeeInstanceList:employeeInstanceList,
			mileageMapByEmployee:mileageMapByEmployee
		]
	}
	
}
