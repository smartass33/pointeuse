package pointeuse

public enum Title {
	M('M'),
	MLLE('MLLE'),
	MME('MME')

	final String value
    Title(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }
}
