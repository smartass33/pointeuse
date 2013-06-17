package pointeuse

import org.springframework.dao.DataIntegrityViolationException

class VacationController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def springSecurityService
	
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [vacationInstanceList: Vacation.list(params), vacationInstanceTotal: Vacation.count()]
    }

	def userList(Integer max) {
		def employee = Employee.get(params["userId"])
		def vacationInstanceList = Vacation.findAllByEmployee(employee)
		params.max = Math.min(max ?: 10, 100)
		[vacationInstanceList: Vacation.list(params), vacationInstanceTotal: Vacation.count(),userId:employee.id,employee:employee]
	}
	
    def create(){ 
		def employee = Employee.get(params["userId"])
		def vacation = new Vacation(params)
		vacation.employee = employee
        [vacationInstance: vacation,employee:employee]
    }

    def save() {
		def year = Year.get(params["year.id"])
		def employee = Employee.get(params["userId"])
		def user = springSecurityService.currentUser
        def vacationInstance = new Vacation(params)
		vacationInstance.period = year.year
		vacationInstance.employee = employee
		vacationInstance.user = user
		vacationInstance.loggingTime = new Date()
        if (!vacationInstance.save(flush: true)) {
            render(view: "create", model: [vacationInstance: vacationInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'vacation.label', default: 'Vacation'), vacationInstance.id])
        redirect(action: "show", id: vacationInstance.id)
    }

    def show(Long id) {
        def vacationInstance = Vacation.get(id)
        if (!vacationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacation.label', default: 'Vacation'), id])
            redirect(action: "list")
            return
        }

        [vacationInstance: vacationInstance]
    }

    def edit(Long id) {
        def vacationInstance = Vacation.get(id)
        if (!vacationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacation.label', default: 'Vacation'), id])
            redirect(action: "list")
            return
        }

        [vacationInstance: vacationInstance]
    }

    def update(Long id, Long version) {
        def vacationInstance = Vacation.get(id)
        if (!vacationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacation.label', default: 'Vacation'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (vacationInstance.version > version) {
                vacationInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'vacation.label', default: 'Vacation')] as Object[],
                          "Another user has updated this Vacation while you were editing")
                render(view: "edit", model: [vacationInstance: vacationInstance])
                return
            }
        }

        vacationInstance.properties = params

        if (!vacationInstance.save(flush: true)) {
            render(view: "edit", model: [vacationInstance: vacationInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'vacation.label', default: 'Vacation'), vacationInstance.id])
        redirect(action: "show", id: vacationInstance.id)
    }

    def delete(Long id) {
        def vacationInstance = Vacation.get(id)
        if (!vacationInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacation.label', default: 'Vacation'), id])
            redirect(action: "list")
            return
        }

        try {
            vacationInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'vacation.label', default: 'Vacation'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'vacation.label', default: 'Vacation'), id])
            redirect(action: "show", id: id)
        }
    }
}
