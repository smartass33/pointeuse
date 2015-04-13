package pointeuse

class SupplementaryTimeTagLib {

	def employeeSupTimeManagement ={attrs,body->
		out<<render(template:"/supplementaryTime/template/employeeSupTimeTemplate")
	}

}
