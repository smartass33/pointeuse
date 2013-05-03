package pointeuse

class EmployeeListTagLib {

	def listEmployee ={attrs,body->
		out<<render(template:"/common/listEmployeeTemplate")
		
	}
	
}
