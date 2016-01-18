package pointeuse

import java.util.Date;


class SubCategory {

	String name
	Date creationDate
	User user
	Category category
	
   static belongsTo = [category:Category]

   static constraints = {
	   creationDate (blank: true,nullable:true)
	   user(blank:true,nullable:true)
   }
   
   String toString(){
	   if (this.category != null){
		   return 'subCategory: name:'+ this.name+', category: '+this.category.name
	   }else{
	   		return 'subCategory: name:'+ this.name
	   }
	   
   }
   

}
