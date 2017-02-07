package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured
import java.text.SimpleDateFormat
import org.hibernate.QueryException

@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class MileageController {

	def mileageService
	def springSecurityService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	def addMileage(){
		log.error('calling addMileage')
		params.each{i->log.error('parameter of list: '+i)}
		//def period = Period.get(params['periodId'])
		def employee = Employee.get(params['employeeId'])
		def value = params.long('mileageValue')
		def date_mileage_picker =params["date_mileage_picker"]
		def calendar = Calendar.instance
		if (date_mileage_picker != null && date_mileage_picker.size()>0){
			calendar.time = new Date().parse("d/M/yyyy HH:mm", date_mileage_picker)
		}
		Period period = (calendar.get(Calendar.MONTH) < 5) ? Period.findByYear(calendar.get(Calendar.YEAR) - 1) : Period.findByYear(calendar.get(Calendar.YEAR))
		def month = calendar.get(Calendar.MONTH) + 1//(params["month"].split(" ").getAt(0)) as int
		def year = calendar.get(Calendar.YEAR)//(month >= 6 ? period.year : period.year + 1)
		def criteria = Mileage.createCriteria()
		def day = calendar.get(Calendar.DAY_OF_MONTH)
		def mileage = criteria.get {
			and {
				eq('employee',employee)
				eq('period',period)
				eq('month',month)
				eq('day',day)
			}
		}
		try{
			if (mileage == null){
				mileage = new Mileage( employee, period, day, month, year,  value,springSecurityService.currentUser)
				 mileage.save flush:true
			}else{
				mileage.value = value
				mileage.save flush:true
			}
		}catch (QueryException qe){
			log.error('Duplicate entry: '+qe.message)
			return
		}finally{
			//sleep(2000)
			//def model = mileageService.getReportData( period, employee.site)
			//render template:"/mileage/template/mileageTemplate",model:model
			//redirect(action: "employeeMileage", periodId: period.id,employeeId:employee.id,siteId:employee.site.id,site:employee.site)
			return
		}
		
	}
	
	def modifyMileage(){
		log.error('calling modifyMileage')
		params.each{i->log.error('parameter of list: '+i)}
		def period = Period.get(params['periodId'])
		def employee = Employee.get(params['employeeId'])
		def value = params.long('value')
		def date_mileage_picker =params["date_mileage_picker"]
		def calendar = Calendar.instance
		if (date_mileage_picker != null && date_mileage_picker.size()>0){
			calendar.time = new Date().parse("d/M/yyyy HH:mm", date_mileage_picker)
		}
		
		
		def month = calendar.get(Calendar.MONTH) + 1//(params["month"].split(" ").getAt(0)) as int
		def year = calendar.get(Calendar.YEAR)//(month >= 6 ? period.year : period.year + 1)	
		def criteria = Mileage.createCriteria()
		def day = calendar.get(Calendar.DAY_OF_MONTH)
		def mileage = criteria.get {
			and {
				eq('employee',employee)
				eq('period',period)
				eq('month',month)
				eq('day',day)
			}
		}
		try{
			if (mileage == null){
				mileage = new Mileage( employee, period, day, month, year,  value,springSecurityService.currentUser)
				 mileage.save flush:true
			}else{
				mileage.value = value
				mileage.save flush:true
			}
		}catch (QueryException qe){
			log.error('Duplicate entry: '+qe.message)
			return
		}finally{
			sleep(2000)
			def model = mileageService.getReportData( period, employee.site)
			//render template:"/mileage/template/mileageTemplate",model:model
			redirect(action: "employeeMileage", periodId: period.id,employeeId:employee.id,siteId:employee.site.id)
			return
		}
	}
	
	
	def list(Integer max) {
		params.each{i->log.error('parameter of list: '+i)}
		def period = Period.get(params['periodId'])
		def fromIndex=params.boolean('fromIndex')
		def fromAnnualReport = (params['fromAnnualReport'] != null) ? params.boolean('fromAnnualReport') :false
		def site = (params['siteId'] != null && !params['siteId'].equals('') ? Site.get(params['siteId']) : null  )
		SimpleDateFormat dateFormat
		def calendar = Calendar.instance
		def criteria
		def monthList=[6,7,8,9,10,11,12,1,2,3,4,5]
		def employeeMileageMap = [:]
		def mileageIDMap = [:]
		def mileageMapByEmployee = [:]
		def milageIDMapByEmployee = [:]
		def month =  calendar.get(Calendar.MONTH) + 1
		def year = (month < 6) ? calendar.get(Calendar.YEAR) - 1 : calendar.get(Calendar.YEAR)
		def myDate = params["myDate"]
		if (myDate != null && myDate instanceof String){
			dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)
		}
		
		if (site == null && !fromIndex){flash.message = message(code: 'site.selection.error')}
		else{flash.message = null}
			
		def model = mileageService.getReportData( period, site)
		render template:"/mileage/template/mileageTemplate",model:model
		
		//respond  model:model
		
		return
	}
	
	def employeeMileage(Integer max) {
		params.each{i->log.error('parameter of list: '+i)}
		def period = Period.get(params['periodId'])
		def employee = Employee.get(params['employeeId'])
		def fromIndex=params.boolean('fromIndex')
		
		def fromAnnualReport = (params['fromAnnualReport'] != null) ? params.boolean('fromAnnualReport') :false		
		def site = (params['siteId'] != null && !params['siteId'].equals('') ? Site.get(params['siteId']) : employee.site  )
		SimpleDateFormat dateFormat
		def calendar = Calendar.instance
		def criteria
		def monthList=[6,7,8,9,10,11,12,1,2,3,4,5]
		def employeeMileageMap = [:]
		def mileageIDMap = [:]
		def mileageMapByEmployee = [:]
		def milageIDMapByEmployee = [:]
		def month =  calendar.get(Calendar.MONTH) + 1
		def year = (month < 6) ? calendar.get(Calendar.YEAR) - 1 : calendar.get(Calendar.YEAR)
		def myDate = params["myDate"]
		if (myDate != null && myDate instanceof String){
			dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)
		}
		
		if (site == null && !fromIndex){flash.message = message(code: 'site.selection.error')}
		else{flash.message = null}
			
		def model = mileageService.getReportData( period, site)
		model << [fromAnnualReport:fromAnnualReport,myDate:myDate]
		if (fromAnnualReport){
			employee = Employee.get(params["employeeId"])
			model << [employee:employee]	
		}
		if (fromIndex != null && fromIndex){
			model << [fromIndex:false]
			return model
		}
		render template:"/mileage/template/mileageTemplate",model:model
		return
	}
	
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Mileage.list(params), model:[milageInstanceCount: Mileage.count()]
    }

    def show(Mileage milageInstance) {
        respond milageInstance
    }

    def create() {
        respond new Mileage(params)
    }

    @Transactional
    def save(Mileage milageInstance) {
        if (milageInstance == null) {
            notFound()
            return
        }

        if (milageInstance.hasErrors()) {
            respond milageInstance.errors, view:'create'
            return
        }

        milageInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'milageInstance.label', default: 'Mileage'), milageInstance.id])
                redirect milageInstance
            }
            '*' { respond milageInstance, [status: CREATED] }
        }
    }

    def edit(Mileage milageInstance) {
        respond milageInstance
    }

    @Transactional
    def update(Mileage milageInstance) {
        if (milageInstance == null) {
            notFound()
            return
        }

        if (milageInstance.hasErrors()) {
            respond milageInstance.errors, view:'edit'
            return
        }

        milageInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Mileage.label', default: 'Mileage'), milageInstance.id])
                redirect milageInstance
            }
            '*'{ respond milageInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Mileage milageInstance) {

        if (milageInstance == null) {
            notFound()
            return
        }

        milageInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Mileage.label', default: 'Mileage'), milageInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'milageInstance.label', default: 'Mileage'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
