package pointeuse

public enum Title {

	//V,M,G,RTT,X
	M('M'),
	MME('MME'),
	MLLE('MLLE')

	final String value
    Title(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }

	static Title valueOfName( String name ) {
		values().find { it.value == name }
	}
}
