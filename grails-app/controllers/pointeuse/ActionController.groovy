package pointeuse


import java.util.Date;

import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
<<<<<<< HEAD
import groovy.time.TimeCategory
=======
>>>>>>> f97e9cdf9e03a347c03000841888e04cc6c7f400

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException
import javax.servlet.http.HttpServletRequest
<<<<<<< HEAD
import java.text.SimpleDateFormat
=======
>>>>>>> f97e9cdf9e03a347c03000841888e04cc6c7f400

import grails.converters.JSON

@Transactional(readOnly = true)

class ActionController {

	def ajaxUploaderService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

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
<<<<<<< HEAD

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
			
		dateFormat = new SimpleDateFormat('HH:mm');
		myDate = dateFormat.parse(date_action_picker)	
		calendar.set(Calendar.HOUR_OF_DAY,myDate.getAt(Calendar.HOUR_OF_DAY))
		calendar.set(Calendar.MINUTE,myDate.getAt(Calendar.MINUTE))
		log.error('calendar.time : '+calendar.time)
		action.date = calendar.time
		action.save flush:true
					
		
		if (params['viewType'] != null)
		viewType = params['viewType'] 
	
		criteria = Action.createCriteria()
	
		switch (viewType) {
				case 'dailyView':
					actionsList = criteria.list {
						and {
							eq('itinerary',itinerary)
							eq('day',calendar.get(Calendar.DAY_OF_MONTH))
							eq('month',calendar.get(Calendar.MONTH) + 1)
							eq('year',calendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
						}
					}
					break
				case 'monthlyView':
					monthCalendar = calendar
					monthCalendar.set(Calendar.DAY_OF_MONTH,1)
					def lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					
					for (int j = 1;j < lastDay + 1;j++){
						
						criteria = Action.createCriteria()
						actionsList = criteria.list {
							and {
								eq('itinerary',itinerary)
								eq('day',monthCalendar.get(Calendar.DAY_OF_MONTH))
								eq('month',monthCalendar.get(Calendar.MONTH) + 1)
								eq('year',monthCalendar.get(Calendar.YEAR))
								eq('isTheoritical',false)
							}
						}
						actionListMap.put(monthCalendar.time,actionsList)
						monthCalendar.roll(Calendar.DAY_OF_MONTH,1)
					}
					break
				default:
					actionsList = criteria.list {
						and {
							eq('itinerary',itinerary)
							eq('day',calendar.get(Calendar.DAY_OF_MONTH))
							eq('month',calendar.get(Calendar.MONTH) + 1)
							eq('year',calendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
						}
					}
					break
			}
		
		criteria = Action.createCriteria()
		theoriticalActionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
	
		hasDiscrepancy = (actionsList != null && actionsList.size() != theoriticalActionsList.size()) ? true : false
		
		if (actionsList != null && actionsList.size() > 0){
			for (Action actionIter in theoriticalActionsList){
				calendar.setTime(actionIter.date)				
				if (actionsList.size() > i && actionsList.get(i) != null){
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
			actionsList:actionsList,
			theoriticalActionsList:theoriticalActionsList,
			hasDiscrepancy:hasDiscrepancy,
			actionListMap:actionListMap,
			timeDiffMap:timeDiffMap,
			viewType:viewType
			]
		return
		
	}
=======
	

>>>>>>> f97e9cdf9e03a347c03000841888e04cc6c7f400
	
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
