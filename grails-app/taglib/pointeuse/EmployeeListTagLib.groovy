package pointeuse

class EmployeeListTagLib {

	def listEmployee ={attrs,body->
		out<<render(template:"/common/listEmployeeTemplate")
		
	}
	
	
	def listDailyTime ={attrs,body->
		out<<render(template:"/common/listDailyTimeTemplate")
		
	}
	
}
