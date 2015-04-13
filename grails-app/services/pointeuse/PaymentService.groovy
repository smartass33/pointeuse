package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

import groovy.time.TimeDuration
import groovy.time.TimeCategory

class PaymentService {

	def getReportData(Period period,Site site){
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
			employeeInstanceList = Employee.findAllBySite(site)
			for (Employee employee:employeeInstanceList){
				paymentMap =[:]
				paymentIDMap= [:]
				criteria = Payment.createCriteria()
				paymentList = criteria.list{
					and {
						eq('employee',employee)
						eq('period',period)
					}
				}
				for (int month in monthList ){
					if (paymentList!=null && paymentList.size()>0){
						for (Payment payment:paymentList){
							if (payment.month == month){
								log.error('payment is created for period: '+period+' and month: '+month)
								paymentIDMap.put(month,payment.id)
								paymentMap.put(month,payment.amountPaid)
								}else{
									log.error('payment does not exist. will generate an =0 cell')
									paymentIDMap.put(month,0)
									paymentMap.put(month,0)
							}
						}
					}else{
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
	
}
