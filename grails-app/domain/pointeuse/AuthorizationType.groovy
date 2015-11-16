package pointeuse

import java.util.Date;


class AuthorizationType {

	String name
	AuthorizationNature nature
	Date creationDate
	User user
	
    static constraints = {
    }
	

}
