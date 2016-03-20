package pointeuse

public enum AbsenceType {

	VACANCE('V'),
	MALADIE('M'),
	GROSSESSE('G'),
	RTT('RTT'),
	AUTRE('R'),
	ANNULATION('-'),
	CSS('CSS'),
	FERIE('F'),
	EXCEPTIONNEL('CE'),
	PATERNITE('CP'),
	DIF('DIF'),
	INJUSTIFIE('AI')
	
	final String value
    AbsenceType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
	

	
}
