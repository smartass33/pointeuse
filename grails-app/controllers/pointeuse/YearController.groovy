package pointeuse

import org.springframework.dao.DataIntegrityViolationException

class YearController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [yearInstanceList: Year.list(params), yearInstanceTotal: Year.count()]
    }

    def create() {
        [yearInstance: new Year(params)]
    }

    def save() {
        def yearInstance = new Year(params)
        if (!yearInstance.save(flush: true)) {
            render(view: "create", model: [yearInstance: yearInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'year.label', default: 'Year'), yearInstance.id])
        redirect(action: "show", id: yearInstance.id)
    }

    def show(Long id) {
        def yearInstance = Year.get(id)
        if (!yearInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'year.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        [yearInstance: yearInstance]
    }

    def edit(Long id) {
        def yearInstance = Year.get(id)
        if (!yearInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'year.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        [yearInstance: yearInstance]
    }

    def update(Long id, Long version) {
        def yearInstance = Year.get(id)
        if (!yearInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'year.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (yearInstance.version > version) {
                yearInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'year.label', default: 'Year')] as Object[],
                          "Another user has updated this Year while you were editing")
                render(view: "edit", model: [yearInstance: yearInstance])
                return
            }
        }

        yearInstance.properties = params

        if (!yearInstance.save(flush: true)) {
            render(view: "edit", model: [yearInstance: yearInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'year.label', default: 'Year'), yearInstance.id])
        redirect(action: "show", id: yearInstance.id)
    }

    def delete(Long id) {
        def yearInstance = Year.get(id)
        if (!yearInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'year.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        try {
            yearInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'year.label', default: 'Year'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'year.label', default: 'Year'), id])
            redirect(action: "show", id: id)
        }
    }
}
