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
	
	def displayDay ={attrs,body->
		out<<render(template:"/employee/template/displayDay")
		
	}
	
	
	def reportTable ={attrs,body->
		out<<render(template:"/employee/template/reportTableTemplate")
		
	}
	
	
}