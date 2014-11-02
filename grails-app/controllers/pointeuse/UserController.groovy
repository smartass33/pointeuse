package pointeuse

import grails.plugins.springsecurity.Secured
import org.springframework.dao.DataIntegrityViolationException
import java.security.MessageDigest
class UserController {
	def authenticateService
	def springSecurityService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [userInstanceList: User.list(params), userInstanceTotal: User.count()]
    }

    def create() {
        [userInstance: new User(params)]
    }
	@Secured(['ROLE_SUPER_ADMIN'])
    def save() {
		def role = params["role"].get('id')
        def userInstance = new User(params)	
        if (!userInstance.save(flush: true)) {

            render(view: "create", model: [userInstance: userInstance])
            return
        }
		if (role != null){
			def roleObject = Role.get(role)
			UserRole.create(userInstance,roleObject)
		}

        flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "show", id: userInstance.id)
    }

    def show(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        [userInstance: userInstance]
    }
	@Secured(['ROLE_SUPER_ADMIN'])
    def edit(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        [userInstance: userInstance]
    }

	@Secured(['ROLE_SUPER_ADMIN'])
    def update(Long id, Long version) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (userInstance.version > version) {
                userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'user.label', default: 'User')] as Object[],
                          "Another user has updated this User while you were editing")
                render(view: "edit", model: [userInstance: userInstance])
                return
            }
        }

        userInstance.properties = params

        if (!userInstance.save(flush: true)) {
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "show", id: userInstance.id)
    }

	@Secured(['ROLE_SUPER_ADMIN'])
    def delete(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        try {
            userInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def isAuthenticated(){
		def username = params["username"]
		def password = params["password"]
		byte[] hash
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		def hashAsString
		def user
		def retour = false
		response.status = 404;
		
				
		if (username == null){
			log.error('authentication failed!')
		} else{
			user = User.findByUsername(username)
		}
		
		if (password == null){
			log.error('authentication failed!')			
		}
		else{
			hash = digest.digest(password.getBytes("UTF-8"));
			hashAsString = hash.encodeHex()
		}
		
		if (user != null){
			if (!user.password.equals(hashAsString.toString())){
				log.error('authentication failed!')
			}else{
				retour = true
				response.status = 200;
			}
		}
		log.error("retour is= "+retour)
		render retour
		return retour
	}
}
