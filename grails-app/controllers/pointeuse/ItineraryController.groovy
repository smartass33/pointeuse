package pointeuse


import grails.plugin.springsecurity.annotation.Secured
import org.springframework.core.io.Resource
import org.springframework.dao.DataIntegrityViolationException
import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable

import groovy.json.JsonSlurper
import groovy.time.TimeDuration
import groovy.time.TimeCategory
import grails.converters.JSON

import org.apache.commons.logging.LogFactory
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.codehaus.groovy.grails.core.io.ResourceLocator

import pl.touk.excel.export.WebXlsxExporter

import java.util.concurrent.*

import groovyx.gpars.GParsConfig
import groovyx.gpars.GParsPool


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

//@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class ItineraryController {
	def springSecurityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Itinerary.list(params), model:[itineraryInstanceCount: Itinerary.count()]
    }


	    def show(Itinerary itineraryInstance) {
        respond itineraryInstance
    }

    def create() {
        respond new Itinerary(params)
    }

	
	def showItineraryActions(){
		log.error('showItineraryActions called')
		params.each{i->log.error('parameter of list: '+i)}
		def itinerary
		def currentCalendar = Calendar.instance
		def criteria 
		def actionList
		def date_picker = params['date_picker'] 
		
		if (params['itineraryId'] != null)
			itinerary = Itinerary.get(params.int('itineraryId'))
		//	date_picker=04/07/2018
	
		if (date_picker != null && date_picker.size() > 0){
			currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
		}
		log.error('currentCalendar: '+currentCalendar.time)
		
		criteria = Action.createCriteria()
		actionList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
				eq('month',currentCalendar.get(Calendar.MONTH) + 1)
				eq('year',currentCalendar.get(Calendar.YEAR))
			}
		}
		return [
			actionList:actionList,
			itineraryId:params['itineraryId']
		]
		//log.error('done')
	}
	
    @Transactional
    def save(Itinerary itineraryInstance) {
		def user = springSecurityService.currentUser
		
		itineraryInstance.creationUser = user
		itineraryInstance.creationDate = new Date()
		itineraryInstance.validate()
        if (itineraryInstance == null) {
            notFound()
            return
        }

        if (itineraryInstance.hasErrors()) {
            respond itineraryInstance.errors, view:'create'
            return
        }

        itineraryInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'itineraryInstance.label', default: 'Itinerary'), itineraryInstance.id])
                redirect itineraryInstance
            }
            '*' { respond itineraryInstance, [status: CREATED] }
        }
    }

    def edit(Itinerary itineraryInstance) {
        respond itineraryInstance
    }

    @Transactional
    def update(Itinerary itineraryInstance) {
        if (itineraryInstance == null) {
            notFound()
            return
        }

        if (itineraryInstance.hasErrors()) {
            respond itineraryInstance.errors, view:'edit'
            return
        }

        itineraryInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Itinerary.label', default: 'Itinerary'), itineraryInstance.id])
                redirect itineraryInstance
            }
            '*'{ respond itineraryInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Itinerary itineraryInstance) {

        if (itineraryInstance == null) {
            notFound()
            return
        }

        itineraryInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Itinerary.label', default: 'Itinerary'), itineraryInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'itineraryInstance.label', default: 'Itinerary'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
