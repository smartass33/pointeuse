package pointeuse

class UserTagLib {

	def userLoginPopup = {attrs,body->
		out<<render(template:"/user/template/userLoginPopup")
	}
	
}
