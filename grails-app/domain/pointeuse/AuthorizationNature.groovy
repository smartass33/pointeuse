package pointeuse

import java.util.Date;


class AuthorizationNature {

	String name
	Date creationDate
	User user

	//static belongsTo = [authorizationType:AuthorizationType]
	
    static constraints = {
    }
	

}
