package pointeuse

import grails.converters.JSON
import groovy.json.JsonSlurper
import java.text.Normalizer

class GeocoderService {

boolean transactional = false

    def geocodeAddress(String address,String town) {
		def jsonMap = [:]
		
		if (address!=null || address.size()==0){
			jsonMap.lat = 0
			jsonMap.lng = 0
		}
		
		address = Normalizer.normalize(address, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		town = Normalizer.normalize(town, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		def base = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&"
		def qs = []
		qs << "address=" + URLEncoder.encode(address) + ","+URLEncoder.encode(town)
		def url = new URL(base + qs.join("&"))
		def connection = url.openConnection()

	      def result = [:]
	      if(connection.responseCode == 200){		  
			  def geoCodeResultJSON = new JsonSlurper().parseText(connection.content.text)
			  jsonMap.lat = geoCodeResultJSON.results.geometry.location.lat[0]
			  jsonMap.lng = geoCodeResultJSON.results.geometry.location.lng[0]
			  jsonMap.address = geoCodeResultJSON.results.formatted_address[0]
			  jsonMap.postCode=geoCodeResultJSON.results.address_components[0].get(geoCodeResultJSON.results.address_components[0].size()-1).short_name as int
			  
	      }
	      else{
	        log.error("GeocoderService.geocodeAddress FAILED")
	        log.error(url)
	        log.error(connection.responseCode)
	        log.error(connection.responseMessage)
	      }      
	      return jsonMap
	   }
}
