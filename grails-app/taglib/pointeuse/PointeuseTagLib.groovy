package pointeuse

class PointeuseTagLib {
	def employeeDataListTable ={attrs,body->
		out<<render(template:"/employeeDataListMap/template/employeeDataListTable")
	}
	
	def authorizationPopup ={attrs,body->
		out<<render(template:"/authorization/template/authorizationCreatePopup",model:[employeeInstanceId:attrs.employeeInstanceId,fromReport:attrs.fromReport,fromEditEmployee:attrs.fromEditEmployee,showEmployee:attrs.showEmployee])
	}
	
	def authorizationTable ={attrs,body->
		out<<render(template:"/authorization/template/authorizationTable",model:[showEmployee:attrs.showEmployee])
	}
	
	def authorizationCategoryTable ={attrs,body->
		out<<render(template:"/authorization/template/authorizationCategoryTable",model:[showEmployee:attrs.showEmployee])
	}
}
