package pointeuse

public enum SupplementaryType {

	HS('HS'),
	HC('HC')

	
	final String value
    SupplementaryType(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
	

	
}
