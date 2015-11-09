package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class MonthlyTotalController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond MonthlyTotal.list(params), model:[monthlyTotalInstanceCount: MonthlyTotal.count()]
    }

    def show(MonthlyTotal monthlyTotalInstance) {
        respond monthlyTotalInstance
    }

    def create() {
        respond new MonthlyTotal(params)
    }

    @Transactional
    def save(MonthlyTotal monthlyTotalInstance) {
        if (monthlyTotalInstance == null) {
            notFound()
            return
        }

        if (monthlyTotalInstance.hasErrors()) {
            respond monthlyTotalInstance.errors, view:'create'
            return
        }

        monthlyTotalInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'monthlyTotalInstance.label', default: 'MonthlyTotal'), monthlyTotalInstance.id])
                redirect monthlyTotalInstance
            }
            '*' { respond monthlyTotalInstance, [status: CREATED] }
        }
    }

    def edit(MonthlyTotal monthlyTotalInstance) {
        respond monthlyTotalInstance
    }

    @Transactional
    def update(MonthlyTotal monthlyTotalInstance) {
        if (monthlyTotalInstance == null) {
            notFound()
            return
        }

        if (monthlyTotalInstance.hasErrors()) {
            respond monthlyTotalInstance.errors, view:'edit'
            return
        }

        monthlyTotalInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'MonthlyTotal.label', default: 'MonthlyTotal'), monthlyTotalInstance.id])
                redirect monthlyTotalInstance
            }
            '*'{ respond monthlyTotalInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(MonthlyTotal monthlyTotalInstance) {

        if (monthlyTotalInstance == null) {
            notFound()
            return
        }

        monthlyTotalInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'MonthlyTotal.label', default: 'MonthlyTotal'), monthlyTotalInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'monthlyTotalInstance.label', default: 'MonthlyTotal'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
