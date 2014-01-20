package pointeuse

class ReportTableTagLib {

	def reportPDFTable ={attrs,body->
		out<<render(template:"/common/reportPDFTableTemplate")
		
	}

}
