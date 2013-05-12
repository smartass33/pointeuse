package pointeuse

import grails.plugins.springsecurity.Secured
import org.apache.log4j.Logger
import org.springframework.dao.DataIntegrityViolationException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar

import groovy.time.TimeCategory
import groovy.sql.Sql
import groovy.time.TimeDuration

class EmployeeController {

	def pdfRenderingService
	def searchableService
	def authenticateService
	def springSecurityService
	def timeManagerService 
	def dataSource
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	long secondInMillis = 1000;
	long minuteInMillis = secondInMillis * 60;
	long hourInMillis = minuteInMillis * 60;
	long dayInMillis = hourInMillis * 24;
	long yearInMillis = dayInMillis * 365;
	Logger log = Logger.getInstance(EmployeeController.class)
	

    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
		def employeeInstanceList
		def employeeInstanceTotal
		def site
		def siteId=params["siteId"]
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
		
		if (params["site"]!=null && !params["site"].equals('')){
			site = Site.get(params["site"] as int)
			siteId=site.id
		}	
		if (siteId!=null && !siteId.equals("")){
			site = Site.get(siteId as int)
		}		
		
		def user = springSecurityService.currentUser 
		def username = user?.getUsername()
        params.max = Math.min(max ?: 20, 100)
		if (site!=null){
			employeeInstanceList = Employee.findAllBySite(site)		
			employeeInstanceTotal = employeeInstanceList.size()
			render template: "/common/listEmployeeTemplate", model:[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceTotal,username:username,isAdmin:isAdmin,siteId:siteId,site:site]
			return
		}else{
			if (params["site"]!=null && params["site"].equals('')){
				employeeInstanceList=Employee.list(params)
				employeeInstanceTotal = employeeInstanceList.totalCount		
				render template: "/common/listEmployeeTemplate", model:[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceTotal,username:username,isAdmin:isAdmin,siteId:null,site:null]
				return
			}
		}
		employeeInstanceList=Employee.list(params)
		employeeInstanceTotal = employeeInstanceList.totalCount
		[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceTotal,username:username,isAdmin:isAdmin,siteId:siteId,site:site]

    }
	
    def create() {
		def service = params["employee.service.id"]
		def employeeInstance =new Employee(params)
		employeeInstance.service=Service.get(service)
        [employeeInstance: employeeInstance]
    }

    def save() {
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
		def service = params["employee.service.id"]
		def site = params["employee.site.id"]
		def employeeInstance =new Employee(params)
		employeeInstance.service=Service.get(service)
		employeeInstance.site=Site.get(site)
        if (!employeeInstance.save(flush: true)) {
            render(view: "create", model: [employeeInstance: employeeInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'employee.label', default: 'Employee'), employeeInstance.id])
        redirect(action: "show", id: employeeInstance.id,params: [isAdmin: isAdmin])
    }
	
	def search = {
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
		def query = "*"+params.q+"*"
		if(query){
			def srchResults = searchableService.search(query)
			render(view: "list",
				   model: [employeeInstanceList: srchResults.results,
						 contactInstanceTotal:srchResults.total,employeeInstanceTotal:srchResults.results.size(),isAdmin:isAdmin])
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

    def edit(Long id) {
		def isAdmin = (params["isAdmin"] != null  && params["isAdmin"].equals("true")) ? true : false
        def employeeInstance = Employee.get(id)
		def siteId=params["siteId"]
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }
        [employeeInstance: employeeInstance,isAdmin:isAdmin,siteId:siteId]	
    }

	
	def yearlyCartouche(long userId,int year,int month){
		if (userId == null){
			userId = params["employeeId"].getAt(0)
		}
		def employeeInstance = Employee.get(userId)
	
			
		if (month>5){
			year=year+1
		}
		
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		//calendar.set(Calendar.DAY_OF_MONTH,1)
		def yearlyCounter = 0
		
		// set the date end of may
		calendar.set(Calendar.MONTH,4)
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))  
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)	
		def maxDate = calendar.time		
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)		
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def minDate = calendar.time
		
		def criteria = Absence.createCriteria()
		
		// get cumul holidays
		def yearlyHolidays = criteria.list {
			and {
				eq('employee',employeeInstance)
				gt('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.VACANCE)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlyRtt = criteria.list {
			and {
				eq('employee',employeeInstance)
				gt('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.RTT)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlySickness = criteria.list {
			and {
				eq('employee',employeeInstance)
				gt('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.MALADIE)
			}
		}
		
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employeeInstance)
				gt('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlySansSolde = criteria.list {
			and {
				eq('employee',employeeInstance)
				gt('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.CSS)
			}
		}
		
		
		criteria = MonthlyTotal.createCriteria()
		def monthsAggregate = criteria.list{
			and {
				eq('employee',employeeInstance)
				ge('month',5)
				eq('year',year-1)
			}
		}
		
		def totalTime = 0
		for (MonthlyTotal monthIter:monthsAggregate){
			totalTime += monthIter.elapsedSeconds
		}
		
		criteria = MonthlyTotal.createCriteria()
		monthsAggregate = criteria.list{
			and {
				eq('employee',employeeInstance)
				lt('month',5)
				eq('year',year)
			}
		}
		
		for (MonthlyTotal monthIter:monthsAggregate){
			totalTime += monthIter.elapsedSeconds
		}
		
		// count sundays during the period:
		// do it in 2 loops: year-1 then year
		while(calendar.getAt(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			if (calendar.getAt(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				yearlyCounter ++
			}
		
			if (calendar.getAt(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
				break
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		
		calendar.set(Calendar.DAY_OF_YEAR,1)
		calendar.set(Calendar.YEAR,year)

		def endPeriodCalendar = Calendar.instance
		
		endPeriodCalendar.set(Calendar.MONTH,4)
		endPeriodCalendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

		while(calendar.getAt(Calendar.DAY_OF_YEAR) <= endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
			if (calendar.getAt(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				yearlyCounter ++
			}
		
			if (calendar.getAt(Calendar.DAY_OF_YEAR) == endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
				break
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		
		 def bankHolidayCounter=0
		criteria = BankHoliday.createCriteria()
		def bankHolidayList = criteria.list{
			and {
				gt('month',5)
				eq('year',year-1)
			}
		}

		for (BankHoliday bankHoliday:bankHolidayList){
			if (bankHoliday.calendar.getAt(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				bankHolidayCounter ++
			}
		}
		criteria = BankHoliday.createCriteria()
		
		bankHolidayList = criteria.list{
			and {
				lt('month',6)
				eq('year',year)
			}
		}
		
		for (BankHoliday bankHoliday:bankHolidayList){
			if (bankHoliday.calendar.getAt(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				bankHolidayCounter ++
			}
		}
		yearlyCounter -= bankHolidayCounter	
		def yearlyPregnancyCredit=30*60*pregnancy.size()	
		def yearTheoritical=(3600*(yearlyCounter*employeeInstance.weeklyContractTime/Employee.WeekOpenedDays+Employee.Pentecote-(WeeklyTotal.WeeklyLegalTime/Employee.WeekOpenedDays)*(yearlySickness.size()+yearlyHolidays.size()+yearlySansSolde.size())) - yearlyPregnancyCredit)as int	
		return [yearlyCounter ,yearlyHolidays.size(),yearlyRtt.size(),yearlySickness.size(),yearTheoritical,yearlyPregnancyCredit,totalTime,yearlySansSolde.size()]
	}
	
	
	def cartouche(long userId,int year,int month){
		if (userId == null){
			userId = params["employeeId"].getAt(0)
		}
		
		def calendar = Calendar.instance
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		def counter = 0
		
		// count sundays within given month
		while(calendar.getAt(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			if (calendar.getAt(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				counter ++
			}	
			if (calendar.getAt(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendar.roll(Calendar.DAY_OF_MONTH, 1)
		}	
		
		// count bank holiday
		def holidayList = BankHoliday.findAllByMonthAndYear(month,calendar.getAt(Calendar.YEAR))
		def holidayCounter = 0
		for (BankHoliday holiday:holidayList){
			if (holiday.calendar.getAt(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				holidayCounter++
			}
		}
				
		counter -= holidayCounter		
		def employeeInstance = Employee.get(userId)
		def criteria = Absence.createCriteria()
		
		// get cumul holidays
		def holidays = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.VACANCE)
			}
		}
		
		// get cumul RTT
		criteria = Absence.createCriteria()
		def rtt = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.RTT)
			}
		}
		// get cumul sickness
		criteria = Absence.createCriteria()
		def sickness = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MALADIE)
			}
		}	
		
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		
		criteria = Absence.createCriteria()
		def sansSolde = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.CSS)
			}
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		
		def supTotals=timeManagerService.computeSupTime(calendar,employeeInstance)
		def payableSupTime=timeManagerService.computeHumanTime(supTotals.get(0))
		def payableCompTime=timeManagerService.computeHumanTime(supTotals.get(1))
		def yearlyCartouche=yearlyCartouche(userId,year,month)		
		def pregnancyCredit=30*60*pregnancy.size()
		// determine monthly theoritical time:
		def monthTheoritical=(3600*(counter*employeeInstance.weeklyContractTime/Employee.WeekOpenedDays+Employee.Pentecote-(WeeklyTotal.WeeklyLegalTime/Employee.WeekOpenedDays)*(sickness.size()+holidays.size()+sansSolde.size())) - pregnancyCredit)as int
		return [params.userId,employeeInstance,calendar,counter ,holidays.size(),rtt.size(),sickness.size(),sansSolde.size(),monthTheoritical,pregnancyCredit,yearlyCartouche.get(0),yearlyCartouche.get(1),yearlyCartouche.get(2),yearlyCartouche.get(3),yearlyCartouche.get(4),yearlyCartouche.get(5),yearlyCartouche.get(6),yearlyCartouche.get(7),payableSupTime,payableCompTime]		
	}
	
	def modifyAbsence(){
		def employeeId = params["employeeId"].getAt(0)
		def employee = Employee.get(employeeId)
		def day = params["day"].getAt(0)
		def criteria
		def updatedSelection = params["updatedSelection"].toString()
		SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
		Date date = dateFormat.parse(day)
		def cal= Calendar.instance
		cal.time=date
		if (!updatedSelection.equals('-') && !updatedSelection.equals('')){
			// check if an absence was already logged:
			criteria = Absence.createCriteria()
			
			// get cumul holidays
			def absence = criteria.get {
				and {
					eq('employee',employee)
					eq('year',cal.get(Calendar.YEAR))
					eq('month',cal.get(Calendar.MONTH)+1)
					eq('day',cal.getAt(Calendar.DAY_OF_MONTH))
				}
			}
	
			if (absence != null){
				if (updatedSelection.equals(AbsenceType.ANNULATION.key)){
					// annulation nŽcessaire: il faut effacer le tupple
					absence.delete(flush: true)
				}else{
					absence.type=updatedSelection
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
					absence.save(flush: true)
				}
			}
		}else{
			flash.message=message(code: 'absence.impossible.update')
		}
		def cartoucheTable = cartouche(employee.id,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1)	
		def workingDays=cartoucheTable.get(3)
		def holiday=cartoucheTable.get(4)
		def rtt=cartoucheTable.get(5)
		def sickness=cartoucheTable.get(6)
		def sansSolde=cartoucheTable.get(7)
		def monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(8))
		def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(9))
		def yearlyHoliday=cartoucheTable.get(11)
		def yearlyRtt=cartoucheTable.get(12)
		def yearlySickness=cartoucheTable.get(13)
		def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(14))
		def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(15))
		def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get(16))
		def yearlySansSolde=cartoucheTable.get(17)
		def payableSupTime=cartoucheTable.get(18)
		def payableCompTime=cartoucheTable.get(19)
		def openedDays = timeManagerService.computeMonthlyHours(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1)

		criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('year',cal.getAt(Calendar.YEAR))
					eq('month',cal.getAt(Calendar.MONTH)+1)
				}
			}
		
		if (monthlyTotal==null){
			def totals=timeManagerService.initializeTotals(employee, cal.time)
			monthlyTotal=totals.get(2)
		}
		def yearInf
		def yearSup
		if ((cal.get(Calendar.MONTH)+1)>4){
			yearInf=cal.get(Calendar.YEAR)
			yearSup=cal.get(Calendar.YEAR)+1
		}else{
			yearInf=cal.get(Calendar.YEAR)-1
			yearSup=cal.get(Calendar.YEAR)
		}
		monthlyTotal=timeManagerService.computeHumanTime(monthlyTotal.elapsedSeconds)
		
		def model=[monthlyTotalRecap:monthlyTotal,yearInf:yearInf,yearSup:yearSup,employee:employee,openedDays:openedDays,workingDays:workingDays,holiday:holiday,rtt:rtt,sickness:sickness,sansSolde:sansSolde,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyHoliday:yearlyHoliday,yearlyRtt:yearlyRtt,yearlySickness:yearlySickness,yearlyTheoritical:yearlyTheoritical,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyActualTotal:yearlyActualTotal,yearlySansSolde:yearlySansSolde,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
		render template: "/common/cartoucheTemplate", model:model
		return
	}
	
	def addingEventToEmployee(Long id){
		def cal = Calendar.instance	
		def type = params["type"]
		if (type.equals("Entrer")){
			type="E"
		}
		if (type.equals("Sortir")){
			type="S"
		}
		def userId = params["userId"]
		def dailyTotal
		def currentDate = cal.time
		def employeeInstance
		def weeklyTotal
		def monthlyTotal
		def criteria
		def timeDiff
		
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).getTime()
		if (id == null){
			employeeInstance = Employee.get(userId)		
		}else{
			employeeInstance = Employee.get(id)
		}
			
		// liste les entrees de la journŽe et vŽrifie que cette valeur n'est pas supŽrieure ˆ une valeur statique
		def inAndOutcriteria = InAndOut.createCriteria()
		def todayEmployeeEntries = inAndOutcriteria.list {
			and {
				eq('employee',employeeInstance)
				eq('type','E')
				gt('time',today)
			}
		}
		//def todayEmployeeEntries = InAndOut.findAllByTimeGreaterThanAndType(today,'E')
		if (todayEmployeeEntries.size() > Employee.entryPerDay){
			flash.message = "TROP D'ENTREES DANS LA JOURNEE. POINTAGE NON PRIS EN COMPTE"
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
			//empecher de represser le bouton pendant 5 min
			if ((timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600)<60){
				flash.message = message(code: 'employee.overlogging.error')
				redirect(action: "pointage", id: employeeInstance.id)
				return
			}
		}
		
		// initialisation
		def inOrOut = timeManagerService.initializeTotals(employeeInstance,currentDate,type,null)
		
		// l'employee effectue une sortie: il faut calculer le temps passŽ depuis la derniere entrŽe
		if (type.equals("S")){					
			if (lastIn != null){
				inOrOut=timeManagerService.addExtraTime(inOrOut, timeDiff)
				// le salariŽ est ˆ temps partiel: il faut calculer les heures complŽmentaires
				if (employeeInstance.weeklyContractTime != 35){
					timeManagerService.computeComplementaryTime(inOrOut.dailyTotal)
				}else{
					timeManagerService.computeSupplementaryTime(inOrOut.dailyTotal)
				}
				lastIn.pointed=true
			}
		}
		//def inOrOut = new InAndOut(employeeInstance, currentDate,type,dailyTotal)
		if (type.equals("S")){employeeInstance.status=false}
		else {employeeInstance.status=true}
		if (type.equals("E")){
			flash.message = message(code: 'inAndOut.create.label', args: [message(code: 'inAndOut.entry.label', default: 'exit'), cal.time])
		}else{
			flash.message = message(code: 'inAndOut.create.label', args: [message(code: 'inAndOut.exit.label', default: 'exit'), cal.time])
		}
		redirect(action: "pointage", id: employeeInstance.id)
		return
	}
	

    def update(Long id, Long version) {
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
		
        employeeInstance.properties = params
		if (params["weeklyContractTime"] != null){
			employeeInstance.weeklyContractTime = params["weeklyContractTime"] as float
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
		
        if (!employeeInstance.save(flush: true)) {
            render(view: "edit", model: [employeeInstance: employeeInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'employee.label', default: 'Employee'), employeeInstance.userName])
        redirect(action: "show", id: employeeInstance.id,params: [isAdmin: isAdmin,siteId:siteId])
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
	
	def trash(){	
		def eventId=params["inOrOutId"] //as int
		def inOrOut = InAndOut.get(eventId)
		def extraTime = new TimeDuration(0,0,0,0)
		def criteria = InAndOut.createCriteria()
		def calendar = Calendar.instance
		calendar.time=inOrOut.time
		
		def nextInOrOut = criteria.get {
			and {
				eq('employee',inOrOut.employee)                                    
				eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
				eq('month',calendar.getAt(Calendar.MONTH)+1)
				eq('year',calendar.getAt(Calendar.YEAR))
				gt('time',inOrOut.time)
				order('time','asc')
				
			}
			maxResults(1)
		}
		criteria = InAndOut.createCriteria()
		def previousInOrOut = criteria.get {
			and {
				eq('employee',inOrOut.employee)
				eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
				eq('month',calendar.getAt(Calendar.MONTH)+1)
				eq('year',calendar.getAt(Calendar.YEAR))
				lt('time',inOrOut.time)
				order('time','desc')
					
			}
			maxResults(1)
		}
		
		//case: in between Entry and Exit
		if ((previousInOrOut != null &&previousInOrOut.type.equals('E')) && (nextInOrOut != null && nextInOrOut.type.equals('S'))){
			// add time between previous and current
			if (inOrOut.type.equals('E')){
				use (TimeCategory){extraTime=inOrOut.time-previousInOrOut.time}
			}
			//add time between next and current
			else {
				use (TimeCategory){extraTime=nextInOrOut.time-inOrOut.time}
			}
			log.error('adding '+extraTime+ 's to dailyTotal')			
		}
		
		// case: entry between exits:
		if (inOrOut.type.equals('E') && (previousInOrOut != null &&previousInOrOut.type.equals('S')) && (nextInOrOut != null && nextInOrOut.type.equals('S'))){
			// add time between previous and current
			use (TimeCategory){extraTime=inOrOut.time-nextInOrOut.time}
			log.error('removing '+extraTime+ 's to dailyTotal')
		}
			
		// first event of the day
		if (previousInOrOut==null){
			if (inOrOut.type.equals('E')){
				if (nextInOrOut!=null && nextInOrOut.type.equals('S')){
					use (TimeCategory){extraTime=inOrOut.time-nextInOrOut.time}		
					log.error('removing '+extraTime+ ' s to dailyTotal')
					
				}
			}
		}
		//last event of the day
		if (nextInOrOut==null){
			if (inOrOut.type.equals('S') && !inOrOut.systemGenerated){
				if (previousInOrOut!=null && previousInOrOut.type.equals('E')){
					use (TimeCategory){extraTime=previousInOrOut.time-inOrOut.time}
					log.error('removing '+extraTime+ ' s to dailyTotal')
					
				}
			}
		}
		
		inOrOut=timeManagerService.addExtraTime(inOrOut,extraTime)

		if (inOrOut.employee.weeklyContractTime != 35){
			timeManagerService.computeComplementaryTime(inOrOut.dailyTotal)
		}else{
			timeManagerService.computeSupplementaryTime(inOrOut.dailyTotal)
		}
		
		
		log.error('removing entry '+inOrOut)
		def employee = inOrOut.employee
		inOrOut.delete(flush:true)
		
		criteria = InAndOut.createCriteria()
		def inAndOutList = criteria.list {
			and {
				eq('employee',employee)
				eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('year',calendar.get(Calendar.YEAR))
				order('time','asc')
			}
		}
		
	//	def reportModel=report(inOrOut.employee.id, calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR))
		render template: "/common/listInAndOutsTemplate", model: [inAndOutList:inAndOutList,day:calendar.getAt(Calendar.DAY_OF_MONTH),month:calendar.getAt(Calendar.MONTH)+1,year:calendar.getAt(Calendar.YEAR)]
	//	render template: "/common/reportTableTemplate", model: reportModel
		
	}
	
	def showDay(){
		def day=params["day"] as int
		def month=params["month"] as int
		def year=params["year"] as int
		def employeeId=params["employeeId"]
		def employee = Employee.get(employeeId)
		def criteria = InAndOut.createCriteria()
		def inAndOutList = criteria.list {
			and {
				eq('employee',employee)
				eq('day',day)
				eq('month',month)
				eq('year',year)
				order('time','asc')
			}
		}
		[inAndOutList:inAndOutList,day:day,month:month,year:year]
	}
	
	def modifyTime(){
		def idList=params["inOrOutId"]
		def timeList=params["time"]
		def dayList=params["day"]
		def monthList=params["month"]
		def yearList=params["year"]
		def employeeId=params["employee.id"]
		def employee = Employee.get(employeeId)
		def newTimeList=params["cell"]
		def fromRegularize=params["fromRegularize"].equals("true") ? true : false
		def month=monthList[0]
		def year=yearList[0]
		
		if (idList==null){
			log.error("list is null")
			def retour = report(employee.id as long,month as int,year as int)
			render(view: "report", model: retour)
			
		}
		try{
			timeManagerService.timeModification( idList, timeList, dayList, monthList, yearList, employee, newTimeList, fromRegularize)
		}catch(PointeuseException ex){
			flash.message = message(code: ex.message)
			def back = report(employee.id as int,month as int,year as int)
			render(view: "report", model: back)
			return
		}

		
		if (fromRegularize){
			redirect(action: "pointage", id: employee.id)
		}else{
		def retour = report(employee.id as long,month as int,year as int)
		render(view: "report", model: retour)
		}
	}
	
	
	def annualReport(Long userId){
		def calendar = Calendar.instance
		def employee = Employee.get(userId)
		def criteria
		def weeklyTotal
		def mapByWeek =[:]
		def myDate = params["myDate"]
		if (myDate != null){
			calendar.time=myDate
		}
		def year=calendar.getAt(Calendar.YEAR)
		calendar.set(Calendar.WEEK_OF_YEAR,1)
		
		
		while(calendar.getAt(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			criteria = WeeklyTotal.createCriteria()
			weeklyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('week',calendar.getAt(Calendar.WEEK_OF_YEAR))
					eq('year',calendar.getAt(Calendar.YEAR))
				}
			}
			if (weeklyTotal !=null){
				def elapsedTime = timeManagerService.computeHumanTime(weeklyTotal.elapsedSeconds)
				mapByWeek.put(calendar.getAt(Calendar.WEEK_OF_YEAR), elapsedTime)		
			}else{
				mapByWeek.put(calendar.getAt(Calendar.WEEK_OF_YEAR), timeManagerService.computeHumanTime(0))
			}
			
			if (calendar.getAt(Calendar.WEEK_OF_YEAR) == calendar.getActualMaximum(Calendar.WEEK_OF_YEAR)){
				break
			}	
		calendar.roll(Calendar.WEEK_OF_YEAR, 1)
		}	
		[employee:employee,mapByWeek:mapByWeek,period:year,userId:userId]	
	}
	
	def recomputeSupTime(){
		def employees = Employee.list()
		def dailyTotals
		for (Employee employee:employees){				
			dailyTotals = DailyTotal.findAllByEmployee(employee)
			for (DailyTotal dailyTotal: dailyTotals){
				def weeklyTotal = dailyTotal.weeklyTotal
				if (weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){		
					weeklyTotal.supplementarySeconds = weeklyTotal.elapsedSeconds-WeeklyTotal.maxWorkingTime		
				}else {
					weeklyTotal.supplementarySeconds=0
				}
				if (dailyTotal.elapsedSeconds > DailyTotal.maxWorkingTime){
					dailyTotal.supplementarySeconds = dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
				}else {
					dailyTotal.supplementarySeconds=0
				}
			}
		}
		flash.message = "heures sup recalculees"
		redirect(action: "list")
	}
	
	
	private  recomputeSupTimeByEmployee(Employee employee){
		def dailyTotals
		dailyTotals = DailyTotal.findAllByEmployee(employee)
		for (DailyTotal dailyTotal: dailyTotals){
			def weeklyTotal = dailyTotal.weeklyTotal
			if (weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){
				weeklyTotal.supplementarySeconds = weeklyTotal.elapsedSeconds-WeeklyTotal.maxWorkingTime
			}else {
				weeklyTotal.supplementarySeconds=0
			}
			if (dailyTotal.elapsedSeconds > DailyTotal.maxWorkingTime){
				dailyTotal.supplementarySeconds = dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
			}else {
				dailyTotal.supplementarySeconds=0
			}
		}
	
	}
	
	def reportLight(Long userId,int monthPeriod,int yearPeriod){
		def siteId=params["siteId"]
		def calendar = Calendar.instance
		def weekName="semaine "
		def weeklyTotalTime = [:]
		def weeklySuppTotalTime = [:]
		def weeklyCompTotalTime = [:]
		def weeklyTotalTimeByEmployee = [:]
		def weeklySupTotalTimeByEmployee = [:]
		def weeklyCompTotalTimeByEmployee = [:]
		def monthlyTotalTimeByEmployee = [:]
		def weeklyAggregate = [:]
		def dailyTotalMap = [:]
		def dailySupTotalMap = [:]
		def holidayMap = [:]
		def previousDay
		def employee
		def mapByDay = [:]
		def weeklyTotalMinutes = 0
		def i=1
		def dailyTotalId=0
		def myDate = params["myDate"]
		def monthlySupTime = 0
		def monthlyCompTime = 0
		
		
		if (myDate != null && myDate instanceof String){
			SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)
		}

			
		if (userId==null){
			userId = params["userId"]
		}
		employee = Employee.get(userId)
	
		//get last day of the month
		if (myDate==null){
			if (yearPeriod!=0){
				calendar.set(Calendar.MONTH,monthPeriod-1)
				calendar.set(Calendar.YEAR,yearPeriod)
				
			}
			calendar.set(Calendar.MONTH,calendar.getAt(Calendar.MONTH))
			calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		}else{
			calendar.time=myDate
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		def calendarLoop = calendar
		int month = calendar.getAt(Calendar.MONTH) // starts at 0
		int year = calendar.getAt(Calendar.YEAR)
		calendarLoop.getTime().clearTime()
		
		while(calendarLoop.getAt(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			// Žlimine les dimanches du rapport
			if (calendarLoop.getAt(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
				weeklyTotalMinutes = 0
				mapByDay = [:]
				i++
			//	calendarLoop.roll(Calendar.DAY_OF_WEEK,1)
			}
			//print calendarLoop.time
			def criteria = DailyTotal.createCriteria()
			def dailyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('day',calendarLoop.getAt(Calendar.DAY_OF_MONTH))
					eq('month',month+1)
					eq('year',year)
				}
			}
			// permet de rŽcupŽrer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId && dailyTotal.weeklyTotal.elapsedSeconds > 0){
				weeklyTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), timeManagerService.computeHumanTime(dailyTotal.weeklyTotal.elapsedSeconds))
				if (dailyTotal.weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){
					weeklySuppTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), dailyTotal.weeklyTotal.supplementarySeconds)
				}else {
					def dailyTotalsOfWeek = dailyTotal.weeklyTotal.dailyTotals
					def supTime=0
					for (DailyTotal dailyTotalSup:dailyTotalsOfWeek){
						supTime += dailyTotalSup.supplementarySeconds
					}
									
					weeklySuppTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR),supTime)
				}
				weeklySupTotalTimeByEmployee.put(employee,weeklySuppTotalTime)
				
				
				if (dailyTotal.weeklyTotal.complementarySeconds > 0){
					weeklyCompTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), dailyTotal.weeklyTotal.complementarySeconds)
					weeklyCompTotalTimeByEmployee.put(employee,weeklyCompTotalTime)
					
				}
				weeklyTotalTimeByEmployee.put(employee,weeklyTotalTime)
				dailyTotalId=dailyTotal.id
			}

			criteria = InAndOut.createCriteria()
			def entriesByDay = criteria{
			and {
				eq('employee',employee)
				eq('day',calendarLoop.getAt(Calendar.DATE))
				eq('month',month+1)
				eq('year',year)
				order('time')
				}
			}
			// put in a map in and outs
			def tmpDate = calendarLoop.getTime()
			if 	(entriesByDay.size()>0){
				if (dailyTotal!=null){
					dailyTotalMap.put(tmpDate, timeManagerService.computeHumanTime(dailyTotal.elapsedSeconds))
					dailySupTotalMap.put(tmpDate, timeManagerService.computeHumanTime(dailyTotal.supplementarySeconds))
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
			////////////
			// retrieve potential sick days and store in map
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
			///////////
			weeklyAggregate.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), mapByDay)
			if (calendarLoop.getAt(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}
		
		def monthlyTotalCriteria = MonthlyTotal.createCriteria()
		def monthlyTotal = monthlyTotalCriteria.get{
			and {
				eq('employee',employee)
				eq('year',calendarLoop.get(Calendar.YEAR))
				eq('month',calendarLoop.get(Calendar.MONTH)+1)
			}
		}
		
		if (monthlyTotal!=null){
			monthlyTotalTimeByEmployee.put(employee, timeManagerService.computeHumanTime(monthlyTotal.elapsedSeconds))
		}
		try {
			if (userId != null){
			def cartoucheTable = cartouche(userId,year,month+1)
			def openedDays = timeManagerService.computeMonthlyHours(year,month)
			def monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(8))
			def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(14))
			def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(15))
			def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get(16))
			
			def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(9))
			def yearInf
			def yearSup
			if (month>4){
				yearInf=year
				yearSup=year+1
			}else{
				yearInf=year-1
				yearSup=year
			}
			weeklySuppTotalTime.each() { key, value ->
				monthlySupTime += value
				weeklySuppTotalTime.put(key,timeManagerService.computeHumanTime(value))
			};
			weeklyCompTotalTime.each() { key, value ->
				//log.error(value)
				monthlyCompTime += value
				weeklyCompTotalTime.put(key,timeManagerService.computeHumanTime(value))
			};
			
			def payableSupTime = timeManagerService.computeHumanTime(monthlySupTime)
			def payableCompTime = timeManagerService.computeHumanTime(monthlyCompTime)
			
			
			[isAdmin:false,siteId:siteId,yearInf:yearInf,yearSup:yearSup,userId:userId,workingDays:cartoucheTable.get(3),holiday:cartoucheTable.get(4),rtt:cartoucheTable.get(5),sickness:cartoucheTable.get(6),sansSolde:cartoucheTable.get(7),yearlyActualTotal:yearlyActualTotal,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTheoritical:yearlyTheoritical,yearlyHoliday:cartoucheTable.get(11),yearlyRtt:cartoucheTable.get(12),yearlySickness:cartoucheTable.get(13),yearlySansSolde:cartoucheTable.get(17),yearlyTheoritical:yearlyTheoritical,period:calendar,monthlyTotal:monthlyTotalTimeByEmployee,weeklyTotal:weeklyTotalTimeByEmployee,weeklySupTotal:weeklySupTotalTimeByEmployee,weeklyCompTotal:weeklyCompTotalTimeByEmployee,dailySupTotalMap:dailySupTotalMap,dailyTotalMap:dailyTotalMap,month:month,year:year,period:calendarLoop.getTime(),dailyTotalMap:dailyTotalMap,holidayMap:holidayMap,weeklyAggregate:weeklyAggregate,employee:employee,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
					}
		}catch (NullPointerException e){
			log.error('error with application: '+e.toString())
		}
		
	}
	
	
	@Secured(['ROLE_ADMIN'])
	def report(Long userId,int monthPeriod,int yearPeriod){
		def siteId=params["siteId"]
		def calendar = Calendar.instance
		def weekName="semaine "
		def weeklyTotalTime = [:]
		def weeklySuppTotalTime = [:]
		def weeklyCompTotalTime = [:]
		def weeklyTotalTimeByEmployee = [:]
		def weeklySupTotalTimeByEmployee = [:]
		def weeklyCompTotalTimeByEmployee = [:]
		def monthlyTotalTimeByEmployee = [:]
		def weeklyAggregate = [:]
		def dailyTotalMap = [:]
		def dailySupTotalMap = [:]
		def holidayMap = [:]
		def previousDay
		def employee
		def mapByDay = [:]
		def weeklyTotalMinutes = 0
		def i=1
		def dailyTotalId=0
		def myDate = params["myDate"]
		def monthlySupTime = 0
		def monthlyCompTime = 0		
		
		
		if (myDate != null && myDate instanceof String){
			SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)			
		}

			
		if (userId==null){
			userId = params["userId"]
		}
		employee = Employee.get(userId)
	
		//get last day of the month
		if (myDate==null){
			if (yearPeriod!=0){
				calendar.set(Calendar.MONTH,monthPeriod-1)
				calendar.set(Calendar.YEAR,yearPeriod)
				
			}
			calendar.set(Calendar.MONTH,calendar.getAt(Calendar.MONTH))
			calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		}else{
			calendar.time=myDate
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		def calendarLoop = calendar
		int month = calendar.getAt(Calendar.MONTH) // starts at 0
		int year = calendar.getAt(Calendar.YEAR)
		calendarLoop.getTime().clearTime()
		
		while(calendarLoop.getAt(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			// Žlimine les dimanches du rapport
			if (calendarLoop.getAt(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
				weeklyTotalMinutes = 0
				mapByDay = [:]					
				i++
			}
			//print calendarLoop.time
			def criteria = DailyTotal.createCriteria()
			def dailyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('day',calendarLoop.getAt(Calendar.DAY_OF_MONTH)) 
					eq('month',month+1)
					eq('year',year)
				}
			}
			// permet de rŽcupŽrer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId && dailyTotal.weeklyTotal.elapsedSeconds > 0){
				weeklyTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), timeManagerService.computeHumanTime(dailyTotal.weeklyTotal.elapsedSeconds))			
				if (dailyTotal.weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){
					weeklySuppTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), dailyTotal.weeklyTotal.supplementarySeconds)
				}else {					
					def dailyTotalsOfWeek = dailyTotal.weeklyTotal.dailyTotals
					def supTime=0
					for (DailyTotal dailyTotalSup:dailyTotalsOfWeek){
						supTime += dailyTotalSup.supplementarySeconds
					}
									
					weeklySuppTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR),supTime)					
				}
				weeklySupTotalTimeByEmployee.put(employee,weeklySuppTotalTime)
				
				
				if (dailyTotal.weeklyTotal.complementarySeconds > 0){
					weeklyCompTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), dailyTotal.weeklyTotal.complementarySeconds)
					weeklyCompTotalTimeByEmployee.put(employee,weeklyCompTotalTime)
					
				}
				weeklyTotalTimeByEmployee.put(employee,weeklyTotalTime)
				dailyTotalId=dailyTotal.id
			} 

			criteria = InAndOut.createCriteria()
			def entriesByDay = criteria{
			and {
				eq('employee',employee)
				eq('day',calendarLoop.getAt(Calendar.DATE))
				eq('month',month+1)
				eq('year',year)
				order('time')
				}
			}
			// put in a map in and outs
			def tmpDate = calendarLoop.getTime()
			if 	(entriesByDay.size()>0){
				if (dailyTotal!=null){
					dailyTotalMap.put(tmpDate, timeManagerService.computeHumanTime(dailyTotal.elapsedSeconds))
					dailySupTotalMap.put(tmpDate, timeManagerService.computeHumanTime(dailyTotal.supplementarySeconds))
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
			////////////
			// retrieve potential sick days and store in map
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
			///////////	
			weeklyAggregate.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), mapByDay)	
			if (calendarLoop.getAt(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}	
		
		def monthlyTotalCriteria = MonthlyTotal.createCriteria()
		def monthlyTotal = monthlyTotalCriteria.get{
			and {
				eq('employee',employee)
				eq('year',calendarLoop.get(Calendar.YEAR))
				eq('month',calendarLoop.get(Calendar.MONTH)+1)
			}
		}
		
		if (monthlyTotal!=null){
			monthlyTotalTimeByEmployee.put(employee, timeManagerService.computeHumanTime(monthlyTotal.elapsedSeconds))
			monthlyTotal=timeManagerService.computeHumanTime(monthlyTotal.elapsedSeconds)
		}	
		try {
			if (userId != null){
			def cartoucheTable = cartouche(userId,year,month+1)
			def openedDays = timeManagerService.computeMonthlyHours(year,month)
			def workingDays=cartoucheTable.get(3)
			def holiday=cartoucheTable.get(4)
			def rtt=cartoucheTable.get(5)
			def sickness=cartoucheTable.get(6)
			def sansSolde=cartoucheTable.get(7)
			def monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(8))
			def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(9))
			def yearlyHoliday=cartoucheTable.get(11)
			def yearlyRtt=cartoucheTable.get(12)
			def yearlySickness=cartoucheTable.get(13)
			def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(14))
			def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(15))
			def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get(16))
			def yearlySansSolde=cartoucheTable.get(17)
	

			 
			
			def yearInf
			def yearSup
			if (month>4){
				yearInf=year
				yearSup=year+1
			}else{
				yearInf=year-1
				yearSup=year
			}
			weeklySuppTotalTime.each() { key, value -> 
				monthlySupTime += value
				weeklySuppTotalTime.put(key,timeManagerService.computeHumanTime(value))
			};
			weeklyCompTotalTime.each() { key, value ->
				monthlyCompTime += value
				weeklyCompTotalTime.put(key,timeManagerService.computeHumanTime(value))
			};
			
			def payableSupTime = timeManagerService.computeHumanTime(monthlySupTime)
			def payableCompTime = timeManagerService.computeHumanTime(monthlyCompTime)
			
			[monthlyTotalRecap:monthlyTotal,payableSupTime:payableSupTime,payableCompTime:payableCompTime,employee:employee,siteId:siteId,yearInf:yearInf,yearSup:yearSup,userId:userId,workingDays:workingDays,holiday:holiday,rtt:rtt,sickness:sickness,sansSolde:sansSolde,yearlyActualTotal:yearlyActualTotal,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTheoritical:yearlyTheoritical,yearlyHoliday:yearlyHoliday,yearlyRtt:yearlyRtt,yearlySickness:yearlySickness,yearlySansSolde:yearlySansSolde,yearlyTheoritical:yearlyTheoritical,period:calendar,monthlyTotal:monthlyTotalTimeByEmployee,weeklyTotal:weeklyTotalTimeByEmployee,weeklySupTotal:weeklySupTotalTimeByEmployee,weeklyCompTotal:weeklyCompTotalTimeByEmployee,dailySupTotalMap:dailySupTotalMap,dailyTotalMap:dailyTotalMap,month:month,year:year,period:calendarLoop.getTime(),dailyTotalMap:dailyTotalMap,holidayMap:holidayMap,weeklyAggregate:weeklyAggregate,employee:employee,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
			
			}
		}catch (NullPointerException e){
			log.error('error with application: '+e.toString())		
		}
	}

	
	def sendEmail(){
		sendMail{
		to "henri.martin@orange.com"
		from "henri.martin@gmail.com"
		subject "Hello to mutliple recipients"
		body "Hello Fred! Hello Ginger!"
		}
	}

		
	def pointage(Long id){		
		try {	
			def username = params["username"]
			def employee
			def mapByDay=[:]
			def totalByDay=[:]
			def dailyCriteria
			def timeDiff
			if (id!=null){
				employee = Employee.get(id)		
			}
			if (username!=null && !username.equals("")){
				employee = Employee.findByUserName(username)	
			}
			
			if (employee==null){
				throw new NullPointerException("unknown employee("+username+")")
			}else{
				log.error('employee successfully authenticated='+username)
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
			
			inAndOutsCriteria = InAndOut.createCriteria()
			def inAndOuts = inAndOutsCriteria.list {
				and {
					eq('employee',employee)
					eq('year',calendar.get(Calendar.YEAR))
					eq('month',calendar.get(Calendar.MONTH)+1)
					eq('day',calendar.get(Calendar.DAY_OF_MONTH))
					order('time','asc')
				}
			}
			 
			def tmpCalendar = Calendar.instance
			tmpCalendar.set(Calendar.DAY_OF_YEAR,tmpCalendar.get(Calendar.DAY_OF_YEAR)-4)
			// iterate over tmpCalendar
			while(tmpCalendar.getAt(Calendar.DAY_OF_YEAR) < (calendar.getAt(Calendar.DAY_OF_YEAR) -1)){
				tmpCalendar.roll(Calendar.DAY_OF_YEAR, 1)
				inAndOutsCriteria = InAndOut.createCriteria()
				def lastInAndOuts = inAndOutsCriteria.list {
					and {
						eq('employee',employee)
						eq('year',calendar.get(Calendar.YEAR))
						eq('month',tmpCalendar.get(Calendar.MONTH)+1)
						eq('day',tmpCalendar.get(Calendar.DAY_OF_MONTH))
						order('time','asc')
					}
				}
				
				mapByDay.put(tmpCalendar.time,lastInAndOuts)

				dailyCriteria = DailyTotal.createCriteria()
				def lastDailyTotal = dailyCriteria.get {
					and {
						eq('employee',employee)
						eq('year',tmpCalendar.get(Calendar.YEAR))
						eq('month',tmpCalendar.get(Calendar.MONTH)+1)
						eq('day',tmpCalendar.get(Calendar.DAY_OF_MONTH))
					}
				}
				if (lastDailyTotal!=null){
					totalByDay.put(tmpCalendar.time, timeManagerService.computeHumanTime(lastDailyTotal.elapsedSeconds))
				}else{
					totalByDay.put(tmpCalendar.time, timeManagerService.computeHumanTime(0))
				}
			}				
			
			dailyCriteria = DailyTotal.createCriteria()
			def dailyTotal = dailyCriteria.get {
				and {
					eq('employee',employee)
					eq('year',calendar.get(Calendar.YEAR))
					eq('month',calendar.get(Calendar.MONTH)+1)
					eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				}
			}

			def humanTime=[0,0,0]
			def dailySupp=[0,0,0]
			if (dailyTotal !=null) {
				humanTime = timeManagerService.computeHumanTime(dailyTotal.elapsedSeconds)
				dailySupp = timeManagerService.computeHumanTime(dailyTotal.supplementarySeconds)
			}
			def entranceStatus=true
			if (inAndOuts==null || inAndOuts.size()==0){
				entranceStatus=true
			}			
			
			[employee: employee,inAndOuts:inAndOuts,dailyTotal:dailyTotal,humanTime:humanTime, dailySupp:dailySupp,mapByDay:mapByDay,entranceStatus:entranceStatus,totalByDay:totalByDay]
		}
		catch (Exception e){
			log.error('error with application: '+e.toString())		
			flash.message = message(code: 'employee.not.found.label',args:[params["username"]])
			redirect(uri:'/')

		}
	}	
	def pdf(){
		log.error('method pdf called')
		def myDate = "01/05/2013"//params["myDate"]
		def userId= params["userId"] as int
		def employee=Employee.get(userId)
		SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
		myDate = dateFormat.parse(myDate)
		def cartoucheTable = cartouche(userId,myDate.getAt(Calendar.YEAR),myDate.getAt(Calendar.MONTH)+1)
		def workingDays=cartoucheTable.get(3)
		def holiday=cartoucheTable.get(4)
		def rtt=cartoucheTable.get(5)
		def sickness=cartoucheTable.get(6)
		def sansSolde=cartoucheTable.get(7)
		def monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(8))
		def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(9))
		def yearlyHoliday=cartoucheTable.get(11)
		def yearlyRtt=cartoucheTable.get(12)
		def yearlySickness=cartoucheTable.get(13)
		def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(14))
		def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(15))
		def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get(16))
		def yearlySansSolde=cartoucheTable.get(17)
		def payableSupTime=cartoucheTable.get(18)
		def payableCompTime=cartoucheTable.get(19)
		def openedDays = timeManagerService.computeMonthlyHours(myDate.getAt(Calendar.YEAR),myDate.getAt(Calendar.MONTH)+1)

		def criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('year',myDate.getAt(Calendar.YEAR))
					eq('month',myDate.getAt(Calendar.MONTH)+1)
				}
			}
		
		if (monthlyTotal==null){
			def totals=timeManagerService.initializeTotals(employee, myDate)
			monthlyTotal=totals.get(2)
		}
		def yearInf
		def yearSup
		if ((myDate.getAt(Calendar.MONTH)+1)>4){
			yearInf=myDate.getAt(Calendar.YEAR)
			yearSup=myDate.getAt(Calendar.YEAR)+1
		}else{
			yearInf=myDate.getAt(Calendar.YEAR)-1
			yearSup=myDate.getAt(Calendar.YEAR)
		}
		monthlyTotal=timeManagerService.computeHumanTime(monthlyTotal.elapsedSeconds)
		
		def modelCartouche=[firstName:employee.firstName,lastName:employee.lastName,monthlyTotalRecap:monthlyTotal,yearInf:yearInf,yearSup:yearSup,employee:employee,openedDays:openedDays,workingDays:workingDays,holiday:holiday,rtt:rtt,sickness:sickness,sansSolde:sansSolde,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyHoliday:yearlyHoliday,yearlyRtt:yearlyRtt,yearlySickness:yearlySickness,yearlyTheoritical:yearlyTheoritical,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyActualTotal:yearlyActualTotal,yearlySansSolde:yearlySansSolde,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
		def modelReport=report(userId,myDate.getAt(Calendar.YEAR),myDate.getAt(Calendar.MONTH)+1)
		
		//def model = (modelCartouche + modelReport).groupBy { it.key }.collect { it.value.collectEntries { it } }
		modelReport<<modelCartouche
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/common/completeReportTemplate', model: modelReport)
		//ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/common/cartoucheTemplate', model: model)
		

		OutputStream outputStream;

		try {
			outputStream = new FileOutputStream ("/Users/henri/file.pdf");
			bytes.writeTo(outputStream);
			return

		}finally {
			if(bytes)
			   bytes.close();

			if(outputStream)
			outputStream.close();
			File file = new File("/Users/henri/file.pdf")
			
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "filename=${file.name}")
			response.outputStream << file.bytes
			return
		}
		

	}

}
