package pointeuse


import grails.plugin.springsecurity.annotation.Secured
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class EmployeeDataListRankController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond EmployeeDataListRank.list(params), model:[employeeDataListRankInstanceCount: EmployeeDataListRank.count()]
    }

    def show(EmployeeDataListRank employeeDataListRankInstance) {
        respond employeeDataListRankInstance
    }

    def create() {
        respond new EmployeeDataListRank(params)
    }

    @Transactional
    def save(EmployeeDataListRank employeeDataListRankInstance) {
        if (employeeDataListRankInstance == null) {
            notFound()
            return
        }

        if (employeeDataListRankInstance.hasErrors()) {
            respond employeeDataListRankInstance.errors, view:'create'
            return
        }

        employeeDataListRankInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'employeeDataListRankInstance.label', default: 'EmployeeDataListRank'), employeeDataListRankInstance.id])
                redirect employeeDataListRankInstance
            }
            '*' { respond employeeDataListRankInstance, [status: CREATED] }
        }
    }

    def edit(EmployeeDataListRank employeeDataListRankInstance) {
        respond employeeDataListRankInstance
    }

    @Transactional
    def update(EmployeeDataListRank employeeDataListRankInstance) {
        if (employeeDataListRankInstance == null) {
            notFound()
            return
        }

        if (employeeDataListRankInstance.hasErrors()) {
            respond employeeDataListRankInstance.errors, view:'edit'
            return
        }

        employeeDataListRankInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'EmployeeDataListRank.label', default: 'EmployeeDataListRank'), employeeDataListRankInstance.id])
                redirect employeeDataListRankInstance
            }
            '*'{ respond employeeDataListRankInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(EmployeeDataListRank employeeDataListRankInstance) {

        if (employeeDataListRankInstance == null) {
            notFound()
            return
        }

        employeeDataListRankInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'EmployeeDataListRank.label', default: 'EmployeeDataListRank'), employeeDataListRankInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeDataListRankInstance.label', default: 'EmployeeDataListRank'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
	@Transactional
	def initiateRank(){
		def dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		def employeeDataListMap= EmployeeDataListMap.find("from EmployeeDataListMap")
		def fieldMap = employeeDataListMap.fieldMap
		(employeeDataListMap.fieldMap).each{k,v->
			if (dataListRank.get(k) == null){
				def employeeDataListRank = new EmployeeDataListRank()
				employeeDataListRank.fieldName = v
				employeeDataListRank.rank = 1 
				employeeDataListRank.save(flush:true)			
			}
		}
		dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		int j = 1
		for (EmployeeDataListRank rankTmp in dataListRank){
			rankTmp.rank = j
			rankTmp.save(flush:true)
			j++
		}
	
	}
}
