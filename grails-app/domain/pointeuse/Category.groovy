package pointeuse

import java.util.Date;


class Category {

	String name
	Date creationDate
	User user
	
	static hasMany = [subCategories:SubCategory]
	
	static constraints = {
		creationDate (blank: true,nullable:true)
		user(blank:true,nullable:true)
	}

	String toString(){
		return 'category: name:'+ this.name
	}

}
