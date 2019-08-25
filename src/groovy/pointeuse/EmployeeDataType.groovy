package pointeuse

public enum EmployeeDataType {

	//V,M,G,RTT,X
	TEXT('TEXT'),
	DATE('DATE'),
	NOMBRE('NUMBER')
	
	final String value
    EmployeeDataType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }	
}
