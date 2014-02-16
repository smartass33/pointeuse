modules = {
    application {
        resource url:'js/application.js'
		//resource url:'js/jquery-ui-timepicker-addon.js'
		
    }
	
	common {
		resource url:"css/main.css"
		//resource url:"js/jquery/jquery-1.10.2.js"
	}
	
	
	report {
		dependsOn "jquery-ui, jquery"	
	//	resource url:"css/images/*"
		resource url:"css/main.css"
	//	resource url:"css/jquery-ui.css"
		resource url: "js/jquery-ui-timepicker-addon.js"
	}
}