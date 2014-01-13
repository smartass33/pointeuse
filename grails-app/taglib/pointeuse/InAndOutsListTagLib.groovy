package pointeuse

class InAndOutsListTagLib {

	def listInAndOuts ={attrs,body->
		out<<render(template:"/common/listInAndOutsTemplate")
		
	}
	
	def inAndOutResult ={attrs,body->
		out<<render(template:"/common/inAndOutResultTemplate")
		
	}
	
}
