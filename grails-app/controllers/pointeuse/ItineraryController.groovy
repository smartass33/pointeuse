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

import groovy.time.TimeCategory;

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

import java.text.ParseException;

//@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
class ItineraryController {
	def springSecurityService
	def itineraryService
	def PDFService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Itinerary.list(params), model:[itineraryInstanceCount: Itinerary.count()]
    }

	def addAction(){
		log.error('addAction called')
		
	}
	
	def show(Itinerary itineraryInstance) {
        respond itineraryInstance
    }

    def create() {
		def isExpanded = false
		def coursierFunction = Function.findByName('Coursier')
		def employeeList = Employee.findAllByFunction(coursierFunction)
		[employeeList:employeeList,checked:isExpanded]
    }
	
	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
	def itineraryPDF(){
		log.error('itinerarySitePDF called')
		params.each{i->log.error('parameter of list: '+i)}
		def folder = grailsApplication.config.pdf.directory
		def itinerary
		def viewType
		def siteTemplate
		def site
		def date_picker
		def serviceResponse
		def retour
		def currentCalendar = Calendar.instance
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('id')){
				viewType = params[name]
				siteTemplate = viewType.contains('BySite') ? true : false
			}
			if (name.contains('siteId'))
				site = Site.get(params.int(name))
			if (name.contains('date_picker')){
				date_picker = params[name]
				if (date_picker != null && date_picker.size() > 0){
					currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
					log.debug('currentCalendar.time: '+currentCalendar.time)
				}
			}
		}	
		
		if (siteTemplate){
			retour = PDFService.generateItineraryMonthlyReportBySite(viewType, itinerary, currentCalendar, site, folder)
		}else{
			retour = PDFService.generateItineraryMonthlyReportByItinerary(viewType, itinerary, currentCalendar, folder)
		}
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
	def trash(){
		log.error('trash called')
		params.each{i->log.debug('parameter of list: '+i)}
		def actionsList
		def criteria
		def action = Action.get(params["actionItemId"])
		def itinerary = action.itinerary
		action.delete flush:true
		criteria = Action.createCriteria()
		actionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		render template: "/itinerary/template/theoriticalActionTable", model: [
			itineraryInstance:itinerary,theoriticalActionsList:actionsList
		]
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
	def addTheoriticalAction(){
		log.error('addAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def itinerary
		def actionType
		def employee
		def currentDate
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('employeeId'))
				employee = Employee.get(params.int(name))
			if (name.contains('siteId'))
				site = Site.get(params.int(name))
			if (name.contains('time_picker')){		
				try {
				currentDate =  new Date().parse("dd/MM/yyyy HH:mm", params[name])
				} catch (ParseException e) {
					currentDate =  new Date().parse("HH:mm", params[name])
				}
			}
			if (name.contains('action.type'))
				actionType = (params[name]).equals('ARRIVEE') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
		}
		
		def action = new Action(itinerary, currentDate, site, itinerary.deliveryBoy, actionType)
		action.isTheoritical = true
		action.isSaturday = itinerary.isSaturday
		action.save flush:true
		def actionsList
		def criteria = Action.createCriteria()
		actionsList= criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		render template: "/itinerary/template/theoriticalActionTable", model: [
			itineraryInstance:itinerary,theoriticalActionsList:actionsList
			]
		return
	}
	
	def itineraryReport(){
		
	}
	
	def itinerarySiteView(){
		
	}

	def showItineraryActions(){
		log.error('showItineraryActions called')
		params.each{i->log.debug('parameter of list: '+i)}
		def itinerary
		def currentCalendar = Calendar.instance
		def criteria 
		def actionsList
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def date_picker
		def viewType
		def timeDiff
		def timeDiffMap = [:]
		def actionListMap = [:]
		def calendar = Calendar.instance
		def monthCalendar = Calendar.instance
		def i = 0
		def hasDiscrepancy = false
		def serviceResponse 
		def site
		def siteTemplate
		def retour
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def actionThOrderList
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('id')){
				viewType = params[name]
				siteTemplate = viewType.contains('BySite') ? true : false
			}
			if (name.contains('siteId'))
				site = Site.get(params.int(name))	
			if (name.contains('date_picker')){
				date_picker = params[name]
				if (date_picker != null && date_picker.size() > 0){
					currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
					log.debug('currentCalendar.time: '+currentCalendar.time)
				}
			}
		}
				
		serviceResponse = itineraryService.getActionMap(viewType, itinerary, currentCalendar, site)
		
		if (siteTemplate){
			theoriticalActionsMap        	= itineraryService.getTheoriticalActionMap(site,false) 
			theoriticalSaturdayActionsMap  	= itineraryService.getTheoriticalActionMap(site,true) 
			theoriticalActionsList         	= itineraryService.getTheoriticalActionList(site,false)
			theoriticalSaturdayActionsList 	= itineraryService.getTheoriticalActionList(site,true)			
		}else{
			theoriticalActionsList         = itineraryService.getTheoriticalActionList(itinerary,false)
			theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(itinerary,true)
		}
		
		
		actionsList = serviceResponse.get('actionsList')
		actionListMap = serviceResponse.get('actionListMap')
		def dailyActionMap = serviceResponse.get('dailyActionMap')
		def actionsThOrderedMap = serviceResponse.get('actionsThOrderedMap')

		if (siteTemplate){
			render template: "/itinerary/template/itinerarySiteReportTemplate", model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:serviceResponse.get('actionListMap'),
				dailyActionMap:serviceResponse.get('dailyActionMap'),
				actionsThOrderedMap:serviceResponse.get('actionsThOrderedMap'),
				theoriticalActionsList:theoriticalActionsList,
				theoriticalSaturdayActionsList:theoriticalSaturdayActionsList,
				theoriticalActionsMap:theoriticalActionsMap,
				theoriticalSaturdayActionsMap:theoriticalSaturdayActionsMap,
				hasDiscrepancy:hasDiscrepancy,
				timeDiffMap:timeDiffMap,
				viewType:viewType,
				siteTemplate:siteTemplate,
				site:site,
				timeDiffMap:timeDiffMap
				]
			return
		}else{
			render template: "/itinerary/template/itineraryReportTemplate", model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:serviceResponse.get('actionListMap'),
				dailyActionMap:serviceResponse.get('dailyActionMap'),
				theoriticalActionsList:theoriticalActionsList,
				theoriticalSaturdayActionsList:theoriticalSaturdayActionsList,
				hasDiscrepancy:hasDiscrepancy,
				timeDiffMap:timeDiffMap,
				viewType:viewType,
				siteTemplate:siteTemplate,
				timeDiffMap:timeDiffMap
				]
			return
		}
	}
	
    @Transactional
    def save(Itinerary itineraryInstance) {
		def user = springSecurityService.currentUser
		params.each{i->log.error('parameter of list: '+i)}
		def deliveryBoy = Employee.get(params.int('deliveryBoyId'))
		itineraryInstance.deliveryBoy = deliveryBoy
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
                flash.message = message(code: 'default.created.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), itineraryInstance.name])

                redirect itineraryInstance
            }
            '*' { respond itineraryInstance, [status: CREATED] }
        }
    }
	
	
	def changeDeliveryBoy(){		
		log.error('changeDeliveryBoy called')
		params.each{i->log.debug('parameter of list: '+i)}
		def itinerary = Itinerary.get(params.int('itineraryInstanceId'))
		def deliveryBoy = Employee.get(params.int('deliveryBoyId'))
		itinerary.deliveryBoy = deliveryBoy
		itinerary.save flush:true
		def coursierFunction = Function.findByName('Coursier')
		def employeeList = Employee.findAllByFunction(coursierFunction)
		def theoriticalActionsList
		def criteria = Action.createCriteria()
		theoriticalActionsList= criteria.list {
			and {
				eq('itinerary',itineraryInstance)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		return
	 //  [itineraryInstance:itinerary,theoriticalActionsList:theoriticalActionsList,employeeList:employeeList]
	}
	
	
    def edit(Itinerary itineraryInstance) {
		def coursierFunction = Function.findByName('Coursier')
		def employeeList = Employee.findAllByFunction(coursierFunction)
		def theoriticalActionsList
		def criteria = Action.createCriteria()
		theoriticalActionsList= criteria.list {
			and {
				eq('itinerary',itineraryInstance)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
       [itineraryInstance:itineraryInstance,theoriticalActionsList:theoriticalActionsList,employeeList:employeeList]
    }

    @Transactional
    def update(Itinerary itineraryInstance) {
		
		params.each{i->log.debug('parameter of list: '+i)}
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
                flash.message = message(code: 'default.updated.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), itineraryInstance.name])
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
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), itineraryInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
