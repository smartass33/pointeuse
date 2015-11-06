package pointeuse

class AuthorizationTagLib {

	def authorizationPopup ={attrs,body->
		out<<render(template:"/authorization/template/authorizationCreatePopup",model:[employeeInstanceId:attrs.employeeInstanceId,fromReport:attrs.fromReport])
	}
	
	def authorizationTable ={attrs,body->
		out<<render(template:"/authorization/template/authorizationTable")
		
	}
	
}
