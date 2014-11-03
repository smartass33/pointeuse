package pointeuse

import grails.plugins.springsecurity.Secured

import org.springframework.dao.DataIntegrityViolationException

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date;

import groovy.time.TimeCategory
import grails.converters.JSON

import org.apache.commons.logging.LogFactory


class EmployeeController {	
	def PDFService
	def utilService
	def mailService
	def searchableService
	def authenticateService
	def springSecurityService
	def timeManagerService 
	def supplementaryTimeService
	def employeeService
	def dataSource
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	long secondInMillis = 1000;
	long minuteInMillis = secondInMillis * 60;
	long hourInMillis = minuteInMillis * 60;
	long dayInMillis = hourInMillis * 24;
	long yearInMillis = dayInMillis * 365;
	private static final log = LogFactory.getLog(this)
	
	

    def index() {
        redirect(action: "list", params: params)
    }

	def modifyAbsenceCounter(){
		def year=params["year"]
		def userId=params["userId"]
		Employee employee = Employee.get(userId)
		AbsenceCounter absenceCount = AbsenceCounter.findByEmployeeAndYear(employee,year)
		[absenceCount:absenceCount]
	}
	

	@Secured(['ROLE_ADMIN'])
	def dailyReport(){
		def dailyMap = [:]
		def dailySupMap = [:]
		def dailyInAndOutMap = [:]
		def siteId=params["site.id"]
		//def currentDate = params["currentDate"]
		def dailyTotal
		def inAndOutList
		def criteria
		def site
		def elapsedSeconds = 0
		def employeeInstanceList
		def currentDate
		def calendar = Calendar.instance
		
		def fromIndex=params.boolean('fromIndex')//.equals('true')?true:false
		
		if (!fromIndex && (siteId == null || siteId.size() == 0)){
			flash.message = message(code: 'ecart.site.selection.error')
			params["fromIndex"]=true
			redirect(action: "dailyReport",params:params)
			return
		}
		
		
		def date_picker =params["date_picker"]
		if (date_picker != null && date_picker.size()>0){
			currentDate =  new Date().parse("dd/MM/yyyy", date_picker)
			calendar.time=currentDate
		}
			
		if (siteId!=null && !siteId.equals("")){
			 site = Site.get(params.int('site.id'))
			 employeeInstanceList = Employee.findAllBySite(site)
		}else{
			employeeInstanceList = Employee.findAll("from Employee")	
		}
		for (Employee employee:employeeInstanceList){		
			criteria = DailyTotal.createCriteria()
			dailyTotal= criteria.get{
				and {
					eq('employee',employee)
					eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
					eq('year',calendar.get(Calendar.YEAR))
					eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				}				
			}
			criteria = InAndOut.createCriteria()			
			inAndOutList= criteria.list{
				and {
						eq('employee',employee)
						eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
						eq('day',calendar.get(Calendar.DAY_OF_MONTH))
						eq('month',calendar.get(Calendar.MONTH)+1)			
						eq('year',calendar.get(Calendar.YEAR))
						order('time')				
					}
			}
			
			if (fromIndex){
				return [site:site,fromIndex:fromIndex,currentDate:calendar.time]
			}
			
			dailyInAndOutMap.put(employee, inAndOutList)
			if (dailyTotal != null){
				elapsedSeconds = (timeManagerService.getDailyTotal(dailyTotal)).get('elapsedSeconds')
			}
			if (elapsedSeconds > DailyTotal.maxWorkingTime){
				dailySupMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds-DailyTotal.maxWorkingTime))	
			}else{
				dailySupMap.put(employee,timeManagerService.computeHumanTime(0))
			}
			dailyMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds))
		}		
		if (site!=null){
			render template: "/employee/template/listDailyTimeTemplate", model:[dailyMap: dailyMap,site:site,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap]
			return	
		}
		[dailyMap: dailyMap,site:site,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap,currentDate:calendar.time]
	}
	
	
	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
		params.each{i->log.debug('parameter of list: '+i)}
		params.sort='site'
		params.max = Math.min(max ?: 20, 100)
		log.debug('browser version: '+request.getHeader('User-Agent') )
		def employeeInstanceList
		def employeeInstanceTotal
		def site
		def siteId=params["site"]
		boolean back = (params["back"] != null && params["back"].equals("true")) ? true : false		
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false	
		
		def user = springSecurityService.currentUser
		def username = user?.getUsername()
					
		if (params["site"]!=null && !params["site"].equals('') && !params["site"].equals('[id:]')){
			site = Site.get(params.int('site'))
			if (site != null)
				siteId=site.id			
		}	
		if (params["siteId"]!=null && !params["siteId"].equals("")){
			site = Site.get(params.int('siteId'))
			if (site != null)
				siteId=site.id
		}		
		if (back){
			if (site!=null){
				employeeInstanceList=Employee.findAllBySite(site)
				employeeInstanceTotal = employeeInstanceList.size()
				employeeInstanceList=Employee.findAllBySite(site,[max:  20,offset: 0,sort:'site'])
				
			}else {
				employeeInstanceList=Employee.findAll("from Employee")	
				employeeInstanceTotal = employeeInstanceList.size()		
				employeeInstanceList=Employee.findAll("from Employee order by site",[max:  20,offset: 0])
			}
		}else{
			if (site!=null){
				employeeInstanceList = Employee.findAllBySite(site)
				employeeInstanceTotal = employeeInstanceList.size()
				render template: "/employee/template/listEmployeeTemplate", model:[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceList.size(),username:username,isAdmin:isAdmin,siteId:siteId,site:site]
				return
			}else{
				employeeInstanceList=Employee.list(params)
				employeeInstanceTotal = employeeInstanceList.totalCount
			}
		}
		[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceTotal,username:username,isAdmin:isAdmin,siteId:siteId,site:site]
    }
	
    def create() {
		def service = params["employee.service.id"]
		def employeeInstance =new Employee(params)
		employeeInstance.service=Service.get(service)
        [employeeInstance: employeeInstance]
    }

    def save() {
		params.each{i-> log.debug('param: '+i)}
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
		def service = params["employee.service.id"]
		def site = params["employee.site.id"]
		def function = params["employee.function.id"]	
		Status status
		def employeeInstance =new Employee(params)
		employeeInstance.service=Service.get(service)
		employeeInstance.site=Site.get(site)
		employeeInstance.function=Function.get(function)

		if ( params["updatedSelection"] == null || (params["updatedSelection"] != null && params["updatedSelection"].size() == 0)){
			status = new Status(null, employeeInstance,StatusType.ACTIF)
			employeeInstance.status = status		
		}
			
        if (!employeeInstance.save(flush: true)) {
            render(view: "create", model: [employeeInstance: employeeInstance])
            return
        }

		utilService.initiateVacations(employeeInstance)
		Contract contract = new Contract(employeeInstance.arrivalDate,employeeInstance)
		contract.save(flush: true)
		if (status != null){
			status.save(flush: true)
		}

        flash.message = message(code: 'default.created.message', args: [message(code: 'employee.label', default: 'Employee'), employeeInstance.lastName])
        redirect(action: "show", id: employeeInstance.id,params: [isAdmin: isAdmin])
    }

	def search = {
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].split(" ").getAt(0).equals("true")) ? true : false
		def query = "*"+params.q+"*"
		if(query){
			def srchResults = searchableService.search(query)
			def employeeList = []
			for (Employee employee:srchResults.results){
				def tmpEmployee = Employee.get(employee.id)
				employeeList.add(tmpEmployee)
			}
			render template: "/employee/template/listEmployeeTemplate", model:[employeeInstanceList: employeeList, employeeInstanceTotal: employeeList.size(),isAdmin:isAdmin]
			return
		}else{
			redirect(action: "list")
		}
	}

	
	
	
    def show(Long id) {		
		def siteId=params["siteId"]
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
        def employeeInstance = Employee.get(id)
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }
        [employeeInstance: employeeInstance,isAdmin:isAdmin,siteId:siteId]
    }

def vacationFollowup(){
		params.each {
			i-> log.error('parameter= '+i)
		}
		def year = params["year"]
		def max = params["max"] != null ? params.int("max") : 20
		def offset = params["offset"] != null ? params.int("offset") : 0
		
		def site
		def siteId
		def employeeInstanceList
		def period
		def criteria
		def initialCAMap=[:]
		def remainingCAMap=[:]
		def takenCAMap=[:]
		def initialRTTMap=[:]
		def remainingRTTMap=[:]
		def takenRTTMap=[:]
		def takenSicknessMap=[:]
		def takenCSSMap=[:]
		def takenAutreMap=[:]
		def takenExceptionnelMap=[:]		
		def takenDifMap=[:]
		def takenSickness
		def takenRTT
		def takenCA
		def takenCSS
		def takenAutre
		def takenExceptionnel
		def takenDIF
		
		def employeeInstanceTotal

		
		if (params["siteId"]!=null && !params["siteId"].equals("")){
			site = Site.get(params["siteId"])
			siteId=site.id	
		}else{
			if (params["site.id"]!=null && !params["site.id"].equals("")){
				def tmpSite = params["site.id"]
				if (tmpSite instanceof String[]){
					if (tmpSite[0]!=""){
						tmpSite=tmpSite[0]!=""?tmpSite[0].toInteger():tmpSite[1].toInteger()
					}
				}else {
					tmpSite=tmpSite.toInteger()
				}
				site = Site.get(tmpSite)						
				siteId=site.id			
			}
		}
		
		if (year!=null && !year.equals("")){
			
			if (year instanceof String[]){
				year= (year[0] != "") ? year[0].toInteger():year[1].toInteger()					
			}else {
				year=year.toInteger()	
			}
		}
		
		def startCalendar = Calendar.instance
		def currentMonth = startCalendar.get(Calendar.MONTH)
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.clearTime()
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,4)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		if (year != null){
			period = Period.get(year)
		}else{
			period = (currentMonth < 5) ? Period.findByYear(startCalendar.get(Calendar.YEAR) - 1) : Period.findByYear(startCalendar.get(Calendar.YEAR))
		}
	
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site)
			employeeInstanceTotal = employeeInstanceList.size()		
			employeeInstanceList = Employee.findAllBySite(site,[max:max,offset:offset])
			
		}else{
			employeeInstanceList = Employee.findAll("from Employee")
			employeeInstanceTotal = employeeInstanceList.size()
			employeeInstanceList = Employee.findAll("from Employee",[max:max,offset:offset])	
		}
		
		
		// for each employee, retrieve absences
		for (Employee employee: employeeInstanceList){
			// step 1: fill initial values
			//CA
			criteria = Vacation.createCriteria()
			//log.error('employeeID: '+employee.id)
			def initialCA = criteria.get{
				and {
					eq('employee',employee)
					eq('period',period)
					eq('type',VacationType.CA)
				}
			}
			if (initialCA != null){
				initialCAMap.put(employee, initialCA.counter)
			}else{
				initialCAMap.put(employee, 0)		
			}
			//RTT
			criteria = Vacation.createCriteria()
			def initialRTT = criteria.get{
				and {
					eq('employee',employee)
					eq('period',period)
					eq('type',VacationType.RTT)
				}
			}
			if (initialRTT != null){
				initialRTTMap.put(employee, initialRTT.counter)
			}else{
				initialRTTMap.put(employee, 0)
			}
			
			// step 2: fill actual counters
			startCalendar.set(Calendar.YEAR,period.year)
			endCalendar.set(Calendar.YEAR,period.year+1)
			//CA
			criteria = Absence.createCriteria()
			takenCA = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.VACANCE)
				}
			}
			if (takenCA!=null){
				remainingCAMap.put(employee, initialCAMap.get(employee)-takenCA.size())
				takenCAMap.put(employee, takenCA.size())
			}else{
				remainingCAMap.put(employee, initialCAMap.get(employee))
				takenCAMap.put(employee, 0)
				
			}
			//RTT
			criteria = Absence.createCriteria()
			takenRTT = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.RTT)
				}
			}
			if (takenRTT!=null){
				remainingRTTMap.put(employee, initialRTTMap.get(employee)-takenRTT.size())
				takenRTTMap.put(employee, takenRTT.size())
				
			}else{
				remainingRTTMap.put(employee, initialRTTMap.get(employee))
				takenRTTMap.put(employee, 0)
			}
			
			// SICKNESS
			criteria = Absence.createCriteria()
			takenSickness = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.MALADIE)
				}
			}			
			if (takenSickness!=null){
				takenSicknessMap.put(employee, takenSickness.size())
			}else{
				takenSicknessMap.put(employee, 0)
			}	
							
			//CSS
			criteria = Absence.createCriteria()
			takenCSS = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.CSS)
				}
			}			
			if (takenCSS!=null){
				takenCSSMap.put(employee, takenCSS.size())
			}else{
				takenCSSMap.put(employee, 0)
			}

			//AUTRE
			criteria = Absence.createCriteria()
			takenAutre = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.AUTRE)
				}
			}
			if (takenAutre!=null){
				takenAutreMap.put(employee, takenAutre.size())
			}else{
				takenAutreMap.put(employee, 0)
			}
			
			//EXCEPTIONNEL
			criteria = Absence.createCriteria()
			takenExceptionnel = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.EXCEPTIONNEL)
				}
			}
			if (takenExceptionnel!=null){
				takenExceptionnelMap.put(employee, takenExceptionnel.size())
			}else{
				takenExceptionnelMap.put(employee, 0)
			}
			
			//DIF
			criteria = Absence.createCriteria()
			takenDIF = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.DIF)
				}
			}
			if (takenDIF!=null){
				takenDifMap.put(employee, takenDIF.size())
			}else{
				takenDifMap.put(employee, 0)
			}
		}
		log.debug("done")
		[
			employeeInstanceTotal:employeeInstanceTotal,
			takenExceptionnelMap:takenExceptionnelMap,
			takenDifMap:takenDifMap,
			period:period,
			employeeInstanceTotal:employeeInstanceTotal,
			site:site,
			siteId:siteId,
			employeeInstanceList:employeeInstanceList,
			takenCSSMap:takenCSSMap,
			takenAutreMap:takenAutreMap,
			takenSicknessMap:takenSicknessMap,
			takenRTTMap:takenRTTMap,
			takenCAMap:takenCAMap,
			initialCAMap:initialCAMap,
			initialRTTMap:initialRTTMap,
			remainingRTTMap:remainingRTTMap,
			remainingCAMap:remainingCAMap
		]	
	}
	
	
	def vacationDisplay(Long id){		
		def isAdmin = (params["isAdmin"] != null  && params["isAdmin"].equals("true")) ? true : false
		def siteId=params["siteId"]
		def employeeInstance = Employee.get(id)		
		def criteria
		def takenCA=[]
		def takenRTT=[]
		def takenCSS=[]
		def takenAutre=[]
		def takenSickness = []
		def takenRTTMap=[:]
		def takenCAMap=[:]
		def yearMap=[:]
		def initialCAMap=[:]
		def remainingCAMap=[:]
		def initialRTTMap=[:]
		def remainingRTTMap=[:]
		def takenSicknessMap=[:]
		def takenCSSMap=[:]
		def takenAutreMap=[:]
		// starting calendar: 1 of June of the period
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.clearTime()
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,4)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		for (Period period:Period.findAll("from Period as p order by p.year asc")){
			yearMap.put(period.year, period.year+'/'+(period.year+1))
			// step 1: fill initial values
			//CA
			criteria = Vacation.createCriteria()
			def initialCA = criteria.get{
				and {
					eq('employee',employeeInstance)
					eq('period',period)
					eq('type',VacationType.CA)
				}
			}
			if (initialCA != null){
				initialCAMap.put(period.year, initialCA.counter)
			}else{
				initialCAMap.put(period.year, 0)
			
			}
			//RTT
			criteria = Vacation.createCriteria()
			def initialRTT = criteria.get{
				and {
					eq('employee',employeeInstance)
					eq('period',period)
					eq('type',VacationType.RTT)
				}
			}
			if (initialRTT != null){
				initialRTTMap.put(period.year, initialRTT.counter)
			}else{
				initialRTTMap.put(period.year, 0)
			}
			
			// step 2: fill actual counters
			startCalendar.set(Calendar.YEAR,period.year)
			endCalendar.set(Calendar.YEAR,period.year+1)
			//CA
			criteria = Absence.createCriteria()
			takenCA = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.VACANCE)
				}
			}
			if (takenCA!=null){
				remainingCAMap.put(period.year, initialCAMap.get(period.year)-takenCA.size())
				takenCAMap.put(period.year, takenCA.size())
			}else{
				remainingCAMap.put(period.year, initialCAMap.get(period.year))
				takenCAMap.put(period.year, 0)
				
			}
			//RTT
			criteria = Absence.createCriteria()
			takenRTT = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.RTT)
				}
			}
			if (takenRTT!=null){
				remainingRTTMap.put(period.year, initialRTTMap.get(period.year)-takenRTT.size())
				takenRTTMap.put(period.year, takenRTT.size())
				
			}else{
				remainingRTTMap.put(period.year, initialRTTMap.get(period.year))
				takenRTTMap.put(period.year, 0)			
			}
			
			criteria = Absence.createCriteria()
			takenSickness = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.MALADIE)
				}
			}
			
			if (takenSickness!=null){
				takenSicknessMap.put(period.year, takenSickness.size())				
			}else{
				takenSicknessMap.put(period.year, 0)
			}		
			
			criteria = Absence.createCriteria()
			takenCSS = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.CSS)
				}
			}
			
			if (takenCSS!=null){
				takenCSSMap.put(period.year, takenCSS.size())				
			}else{
				takenCSSMap.put(period.year, 0)		
			}

			criteria = Absence.createCriteria()
			takenAutre = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.AUTRE)
				}
			}
			
			if (takenAutre!=null){
				takenAutreMap.put(period.year, takenAutre.size())
			}else{
				takenAutreMap.put(period.year, 0)
			}				
		}
	[takenCSSMap:takenCSSMap,takenAutreMap:takenAutreMap,takenSicknessMap:takenSicknessMap,takenRTTMap:takenRTTMap,takenCAMap:takenCAMap,employeeInstance: employeeInstance,isAdmin:isAdmin,siteId:siteId,yearMap:yearMap,initialCAMap:initialCAMap,initialRTTMap:initialRTTMap,remainingRTTMap:remainingRTTMap,remainingCAMap:remainingCAMap]	

	}
	
	
    def edit(Long id) {
		def isAdmin = (params["isAdmin"] != null  && params["isAdmin"].equals("true")) ? true : false
		def fromSite = (params["fromSite"] != null  && params["fromSite"].equals("true")) ? true : false
		def back = (params["back"] != null  && params["back"].equals("true")) ? true : false		
        def employeeInstance = Employee.get(id)
		def myDate = params["myDate"] 
		def orderedVacationList=[]
		def previousContracts
		// starting calendar: 1 of June of the period
		def startCalendar = Calendar.instance		
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.clearTime()
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,4)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		def periodList= Period.findAll("from Period as p order by year asc")
		
		for (Period period:periodList){
			def vacations = Vacation.findAllByEmployeeAndPeriod(employeeInstance,period,[sort:'type',order:'asc'])
			for (Vacation vacation:vacations){
				orderedVacationList.add(vacation)
			}			
		}		
		
		previousContracts = Contract.findAllByEmployee(employeeInstance,[sort:'startDate',order:'desc'])
		
		def siteId=params["siteId"]
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }
		def arrivalDate = employeeInstance.arrivalDate
		def retour = [
			back:back,
			myDateFromEdit:myDate,
			previousContracts:previousContracts,
			arrivalDate:arrivalDate,
			orderedVacationList:orderedVacationList,
			orderedVacationListfromSite:fromSite,
			employeeInstance: employeeInstance,
			isAdmin:isAdmin,siteId:siteId
		]		
		return retour
	}
	

	def getAjaxSupplementaryTime(Long id) {
		def year = params.int('year')
		def period = params['period']
		def month = params.int('month')
		log.error("getAjaxSupplementaryTime triggered with params: month="+month+" and year="+year)
		def employee = Employee.get(id)		
		def model = timeManagerService.getYearSupTime(employee,year,month)
		model << [id:id,month:month,year:year]
		render template: "/employee/template/yearSupplementaryTime", model: model
		return 
	}
	
	
	def getSupplementaryTime(Long id) {
		def employeeInstance = Employee.get(id)
		def data
		//def periodList= Period.findAll("from Period as p order by year asc")
		def period = ((new Date()).getAt(Calendar.MONTH) >= 5) ? Period.findByYear(new Date().getAt(Calendar.YEAR)) : Period.findByYear(new Date().getAt(Calendar.YEAR) - 1)	
		data = supplementaryTimeService.getAllSupAndCompTime(employeeInstance,period)	
		def model = [employeeInstance:employeeInstance]
		model << data 		
		//render template: "/employee/template/paidHSEditTemplate", model:model
		return model
	}
	
	
	

	def cartouche(long userId,int year,int month){
		def employeeInstance = Employee.get(userId)
		return timeManagerService.getCartoucheData(employeeInstance,year,month)
	}
	
	
	def modifyAbsence(){
		def employee = Employee.get(params.int('employeeId'))
		def day = params["day"]
		def supTime=params['payableSupTime']
		def updatedSelection = params["updatedSelection"].toString()
		if (updatedSelection.equals('G'))
			updatedSelection = AbsenceType.GROSSESSE
		if (updatedSelection.equals('-'))
			updatedSelection = AbsenceType.ANNULATION
		SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
		Date date = dateFormat.parse(day)
		def cal= Calendar.instance
		def criteria
		cal.time=date
		if (!updatedSelection.equals('')){
			// check if an absence was already logged:
			criteria = Absence.createCriteria()
			
			// get cumul holidays
			def absence = criteria.get {
				and {
					eq('employee',employee)
					eq('year',cal.get(Calendar.YEAR))
					eq('month',cal.get(Calendar.MONTH)+1)
					eq('day',cal.get(Calendar.DAY_OF_MONTH))
				}
			}
	
			if (absence != null){
				if (updatedSelection.equals(AbsenceType.ANNULATION.key)){
					// annulation n�cessaire: il faut effacer le tupple
					absence.delete(flush: true)
				}else{
					absence.type=updatedSelection
					
					if (absence.month<6){
						absence.period=Period.findByYear(absence.year-1)
					}else{
						absence.period=Period.findByYear(absence.year)
					}				
					absence.save(flush: true)
				}
			}else {
				if (!updatedSelection.equals(AbsenceType.ANNULATION)){
					absence = new Absence()
					absence.date=date
					absence.employee=employee
					absence.day=cal.get(Calendar.DAY_OF_MONTH)
					absence.month=cal.get(Calendar.MONTH)+1
					absence.year=cal.get(Calendar.YEAR)
					absence.type=updatedSelection
					
					if (absence.month<6){
						absence.period=Period.findByYear(absence.year-1)
					}else{
						absence.period=Period.findByYear(absence.year)
					}			
					absence.save(flush: true)
				}
			}
		}else{
			flash.message=message(code: 'absence.impossible.update')
		}
		
		def cartoucheTable = timeManagerService.getReportData(null, employee,  null, cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR))
		def openedDays = timeManagerService.computeMonthlyHours(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1)
		def yearInf
		def yearSup
		if ((cal.get(Calendar.MONTH)+1)>5){
			yearInf=cal.get(Calendar.YEAR)
			yearSup=cal.get(Calendar.YEAR)+1
		}else{
			yearInf=cal.get(Calendar.YEAR)-1
			yearSup=cal.get(Calendar.YEAR)
		}
		Period period = ((cal.get(Calendar.MONTH)+1)>5)?Period.findByYear(cal.get(Calendar.YEAR)):Period.findByYear(cal.get(Calendar.YEAR)-1)
		def model=[
			period2:period,
			period:cal.time,
			firstName:employee.firstName,
			lastName:employee.lastName,
			weeklyContractTime:employee.weeklyContractTime,
			matricule:employee.matricule,
			yearInf:yearInf,
			yearSup:yearSup,
			employee:employee,
			openedDays:openedDays

			]
		model << cartoucheTable
		render template: "/employee/template/cartoucheTemplate", model:model
		return
	}
	
	def addingEventToEmployee(){
		def cal = Calendar.instance	
		def type = params["type"].equals("Entrer") ? "E" : "S" 
		def isOutSideSite=params["isOutSideSite"].equals("true") ? true : false
		Employee employeeInstance = Employee.get(params.int('userId'))		
		def currentDate = cal.time
		def criteria
		def entranceStatus=false
		def timeDiff		
		def flashMessage=true
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).time
			
		
		log.error('trying to add event to employee '+employeeInstance.firstName+' '+employeeInstance.lastName+' with type: '+type)
		// liste les entrees de la journée et vérifie que cette valeur n'est pas supérieure à une valeur statique
		def inAndOutcriteria = InAndOut.createCriteria()
		def todayEmployeeEntries = inAndOutcriteria.list {
			and {
				eq('employee',employeeInstance)
				eq('type','E')
				gt('time',today)
			}
		}
		if (todayEmployeeEntries.size() > Employee.entryPerDay){
			flash.message = "TROP D'ENTREES DANS LA JOURNEE. POINTAGE NON PRIS EN COMPTE"
			log.error("employee: "+employeeInstance.lastName+" proceeded to too many entries")
			redirect(action: "show", id: employeeInstance.id)
			return
		}

		criteria = InAndOut.createCriteria()
		def lastIn = criteria.get {
			and {
				eq('employee',employeeInstance)
				eq('pointed',false)
				eq('day',today.getAt(Calendar.DATE))
				eq('month',today.getAt(Calendar.MONTH)+1)
				eq('year',today.getAt(Calendar.YEAR))
				order('time','desc')
			}
			maxResults(1)
		}
		
		if (lastIn != null){
			def LIT = lastIn.time
			use (TimeCategory){timeDiff=currentDate-LIT}
			//empecher de represser le bouton pendant 30 seconds
			if ((timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600)<30){
				flash.message = message(code: 'employee.overlogging.error')
				flashMessage=false
			}
		}
		
		// initialisation
		if (flashMessage){
			def inOrOut = timeManagerService.initializeTotals(employeeInstance,currentDate,type,null,isOutSideSite)
			log.error('entry created with params: '+inOrOut)
		}
		criteria = InAndOut.createCriteria()
		def inAndOuts = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('day',today.getAt(Calendar.DATE))
				eq('month',today.getAt(Calendar.MONTH)+1)
				eq('year',today.getAt(Calendar.YEAR))
				order('time','asc')
			}
		}
		
		if (lastIn!=null){
			if (flashMessage){
				entranceStatus = lastIn.type.equals("S") ? true : false
			}else{
				entranceStatus = lastIn.type.equals("S") ? false : true
			}
		}else{
			entranceStatus=true
		}
		if (type.equals("E")){
			if (flashMessage)
				flash.message = message(code: 'inAndOut.create.label', args: [message(code: 'inAndOut.entry.label', default: 'exit'), cal.time])
		}else{
			if (flashMessage)		
				flash.message = message(code: 'inAndOut.create.label', args: [message(code: 'inAndOut.exit.label', default: 'exit'), cal.time])
		}
		
		def model = timeManagerService.getPointageData( employeeInstance)
		model << [userId: employeeInstance.id,employee: employeeInstance]
		render template: "/employee/template/last5DaysTemplate", model:model
	}
	

    def update(Long id, Long version) {
		params.each{i->
			log.error('param for update: '+i)
		}		
		
		def siteId=params["siteId"]
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
        def employeeInstance = Employee.get(id)
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (employeeInstance.version > version) {
                employeeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'employee.label', default: 'Employee')] as Object[],
                          "Another user has updated this Employee while you were editing")
                render(view: "edit", model: [employeeInstance: employeeInstance])
                return
            }
        }
		
		
		def employeeStatus = employeeInstance.status
		
		log.error('employee status: '+employeeInstance.status)
		
        employeeInstance.properties = params
		
		
		if (params["weeklyContractTime"] != null){
			employeeInstance.weeklyContractTime = params.float("weeklyContractTime")
		}

		def service = params["employee.service.id"]
		if (service!=null && !service.equals('')){
			service = Service.get(service)
			employeeInstance.service=service
		}
		
		def site = params["employee.site.id"]
		if (site!=null && !site.equals('')){
			site = Site.get(site)
			employeeInstance.site=site
		}
		
		def function = params["employee.function.id"]
		if (function!=null && !function.equals('')){
			function = Function.get(function)
			employeeInstance.function=function
		}
		employeeInstance.status = employeeStatus		
        if (!employeeInstance.save(flush: true)) {
            render(view: "edit", model: [employeeInstance: employeeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'employee.label', default: 'Employee'), employeeInstance.userName])
        redirect(action: "edit", id: employeeInstance.id,params: [isAdmin: isAdmin,siteId:siteId])
    }

	
	def changeContractParams(){
		def employee = Employee.get(params["userId"])
		def contract = Contract.get(params["contractId"])
		def newDate
		def newValue
		
		if (params["newDate"] != null) {			
			if (params["newDate"] == ''){
				if (params["type"].contains('startDate')){
					// throw error: start date cannot be null
					flash.message2 = message(code: 'employee.startdate.not.null')
				}else {
					contract.endDate = null
				}
			}else{
				newDate = new Date().parse("d/M/yyyy",params["newDate"])		
				if (params["type"].contains('startDate')){
					contract.startDate = newDate					
				}else {
					contract.endDate = newDate				
				}
			}	
		}
		
		if (params["newValue"] != null){
			newValue = params["newValue"].toFloat()
			contract.weeklyLength = newValue
		}		
		def contracts = Contract.findAllByEmployee(employee,[sort:'startDate',order:'desc'])
		def model = [previousContracts:contracts,employeeInstance: employee]	
		render template: "/employee/template/contractTable", model: model
		return
	}
	
	
	
	def trashContract(){
		def contractId=params["contractId"]
		def contract = Contract.get(params["contractId"])
		def employee = contract.employee
		log.error('removing contract '+contract)
		contract.delete(flush:true)
		def contracts = Contract.findAllByEmployee(employee,[sort:'startDate',order:'desc'])
		def model = [previousContracts:contracts,employeeInstance: employee]	
		render template: "/employee/template/contractTable", model: model
		return
	}
	
    def delete(Long id) {
        def employeeInstance = Employee.get(id)
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }

        try {
            employeeInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			log.error('error with application: '+e.toString())
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "show", id: id)
        }
    }
	
	
	def validate(){
		def eventId=params["inOrOutId"]
		def inOrOut = InAndOut.get(eventId)
		log.error('validating entry '+inOrOut)
		inOrOut.regularizationType=InAndOut.MODIFIEE_ADMIN
		inOrOut.save(flush:true)
	}
	
	
	def trash(){
		def eventId=params["inOrOutId"]
		def inOrOut = InAndOut.get(eventId)
		def month = inOrOut.month
		def year = inOrOut.year
		def employee = inOrOut.employee
		log.error('removing entry '+inOrOut)
		inOrOut.delete(flush:true)
		def report = timeManagerService.getReportData(null, employee,  null, month, year)			
		render template: "/employee/template/reportTableTemplate", model: report
		return
	}
		
	def modifyTime(){
		def idList=params["inOrOutId"]
		def dayList=params["day"]
		def monthList=params["month"]
		def yearList=params["year"]
		def employee = Employee.get(params.long("userId"))
		if (employee == null){
			employee = Employee.get(params["employee.id"])
		}
		def newTimeList=params["cell"]
		def fromRegularize=params["fromRegularize"].equals("true") ? true : false

		try{
			def month=monthList[0]
			def year=yearList[0]		
			if (idList==null){
				log.error("list is null")
				def retour = report(employee.id as long,month as int,year as int)
				render(view: "report", model: retour)
			}
			timeManagerService.timeModification( idList, dayList, monthList, yearList, employee, newTimeList, fromRegularize)
				// now, find if employee still has errors:			
			if (fromRegularize){
				redirect(action: "pointage", id: employee.id)
			}else{
				def retour = report(employee.id as long,month as int,year as int)
				render(view: "report", model: retour)
			}
		}
		catch(PointeuseException ex){
			flash.message = message(code: ex.message)
			if (fromRegularize){
			render(view: "index")
			}else{
			def retour = report(employee.id as long,month as int,year as int)
			render(view: "report", model: retour)
			}
			
		}catch(NullPointerException e2){
			flash.message = message(code: "appliquer.inutile.message")
			log.error(e2.message)
			if (fromRegularize){
			render(view: "index")
			}else{
				def currentMonth=params.int('myDate_month')
				def currentYear=params.int('myDate_year')	
				def retour = report(employee.id as long,currentMonth as int,currentYear as int)
				render(view: "report", model: retour)
			}
		}
	}
	

	def annualReport(Long userId){
		def year
		def month
		def calendar = Calendar.instance		
		boolean isAjax = params["isAjax"].equals("true") ? true : false
		if (userId == null){
			userId = params.long('userId')
		}
		Employee employee = Employee.get(userId)
		
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
		log.error('annualReport called with param: employee='+employee+' and period='+period)
		
		def model = timeManagerService.getAnnualReportData(year, employee)
		model << [period:period]
		if (isAjax){
			render template: "/employee/template/annualReportTemplate", model:	model
			return
		}
		else{
			model
		}
	}
	
	def annualReportLight(Long userId){
		def year
		def month
		def calendar = Calendar.instance
		boolean isAjax = params["isAjax"].equals("true") ? true : false
		Employee employee = Employee.get(userId)
		
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
		
		if (userId==null){
			log.error('userId is null. exiting')
			return
		}
		[employee:employee,userId:employee.id]				
	}
	
	def reportLight(Long userId,int monthPeriod,int yearPeriod){
		def yearInf
		def yearSup
		def siteId=params["siteId"]
		def calendar = Calendar.instance
		def weekName="semaine "
		def weeklyTotalTime = [:]
		def weeklySuppTotalTime = [:]
		def weeklyTotalTimeByEmployee = [:]
		def weeklySupTotalTimeByEmployee = [:]
		def monthlyTotalTimeByEmployee = [:]
		def weeklyAggregate = [:]
		def dailyTotalMap = [:]
		def dailyBankHolidayMap = [:]
		def dailySupTotalMap = [:]
		def holidayMap = [:]
		def employee
		def mapByDay = [:]
		def dailyTotalId=0
		def myDate = params["myDate"]
		def monthlySupTime = 0
		def monthlyTotalTime = 0
		def criteria
		def dailySeconds = 0
		def weeklySupTime
		def currentWeek
		
		if (myDate != null && myDate instanceof String){
			SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)			
		}
			
		def pppp = params["userId"]	
		if (userId==null){
			if (params["userId"] instanceof String)
				userId = params["userId"]
		}
		employee = Employee.get(userId)
	
		//get last day of the month
		if (myDate==null){
			if (yearPeriod!=0){
				calendar.set(Calendar.MONTH,monthPeriod-1)
				calendar.set(Calendar.YEAR,yearPeriod)			
			}
			calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH))
			calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		}else{
			calendar.time=myDate
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
			
		def calendarLoop = calendar
		int month = calendar.get(Calendar.MONTH)+1 // starts at 0
		int year = calendar.get(Calendar.YEAR)
		calendarLoop.getTime().clearTime()
		def lastWeekParam = utilService.getLastWeekOfMonth(month, year)
		def isSunday=lastWeekParam.get(1)
		
		currentWeek=0//calendar.get(Calendar.WEEK_OF_YEAR)
		
		while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			// �limine les dimanches du rapport
			if (calendarLoop.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
				mapByDay = [:]					
			}
			//print calendarLoop.time
			criteria = DailyTotal.createCriteria()
			def dailyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH)) 
					eq('month',month)
					eq('year',year)
				}
			}
			// permet de récupérer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId){
				
				
				dailySeconds = (timeManagerService.getDailyTotal(dailyTotal)).get("elapsedSeconds")
				monthlyTotalTime += dailySeconds
				def previousValue=weeklyTotalTime.get(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR))
				if (previousValue!=null){
					def newValue=previousValue.get(0)*3600+previousValue.get(1)*60+previousValue.get(2)
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), timeManagerService.computeHumanTime(dailySeconds+newValue))
				}else{
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), timeManagerService.computeHumanTime(dailySeconds))
				}
				
				if (!isSunday && calendarLoop.get(Calendar.WEEK_OF_YEAR)==lastWeekParam.get(0) ){
					weeklySupTime = 0
				}else{
					weeklySupTime = timeManagerService.computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
				}
				weeklySuppTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR),timeManagerService.computeHumanTime(Math.round(weeklySupTime)))
				if (currentWeek != calendarLoop.get(Calendar.WEEK_OF_YEAR)){
					monthlySupTime += weeklySupTime
					currentWeek = calendarLoop.get(Calendar.WEEK_OF_YEAR)
				}
				weeklySupTotalTimeByEmployee.put(employee,weeklySuppTotalTime)
				weeklyTotalTimeByEmployee.put(employee,weeklyTotalTime)
				dailyTotalId=dailyTotal.id
			} 
			
			
			criteria = InAndOut.createCriteria()
			def entriesByDay = criteria{
				and {
					eq('employee',employee)
					eq('day',calendarLoop.getAt(Calendar.DATE))
					eq('month',month)
					eq('year',year)
					order('time')
					}
			}
			// put in a map in and outs
			def tmpDate = calendarLoop.time
			if 	(entriesByDay.size()>0){
				if (dailyTotal!=null){
					dailyTotalMap.put(tmpDate, timeManagerService.computeHumanTime(dailySeconds))
					dailySupTotalMap.put(tmpDate, timeManagerService.computeHumanTime(Math.max(dailySeconds-DailyTotal.maxWorkingTime,0)))
				}else {
					dailyTotalMap.put(tmpDate, timeManagerService.computeHumanTime(0))
					dailySupTotalMap.put(tmpDate, timeManagerService.computeHumanTime(0))
				}		
				mapByDay.put(tmpDate, entriesByDay)
			}	
			else{
				dailyTotalMap.put(tmpDate, timeManagerService.computeHumanTime(0))
				mapByDay.put(tmpDate, null)
			}		
			
			// find out if day is a bank holiday:
			criteria = BankHoliday.createCriteria()
			def bankHoliday = criteria.get{
				and {
					eq('year',calendarLoop.get(Calendar.YEAR))
					eq('month',calendarLoop.get(Calendar.MONTH)+1)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
				}
			}
			if (bankHoliday!=null){
				dailyBankHolidayMap.put(tmpDate, true)
			}else{
				dailyBankHolidayMap.put(tmpDate, false)
			
			}
			
			def absenceCriteria = Absence.createCriteria()			
			def dailyAbsence = absenceCriteria.get {
				and {
					eq('employee',employee)
					eq('year',calendarLoop.get(Calendar.YEAR))
					eq('month',calendarLoop.get(Calendar.MONTH)+1)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))				
				}
			}
			holidayMap.put(tmpDate, dailyAbsence)
			weeklyAggregate.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), mapByDay)	
			if (calendarLoop.get(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}	
		try {
			if (userId != null){
			def cartoucheTable = timeManagerService.getCartoucheData(employee,year,month)
			def monthTheoritical = cartoucheTable.get('monthTheoritical')
			def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get('pregnancyCredit'))
			def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyTheoritical'))
			def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyPregnancyCredit'))
			def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyActualTotal'))
			def payableSupTime = timeManagerService.computeHumanTime(Math.round(monthlySupTime))
			def payableCompTime = timeManagerService.computeHumanTime(0)
			if (employee.weeklyContractTime!=35){
				if (monthlyTotalTime > monthTheoritical){
					payableCompTime = timeManagerService.computeHumanTime(Math.max(monthlyTotalTime-monthTheoritical-monthlySupTime,0))
				}
			}
			
			monthlyTotalTimeByEmployee.put(employee, timeManagerService.computeHumanTime(monthlyTotalTime))
			monthTheoritical = timeManagerService.getTimeAsText(timeManagerService.computeHumanTime(cartoucheTable.get('monthTheoritical')),true)		

			if (month>5){
				yearInf=year
				yearSup=year+1
			}else{
				yearInf=year-1
				yearSup=year
			}

			def model=[isAdmin:false,siteId:siteId,yearInf:yearInf,yearSup:yearSup,userId:userId,yearlyActualTotal:yearlyActualTotal,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTheoritical:yearlyTheoritical,monthlyTotal:monthlyTotalTimeByEmployee,weeklyTotal:weeklyTotalTimeByEmployee,weeklySupTotal:weeklySupTotalTimeByEmployee,dailySupTotalMap:dailySupTotalMap,dailyTotalMap:dailyTotalMap,month:month,year:year,period:calendarLoop.getTime(),holidayMap:holidayMap,weeklyAggregate:weeklyAggregate,employee:employee,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
			model << cartoucheTable
			return model
		}
		}catch (NullPointerException e){
			log.error('error with application: '+e.toString())
		}	
	}
	
	
	@Secured(['ROLE_ADMIN'])
	def report(Long userId,int monthPeriod,int yearPeriod){
		def siteId=params["siteId"]
		def myDate = params["myDate"]
		def myDateFromEdit = params["myDateFromEdit"]
		SimpleDateFormat dateFormat
		def employee
		def report = [:]
		def currentContract

		if (myDate != null && myDate instanceof String){
			dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)			
		}
		if (myDateFromEdit != null && myDateFromEdit instanceof String){
			dateFormat = new SimpleDateFormat('MM/yyyy');
			myDate = dateFormat.parse(myDateFromEdit)
		}
		
		
		if (userId==null && params["userId"] != null ){
			employee = Employee.get(params["userId"])
		}else{
			employee = Employee.get(userId)
		}
		//get last day of the month
		if (employee){			
			report = timeManagerService.getReportData(siteId, employee,  myDate, monthPeriod, yearPeriod)
			currentContract = report.getAt('currentContract')
			if (currentContract == null){
				flash.message = message(code: 'employee.not.arrived.error')
				report = timeManagerService.getReportData(siteId, employee,  null, employee.arrivalDate.getAt(Calendar.MONTH)+1, employee.arrivalDate.getAt(Calendar.YEAR))
			}else {
				return report
			}
			
		}
	}


	
	def pointage(Long id){	
		log.error('pointagege called')
		try {	
			def username = params["username"]
			def employee
			def entranceStatus
			def mapByDay=[:]
			def totalByDay=[:]
			def dailyCriteria
			def elapsedSeconds=0
			if (id!=null){
				employee = Employee.get(id)		
			}
			if (username!=null && !username.equals("")){
				employee = Employee.findByUserName(username)	
			}
			
			if (employee==null){
				throw new NullPointerException("unknown employee("+username+")")
			}else{
				log.error('employee successfully authenticated='+employee)
			}
			
			def calendar = Calendar.instance	
			def inAndOutsCriteria = InAndOut.createCriteria()
			def systemGeneratedEvents = inAndOutsCriteria.list {
				and {
					eq('employee',employee)
					lt('time',calendar.time)
					eq('systemGenerated',true)
					order('time','asc')
				}
			}
			
			if (systemGeneratedEvents!=null && systemGeneratedEvents.size()>0){
				log.error('redirecting user to regularization page')
				render(view: "regularize", model: [systemGeneratedEvents: systemGeneratedEvents,employee:employee])
				return
			}
				
			def model = timeManagerService.getPointageData(employee)
			model << [userId: employee.id,employee: employee]
			return model
			
		}
		catch (Exception e){
			log.error('error with application: '+e.toString())		
			flash.message = message(code: 'employee.not.found.label',args:[params["username"]])
			redirect(uri:'/')
			
		}
	}	

	
	def ecartPDF(){
		def siteId=params["site.id"]
		def year = params["year"]
		def folder = grailsApplication.config.pdf.directory
		def calendar = Calendar.instance
		def refCalendar = Calendar.instance
		def site
		def period
		def monthList=[]
		
		if (year!=null && !year.equals("")){
			if (year instanceof String[]){
				year=(year[0]!="")?year[0].toInteger():year[1].toInteger()
			}else {
				year=year.toInteger()
			}
			period = Period.get(year)
		}else{
			period = Period.findByYear(calendar.get(Calendar.YEAR))
		}
			 
		 refCalendar.set(Calendar.MONTH,5)
		 refCalendar.set(Calendar.YEAR,period.year)
		 
		 if (refCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)){
			 while(refCalendar.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)){
				 log.debug('refCalendar: '+refCalendar.time)
				 monthList.add(refCalendar.get(Calendar.MONTH)+1)
				 refCalendar.roll(Calendar.MONTH, 1)
				 if (refCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
					 break
				 }
			 }
	 	}else{
			 while(refCalendar.get(Calendar.MONTH) <= 11){
				 log.debug('refCalendar: '+refCalendar.time)
				 monthList.add(refCalendar.get(Calendar.MONTH)+1)
				 if (refCalendar.get(Calendar.MONTH)==11){
					 break
				 }
				 refCalendar.roll(Calendar.MONTH, 1)
			 }
			 refCalendar.set(Calendar.MONTH,0)
			 refCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR))
			 while(refCalendar.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)){
				 log.debug('refCalendar: '+refCalendar.time)
				 monthList.add(refCalendar.get(Calendar.MONTH)+1)
				 refCalendar.roll(Calendar.MONTH, 1)
				 if (refCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
					 break
				 }
			 }	 
	 	} 
				 
		if (params["site.id"]!=null && !params["site.id"].equals("")){
			def tmpSite = params["site.id"]
			if (tmpSite instanceof String[]){
				tmpSite=(tmpSite[0]!="")?tmpSite[0].toInteger():tmpSite[1].toInteger()
			}else {
				tmpSite=tmpSite.toInteger()
			}
			site = Site.get(tmpSite)
			siteId=site.id
		}
		def retour = PDFService.generateEcartSheet(site, folder, monthList, period)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	def siteMonthlyPDF(){
		def myDate = params["myDate"]
		def site
		def siteId
		Calendar calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
		
		if (myDate==null || myDate.equals("")){
			myDate=calendar.time
		}else {
			calendar.time=myDate
		}
	

		if (params["site.id"]!=null && !params["site.id"].equals('')){
			siteId = params["site.id"].toInteger()
			site = Site.get(siteId)
			siteId=site.id
		}else{
			flash.message = message(code: 'pdf.site.selection.error')
        	redirect(action: "list")
			return
		}
			
		def retour = PDFService.generateSiteMonthlyTimeSheet(myDate,site,folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	
	def allSiteMonthlyPDF(){
		def myDate = params["myDate"]

		Calendar calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
			
		def retour = PDFService.generateAllSitesMonthlyTimeSheet(calendar.time=myDate,folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	
	def dailyTotalPDF(){
		def site
		def siteId
		Calendar calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
		
		def date_picker =params["date_picker"]
		if (date_picker != null && date_picker.size()>0){
			

			calendar.time = new Date().parse("dd/MM/yyyy", date_picker)
		}


		if (params["site.id"]!=null && !params["site.id"].equals('')){
			siteId = params["site.id"].toInteger()
			site = Site.get(params.int("site.id"))
		}else{
			flash.message = message(code: 'pdf.site.selection.error')
			redirect(action: "list")
			return
		}
			
		def retour = PDFService.generateDailySheet(site,folder,calendar.time)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
		
	}
	
	def userPDF(){
		def myDate = params["myDate"]
		def userId = params.int('userId')
		Employee employee = Employee.get(userId)
		Calendar calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
		
		if (myDate==null || myDate.equals("")){
			myDate=calendar.time
		}else {
			calendar.time=myDate
		}
		def retour = PDFService.generateUserMonthlyTimeSheet(myDate,employee,folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	

	
	
	def annualTotalPDF(Long userId){	
		params.each{i-> log.error(i)}
		def year
		def month
		def calendar = Calendar.instance
		Employee employee = Employee.get(userId)
		def folder = grailsApplication.config.pdf.directory
		
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
		
		Period period = Period.get(params.int("year"))
		if (period != null){
			year = period.year
		}
		
		if (userId==null){
			log.error('userId is null. exiting')
			return
		}
		def retour = PDFService.generateUserAnnualTimeSheet(year,month,employee,folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	 
	 
	 def ecartFollowup(){
		 def fromIndex=params.boolean('fromIndex')
		 
		 def siteId=params["site.id"]
		 if (!fromIndex && (siteId == null || siteId.size() == 0)){
			 flash.message = message(code: 'ecart.site.selection.error')
			 params["fromIndex"]=true
			 redirect(action: "ecartFollowup",params:params)
			 return
		 }
		 def year = params["year"]		 
		 def employeeInstanceList
		 def employeeInstanceTotal
		 def calendar = Calendar.instance
		 def refCalendar = Calendar.instance 
		 def site
		 def period
		 def monthList=[]
		 
		 if (year!=null && !year.equals("")){
			 if (year instanceof String[]){
				 year=(year[0]!="")?year[0].toInteger():year[1].toInteger()
			 }else {
				 year=year.toInteger()
			 }
			 period = Period.get(year)
		 }else{
		 	period = Period.findByYear(calendar.get(Calendar.YEAR))
		 }
		 	 
		 refCalendar.set(Calendar.MONTH,5)
		 refCalendar.set(Calendar.YEAR,period.year)
		 
		 if (refCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)){
			 while(refCalendar.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)){
				 log.debug('refCalendar: '+refCalendar.time)
				 monthList.add(refCalendar.get(Calendar.MONTH)+1)
				 refCalendar.roll(Calendar.MONTH, 1)
				 if (refCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
					 break
				 }
			 }
	 	}else{
			 while(refCalendar.get(Calendar.MONTH) <= 11){
				 log.debug('refCalendar: '+refCalendar.time)
				 monthList.add(refCalendar.get(Calendar.MONTH)+1)
				 if (refCalendar.get(Calendar.MONTH)==11){
					 break
				 }
				 refCalendar.roll(Calendar.MONTH, 1)
			 }
			 refCalendar.set(Calendar.MONTH,0)
			 refCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR))
			 while(refCalendar.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)){
				 log.debug('refCalendar: '+refCalendar.time)
				 monthList.add(refCalendar.get(Calendar.MONTH)+1)
				 refCalendar.roll(Calendar.MONTH, 1)
				 if (refCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
					 break
				 }
			 }	 
	 	} 
		 		 
		if (params["site.id"]!=null && !params["site.id"].equals("")){
			 def tmpSite = params["site.id"]
			 if (tmpSite instanceof String[]){
				 tmpSite=(tmpSite[0]!="")?tmpSite[0].toInteger():tmpSite[1].toInteger()
			 }else {
				 tmpSite=tmpSite.toInteger()
			 }
			 site = Site.get(tmpSite)
			 siteId=site.id
			 employeeInstanceList = Employee.findAllBySite(site)
			 employeeInstanceTotal = employeeInstanceList.size()	 
		 }else{
			 employeeInstanceList=Employee.list(params)
			 employeeInstanceTotal = employeeInstanceList.size()
		 }	 
		 
		
		 if (fromIndex){
			 return [site:site,fromIndex:fromIndex,period:period]
		 }
		 
		def ecartData = timeManagerService.getEcartData(site, monthList, period)
		def retour = [
			site:site,
			fromIndex:fromIndex,
			period:period,
			employeeInstanceTotal:employeeInstanceTotal,
			monthList:monthList,
			employeeInstanceList:employeeInstanceList
		]
	 	retour << ecartData
		 
		render template: "/employee/template/ecartTemplate", model: retour

		return retour
	}
	 
	def appendContract(){
		log.error('appendContract called')
	}
	
	def addNewContract = {
		params.each{i-> log.error(i)}
		log.error('addNewContract called')
		Contract contract = new Contract()
		Employee employee = Employee.get(params.id)
		
		def dateFormat = new SimpleDateFormat('d/M/yyyy');
		if (params['newStartDate'] != null)
			contract.startDate = dateFormat.parse(params['newStartDate'])
		if (params['newEndDate'] != null && params['newEndDate'].size() > 0)
			contract.endDate = dateFormat.parse(params['newEndDate'])
		
		if (utilService.detectCollidingContract(contract.startDate,contract.endDate,employee)){
			log.error('contract dates are incomplatible, exiting')
			flash.message = message(code: 'contract.incompatible.values')
			redirect(action: "edit", params: [id:params.id,isAdmin:false])
			return
		}			
		contract.year=contract.startDate.getAt(Calendar.YEAR)
		contract.month=(contract.startDate.getAt(Calendar.MONTH))+1
		contract.weeklyLength = params.float('newContractValue')
		contract.employee = Employee.get(params.id)
		contract.loggingTime = new Date()
		contract.save()
		redirect(action: "edit", params: [id:params.id,isAdmin:false])		
	  }
	 
	def payHS(){
		log.error('payHS called')
		params.each{i->
			
			log.error(i)
		}

		def userId = (params["userId"].split(" ").getAt(0)) as long
		def year = (params["period"].split(" ").getAt(0)) as long
		def type = ((params["type"].split(" ").getAt(0))).equals('HS')?SupplementaryType.HS:SupplementaryType.HC

		def value = params.long('value')
		def employee = Employee.get(userId)
		def period = Period.findByYear(year)
		log.error('employeeId: '+employee.id)
		
		def criteria
		criteria = SupplementaryTime.createCriteria()
		def supplementaryOccurence = criteria.get{
			and {
				eq('employee',employee)
				eq('period',period)
				eq('type',type)
			}
		}
		
		if (supplementaryOccurence != null){
			supplementaryOccurence.value=value
			supplementaryOccurence.save()
			//update value
		}else{
			SupplementaryTime ST = new SupplementaryTime()
			ST.employee = employee
			ST.loggingTime = new Date()
			ST.value = value
			ST.type = type
			ST.period = period			
			ST.user =  springSecurityService.currentUser
			ST.save()

		}
		def data = supplementaryTimeService.getAllSupAndCompTime(employee)
		def model = [employeeInstance: employee] << data
		render template: "/employee/template/paidHSEditTemplate", model: tasks model
		return
		
	}
	
	def trigger (){
		log.error("I HAVE BEEN TRIGGERED!!")

		SimpleDateFormat dateFormat = new SimpleDateFormat('d/MM/yyyy');
		def startDate= dateFormat.parse('22/05/2013')
		
		def endDate= dateFormat.parse('30/06/2014')
		
		timeManagerService.openDaysBetweenDates(startDate,endDate)
		
		return
	}
	
	def changeContractStatus (){
		def employeeId = params['employeeId']
		def statusId = params['updatedSelection']
		def employee = Employee.get(employeeId)
		def status = employee.status
		SimpleDateFormat dateFormat = new SimpleDateFormat('d/MM/yyyy');
		
		 
		if (params["newDate"] != null && params["newDate"].size() > 0){
			status.date = dateFormat.parse(params["newDate"])
		}else{
			for (StatusType type : StatusType.values()) {
				log.error('current enum eval': type)
				if (statusId.equals(type.toString())){
					log.error('changing contract status to: '+type)
					status.type = type
					status.loggingDate = new Date()
					if (statusId.equals('A')){
						log.error('setting the date to null')
						status.date = null
					}else {
						log.error('setting the date to new Date()')
						status.date = new Date()	
					}
				}
			}		
		}
		status.save(flush:true)
		
		render template: '/employee/template/contractStatus', model:[employeeInstance:employee]
		return
	}
	
	
	def sendMail (){	
		mailService.sendMail {
			to "henri.martin@gmail.com"
			from "pointeuse@biolab33.com"
			subject "Hello Fred"
			body 'this is some text'
		 }
		log.error('mail sent')
	  }
	
	def removeCSS(){
		def criteria
		def entryDate
		def employeeList = Employee.findAll("from Employee")
		for (Employee employee: employeeList){
		//	log.error('employee: '+employee)
			entryDate = employee.arrivalDate
			criteria = Absence.createCriteria()			
			log.error('employee entry date: '+entryDate)
			def sansSoldeList = criteria.list {
				and {
					eq('employee',employee)
					eq('year',entryDate.getAt(Calendar.YEAR))
					eq('month',entryDate.getAt(Calendar.MONTH)+1)
					lt('date',entryDate)
					eq('type',AbsenceType.CSS)
				}
			}
			if (sansSoldeList.size()>0){
				log.error('sans solde is not null= '+sansSoldeList+ 'for employee: '+employee)
				for (def sansSolde:sansSoldeList){
					log.error('removing sansSolde: '+sansSolde.date)
					sansSolde.delete(flush:true)
				}
			}
		}
	}
	
	def getUser(){
		def cal = Calendar.instance
		def currentDate = cal.time
		def userName = params['username']
		def isOutSideSite=params["isOutSideSite"].equals("true") ? true : false
		def employee = Employee.findByUserName(userName)
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).time
		def timeDiff
		def entranceStatus = false
		def type = "E"
		def criteria = InAndOut.createCriteria()
		def lastIn = criteria.get {
			and {
				eq('employee',employee)
				eq('pointed',false)
				eq('day',today.getAt(Calendar.DATE))
				eq('month',today.getAt(Calendar.MONTH)+1)
				eq('year',today.getAt(Calendar.YEAR))
				order('time','desc')
			}
			maxResults(1)
		}
		
		if (lastIn != null){
			def LIT = lastIn.time
			use (TimeCategory){timeDiff=currentDate-LIT}
			//empecher de represser le bouton pendant 30 seconds
			if ((timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600)<30){
				log.error('time between logs is not sufficient')
				response.status = 504;
				return
			}
			type=lastIn.type.equals("S") ? "E" :"S"
			entranceStatus = lastIn.type.equals("S") ? true : false
		}
		def inOrOut = timeManagerService.initializeTotals(employee,currentDate,type,null,isOutSideSite)
		def jsonEmployee = new JSONEmployee(employee,entranceStatus)
		def jsonInAndOut = new JSONInAndOut(inOrOut,jsonEmployee)
		jsonEmployee.inOrOuts.add(jsonInAndOut)
		response.contentType = "application/json"
		render jsonEmployee as JSON
		//return 'OK'
	}
	
	def logEmployee(){
		def cal = Calendar.instance
		def currentDate = cal.time
		def userName = params['username']
		def isOutSideSite=params["isOutSideSite"].equals("true") ? true : false
		def employee = Employee.findByUserName(userName)
		def timeDiff
		def type = "E"
		
		if (employee == null){
			flash.message = message(code: 'employee.not.found.label', args:[userName])
			render template: "/employee/template/entryValidation", model: [employee: null,inOrOut:null,flash:flash]
			return
		}
		
		def criteria = InAndOut.createCriteria()
		def lastIn = criteria.get {
			and {
				eq('employee',employee)
				eq('pointed',false)
				eq('day',cal.get(Calendar.DATE))
				eq('month',cal.get(Calendar.MONTH)+1)
				eq('year',cal.get(Calendar.YEAR))
				order('time','desc')
			}
			maxResults(1)
		}
		
		if (lastIn != null){
			def LIT = lastIn.time
			use (TimeCategory){timeDiff=currentDate-LIT}
			//empecher de represser le bouton pendant 30 seconds
			if ((timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600)<30){
				flash.message = message(code: 'employee.overlogging.error')
				log.error('time between logs is not sufficient')
				render template: "/employee/template/entryValidation", model: [employee: employee,inOrOut:null,flash:flash]
				return
			}
			type=lastIn.type.equals("S") ? "E" :"S"
		}
		def inOrOut = timeManagerService.initializeTotals(employee,currentDate,type,null,isOutSideSite)
		render template: "/employee/template/entryValidation", model: [employee: employee,inOrOut:inOrOut,flash:null]
		return
	}
	
	
	def getEmployees() {
		params.sort='site'
		def username=params["username"]
		def password=params["password"]
		def employeeInstanceList
		def site
		def siteId=params["site"]
		byte[] hash
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		def hashAsString
		def user
		
		
		if (username == null){
			log.error('authentication failed!')
			response.status = 401;
			return
		}
		else{
			user = User.findByUsername(username)
		}
		
		if (password == null){
			log.error('authentication failed!')
			response.status = 401;
			return
		}
		else{
			hash = digest.digest(password.getBytes("UTF-8"));
			hashAsString = hash.encodeHex()
		}
		
		if (user != null && !user.password.equals(hashAsString.toString())){
			log.error('authentication failed!')
			response.status = 401;
			return
		}
		
		if (params["site"]!=null && !params["site"].equals('') && !params["site"].equals('[id:]')){
			site = Site.get(params.int('site'))
			if (site != null)
				siteId=site.id
		}
		if (params["siteId"]!=null && !params["siteId"].equals("")){
			site = Site.get(params.int('siteId'))
			if (site != null)
				siteId=site.id
		}

		if (site!=null){
			employeeInstanceList = Employee.findAllBySite(site)
		}else{
			employeeInstanceList=Employee.list(params)
		}
	//	response.contentType = "application/json"
		render employeeInstanceList// as JSON
	}
	
	def updateUsername(Long id, Long version) {		
		def employeeInstance = Employee.get(id)
		if (!employeeInstance) {
			return
		}
		def employeeStatus = employeeInstance.status	
		log.error('employee status: '+employeeInstance.status)	
		employeeInstance.userName = params['username']
		employeeInstance.save(flush: true)		
		render employeeInstance
	}
}
