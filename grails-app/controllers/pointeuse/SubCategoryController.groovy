package pointeuse


import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class SubCategoryController {
	def springSecurityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond SubCategory.list(params), model:[subCategoryInstanceCount: SubCategory.count()]
    }

    def show(SubCategory subCategoryInstance) {
        respond subCategoryInstance
    }

    def create() {
        respond new SubCategory(params)
    }

    @Transactional
    def save(SubCategory subCategoryInstance) {
        if (subCategoryInstance == null) {
            notFound()
            return
        }
		subCategoryInstance.creationDate = new Date()
		subCategoryInstance.user = springSecurityService.currentUser
		subCategoryInstance.validate()
        if (subCategoryInstance.hasErrors()) {
            respond subCategoryInstance.errors, view:'create'
            return
        }

        subCategoryInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'subCategory.label', default: 'SubCategory'), subCategoryInstance.name])
                redirect subCategoryInstance
            }
            '*' { respond subCategoryInstance, [status: CREATED] }
        }
    }

    def edit(SubCategory subCategoryInstance) {
        respond subCategoryInstance
    }

    @Transactional
    def update(SubCategory subCategoryInstance) {
        if (subCategoryInstance == null) {
            notFound()
            return
        }

        if (subCategoryInstance.hasErrors()) {
            respond subCategoryInstance.errors, view:'edit'
            return
        }

        subCategoryInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'subCategory.label', default: 'SubCategory'), subCategoryInstance.name])
                redirect subCategoryInstance
            }
            '*'{ respond subCategoryInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(SubCategory subCategoryInstance) {

        if (subCategoryInstance == null) {
            notFound()
            return
        }

        subCategoryInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'subCategory.label', default: 'SubCategory'), subCategoryInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'subCategory.label', default: 'SubCategory'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
