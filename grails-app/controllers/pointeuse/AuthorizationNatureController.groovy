package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AuthorizationNatureController {

	def springSecurityService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond AuthorizationNature.list(params), model:[authorizationNatureInstanceCount: AuthorizationNature.count()]
    }

    def show(AuthorizationNature authorizationNatureInstance) {
        respond authorizationNatureInstance
    }

    def create() {
        respond new AuthorizationNature(params)
    }

    @Transactional
    def save(AuthorizationNature authorizationNatureInstance) {
		def user = springSecurityService.currentUser
		authorizationNatureInstance.user = user
		authorizationNatureInstance.creationDate = new Date()
		
        if (authorizationNatureInstance == null) {
            notFound()
            return
        }
		authorizationNatureInstance.validate()
        if (authorizationNatureInstance.hasErrors()) {
            respond authorizationNatureInstance.errors, view:'create'
            return
        }

        authorizationNatureInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'authorizationNature.label', default: 'AuthorizationNature'), authorizationNatureInstance.name])
                redirect authorizationNatureInstance
            }
            '*' { respond authorizationNatureInstance, [status: CREATED] }
        }
    }

    def edit(AuthorizationNature authorizationNatureInstance) {
        respond authorizationNatureInstance
    }

    @Transactional
    def update(AuthorizationNature authorizationNatureInstance) {
        if (authorizationNatureInstance == null) {
            notFound()
            return
        }

        if (authorizationNatureInstance.hasErrors()) {
            respond authorizationNatureInstance.errors, view:'edit'
            return
        }

        authorizationNatureInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'AuthorizationNature.label', default: 'AuthorizationNature'), authorizationNatureInstance.id])
                redirect authorizationNatureInstance
            }
            '*'{ respond authorizationNatureInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(AuthorizationNature authorizationNatureInstance) {
		try{
	        if (authorizationNatureInstance == null) {
	            notFound()
	            return
	        }
			
	        authorizationNatureInstance.delete flush:true
	
	        request.withFormat {
	            form {
	                flash.message = message(code: 'default.deleted.message', args: [message(code: 'AuthorizationNature.label', default: 'AuthorizationNature'), authorizationNatureInstance.id])
	                redirect action:"index", method:"GET"
	            }
	            '*'{ render status: NO_CONTENT }
        }
		}catch (Exception e){
			flash.message = message('impossible.delete.message', args: [authorizationNatureInstance.name])
			redirect AuthorizationNature
			return
		}
		
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'authorizationNature.label', default: 'AuthorizationNature'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
