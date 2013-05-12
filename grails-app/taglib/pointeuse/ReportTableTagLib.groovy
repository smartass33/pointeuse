package pointeuse

class ReportTableTagLib {

	def reportTable ={attrs,body->
		out<<render(template:"/common/reportTableTemplate")
		
	}
	
<<<<<<< HEAD
	def reportPDFTable ={attrs,body->
		out<<render(template:"/common/reportPDFTableTemplate")
		
	}
	
=======
>>>>>>> 0095940169cefc51b9bedc9af4862b44ffde0bad
}
