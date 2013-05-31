package pointeuse

import org.springframework.dao.DataIntegrityViolationException

class AbsenceCounterController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [absenceCounterInstanceList: AbsenceCounter.list(params), absenceCounterInstanceTotal: AbsenceCounter.count()]
    }

    def create() {
        [absenceCounterInstance: new AbsenceCounter(params)]
    }

    def save() {
        def absenceCounterInstance = new AbsenceCounter(params)
        if (!absenceCounterInstance.save(flush: true)) {
            render(view: "create", model: [absenceCounterInstance: absenceCounterInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), absenceCounterInstance.id])
        redirect(action: "show", id: absenceCounterInstance.id)
    }

    def show(Long id) {
        def absenceCounterInstance = AbsenceCounter.get(id)
        if (!absenceCounterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), id])
            redirect(action: "list")
            return
        }

        [absenceCounterInstance: absenceCounterInstance]
    }

    def edit(Long id) {
        def absenceCounterInstance = AbsenceCounter.get(id)
        if (!absenceCounterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), id])
            redirect(action: "list")
            return
        }

        [absenceCounterInstance: absenceCounterInstance]
    }

    def update(Long id, Long version) {
        def absenceCounterInstance = AbsenceCounter.get(id)
        if (!absenceCounterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (absenceCounterInstance.version > version) {
                absenceCounterInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'absenceCounter.label', default: 'AbsenceCounter')] as Object[],
                          "Another user has updated this AbsenceCounter while you were editing")
                render(view: "edit", model: [absenceCounterInstance: absenceCounterInstance])
                return
            }
        }

        absenceCounterInstance.properties = params

        if (!absenceCounterInstance.save(flush: true)) {
            render(view: "edit", model: [absenceCounterInstance: absenceCounterInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), absenceCounterInstance.id])
        redirect(action: "show", id: absenceCounterInstance.id)
    }

    def delete(Long id) {
        def absenceCounterInstance = AbsenceCounter.get(id)
        if (!absenceCounterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), id])
            redirect(action: "list")
            return
        }

        try {
            absenceCounterInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'absenceCounter.label', default: 'AbsenceCounter'), id])
            redirect(action: "show", id: id)
        }
    }
}
