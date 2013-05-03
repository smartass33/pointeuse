class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		//"500"(view:'/error')
		
		"500"(controller: "errors", action:"serverError") 
		"404"(controller:"errors", action:"notFound")
		
		"/inAndOut/save" {
			controller = "inAndOut"
			action = "save"
		}
		
		
		"/inAndOut/timeModification" {
			controller = "employee"
			action = "timeModification"
		}
	}
}
