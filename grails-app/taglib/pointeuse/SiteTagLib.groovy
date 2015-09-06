package pointeuse

class SiteTagLib {

	def siteMonthlyTotal ={attrs,body->
		out<<render(template:"/site/template/siteMonthlyTemplate")
		
	}

	def siteDetailTable ={attrs,body->
		out<<render(template:"/site/template/siteDetailTableTemplate")
		
	}

}
