package pointeuse

class ReportTableTagLib {

	def reportTable ={attrs,body->
		out<<render(template:"/common/reportTableTemplate")
		
	}
	
	def reportPDFTable ={attrs,body->
		out<<render(template:"/common/reportPDFTableTemplate")
		
	}

}
