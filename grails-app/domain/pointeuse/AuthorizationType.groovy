package pointeuse

import java.util.Date;


class AuthorizationType {

	String type
	AuthorizationNature nature
	Date creationDate
	User user
	
    static constraints = {
    }
	

}
