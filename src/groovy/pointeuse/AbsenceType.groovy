package pointeuse

public enum AbsenceType {

	VACANCE('V'),
	CHOMAGE('CH'),
	GARDE_ENFANT('GE'),
	MALADIE('M'),
	GROSSESSE('G'),
	MATERNITE('CM'),
	RTT('RTT'),
	AUTRE('R'),
	ANNULATION('-'),
	CSS('CSS'),
	FERIE('F'),
	EXCEPTIONNEL('CE'),
	PATERNITE('CP'),
	PARENTAL('PAR'),
	DIF('DIF'),
	DON('DON'),
	FORMATION('FO'),
	INJUSTIFIE('AI')
	
	final String value
    AbsenceType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
	

	
}
