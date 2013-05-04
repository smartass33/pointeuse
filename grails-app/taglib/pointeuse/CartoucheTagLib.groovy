package pointeuse

class CartoucheTagLib {

	def cartouche ={attrs,body->
		out<<render(template:"/common/cartoucheTemplate")
		
	}
	
}
