package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AuthorizationController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
		def employeeInstanceId = params.int('employeeInstanceId')
		def employeeInstance = Employee.get(employeeInstanceId)
        params.max = Math.min(max ?: 10, 100)
		respond Authorization.list(params),model:[employeeInstance:employeeInstance,authorizationInstanceCount: Authorization.count(),employeeInstanceId:employeeInstanceId]
    }

    def show(Authorization authorizationInstance) {
        respond authorizationInstance
    }

    def create() {
		def employeeInstanceId = params.int('employeeInstanceId')
		def employeeInstance = Employee.get(employeeInstanceId)
		respond new Authorization(params),model:[employeeInstance:employeeInstance,employeeInstanceId:employeeInstanceId]
    }

    @Transactional
    def save(Authorization authorizationInstance) {
		//params.each{i-> log.error(i)}
		def typeId = params.int('type')
		def authorizationType = AuthorizationType.get(typeId)
		def employeeInstanceId = params.int('employeeInstanceId')
		def employee = Employee.get(employeeInstanceId)
		if (employee != null){
			authorizationInstance.employee = employee
		}
		if (authorizationType != null){
			authorizationInstance.type = authorizationType
		}
		
        if (authorizationInstance == null) {
            notFound()
            return
        }
		authorizationInstance.validate()
        if (authorizationInstance.hasErrors()) {
            respond authorizationInstance.errors, view:'create'
            return
        }
        authorizationInstance.save flush:true
		def authorizationInstanceList = Authorization.findAllByEmployee(employee)
		flash.message = message(code: 'default.created.message', args: [message(code: 'authorizationInstance.label', default: 'Authorization'), authorizationInstance.id])	
		render template: "/authorization/template/authorizationTable", model: [authorizationInstanceList:authorizationInstanceList]
		return
    }

    def edit(Authorization authorizationInstance) {
        respond authorizationInstance
    }

    @Transactional
    def update(Authorization authorizationInstance) {
        if (authorizationInstance == null) {
            notFound()
            return
        }

        if (authorizationInstance.hasErrors()) {
            respond authorizationInstance.errors, view:'edit'
            return
        }

        authorizationInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Authorization.label', default: 'Authorization'), authorizationInstance.id])
                redirect authorizationInstance
            }
            '*'{ respond authorizationInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Authorization authorizationInstance) {
        if (authorizationInstance == null) {
            notFound()
            return
        }

        authorizationInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Authorization.label', default: 'Authorization'), authorizationInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'authorizationInstance.label', default: 'Authorization'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	@Transactional
	def trashAuthorization(){
		params.each{i-> log.error(i)}
		def authorization = Authorization.get(params.int('authorizationInstanceId'))
		def employee = authorization.employee
		authorization.delete flush:true
		def authorizationInstanceList = Authorization.findAllByEmployee(employee)
		render template: "/authorization/template/authorizationTable", model: [authorizationInstanceList:authorizationInstanceList]
		return
 
		
	}
}
