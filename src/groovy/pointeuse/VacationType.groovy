package pointeuse

public enum VacationType {

	CA('CA'),
	RTT('RTT')

	
	final String value
    VacationType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
	

	
}
