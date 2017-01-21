package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AnomalyTypeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond AnomalyType.list(params), model:[anomalyTypeInstanceCount: AnomalyType.count()]
    }

    def show(AnomalyType anomalyTypeInstance) {
        respond anomalyTypeInstance
    }

    def create() {
        respond new AnomalyType(params)
    }

    @Transactional
    def save(AnomalyType anomalyTypeInstance) {
        if (anomalyTypeInstance == null) {
            notFound()
            return
        }

        if (anomalyTypeInstance.hasErrors()) {
            respond anomalyTypeInstance.errors, view:'create'
            return
        }

        anomalyTypeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'anomalyTypeInstance.label', default: 'AnomalyType'), anomalyTypeInstance.id])
                redirect anomalyTypeInstance
            }
            '*' { respond anomalyTypeInstance, [status: CREATED] }
        }
    }

    def edit(AnomalyType anomalyTypeInstance) {
        respond anomalyTypeInstance
    }

    @Transactional
    def update(AnomalyType anomalyTypeInstance) {
        if (anomalyTypeInstance == null) {
            notFound()
            return
        }

        if (anomalyTypeInstance.hasErrors()) {
            respond anomalyTypeInstance.errors, view:'edit'
            return
        }

        anomalyTypeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'AnomalyType.label', default: 'AnomalyType'), anomalyTypeInstance.id])
                redirect anomalyTypeInstance
            }
            '*'{ respond anomalyTypeInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(AnomalyType anomalyTypeInstance) {

        if (anomalyTypeInstance == null) {
            notFound()
            return
        }

        anomalyTypeInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'AnomalyType.label', default: 'AnomalyType'), anomalyTypeInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'anomalyTypeInstance.label', default: 'AnomalyType'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
