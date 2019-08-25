package pointeuse

public enum ItineraryNature {

	ARRIVEE('ARR'),
	DEPART('DEP')
	
	final String value
    ItineraryNature(String value){ this.value = value }
    @Override
    String toString(){ value }
    String getKey() { name() }

}
