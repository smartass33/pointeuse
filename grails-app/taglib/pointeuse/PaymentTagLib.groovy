package pointeuse

import groovy.time.TimeDuration;
import java.util.concurrent.TimeUnit;

class PaymentTagLib {

	static namespace = "my"
	
	def paymentManagementTable ={attrs,body->
		out << render(template:"/payment/template/paymentTemplate")
	}

	
	def humanTimeTD ={attr ->
		attr.value = (attr.value == null) ? attr.value = 0 : attr.value
		out << writeHumanTime(attr.value as long)		
	}
	
	def humanTimeDecimalTD ={attr ->
		attr.value = (attr.value == null) ? attr.value = 0 : attr.value
		out << writeHumanTimeWithDecimal(attr.value as long)
	}
	
	def humanTimeTextField = { attrs ->
		attrs.type = "text"
		attrs.tagName = "textField"
		def result = field(attrs)
		if (result) {
			out << result
		}
	 }
	
	/**
	 * A general tag for creating fields
	 */
	def field = {attrs ->
		resolveAttributes(attrs)
		attrs.id = attrs.id ? attrs.id : attrs.name
		out << "<input type=\"${attrs.remove('type')}\" "
		if (attrs.value != null){
			attrs.value = writeHumanTime(attrs.value as long)
		}
		outputAttributes(attrs)
		out << "/>"
	}
	
	/**
	 * Dump out attributes in HTML compliant fashion
	 */
	void outputAttributes(attrs)
	{
		attrs.remove('tagName') // Just in case one is left
		def writer = getOut()
		attrs.each {k, v ->
			writer << "$k=\"${v.encodeAsHTML()}\" "
		}
	}
	
	/**
	 * Check required attributes, set the id to name if no id supplied, extract bean values etc.
	 */
	void resolveAttributes(attrs)
	{
		if (!attrs.name && !attrs.field) {
			throwTagError("Tag [${attrs.tagName}] is missing required attribute [name] or [field]")
		}
		attrs.remove('tagName')

		attrs.id = (!attrs.id ? attrs.name : attrs.id)

		def val = attrs.remove('bean')
		if (val) {
			if (attrs.name.indexOf('.'))
				attrs.name.split('\\.').each {val = val?."$it"}
			else {
				val = val[name]
			}
			attrs.value = val
		}
		attrs.value = (attrs.value != null ? attrs.value : "")
	}
	
	String writeHumanTime(long inputSeconds){
		boolean isNegative = (inputSeconds < 0) ? true : false
		def outputString = ''
		def diff = inputSeconds
		long hours = TimeUnit.SECONDS.toHours(diff);
		diff = diff - (hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff = diff - (minutes*60);
		long seconds = TimeUnit.SECONDS.toSeconds(diff);	
		if (!isNegative){
			if (hours < 10)
				outputString += '0'
			outputString += hours
			outputString += ':'
			
			if (minutes < 10)
				outputString += '0'
			outputString += minutes
		}else{
			outputString += '-'
			if (Math.abs(hours)<10)
				outputString += '0'
			outputString += Math.abs(hours)
			outputString += ':'
			if (Math.abs(minutes)<10)
				outputString += '0'
				outputString += Math.abs(minutes)
		}
		return outputString
	}
	
	String writeHumanTimeWithDecimal(long inputSeconds){
		boolean isNegative = (inputSeconds < 0) ? true : false
		def outputString = ''
		def diff = inputSeconds
		long hours = TimeUnit.SECONDS.toHours(diff);
		diff = diff - (hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff = diff - (minutes*60);
		long seconds = TimeUnit.SECONDS.toSeconds(diff);	
		def decimal = Math.abs((minutes/60).setScale(2,2))
		
		if (!isNegative){
			outputString = hours + decimal
			if (hours < 10){
				outputString = '0' + outputString.round(2) 	
			}
		}else{
			outputString = Math.abs(hours) + decimal
			if (Math.abs(hours) < 10){
				outputString = '-0' + outputString.round(2) 	
			}else{
				outputString = '-' + outputString.round(2) 
			}
		}
		return outputString
	}
}
