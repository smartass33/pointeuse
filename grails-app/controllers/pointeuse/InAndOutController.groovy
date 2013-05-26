package pointeuse

import java.text.DateFormat
import java.util.Calendar;
import java.util.Date
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.web.servlet.mvc.exceptions.CannotRedirectException
import org.springframework.dao.DataIntegrityViolationException
import groovy.time.TimeDuration;
import groovy.time.TimeCategory


class InAndOutController {

	def springSecurityService
	def timeManagerService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [inAndOutInstanceList: InAndOut.list(params,sort:'time'), inAndOutInstanceTotal: InAndOut.count()]
    }

    def create(Long id) {
		def employee
		def complete = params["complete"] ? true : false
		def reportRedirect = params["report"] ? true : false
		def inAndOutInstance = new InAndOut(params)
		
		if (id!=null){
			employee=Employee.get(id)
			inAndOutInstance.employee=employee
		}
        [inAndOutInstance: inAndOutInstance,complete:complete,reportRedirect:reportRedirect]
    }
	
    def save() {
		def user = springSecurityService.currentUser
		def type=params["event.type"]
		def reasonId=params["reason.id"]
		def employee
		def employeeId
		def timeDiff
		def userId = params["userId"]
		def hour = params["myTime_hour"] as int
		def minute = params["myTime_minute"] as int
		def second = params["myTime_second"] as int
		def instanceDate = params["inOrOutDate"]
		def fromReport = params["fromReport"].equals('true') ? true:false
		if (userId != null && userId != ""){
			employee=Employee.get(userId)
			employeeId=employee.id
		}else{
			flash.message = message(code: 'employee.not.null')
			if (fromReport){
				redirect(action: "list")
			}else{
				redirect(action: "pointage", controller:"employee")
			}
			return
		}

		
		def calendar = Calendar.instance
		def currentTime = calendar.time
		if (instanceDate != null){
			calendar.time=instanceDate
		}

		if (type==null || (type != null && type.equals(""))){
			flash.message = message(code: 'inAndOut.type.not.null')
			if (fromReport){
				redirect(action: "report", controller:"employee", id: employeeId, params:[userId:employeeId,myDate:instanceDate.format('dd/MM/yyyy')])
			}else{
				redirect(action: "pointage", controller:"employee", id: employeeId)
			}
			return
		}
		
		
		
		calendar.set(Calendar.HOUR_OF_DAY,hour)
		calendar.set(Calendar.MINUTE,minute)
		calendar.set(Calendar.SECOND,second)	
		def today = Calendar.instance
		if(calendar.time>today.time){
			flash.message = message(code: 'inAndOut.in.futur.error')
			if (fromReport){
				redirect(action: "report", controller:"employee", id: employeeId, params:[userId:employeeId,myDate:instanceDate.format('dd/MM/yyyy')])
			}else{
				redirect(action: "pointage", controller:"employee", id: employee.id)
			}
			return
		}
		
		
		def criteria = InAndOut.createCriteria()
		def lastIn = criteria.get {
			and {
				eq('employee',employee)
				eq('day',calendar.get(Calendar.DATE))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('year',calendar.get(Calendar.YEAR))
				lt('time',calendar.time)
				order('time','desc')
			}
			maxResults(1)
		}
			
		if (lastIn != null){
			use (TimeCategory){timeDiff=calendar.time-lastIn.time}
		}
		if (timeDiff !=null && (timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600) < 30 && (timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600)!=0){
			flash.message = message(code: 'employee.overlogging.error')
			if (fromReport){
				redirect(action: "report", controller:"employee", id: employeeId, params:[userId:employeeId,myDate:instanceDate.format('dd/MM/yyyy')])
			}else{
				redirect(action: "pointage", controller:"employee", id: employeeId)
			}
			return
		}
		
		
		def inAndOutInstance = timeManagerService.initializeTotals(employee, calendar.time,type,null)
		inAndOutInstance.regularization=true
		if (fromReport){
			inAndOutInstance.regularizationType=InAndOut.INITIALE_ADMIN
		}else{
			inAndOutInstance.regularizationType=InAndOut.INITIALE_SALARIE
		}
		if (reasonId != null && userId != ""){
			def reason=Reason.get(reasonId)
			if (reason!=null){
				inAndOutInstance.reason=reason
			}
		}
		if (user!=null){
			inAndOutInstance.modifyingUser=user
			log.error("user "+user?.username+" added "+inAndOutInstance)
		}
	

		
		timeManagerService.regularizeTime(type,userId,inAndOutInstance,calendar)
		
		if (inAndOutInstance.type.equals('E')){
			flash.message = message(code: 'inAndOut.create.label', args: [message(code:'inAndOut.entry.label'), calendar.time])
		}else{
			flash.message = message(code: 'inAndOut.create.label', args: [message(code:'inAndOut.exit.label'), calendar.time])	
		}
		try {
			if (fromReport){
				redirect(action: "report", controller:"employee", id: employeeId, params:[userId:employeeId,myDate:instanceDate.format('dd/MM/yyyy')])
				
			}else{
				redirect(action: "pointage", controller:"employee", id: employeeId)
			}
			return
		}catch(CannotRedirectException e){
			log.error(e.toString())
		}
		
    }

    def show(Long id) {
        def inAndOutInstance = InAndOut.get(id)
        if (!inAndOutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), id])
            redirect(action: "list")
            return
        }
        [inAndOutInstance: inAndOutInstance]
    }

    def edit(Long id) {
        def inAndOutInstance = InAndOut.get(id)
        if (!inAndOutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), id])
            redirect(action: "list")
            return
        }
		[inAndOutInstance: inAndOutInstance]
    }

    def update(Long id, Long version) {
        def inAndOutInstance = InAndOut.get(id)
        if (!inAndOutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (inAndOutInstance.version > version) {
                inAndOutInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'inAndOut.label', default: 'InAndOut')] as Object[],
                          "Another user has updated this InAndOut while you were editing")
                render(view: "edit", model: [inAndOutInstance: inAndOutInstance])
                return
            }
        }

        inAndOutInstance.properties = params

        if (!inAndOutInstance.save(flush: true)) {
            render(view: "edit", model: [inAndOutInstance: inAndOutInstance])
            return
        }
		def userId=inAndOutInstance.employee.id
		flash.message = message(code: 'default.updated.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), inAndOutInstance.id])
		redirect(action: "show",controller:"employee",id:userId)	
		return
    }

    def delete(Long id) {
        def inAndOutInstance = InAndOut.get(id)
        if (!inAndOutInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), id])
            redirect(action: "list")
            return
        }
        try {
            inAndOutInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'inAndOut.label', default: 'InAndOut'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def updateError(Long id){
		def inAndOutInstance = InAndOut.get(id)
		inAndOutInstance.systemGenerated=false
		render(view: "edit",model: [inAndOutInstance:inAndOutInstance])		
	}
	
	def getOddInAndOuts(){
		def criteria = InAndOut.createCriteria()
		def inAndOuts = criteria.list {
			and {
				eq('systemGenerated',true)
			}
		}
		
		for (InAndOut inAndOut:inAndOuts){
			criteria = InAndOut.createCriteria()
			def previousInAndOuts = criteria.list{
				and{
					eq('employee',inAndOut.employee)
					eq('day',inAndOut.day)
					eq('month',inAndOut.month)
					eq('year',inAndOut.year)
					lt('time',inAndOut.time)
				}
			}
			if (previousInAndOuts!=null && previousInAndOuts.size()==0){
				log.error('orphan inAnOut: '+inAndOut)
			}
		}
	}
	
	
}
