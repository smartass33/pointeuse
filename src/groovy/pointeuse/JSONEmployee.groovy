package pointeuse

import java.util.Date;

public class JSONEmployee {

	String firstName;
	String lastName;
	Boolean status;
	def inOrOuts;

	JSONEmployee(Employee employee,Boolean status){
		this.firstName = employee.firstName
		this.lastName = employee.lastName
		this.status = status
		this.inOrOuts = new ArrayList()
	}
	
}
