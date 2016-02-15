package pointeuse

import java.util.Date;

public class JSONEmployee {

	String firstName;
	String lastName;
	String userName;


	JSONEmployee(Employee employee){
		this.firstName = employee.firstName
		this.lastName = employee.lastName
		this.userName = employee.userName
	}
	
}
