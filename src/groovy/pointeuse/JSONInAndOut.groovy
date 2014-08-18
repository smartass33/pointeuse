package pointeuse

import java.util.Date;

public class JSONInAndOut {

	Date time
	String type
	JSONEmployee employee

	JSONInAndOut(InAndOut inAndOut,JSONEmployee employee){
		this.employee = employee
		this.type = inAndOut.type
		this.time = inAndOut.time
	}
	
}
