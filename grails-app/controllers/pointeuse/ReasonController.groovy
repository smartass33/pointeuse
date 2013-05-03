package pointeuse

import org.springframework.dao.DataIntegrityViolationException

class ReasonController {

	def springSecurityService
	
	
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [reasonInstanceList: Reason.list(params), reasonInstanceTotal: Reason.count()]
    }

    def create() {
        [reasonInstance: new Reason(params)]
    }

    def save() {
        def reasonInstance = new Reason(params)
		
		if (reasonInstance.name.size()==0){
			flash.message = message(code: 'reason.creation.error.label')
			render(view: "create", model: [reasonInstance: reasonInstance])
			return
		}
		def user = springSecurityService.currentUser
		reasonInstance.creatingUser=user
		reasonInstance.creatingDate=new Date()
		
        if (!reasonInstance.save(flush: true)) {
            render(view: "create", model: [reasonInstance: reasonInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'reason.label', default: 'Reason'), reasonInstance.id])
        redirect(action: "show", id: reasonInstance.id)
    }

    def show(Long id) {
        def reasonInstance = Reason.get(id)
        if (!reasonInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'reason.label', default: 'Reason'), id])
            redirect(action: "list")
            return
        }

        [reasonInstance: reasonInstance]
    }

    def edit(Long id) {
        def reasonInstance = Reason.get(id)
        if (!reasonInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'reason.label', default: 'Reason'), id])
            redirect(action: "list")
            return
        }

        [reasonInstance: reasonInstance]
    }

    def update(Long id, Long version) {
        def reasonInstance = Reason.get(id)
        if (!reasonInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'reason.label', default: 'Reason'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (reasonInstance.version > version) {
                reasonInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'reason.label', default: 'Reason')] as Object[],
                          "Another user has updated this Reason while you were editing")
                render(view: "edit", model: [reasonInstance: reasonInstance])
                return
            }
        }

        reasonInstance.properties = params

        if (!reasonInstance.save(flush: true)) {
            render(view: "edit", model: [reasonInstance: reasonInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'reason.label', default: 'Reason'), reasonInstance.id])
        redirect(action: "show", id: reasonInstance.id)
    }

    def delete(Long id) {
        def reasonInstance = Reason.get(id)
        if (!reasonInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'reason.label', default: 'Reason'), id])
            redirect(action: "list")
            return
        }

        try {
            reasonInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'reason.label', default: 'Reason'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'reason.label', default: 'Reason'), id])
            redirect(action: "show", id: id)
        }
    }
}
