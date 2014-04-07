package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ContractController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Contract.list(params), model:[contractInstanceCount: Contract.count()]
    }

    def show(Contract contractInstance) {
        respond contractInstance
    }

    def create() {
        respond new Contract(params)
    }

    @Transactional
    def save(Contract contractInstance) {
        if (contractInstance == null) {
            notFound()
            return
        }

        if (contractInstance.hasErrors()) {
            respond contractInstance.errors, view:'create'
            return
        }

        contractInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'contractInstance.label', default: 'Contract'), contractInstance.id])
                redirect contractInstance
            }
            '*' { respond contractInstance, [status: CREATED] }
        }
    }

    def edit(Contract contractInstance) {
        respond contractInstance
    }

    @Transactional
    def update(Contract contractInstance) {
        if (contractInstance == null) {
            notFound()
            return
        }

        if (contractInstance.hasErrors()) {
            respond contractInstance.errors, view:'edit'
            return
        }

        contractInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Contract.label', default: 'Contract'), contractInstance.id])
                redirect contractInstance
            }
            '*'{ respond contractInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Contract contractInstance) {

        if (contractInstance == null) {
            notFound()
            return
        }

        contractInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Contract.label', default: 'Contract'), contractInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'contractInstance.label', default: 'Contract'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	@Transactional
	def createBulkContract(){
		def employeeList = Employee.findAll()
		def contractList
		
		for (Employee employee:employeeList){
			contractList = Contract.findAllByEmployee(employee)
			// in that case, need to create contract
			if ((contractList == null) || (contractList != null && contractList.size() == 0) ){
				Contract contract = new Contract()
				contract.startDate = employee.arrivalDate
				contract.year=contract.startDate.getAt(Calendar.YEAR)
				contract.month=(contract.startDate.getAt(Calendar.MONTH))+1
				//contract.period=contract.month<6?Period.findByYear(contract.year-1):Period.findByYear(contract.year)
				contract.weeklyLength = employee.weeklyContractTime
				contract.employee = employee
				contract.loggingTime = new Date()
				contract.save()		
			}
		}
	}
}
