package pointeuse


import java.util.Date;

import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException
import javax.servlet.http.HttpServletRequest

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
