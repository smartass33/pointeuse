package pointeuse

class CartoucheTagLib {

	def cartouchePDF ={attrs,body->
		out<<render(template:"/common/cartouchePDFTemplate")
		
	}
	
	def cartouche ={attrs,body->
		out<<render(template:"/common/cartoucheTemplate")
		
	}
	
	def completeReport ={attrs,body->
		out<<render(template:"/common/completeReportTemplate")
		
	}
	
	
}
