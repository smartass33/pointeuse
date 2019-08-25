class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/redirection"(uri: "/redirection.html")
		"/time"(uri: "/time.html")
		
		"/"(view:"/index")
		//"500"(view:'/error')
		
		"500"(controller: "errors", action:"serverError") 
		"404"(controller:"errors", action:"notFound")
		
		"/inAndOut/save" {
			controller = "inAndOut"
			action = "save"
		}
		
		
		"/inAndOut/modifyTime" {
			controller = "employee"
			action = "modifyTime"
		}
	}
}
