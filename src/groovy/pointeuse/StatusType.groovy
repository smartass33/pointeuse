package pointeuse

public enum StatusType {

	//V,M,G,RTT,X
	ACTIF('A'),
	SUSPENDU('S'),
	TERMINE('T')
	
	final String value
    StatusType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
	

	
}
