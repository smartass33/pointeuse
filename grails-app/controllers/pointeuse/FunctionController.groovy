package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class FunctionController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		params << [sort: "ranking", order: "asc"]
        respond Function.list(params), model:[functionInstanceCount: Function.count()]
    }

    def show(Function functionInstance) {
        respond functionInstance
    }

    def create() {
        respond new Function(params)
    }

    @Transactional
    def save(Function functionInstance) {
        if (functionInstance == null) {
            notFound()
            return
        }

        if (functionInstance.hasErrors()) {
            respond functionInstance.errors, view:'create'
            return
        }

        functionInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.feminine.created.message', args: [message(code: 'function.label', default: 'Function'), functionInstance.name])
                redirect functionInstance
            }
            '*' { respond functionInstance, [status: CREATED] }
        }
    }

    def edit(Function functionInstance) {
        respond functionInstance
    }

    @Transactional
    def update(Function functionInstance) {
        if (functionInstance == null) {
            notFound()
            return
        }

        if (functionInstance.hasErrors()) {
            respond functionInstance.errors, view:'edit'
            return
        }

        functionInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.feminine.updated.message', args: [message(code: 'function.label', default: 'Function'), functionInstance.name])
                redirect functionInstance
            }
            '*'{ respond functionInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Function functionInstance) {

        if (functionInstance == null) {
            notFound()
            return
        }

        functionInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.feminine.deleted.message', args: [message(code: 'function.label', default: 'Function'), functionInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'function.label', default: 'Function'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
