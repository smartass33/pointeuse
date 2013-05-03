package pointeuse

class HeaderMenuTagLib {

	def headerMenu ={attrs,body->
		out<<render(template:"/common/headerMenuTemplate")
		
	}
	
}
