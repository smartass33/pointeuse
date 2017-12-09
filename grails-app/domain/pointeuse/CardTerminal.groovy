package pointeuse

class CardTerminal {

	String ip
	String hostname
	Date creationDate
	Date lastKeepAlive
	Site site
	static belongsTo = [site:Site]
	

	
    static constraints = {
    }
}
