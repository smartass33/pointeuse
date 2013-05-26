package pointeuse

class PointageTagLib {

	def currentDay ={attrs,body->
		out<<render(template:"/common/currentDayTemplate")
		
	}


}
