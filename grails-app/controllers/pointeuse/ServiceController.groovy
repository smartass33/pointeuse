package pointeuse

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

class ServiceController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		params.sort='name'
        [serviceInstanceList: Service.list(params), serviceInstanceTotal: Service.count()]
    }

    def create() {
        [serviceInstance: new Service(params)]
    }

    def save() {
        def serviceInstance = new Service(params)
        if (!serviceInstance.save(flush: true)) {
            render(view: "create", model: [serviceInstance: serviceInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'service.label', default: 'Service'), serviceInstance.id])
        redirect(action: "show", id: serviceInstance.id)
    }

    def show(Long id) {
        def serviceInstance = Service.get(id)
        if (!serviceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'service.label', default: 'Service'), id])
            redirect(action: "list")
            return
        }

        [serviceInstance: serviceInstance]
    }

    def edit(Long id) {
        def serviceInstance = Service.get(id)
        if (!serviceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'service.label', default: 'Service'), id])
            redirect(action: "list")
            return
        }

        [serviceInstance: serviceInstance]
    }

    def update(Long id, Long version) {
        def serviceInstance = Service.get(id)
        if (!serviceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'service.label', default: 'Service'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (serviceInstance.version > version) {
                serviceInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'service.label', default: 'Service')] as Object[],
                          "Another user has updated this Service while you were editing")
                render(view: "edit", model: [serviceInstance: serviceInstance])
                return
            }
        }

        serviceInstance.properties = params

        if (!serviceInstance.save(flush: true)) {
            render(view: "edit", model: [serviceInstance: serviceInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'service.label', default: 'Service'), serviceInstance.id])
        redirect(action: "show", id: serviceInstance.id)
    }

    def delete(Long id) {
        def serviceInstance = Service.get(id)
        if (!serviceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'service.label', default: 'Service'), id])
            redirect(action: "list")
            return
        }

        try {
            serviceInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'service.label', default: 'Service'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'service.label', default: 'Service'), id])
            redirect(action: "show", id: id)
        }
    }
}
