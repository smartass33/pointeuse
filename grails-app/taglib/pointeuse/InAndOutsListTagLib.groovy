package pointeuse

class InAndOutsListTagLib {

	def listInAndOuts ={attrs,body->
		out<<render(template:"/common/listInAndOutsTemplate")
		
	}
	
}