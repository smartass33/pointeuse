package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AbsenceTypeConfigController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	
	def test(){
		def criteria 
		def types = AbsenceTypeConfig.findAll()
		
		for (def type:types){
			log.error('type.name: '+type.shortName)
			log.error('AbsenceType.VACANCE: '+AbsenceType.VACANCE)
			def toCompare = type.name as AbsenceType 
			if (toCompare == AbsenceType.VACANCE){
				log.error("string are identical")
			}
			
			criteria = Absence.createCriteria()
			def yearlyHolidays = criteria.list {
				and {				
					eq('type',toCompare)
				}
			}
			log.error('yearlyHolidays: '+yearlyHolidays)
			
		}
		
	}
	
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond AbsenceTypeConfig.list(params), model:[absenceTypeConfigInstanceCount: AbsenceTypeConfig.count()]
    }

    def show(AbsenceTypeConfig absenceTypeConfigInstance) {
        respond absenceTypeConfigInstance
    }

    def create() {
        respond new AbsenceTypeConfig(params)
    }

    @Transactional
    def save(AbsenceTypeConfig absenceTypeConfigInstance) {
        if (absenceTypeConfigInstance == null) {
            notFound()
            return
        }

        if (absenceTypeConfigInstance.hasErrors()) {
            respond absenceTypeConfigInstance.errors, view:'create'
            return
        }

        absenceTypeConfigInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'absenceTypeConfigInstance.label', default: 'AbsenceTypeConfig'), absenceTypeConfigInstance.id])
                redirect absenceTypeConfigInstance
            }
            '*' { respond absenceTypeConfigInstance, [status: CREATED] }
        }
    }

    def edit(AbsenceTypeConfig absenceTypeConfigInstance) {
        respond absenceTypeConfigInstance
    }

    @Transactional
    def update(AbsenceTypeConfig absenceTypeConfigInstance) {
        if (absenceTypeConfigInstance == null) {
            notFound()
            return
        }

        if (absenceTypeConfigInstance.hasErrors()) {
            respond absenceTypeConfigInstance.errors, view:'edit'
            return
        }

        absenceTypeConfigInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'AbsenceTypeConfig.label', default: 'AbsenceTypeConfig'), absenceTypeConfigInstance.id])
                redirect absenceTypeConfigInstance
            }
            '*'{ respond absenceTypeConfigInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(AbsenceTypeConfig absenceTypeConfigInstance) {

        if (absenceTypeConfigInstance == null) {
            notFound()
            return
        }

        absenceTypeConfigInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'AbsenceTypeConfig.label', default: 'AbsenceTypeConfig'), absenceTypeConfigInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'absenceTypeConfigInstance.label', default: 'AbsenceTypeConfig'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
