package pointeuse


import java.util.Date;

import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import groovy.time.TimeCategory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException
import javax.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat

import grails.converters.JSON

@Transactional(readOnly = true)

class ActionController {

	def ajaxUploaderService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	def itineraryService

	@Secured(['ROLE_ADMIN'])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Action.list(params), model:[actionInstanceCount: Action.count()]
    }

	@Secured(['ROLE_ADMIN'])
    def show(Action actionInstance) {
        respond actionInstance
    }

	@Secured(['ROLE_ADMIN'])
    def create() {
        respond new Action(params)
    }

	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def modifyTheoriticalAction(){
		log.error('modifyTheoriticalAction called')
		def calendar = Calendar.instance
		def actionsList
		def criteria = Action.createCriteria()
		def action
		def itinerary
		def date_action_picker
		def dateFormat
		def myDate

		params.each { name, value ->
			if (name.contains('actionItemId')){
				action = Action.get(params.int(name))
				itinerary = action.itinerary
				calendar.time = action.date
			}		
			if (name.contains('date_action_picker')){
				date_action_picker = params[name]
			}
		}			

		
		dateFormat = new SimpleDateFormat('HH:mm');
		myDate = dateFormat.parse(date_action_picker)
		calendar.set(Calendar.HOUR_OF_DAY,myDate.getAt(Calendar.HOUR_OF_DAY))
		calendar.set(Calendar.MINUTE,myDate.getAt(Calendar.MINUTE))
		log.error('calendar.time : '+calendar.time)
		action.date = calendar.time
		action.save flush:true
		
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
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def trash(){
		log.error('trash called')
		params.each{i->log.error('parameter of list: '+i)}
		def viewType
		def itinerary
		def action
		def calendar = Calendar.instance
		def date_action_picker
		SimpleDateFormat dateFormat
		def criteria
		def actionsList
		def actionListMap = [:]
		def timeDiffMap = [:]
		def monthCalendar = Calendar.instance
		def theoriticalActionsList
		def hasDiscrepancy = false
		def i = 0
		def timeDiff
		def myDate
		def actionType
		def serviceResponse

		
		
		params.each { name, value ->
			if (name.contains('ActionItemId')){
				action = Action.get(params.int(name))
				itinerary = action.itinerary
				calendar.time = action.date
				action.delete flush:true
			}
			if (name.contains('action_picker')){
				date_action_picker = params[name]
			}

		}		
		
		if (params['viewType'] != null)
			viewType = params['viewType']

		serviceResponse = itineraryService.getActionList(viewType, itinerary,currentCalendar)
	
		criteria = Action.createCriteria()
		theoriticalActionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
	
		hasDiscrepancy = (serviceResponse.get('actionsList') != null && serviceResponse.get('actionsList').size() != theoriticalActionsList.size()) ? true : false
		
		if (serviceResponse.get('actionsList') != null && serviceResponse.get('actionsList').size() > 0){
			for (Action actionIter in theoriticalActionsList){
				calendar.setTime(actionIter.date)
				if (serviceResponse.get('actionsList').size() > i && serviceResponse.get('actionsList').get(i) != null){
					use (TimeCategory){timeDiff = calendar.time - actionsList.get(i).date}
					timeDiffMap.put(i, timeDiff)
				}else{
					timeDiffMap.put(i, 0)
				}
				i++
			}
		}
		
		render template: "/itinerary/template/itineraryReportTemplate", model: [
			itineraryInstance:itinerary,
			actionsList:serviceResponse.get('actionsList'),
			theoriticalActionsList:theoriticalActionsList,
			hasDiscrepancy:hasDiscrepancy,
			actionListMap:serviceResponse.get('actionListMap'),
			timeDiffMap:timeDiffMap,
			viewType:viewType
			]
		return
		
		
	}
	
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def regularizeAction(){
		log.error('regularizeAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def itinerary
		def employee
		def viewType
		def calendar = Calendar.instance
		def date_action_picker
		SimpleDateFormat dateFormat
		def criteria
		def actionsList
		def actionListMap = [:]
		def timeDiffMap = [:]
		def monthCalendar = Calendar.instance
		def theoriticalActionsList
		def hasDiscrepancy = false
		def i = 0
		def timeDiff
		def myDate
		def actionType
		def serviceResponse
		
		params.each { name, value ->
			if (name.contains('action_picker')){
				date_action_picker = params[name]
			}
			if (name.contains('itineraryId')){
				itinerary = Itinerary.get(params.int(name))
			}
			if (name.contains('itineraryId')){
				site = Site.get(params.int(name))
			}
			if (name.contains('employeeId')){
				employee = Employee.get(params.int(name))
			}
			if (name.contains('action.type')){
				actionType = params['action.type']
			}
		}
		
		
		dateFormat = new SimpleDateFormat('dd/MM/yyyy HH:mm');
		myDate = dateFormat.parse(date_action_picker)
		//calendar.set(Calendar.HOUR_OF_DAY,myDate.getAt(Calendar.HOUR_OF_DAY))
		//calendar.set(Calendar.MINUTE,myDate.getAt(Calendar.MINUTE))
		calendar.time = myDate
			
		///actionType = params['action.type']
		if (actionType != null){
			actionType = actionType.equals('ARR') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
		}
		def action = new Action(itinerary, calendar.time, site, employee, actionType)
		action.save flush:true
		log.error('done')
		
		if (params['viewType'] != null)
			viewType = params['viewType']
	
		serviceResponse = itineraryService.getActionList(viewType, itinerary, calendar)
		
		criteria = Action.createCriteria()
		theoriticalActionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
	
		hasDiscrepancy = (serviceResponse.get('actionsList') != null && serviceResponse.get('actionsList').size() != theoriticalActionsList.size()) ? true : false
		
		if (serviceResponse.get('actionsList') != null && serviceResponse.get('actionsList').size() > 0){
			for (Action actionIter in theoriticalActionsList){
				calendar.setTime(actionIter.date)
				if (serviceResponse.get('actionsList').size() > i && serviceResponse.get('actionsList').get(i) != null){
					use (TimeCategory){timeDiff = calendar.time - serviceResponse.get('actionsList').get(i).date}
					timeDiffMap.put(i, timeDiff)
				}else{
					timeDiffMap.put(i, 0)
				}
				i++
			}
		}
		
		render template: "/itinerary/template/itineraryReportTemplate", model: [
			itineraryInstance:itinerary,
			actionsList:serviceResponse.get('actionsList'),
			theoriticalActionsList:theoriticalActionsList,
			hasDiscrepancy:hasDiscrepancy,
			actionListMap:serviceResponse.get('actionListMap'),
			timeDiffMap:timeDiffMap,
			viewType:viewType
			]
		return

	}
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def modifyAction(){
		log.error('modifyAction called')
		params.each{i->log.error('parameter of list: '+i)}
		
		def viewType
		def itinerary
		def action
		def calendar = Calendar.instance
		def date_action_picker
		SimpleDateFormat dateFormat
		def criteria
		def actionsList
		def actionListMap = [:]
		def timeDiffMap = [:]
		def monthCalendar = Calendar.instance
		def theoriticalActionsList
		def hasDiscrepancy = false
		def i = 0
		def timeDiff
		def myDate
		def actionType

			
		params.each { name, value ->
			if (name.contains('ActionItemId')){
				action = Action.get(params.int(name))
				itinerary = action.itinerary
				calendar.time = action.date
			}
			if (name.contains('action_picker')){
				date_action_picker = params[name] 
			}
			if (name.contains('itineraryId')){
				itinerary = Itinerary.get(params.int(name))			
			}
		}
		if (params['actionType'] != null ){
			action.nature = (params['actionType']).equals('ARR') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
		}
		
		dateFormat = new SimpleDateFormat('HH:mm');
		myDate = dateFormat.parse(date_action_picker)	
		calendar.set(Calendar.HOUR_OF_DAY,myDate.getAt(Calendar.HOUR_OF_DAY))
		calendar.set(Calendar.MINUTE,myDate.getAt(Calendar.MINUTE))
		log.error('calendar.time : '+calendar.time)
		action.date = calendar.time
		action.save flush:true
					
		
		if (params['viewType'] != null)
			viewType = params['viewType'] 
	
		serviceResponse = itineraryService.getActionList(viewType, itinerary,currentCalendar)	
		
		criteria = Action.createCriteria()
		theoriticalActionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
	
		hasDiscrepancy = (serviceResponse.get('actionsList') != null && serviceResponse.get('actionsList').size() != theoriticalActionsList.size()) ? true : false
		
		if (serviceResponse.get('actionsList') != null && serviceResponse.get('actionsList').size() > 0){
			for (Action actionIter in theoriticalActionsList){
				calendar.setTime(actionIter.date)				
				if (serviceResponse.get('actionsList').size() > i && serviceResponse.get('actionsList').get(i) != null){
					use (TimeCategory){timeDiff = calendar.time - serviceResponse.get('actionsList').get(i).date}
					timeDiffMap.put(i, timeDiff)
				}else{
					timeDiffMap.put(i, 0)
				}
				i++
			}
		}
		
		render template: "/itinerary/template/itineraryReportTemplate", model: [
			itineraryInstance:itinerary,
			actionsList:serviceResponse.get('actionsList'),
			theoriticalActionsList:theoriticalActionsList,
			hasDiscrepancy:hasDiscrepancy,
			actionListMap:serviceResponse.get('actionListMap'),
			timeDiffMap:timeDiffMap,
			viewType:viewType
			]
		return
		
	}
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def addAction(){
		log.error('addAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def itinerary
		def actionType
		def employee
		if (params['employeeId'] != null)
			employee = Employee.get(params.int('employeeId'))
		if (params['siteId'] != null)
			site = Site.get(params.int('siteId'))
		if (params['itineraryId'] != null)
			itinerary = Itinerary.get(params.int('itineraryId'))
			
		actionType = params['action.type']
		if (actionType != null){
			
			actionType = actionType.equals('ARR') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
		}
		def action = new Action(itinerary, new Date(), site, employee, actionType) 	
		action.save flush:true
		log.error('done')
	}
	
    @Transactional
    def save(Action actionInstance) {
        if (actionInstance == null) {
            notFound()
            return
        }

        if (actionInstance.hasErrors()) {
            respond actionInstance.errors, view:'create'
            return
        }

        actionInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'actionInstance.label', default: 'Action'), actionInstance.id])
                redirect actionInstance
            }
            '*' { respond actionInstance, [status: CREATED] }
        }
    }
	
    def edit(Action actionInstance) {
        respond actionInstance
    }

    @Transactional
    def update(Action actionInstance) {
        if (actionInstance == null) {
            notFound()
            return
        }

        if (actionInstance.hasErrors()) {
            respond actionInstance.errors, view:'edit'
            return
        }

        actionInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Action.label', default: 'Action'), actionInstance.id])
                redirect actionInstance
            }
            '*'{ respond actionInstance, [status: OK] }
        }
    }

	@Secured(['ROLE_ADMIN'])
    @Transactional
    def delete(Action actionInstance) {

        if (actionInstance == null) {
            notFound()
            return
        }

        actionInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Action.label', default: 'Action'), actionInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }
	
    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'actionInstance.label', default: 'Action'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
