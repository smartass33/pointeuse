package pointeuse

class EmployeeListTagLib {

	def ecart ={attrs,body->
		out<<render(template:"/common/ecartTemplate")
		
	}
	
	def listEmployee ={attrs,body->
		out<<render(template:"/common/listEmployeeTemplate")
		
	}
	
	def listSiteEmployee ={attrs,body->
		out<<render(template:"/common/listSiteEmployeeTemplate")
		
	}
	
	
	def listDailyTime ={attrs,body->
		out<<render(template:"/common/listDailyTimeTemplate")
		
	}
	
	
	def annualReportTable ={attrs,body->
		out<<render(template:"/common/annualReportTemplate")
		
	}
	
	
	def vacationEditTable ={attrs,body->
		out<<render(template:"/common/vacationEditTemplate")
		
	}
	
	
	def listVacationEmployee ={attrs,body->
		out<<render(template:"/common/listVacationEmployeeTemplate")
		
	}
	
}
