package pointeuse

public enum AbsenceType {

	//V,M,G,RTT,X
	VACANCE('V'),
	MALADIE('M'),
	GROSSESSE('G'),
	RTT('RTT'),
	AUTRE('A'),
	ANNULATION('-'),
	CSS('CSS')
	
	final String value
    AbsenceType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
	

	
}
