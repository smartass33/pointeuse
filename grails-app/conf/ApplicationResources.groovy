modules = {
    application {
        resource url:'js/application.js'
    }
	
	common {
		resource url:"css/main.css"
	}
		
	report {
		dependsOn "jquery-ui, jquery"	
		resource url:"css/main.css"
		resource url: "js/jquery-ui-timepicker-addon.js"
	}
} 