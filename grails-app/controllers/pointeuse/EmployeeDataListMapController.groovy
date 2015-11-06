package pointeuse



import static org.springframework.http.HttpStatus.*

import java.util.Date;

import grails.transaction.Transactional

@Transactional(readOnly = true)
class EmployeeDataListMapController {

	def springSecurityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMapInstance = criteria.get {
			maxResults(1)
		}
		
        [employeeDataListMapInstance: employeeDataListMapInstance]
    }

    def show(EmployeeDataListMap employeeDataListMapInstance) {
        respond employeeDataListMapInstance
    }

    def create() {
        respond new EmployeeDataListMap(params)
    }

    @Transactional
    def save(EmployeeDataListMap employeeDataListMapInstance) {
        if (employeeDataListMapInstance == null) {
            notFound()
            return
        }

        if (employeeDataListMapInstance.hasErrors()) {
            respond employeeDataListMapInstance.errors, view:'create'
            return
        }

        employeeDataListMapInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'employeeDataListMapInstance.label', default: 'EmployeeDataListMap'), employeeDataListMapInstance.id])
                redirect employeeDataListMapInstance
            }
            '*' { respond employeeDataListMapInstance, [status: CREATED] }
        }
    }

    def edit(EmployeeDataListMap employeeDataListMapInstance) {
        respond employeeDataListMapInstance
    }

    @Transactional
    def update(EmployeeDataListMap employeeDataListMapInstance) {
        if (employeeDataListMapInstance == null) {
            notFound()
            return
        }

        if (employeeDataListMapInstance.hasErrors()) {
            respond employeeDataListMapInstance.errors, view:'edit'
            return
        }

        employeeDataListMapInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'EmployeeDataListMap.label', default: 'EmployeeDataListMap'), employeeDataListMapInstance.id])
                redirect employeeDataListMapInstance
            }
            '*'{ respond employeeDataListMapInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(EmployeeDataListMap employeeDataListMapInstance) {

        if (employeeDataListMapInstance == null) {
            notFound()
            return
        }

        employeeDataListMapInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'EmployeeDataListMap.label', default: 'EmployeeDataListMap'), employeeDataListMapInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeDataListMapInstance.label', default: 'EmployeeDataListMap'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	@Transactional
	def addNewEmployeeData(){
		def user = springSecurityService.currentUser
		log.error('addNewEmployeeData called')
		params.each{i->log.error('parameter of list: '+i)}
		def fieldName = params['fieldname']
		def type = params['type']
		def isHiddenField = false
		
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMap = criteria.get {
			maxResults(1)
		}
		
		if (employeeDataListMap == null){
			employeeDataListMap = new EmployeeDataListMap(new Date(),user)
			employeeDataListMap.fieldMap = [:]
			employeeDataListMap.hiddenFieldMap = [:]
		}
		
		if (isHiddenField){
			employeeDataListMap.hiddenFieldMap.put(fieldName,type)
		}else{
			employeeDataListMap.fieldMap.put(fieldName,type)		
		}
		employeeDataListMap.modificationUser = user
		employeeDataListMap.lastModification = new Date()
		employeeDataListMap.save(flush:true)
		log.error('employeeDataListMap saved')
		redirect(action: "index")		
	}
}
