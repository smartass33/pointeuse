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
		log.error('saving authorizationInstance')
		//params.each{i-> log.error(i)}
		def typeId = params.int('type')
		def fromEditEmployee = params.boolean('fromEditEmployee')
		def showEmployee = params.boolean('showEmployee')
		def authorizationType = AuthorizationType.get(typeId)
		def employeeInstanceId = params.int('employeeInstanceId')
		def employee = Employee.get(employeeInstanceId)
		if (employee == null){
			employee = Employee.get(params.int('employee'))
		}
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
		def authorizationInstanceList
		if (fromEditEmployee){
			authorizationInstanceList = Authorization.findAllByEmployee(employee)		
		}else{
			authorizationInstanceList = Authorization.list()	
		}	
		flash.message = message(code: 'default.feminine.created.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), authorizationInstance.type.type])	
		render template: "/authorization/template/authorizationTable", model: [authorizationInstanceList:authorizationInstanceList,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]
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
                flash.message = message(code: 'default.updated.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), authorizationInstance.id])
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
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), authorizationInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	@Transactional
	def trashAuthorization(){
		log.error('trashAuthorization called')
	//	params.each{i-> log.error(i)}
		def fromEditEmployee = params.boolean('fromEditEmployee')
		def showEmployee = params.boolean('showEmployee')
		def authorization = Authorization.get(params.int('authorizationInstanceId'))
		def employee = authorization.employee
		def oldType = employee.lastName +', ' +authorization.type.type
		authorization.delete flush:true
		def authorizationInstanceList
		if (fromEditEmployee){
			authorizationInstanceList = Authorization.findAllByEmployee(employee)		
		}else{
			authorizationInstanceList = Authorization.list()	
		}		
		flash.message = message(code: 'default.feminine.deleted.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), oldType])	
		render template: "/authorization/template/authorizationTable", model: [authorizationInstanceList:authorizationInstanceList,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]
		return
 
		
	}
}
