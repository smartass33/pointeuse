package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SupplementaryTimeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond SupplementaryTime.list(params), model:[supplementaryTimeInstanceCount: SupplementaryTime.count()]
    }

    def show(SupplementaryTime supplementaryTimeInstance) {
        respond supplementaryTimeInstance
    }

    def create() {
        respond new SupplementaryTime(params)
    }

    @Transactional
    def save(SupplementaryTime supplementaryTimeInstance) {
        if (supplementaryTimeInstance == null) {
            notFound()
            return
        }

        if (supplementaryTimeInstance.hasErrors()) {
            respond supplementaryTimeInstance.errors, view:'create'
            return
        }

        supplementaryTimeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'supplementaryTimeInstance.label', default: 'SupplementaryTime'), supplementaryTimeInstance.id])
                redirect supplementaryTimeInstance
            }
            '*' { respond supplementaryTimeInstance, [status: CREATED] }
        }
    }

    def edit(SupplementaryTime supplementaryTimeInstance) {
        respond supplementaryTimeInstance
    }

    @Transactional
    def update(SupplementaryTime supplementaryTimeInstance) {
        if (supplementaryTimeInstance == null) {
            notFound()
            return
        }

        if (supplementaryTimeInstance.hasErrors()) {
            respond supplementaryTimeInstance.errors, view:'edit'
            return
        }

        supplementaryTimeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'SupplementaryTime.label', default: 'SupplementaryTime'), supplementaryTimeInstance.id])
                redirect supplementaryTimeInstance
            }
            '*'{ respond supplementaryTimeInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(SupplementaryTime supplementaryTimeInstance) {

        if (supplementaryTimeInstance == null) {
            notFound()
            return
        }

        supplementaryTimeInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'SupplementaryTime.label', default: 'SupplementaryTime'), supplementaryTimeInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'supplementaryTimeInstance.label', default: 'SupplementaryTime'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
	def list(Integer max) {
		def supplementaryTimeList
		def supTimeMap = [:]
		Employee employee = Employee.get( params.long('userId'))
		params.max = Math.min(max ?: 10, 100)
		def criteria
		if (employee != null){
		//	supplementaryTimeList = SupplementaryTime.findAllByEmployeeAndValue(employee)
			criteria = SupplementaryTime.createCriteria()
			supplementaryTimeList = criteria.list{
				and {
					eq('employee',employee)
					//eq('period',period)
					gt('value',0 as double)
				}
			}
			
		}else{
			criteria = SupplementaryTime.createCriteria()
			supplementaryTimeList = criteria.list{
				and {
					//eq('employee',employee)
					//eq('period',period)
					gt('value',0 as double)
				}
			}
		}
		for (SupplementaryTime loopSupTime:supplementaryTimeList){
			
		}
		
		def supplementaryTimeTotal = supplementaryTimeList.size()
		[supplementaryTimeList: supplementaryTimeList, supplementaryTimeTotal:supplementaryTimeTotal,employee:employee]
	}
	
	def supplementaryTimeReport(){
		def userId
		def year
		def month
		def calendar = Calendar.instance

		Employee employee = Employee.get( params.long('userId'))
		
		if (params["myDate_year"] != null && !params["myDate_year"].equals('')){
			year = params.int('myDate_year')
		}else{
			year = calendar.get(Calendar.YEAR) - 1
		}
		if (params["myDate_month"] != null && !params["myDate_month"].equals('')){
			month = params.int('myDate_month')
		}else{
			month = calendar.get(Calendar.MONTH)+1
		}
		if (month < 6){
			year = year - 1
		}
		
		def  period = Period.get(params.int('periodId'))
		if (period != null){
			year = period.year
		} else{
			period = Period.findByYear(year)
		}
		
		
		if (userId==null){
			log.error('userId is null. exiting')
			return
		}
	}
	
	@Transactional
	def updateSupTime(){
		def criteria
		Employee employee = Employee.get( params.long('userId'))
		def newVal = params.double('val')
		def supTime = SupplementaryTime.get( params.long('supTimeInstance'))
		supTime.amountPaid = newVal
		supTime.save flush:true
		
		log.error('val: '+newVal)
		
		criteria = SupplementaryTime.createCriteria()
		def supplementaryTimeList = criteria.list{
			and {
				eq('employee',employee)
				//eq('period',period)
				gt('value',0 as double)
			}
		}
		def supplementaryTimeTotal = supplementaryTimeList.size()
		render template: "/supplementaryTime/template/employeeSupTimeTemplate", model:[employee:employee,supplementaryTimeList:supplementaryTimeList,supplementaryTimeTotal:supplementaryTimeTotal]
		return
		
	}
}
