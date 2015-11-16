package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AuthorizationTypeController {

	def springSecurityService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond AuthorizationType.list(params), model:[authorizationTypeInstanceCount: AuthorizationType.count()]
    }

    def show(AuthorizationType authorizationTypeInstance) {
        respond authorizationTypeInstance
    }

    def create() {
        respond new AuthorizationType(params)
    }

    @Transactional
    def save(AuthorizationType authorizationTypeInstance) {
		try  {
			def user = springSecurityService.currentUser
			authorizationTypeInstance.user = user
			authorizationTypeInstance.creationDate = new Date()
	        if (authorizationTypeInstance == null) {
	            notFound()
	            return
	        }
	
			authorizationTypeInstance.validate()
	        if (authorizationTypeInstance.hasErrors()) {
	            respond authorizationTypeInstance.errors, view:'create'
	            return
	        }
	
	        authorizationTypeInstance.save flush:true
	
	        request.withFormat {
	            form {
	                flash.message = message(code: 'default.created.message', args: [message(code: 'authorizationType.label', default: 'AuthorizationType'), authorizationTypeInstance.name])
	                redirect authorizationTypeInstance
	            }
	            '*' { respond authorizationTypeInstance, [status: CREATED] }
	        }
		}catch (Exception e){
			flash.message = message('impossible.delete.message', args: [authorizationTypeInstance.name])
			redirect authorizationTypeInstance
			return
		}
    }

    def edit(AuthorizationType authorizationTypeInstance) {
        respond authorizationTypeInstance
    }

    @Transactional
    def update(AuthorizationType authorizationTypeInstance) {
        if (authorizationTypeInstance == null) {
            notFound()
            return
        }

        if (authorizationTypeInstance.hasErrors()) {
            respond authorizationTypeInstance.errors, view:'edit'
            return
        }

        authorizationTypeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'AuthorizationType.label', default: 'AuthorizationType'), authorizationTypeInstance.id])
                redirect authorizationTypeInstance
            }
            '*'{ respond authorizationTypeInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(AuthorizationType authorizationTypeInstance) {

        if (authorizationTypeInstance == null) {
            notFound()
            return
        }

        authorizationTypeInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'AuthorizationType.label', default: 'AuthorizationType'), authorizationTypeInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'authorizationType.label', default: 'AuthorizationType'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
