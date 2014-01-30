package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class EventLogController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond EventLog.list(params), model:[eventLogInstanceCount: EventLog.count()]
    }

    def show(EventLog eventLogInstance) {
        respond eventLogInstance
    }

    def create() {
        respond new EventLog(params)
    }

    @Transactional
    def save(EventLog eventLogInstance) {
        if (eventLogInstance == null) {
            notFound()
            return
        }

        if (eventLogInstance.hasErrors()) {
            respond eventLogInstance.errors, view:'create'
            return
        }

        eventLogInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'eventLogInstance.label', default: 'EventLog'), eventLogInstance.id])
                redirect eventLogInstance
            }
            '*' { respond eventLogInstance, [status: CREATED] }
        }
    }

    def edit(EventLog eventLogInstance) {
        respond eventLogInstance
    }

    @Transactional
    def update(EventLog eventLogInstance) {
        if (eventLogInstance == null) {
            notFound()
            return
        }

        if (eventLogInstance.hasErrors()) {
            respond eventLogInstance.errors, view:'edit'
            return
        }

        eventLogInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'EventLog.label', default: 'EventLog'), eventLogInstance.id])
                redirect eventLogInstance
            }
            '*'{ respond eventLogInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(EventLog eventLogInstance) {

        if (eventLogInstance == null) {
            notFound()
            return
        }

        eventLogInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'EventLog.label', default: 'EventLog'), eventLogInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'eventLogInstance.label', default: 'EventLog'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
