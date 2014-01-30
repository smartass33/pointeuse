package pointeuse

import org.springframework.dao.DataIntegrityViolationException

class SupplementaryTimeController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [supplementaryTimeInstanceList: SupplementaryTime.list(params), supplementaryTimeInstanceTotal: SupplementaryTime.count()]
    }

    def create() {
        [supplementaryTimeInstance: new SupplementaryTime(params)]
    }

    def save() {
        def supplementaryTimeInstance = new SupplementaryTime(params)
        if (!supplementaryTimeInstance.save(flush: true)) {
            render(view: "create", model: [supplementaryTimeInstance: supplementaryTimeInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), supplementaryTimeInstance.id])
        redirect(action: "show", id: supplementaryTimeInstance.id)
    }

    def show(Long id) {
        def supplementaryTimeInstance = SupplementaryTime.get(id)
        if (!supplementaryTimeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), id])
            redirect(action: "list")
            return
        }

        [supplementaryTimeInstance: supplementaryTimeInstance]
    }

    def edit(Long id) {
        def supplementaryTimeInstance = SupplementaryTime.get(id)
        if (!supplementaryTimeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), id])
            redirect(action: "list")
            return
        }

        [supplementaryTimeInstance: supplementaryTimeInstance]
    }

    def update(Long id, Long version) {
        def supplementaryTimeInstance = SupplementaryTime.get(id)
        if (!supplementaryTimeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (supplementaryTimeInstance.version > version) {
                supplementaryTimeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'supplementaryTime.label', default: 'SupplementaryTime')] as Object[],
                          "Another user has updated this SupplementaryTime while you were editing")
                render(view: "edit", model: [supplementaryTimeInstance: supplementaryTimeInstance])
                return
            }
        }

        supplementaryTimeInstance.properties = params

        if (!supplementaryTimeInstance.save(flush: true)) {
            render(view: "edit", model: [supplementaryTimeInstance: supplementaryTimeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), supplementaryTimeInstance.id])
        redirect(action: "show", id: supplementaryTimeInstance.id)
    }

    def delete(Long id) {
        def supplementaryTimeInstance = SupplementaryTime.get(id)
        if (!supplementaryTimeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), id])
            redirect(action: "list")
            return
        }

        try {
            supplementaryTimeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'supplementaryTime.label', default: 'SupplementaryTime'), id])
            redirect(action: "show", id: id)
        }
    }
}
