package pointeuse

class EmployeeTagLib {

	def ecart ={attrs,body->
		out<<render(template:"/employee/template/ecartTemplate")
		
	}
	
	def listEmployee ={attrs,body->
		out<<render(template:"/employee/template/listEmployeeTemplate")
		
	}
	
	def listSiteEmployee ={attrs,body->
		out<<render(template:"/employee/template/listSiteEmployeeTemplate")
		
	}
	
	
	def listDailyTime ={attrs,body->
		out<<render(template:"/employee/template/listDailyTimeTemplate")
		
	}
	
	
	def annualReportTable ={attrs,body->
		out<<render(template:"/employee/template/annualReportTemplate")
		
	}
	
	
	def vacationEditTable ={attrs,body->
		out<<render(template:"/employee/template/vacationEditTemplate")
		
	}
	
	def paidHSEditTable ={attrs,body->
		out<<render(template:"/employee/template/paidHSEditTemplate")
		
	}
	
	
	def listVacationEmployee ={attrs,body->
		out<<render(template:"/employee/template/listVacationEmployeeTemplate")
		
	}
	
	
	def employeeContractTable ={attrs,body->
		out<<render(template:"/employee/template/contractTable")
		
	}
	
	
	def reportTable ={attrs,body->
		out<<render(template:"/employee/template/reportTableTemplate")
		
	}
	
	
	def contractStatus ={attrs,body->
		out<<render(template:"/employee/template/contractStatus")
		
	}	
	
	def currentDay ={attrs,body->
		out<<render(template:"/employee/template/currentDayTemplate")
		
	}
	
	def last5days = {attrs,body->
		out<<render(template:"/employee/template/last5DaysTemplate")
		
	}
	
	def yearSupTime = {attrs,body->
		out<<render(template:"/employee/template/yearSupplementaryTime")
		
	}
	
	def offHoursTime = {attrs,body->
		out<<render(template:"/employee/template/offHoursTime")
		
	}
	
	def fieldUpdatePopup = {attrs,body->		
		out<<render(template:"/employeeDataListMap/template/fieldUpdatePopup",model:[fieldName:attrs.fieldName,type:attrs.type])
	}
	
	def weeklyTime = {attrs,body->		
		out<<render(template:"/employee/template/listWeeklyTimeTemplate")
	}
	

}
