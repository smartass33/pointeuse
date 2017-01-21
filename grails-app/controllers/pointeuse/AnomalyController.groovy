package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AnomalyController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Anomaly.list(params), model:[anomalyInstanceCount: Anomaly.count()]
    }

    def show(Anomaly anomalyInstance) {
        respond anomalyInstance
    }

    def create() {
        respond new Anomaly(params)
    }

    @Transactional
    def save(Anomaly anomalyInstance) {
        if (anomalyInstance == null) {
            notFound()
            return
        }

        if (anomalyInstance.hasErrors()) {
            respond anomalyInstance.errors, view:'create'
            return
        }

        anomalyInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'anomalyInstance.label', default: 'Anomaly'), anomalyInstance.id])
                redirect anomalyInstance
            }
            '*' { respond anomalyInstance, [status: CREATED] }
        }
    }

    def edit(Anomaly anomalyInstance) {
        respond anomalyInstance
    }

    @Transactional
    def update(Anomaly anomalyInstance) {
        if (anomalyInstance == null) {
            notFound()
            return
        }

        if (anomalyInstance.hasErrors()) {
            respond anomalyInstance.errors, view:'edit'
            return
        }

        anomalyInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Anomaly.label', default: 'Anomaly'), anomalyInstance.id])
                redirect anomalyInstance
            }
            '*'{ respond anomalyInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Anomaly anomalyInstance) {

        if (anomalyInstance == null) {
            notFound()
            return
        }

        anomalyInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Anomaly.label', default: 'Anomaly'), anomalyInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'anomalyInstance.label', default: 'Anomaly'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
