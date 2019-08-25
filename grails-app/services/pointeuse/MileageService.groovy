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
		def mileageMapByEmployee = [:]
		def employeeInstanceList
		def criteria
		def mileageList
		def supYear
		def infYear
		def supMonth
		def infMonth
		def monthlyPeriodValue = 0
		def calendar = Calendar.instance
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site,[sort:'lastName',order:'asc'])
			for (Employee employee:employeeInstanceList){
				mileageMap =[:]
				//mileageIDMap= [:]
				for (int month in monthList ){
					monthlyPeriodValue = 0
					criteria = Mileage.createCriteria()					
					supMonth = month
					if (month < 6){
						supYear = period.year + 1
					}else{					
							supYear = period.year
							if (month == 12){
								supYear = period.year + 1
								supMonth = 1
							}
					}
					calendar.set(Calendar.YEAR,supYear)
					calendar.set(Calendar.MONTH,supMonth - 1)
					mileageList = criteria.list {
						or{
							and {
								eq('employee',employee)
								eq('month',supMonth)
								eq('year',supYear)
								le('day',calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
							}
							and {
								eq('employee',employee)
								eq('month',supMonth)
								eq('year',supYear)
								ge('day',1)
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
	
	def getAllSitesOverPeriod(def siteList,def infYear, def supYear){
		def employeeSiteList
		def mileageByEmployeeYear = [:]
		def employeeMileageYearMap = [:]
		def mileageBySiteMap = [:]
		
		for (Site site in siteList){
			employeeSiteList = site.employees
			mileageByEmployeeYear = [:]
			for (employee in employeeSiteList){
				employeeMileageYearMap = [:]
				for (int i = infYear; i <= supYear; i++){
					def employeeYearlyMileage = 0
					def yearMileageList = Mileage.findAllByEmployeeAndYear(employee,i)

					for (Mileage mileage in yearMileageList){
						employeeYearlyMileage += mileage.value
					}
					employeeMileageYearMap.put(i,employeeYearlyMileage)
					
				}
				mileageByEmployeeYear.put(employee,employeeMileageYearMap)
			}
			mileageBySiteMap.put(site,mileageByEmployeeYear)
		}
		return [mileageBySiteMap:mileageBySiteMap]
	}
	
	
}
