package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

import groovy.time.TimeDuration
import groovy.time.TimeCategory

class PaymentService {

	def getReportData(Period period,Site site){
		log.error('calling Payment getReportData with period: '+period+' and site: '+site)
		def monthList=[6,7,8,9,10,11,12,1,2,3,4,5]
		def paymentMap = [:]
		def paymentIDMap = [:]
		def paymentMapByEmployee = [:]
		def paymentMapByStringByEmployee = [:]
		def paymentMapAsString = [:]
		def paymentIDMapByEmployee = [:]
		def employeeInstanceList
		def criteria
		def paymentList
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site,[sort:'lastName',order:'asc'])
			for (Employee employee:employeeInstanceList){
				paymentMap =[:]
				paymentIDMap= [:]

				for (int month in monthList ){				
					Payment existingPayment = Payment.find('from Payment where employee = :employee and period = :period and month = :month',[employee:employee,period:period,month:month])
					if (existingPayment != null){
						log.debug('payment is created for period: '+period+' and month: '+month)
						paymentIDMap.put(month,existingPayment.id)
						paymentMap.put(month,existingPayment.amountPaid)
					}else{
						log.debug('payment does not exist. will generate an =0 cell')
						paymentIDMap.put(month,0)
						paymentMap.put(month,0)
					}
				}	
				paymentMapByEmployee.put(employee,paymentMap)
				paymentIDMapByEmployee.put(employee,paymentIDMap)
			}
		}
		return [site:site,period:period,employeeInstanceList:employeeInstanceList,paymentMapByEmployee:paymentMapByEmployee,paymentIDMapByEmployee:paymentIDMapByEmployee]	
	}
	
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
}
