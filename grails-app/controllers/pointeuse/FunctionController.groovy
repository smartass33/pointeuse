package pointeuse

import org.springframework.dao.DataIntegrityViolationException

class FunctionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [functionInstanceList: Function.list(params), functionInstanceTotal: Function.count()]
    }

    def create() {
        [functionInstance: new Function(params)]
    }

    def save() {
        def functionInstance = new Function(params)
        if (!functionInstance.save(flush: true)) {
            render(view: "create", model: [functionInstance: functionInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'function.label', default: 'Function'), functionInstance.name])
        redirect(action: "show", id: functionInstance.id)
    }

    def show(Long id) {
        def functionInstance = Function.get(id)
        if (!functionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'function.label', default: 'Function'), id])
            redirect(action: "list")
            return
        }

        [functionInstance: functionInstance]
    }

    def edit(Long id) {
        def functionInstance = Function.get(id)
        if (!functionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'function.label', default: 'Function'), id])
            redirect(action: "list")
            return
        }

        [functionInstance: functionInstance]
    }

    def update(Long id, Long version) {
        def functionInstance = Function.get(id)
        if (!functionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'function.label', default: 'Function'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (functionInstance.version > version) {
                functionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'function.label', default: 'Function')] as Object[],
                          "Another user has updated this Function while you were editing")
                render(view: "edit", model: [functionInstance: functionInstance])
                return
            }
        }

        functionInstance.properties = params

        if (!functionInstance.save(flush: true)) {
            render(view: "edit", model: [functionInstance: functionInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'function.label', default: 'Function'), functionInstance.id])
        redirect(action: "show", id: functionInstance.id)
    }

    def delete(Long id) {
        def functionInstance = Function.get(id)
        if (!functionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'function.label', default: 'Function'), id])
            redirect(action: "list")
            return
        }

        try {
            functionInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'function.label', default: 'Function'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'function.label', default: 'Function'), id])
            redirect(action: "show", id: id)
        }
    }
}
