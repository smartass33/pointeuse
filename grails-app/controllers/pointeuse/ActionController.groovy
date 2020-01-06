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
import java.text.ParseException

import grails.converters.JSON

@Transactional(readOnly = true)

@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
class ActionController {

	def ajaxUploaderService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	def itineraryService

	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
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

	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS','ROLE_TOURNEE'])
	def modifyTheoriticalAction(){
		log.error('modifyTheoriticalAction called')
		def calendar = Calendar.instance
		def actionsList
		def criteria = Action.createCriteria()
		def action
		def itinerary
		def date_action_picker
		def dateFormat
		def currentDate

		params.each { name, value ->
			if (name.contains('actionItemId')){
				action = Action.get(params.int(name))
				itinerary = action.itinerary
				calendar.time = action.date
			}		

			
			if (name.contains('action_picker')){
				date_action_picker = params[name]
				try {
					currentDate =  new Date().parse("dd/MM/yyyy HH:mm", params[name])
				} catch (ParseException e) {
					currentDate =  new Date().parse("HH:mm", params[name])
				}
				
			}
			
			
		}			

		
		calendar.set(Calendar.HOUR_OF_DAY,currentDate.getAt(Calendar.HOUR_OF_DAY))
		calendar.set(Calendar.MINUTE,currentDate.getAt(Calendar.MINUTE))
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
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS','ROLE_TOURNEE'])
	def trash(){
		log.error('trash called')
		params.each{i->log.debug('parameter of list: '+i)}
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
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def monthCalendar = Calendar.instance
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def hasDiscrepancy = false
		def i = 0
		def timeDiff
		def myDate
		def actionType
		def serviceResponse
		def site
		def siteTemplate
		
		params.each { name, value ->
			if (name.contains('ActionItemId')){
				action = Action.get(params.int(name))
				itinerary = action.itinerary
				calendar.time = action.date
				site = action.site
				action.delete flush:true
			}
			if (name.contains('action_picker')){
				date_action_picker = params[name]
			}
			if (name.contains('viewType')){
				viewType = params[name]
				siteTemplate = (params[name]).contains('BySite') ? true : false
			}
		}		
		
		serviceResponse = itineraryService.getActionMap(viewType, itinerary, calendar, site)
		
		if (siteTemplate){
			theoriticalActionsMap        	= itineraryService.getTheoriticalActionMap(site,false) 
			theoriticalSaturdayActionsMap  	= itineraryService.getTheoriticalActionMap(site,true) 
			theoriticalActionsList         	= itineraryService.getTheoriticalActionList(site,false)
			theoriticalSaturdayActionsList 	= itineraryService.getTheoriticalActionList(site,true)			
		}else{
			theoriticalActionsList         = itineraryService.getTheoriticalActionList(itinerary,false)
			theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(itinerary,true)
		}
		
		calendar.set(Calendar.DAY_OF_MONTH,1)
		def actionIterList 
		def orderedActionList = []
		def theoriticalListRef = []
		
		for (int j = 1;j < calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;j++){
			
			theoriticalListRef = (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) ? theoriticalActionsList.collect() : theoriticalSaturdayActionsList.collect()
			actionIterList = serviceResponse.get('actionListMap').get(calendar.time)
			
			// compare to theoriticalActionsList
			if (theoriticalListRef != null && actionIterList != null && theoriticalListRef.size() == actionIterList.size()){
				orderedActionList = itineraryService.orderList(theoriticalListRef, actionIterList, [])
			}else{
				orderedActionList = actionIterList
			}
			actionListMap.put(calendar.time,orderedActionList)
			orderedActionList = []
			calendar.roll(Calendar.DAY_OF_MONTH,1)
		}
	
		if (siteTemplate){
			render template: "/itinerary/template/itinerarySiteReportTemplate", model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:actionListMap,
				dailyActionMap:serviceResponse.get('dailyActionMap'),
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
	
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS','ROLE_TOURNEE'])
	def regularizeAction(){
		log.error('regularizeAction called')
		params.each{i->log.debug('parameter of list: '+i)}
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
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def monthCalendar = Calendar.instance
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def hasDiscrepancy = false
		def i = 0
		def timeDiff
		def myDate
		def actionType
		def serviceResponse
		def siteTemplate
		
		params.each { name, value ->
			if (name.contains('action_picker')){
				date_action_picker = params[name]
			}
			if (name.contains('itineraryId')){
				itinerary = Itinerary.get(params.int(name))
			}
			if (name.contains('siteId')){
				site = Site.get(params.int(name))
			}
			if (name.contains('employeeId')){
				employee = Employee.get(params.int(name))
			}
			if (name.contains('action.type') && value.size() > 0){
				actionType = (value.equals('ARR') )? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
			}
			if (name.contains('viewType')){
				viewType = params[name]
				siteTemplate = viewType.contains('BySite') ? true : false			
			}
		}
		
		dateFormat = new SimpleDateFormat('dd/MM/yyyy HH:mm');
		myDate = dateFormat.parse(date_action_picker)
		calendar.time = myDate
			
		def action = new Action(itinerary, calendar.time, site, employee, actionType)
		action.save flush:true
	
		serviceResponse = itineraryService.getActionMap(viewType, itinerary, calendar, site)
		
		if (siteTemplate){
			theoriticalActionsMap        	= itineraryService.getTheoriticalActionMap(site,false) 
			theoriticalSaturdayActionsMap  	= itineraryService.getTheoriticalActionMap(site,true) 
			theoriticalActionsList         	= itineraryService.getTheoriticalActionList(site,false)
			theoriticalSaturdayActionsList 	= itineraryService.getTheoriticalActionList(site,true)			
		}else{
			theoriticalActionsList         = itineraryService.getTheoriticalActionList(itinerary,false)
			theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(itinerary,true)
		}
		
		calendar.set(Calendar.DAY_OF_MONTH,1)
		def actionIterList 
		def orderedActionList = []
		def theoriticalListRef = []
		
		for (int j = 1;j < calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;j++){
			
			theoriticalListRef = (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) ? theoriticalActionsList.collect() : theoriticalSaturdayActionsList.collect()
			actionIterList = serviceResponse.get('actionListMap').get(calendar.time)
			
			// compare to theoriticalActionsList
			if (theoriticalListRef != null && actionIterList != null && theoriticalListRef.size() == actionIterList.size()){
				orderedActionList = itineraryService.orderList(theoriticalListRef, actionIterList, [])
			}else{
				orderedActionList = actionIterList
			}
			actionListMap.put(calendar.time,orderedActionList)
			orderedActionList = []
			calendar.roll(Calendar.DAY_OF_MONTH,1)
		}
	
		if (siteTemplate){
			render template: "/itinerary/template/itinerarySiteReportTemplate", model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:actionListMap,
				dailyActionMap:serviceResponse.get('dailyActionMap'),
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
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS','ROLE_TOURNEE'])
	def modifyAction(){
		log.error('modifyAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def viewType
		def itinerary
		def action
		def calendar = Calendar.instance
		def currentCalendar = Calendar.instance
		def date_action_picker
		SimpleDateFormat dateFormat
		def criteria
		def actionsList
		def actionListMap = [:]
		def timeDiffMap = [:]
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def monthCalendar = Calendar.instance
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def hasDiscrepancy = false
		def i = 0
		def timeDiff
		def myDate
		def actionType
		def serviceResponse
		def site
		def siteTemplate
		def currentDate
		def commentary
		def fnc
		def other
		def itineraryNew
			
		params.each { name, value ->
			if (name.contains('ActionItemId')){
				action = Action.get(params.int(name))
				itinerary = action.itinerary
				calendar.time = action.date
				log.error('calendar.time: '+calendar.time)
			}
			if (name.contains('actionType') && value.size() > 0){
				action.nature = (value.equals('ARR') )? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
			}
			if (name.contains('commentary') && value.size() > 0){
				commentary = value
			}
			if (name.contains('fnc') && value.size() > 0){
				fnc = value
			}
			if (name.contains('other') && value.size() > 0){
				other = value
			}
			if (name.contains('itineraryId') && !name.contains('null')){
				itinerary = Itinerary.get(params.int(name))
			}
			if (name.contains('viewType')){
				viewType = params['viewType']
				siteTemplate = viewType.contains('BySite') ? true : false
			}		
			if (name.contains('action_picker')){
				date_action_picker = params[name] 
				try {
					currentDate =  new Date().parse("dd/MM/yyyy HH:mm", params[name])
				} catch (ParseException e) {
					currentDate =  new Date().parse("HH:mm", params[name])
				}		
			}
		}
		
		
		def itineraryID = params['itineraryId']
		
		calendar.set(Calendar.HOUR_OF_DAY,currentDate.getAt(Calendar.HOUR_OF_DAY))
		calendar.set(Calendar.MINUTE,currentDate.getAt(Calendar.MINUTE))
		action.date = calendar.time
		if (commentary != null)
			action.commentary = commentary
		if (itineraryID != null){
			itineraryNew = Itinerary.get(itineraryID)
			action.itinerary = itineraryNew
		}
		if (fnc != null)
			action.fnc = fnc
		if (other != null)
			action.other = other

		action.save flush:true
		
		if (siteTemplate){
			site = action.site
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
		
		currentCalendar.set(Calendar.DAY_OF_MONTH,1)
		def actionIterList 
		def orderedActionList = []
		def theoriticalListRef = []
		
		for (int j = 1;j < currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;j++){
			log.debug("date: "+currentCalendar.time)
			theoriticalListRef = (currentCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) ? theoriticalActionsList.collect() : theoriticalSaturdayActionsList.collect()
			actionIterList = serviceResponse.get('actionListMap').get(currentCalendar.time)
			
			// compare to theoriticalActionsList
			if (theoriticalListRef != null && actionIterList != null && theoriticalListRef.size() == actionIterList.size()){
				orderedActionList = itineraryService.orderList(theoriticalListRef, actionIterList, [])
			}else{
				orderedActionList = actionIterList
			}
			actionListMap.put(currentCalendar.time,orderedActionList)
			orderedActionList = []
			currentCalendar.roll(Calendar.DAY_OF_MONTH,1)
		}
		if (siteTemplate){
			render template: "/itinerary/template/itinerarySiteReportTemplate",
				model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:actionListMap,
				dailyActionMap:serviceResponse.get('dailyActionMap'),
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
			render template: "/itinerary/template/itineraryReportTemplate",
				model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:actionListMap,
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
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS','ROLE_TOURNEE'])
	def addAction(){
		log.error('addAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def itinerary
		def actionType
		def employee
		def currentDate
		def isNotDone = false
		def isRelay = false
		
		params.each { name, value ->
			if (name.contains('employeeId')){
				employee = Employee.get(params.int(name))
			}
			if (name.contains('itineraryId')){
				itinerary = Itinerary.get(params.int(name))
			}
			if (name.contains('siteId')){
				site = Site.get(params.int(name))
			}
			if (name.contains('time_picker')){
				currentDate =  new Date().parse("dd/MM/yyyy HH:mm", params[name])
			}
			if (name.contains('action.type')){
				actionType = (params[name]).equals('ARR') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
			}
			if (name.contains('chkBoxNE')){
				if (params[name] != null && params[name].size() > 0 )
					isNotDone = true
			}
			if (name.contains('chkBoxRELAY')){
				if (params[name] != null && params[name].size() > 0 )
					isRelay = true
			}
			
		}
		
		def action = new Action(itinerary, currentDate, site, employee, actionType) 	
		action.isNotDone = isNotDone
		action.isRelay = isRelay
		action.save flush:true
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

	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
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
