package pointeuse

import grails.plugins.springsecurity.Secured
import org.apache.log4j.Logger
import org.springframework.dao.DataIntegrityViolationException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter


import pdf.PdfService;

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

	def searchableService
	PdfService pdfService
	def authenticateService
	def springSecurityService
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
		}else{
			employeeInstanceList=Employee.list(params)
			employeeInstanceTotal = employeeInstanceList.totalCount		
		}
		
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
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

	
	def initializeMissingEvents(long eventId){
		//def employee = Employee.get(employeeId)
		def inOrOut = InAndOut.get(eventId)
		initializeTotals(inOrOut.employee, inOrOut.time,"",inOrOut)
		
	}		
	
	
	def initializeTotals(Employee employee, Date currentDate,String type,def event){
		def criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('year',currentDate.getAt(Calendar.YEAR))
					eq('month',currentDate.getAt(Calendar.MONTH)+1)
				}
			}
		
		if (monthlyTotal==null){
			monthlyTotal = new MonthlyTotal(employee,currentDate)
			employee.monthlyTotals.add(monthlyTotal)	
			monthlyTotal.save(flush:true)
			
		}
		
		criteria = WeeklyTotal.createCriteria()
		def weeklyTotal = criteria.get {
			and {
				eq('employee',employee)
				eq('year',currentDate.getAt(Calendar.YEAR))
				eq('month',currentDate.getAt(Calendar.MONTH)+1)
				eq('week',currentDate.getAt(Calendar.WEEK_OF_YEAR))
			}
		}
		
		if (weeklyTotal == null){
			weeklyTotal = new WeeklyTotal(employee,currentDate)
			monthlyTotal.weeklyTotals.add(weeklyTotal)
			weeklyTotal.monthlyTotal=monthlyTotal
			weeklyTotal.save(flush:true)
			
		}

		criteria = DailyTotal.createCriteria()
		def dailyTotal = criteria.get {
			and {
				eq('employee',employee)
				eq('year',currentDate.getAt(Calendar.YEAR))
				eq('month',currentDate.getAt(Calendar.MONTH)+1)
				eq('day',currentDate.getAt(Calendar.DAY_OF_MONTH))
			}
		}
		if (dailyTotal==null){
			dailyTotal = new DailyTotal(employee,currentDate)
			dailyTotal.weeklyTotal=weeklyTotal
			weeklyTotal.dailyTotals.add(dailyTotal)
			dailyTotal.save(flush:true)
			
		}
		 
		if (event==null){
			def inOrOut = new InAndOut(employee, currentDate,type)
			inOrOut.dailyTotal=dailyTotal
			dailyTotal.inAndOuts.add(inOrOut)
			employee.inAndOuts.add(inOrOut)
			dailyTotal.exitCount=dailyTotal.exitCount+1
			return inOrOut
			
		}else{
			event.dailyTotal=dailyTotal
			return event
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

	def password(Long id){
		def employeeInstance = Employee.get(id)
		render(view: "password",model: [userId: params.id,employeeInstance:employeeInstance])
		return
	}
	
	def cartoucheUpdate(){
		def employeeId= params["employeeId"].getAt(0)
		def employee = Employee.get(employeeId)
		def calendar = Calendar.instance
		calendar.set(Calendar.DAY_OF_MONTH,1)
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
		def month = calendar.getAt(Calendar.MONTH)+1
		def year = calendar.getAt(Calendar.YEAR)
		def holiday = BankHoliday.findAllByMonthAndYear(calendar.getAt(Calendar.MONTH)+1,calendar.getAt(Calendar.YEAR))
		
		counter -= holiday.size()
		
		def criteria = Absence.createCriteria()
		
		// get cumul holidays
		def holidays = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.VACANCE)
			}
		}
		
		// get cumul RTT
		criteria = Absence.createCriteria()
		def rtt = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.RTT)
			}
		}
		// get cumul sickness
		criteria = Absence.createCriteria()
		def sickness = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MALADIE)
			}
		}		
		return [period:calendar,workingDays:counter as float,holiday:holidays.size(),rtt:rtt.size(),sickness:sickness.size()]		
	}
	
	def yearlyCartouche(long userId,int year,int month){
		if (userId == null){
			userId = params["employeeId"].getAt(0)
		}
		def employeeInstance = Employee.get(userId)
	
			
		if (month>4){
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
		
		def yearlyCartouche=yearlyCartouche(userId,year,month)		
		def pregnancyCredit=30*60*pregnancy.size()
		// determine monthly theoritical time:
		def monthTheoritical=(3600*(counter*employeeInstance.weeklyContractTime/Employee.WeekOpenedDays+Employee.Pentecote-(WeeklyTotal.WeeklyLegalTime/Employee.WeekOpenedDays)*(sickness.size()+holidays.size()+sansSolde.size())) - pregnancyCredit)as int
		
		//def monthTheoritical=(3600*(counter*employeeInstance.weeklyContractTime/6+7/12-sickness.size()*35/6-holidays.size()*35/6 -sansSolde.size()*35/6) - pregnancyCredit)as int	
		return [params.userId,employeeInstance,calendar,counter ,holidays.size(),rtt.size(),sickness.size(),sansSolde.size(),monthTheoritical,pregnancyCredit,yearlyCartouche.get(0),yearlyCartouche.get(1),yearlyCartouche.get(2),yearlyCartouche.get(3),yearlyCartouche.get(4),yearlyCartouche.get(5),yearlyCartouche.get(6),yearlyCartouche.get(7)]		
	}
	
	def validatePassword(Long id){
		def employeeInstance = Employee.get(id)
		if (params["password"].equals(employeeInstance.password)){
			redirect(action: "show", id: employeeInstance.id)

		}
		else{
			flash.message = "WRONG PASSWORD"
			redirect(action: "password", id: employeeInstance.id)
			return
		}
	}
	
	def getMaxEntriesExists(Long id){	
		def calendar = Calendar.instance
		int month = calendar.getAt(Calendar.MONTH) // starts at 0, thus august
		int year = calendar.getAt(Calendar.YEAR)//2012
		def sessionFactory
		def example = {
			def sql = new Sql(sessionFactory.curentSession.connection())
			[ temp: sql.rows("select max(entry_count+exit_count) from daily_total where month="+month+" and year="+year+ "and employee_id="+id) ]
		}
	}
	
	def getAbsencePerDate(Calendar cal,Employee employee){
		def criteria = Absence.createCriteria()
		
		def monthlyAbsenceList = criteria.list {
			and {
				eq('employee',employee)
				eq('year',cal.get(Calendar.YEAR))
				eq('month',cal.get(Calendar.MONTH)+1)
			}
		}
		return monthlyAbsenceList
		
	}

	private computeSupplementaryTime(DailyTotal dailyTotal){
		def weeklyTotal = dailyTotal.weeklyTotal
		if (weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){
			weeklyTotal.supplementarySeconds = weeklyTotal.elapsedSeconds-WeeklyTotal.maxWorkingTime		
		}else {
			weeklyTotal.supplementarySeconds = 0
		}
		//else 
		if (dailyTotal.elapsedSeconds > DailyTotal.maxWorkingTime){
			dailyTotal.supplementarySeconds = dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
		}else {
			dailyTotal.supplementarySeconds =0
		}
	}
	
	
	private computeComplementaryTime(DailyTotal dailyTotal){
		def weeklyTotal = dailyTotal.weeklyTotal
		def sup = 0
		def HC = 0
		def HS = 0
		// calculer les HS et HC hebdo
		
		// ne peuvent pas exceder 1/3 du temps hebdo prŽvu au contrat:
		if (weeklyTotal.elapsedSeconds > 3600*dailyTotal.employee.weeklyContractTime){
			//on est en dessous du seuil des 10%: il n'y a que des HC
			if (weeklyTotal.elapsedSeconds < 4*3600*dailyTotal.employee.weeklyContractTime/3){
				HC = weeklyTotal.elapsedSeconds - 3600*dailyTotal.employee.weeklyContractTime
			}
			// on est au dessus: il faut comptabiliser HC et HS
			else{
				HC = 3600*dailyTotal.employee.weeklyContractTime/3
				HS = weeklyTotal.elapsedSeconds-4*3600*dailyTotal.employee.weeklyContractTime/3
			}
		}
		weeklyTotal.supplementarySeconds=HS
		weeklyTotal.complementarySeconds=HC
		
		// calculer les HS quotidiennes
		if (dailyTotal.elapsedSeconds > DailyTotal.maxWorkingTime){
			dailyTotal.supplementarySeconds = dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
		}else {
			dailyTotal.supplementarySeconds = 0
		}
	}
	
	def modifyAbsence(){
		def employeeId= params["employeeId"].getAt(0)
		def employee = Employee.get(employeeId)
		def day = params["day"].getAt(0)
		def updatedSelection = params["updatedSelection"].toString()
		
		SimpleDateFormat dateFormat = new SimpleDateFormat('dd MM yyyy');
		Date date = dateFormat.parse(day)
		def cal= Calendar.instance
		cal.setTime(date)
		
		// check if an absence was already logged:
		def criteria = Absence.createCriteria()
		
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
		 	
		def cartoucheTable = cartouche(employee.id,absence.year,absence.month)		
		return [workingDays:cartoucheTable.get(3),holiday:cartoucheTable.get(4),rtt:cartoucheTable.get(5),sickness:cartoucheTable.get(6)]
	}
	
	
	def updateTime(Long id){
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
		def inOrOut = initializeTotals(employeeInstance,currentDate,type,null)
		
		// l'employee effectue une sortie: il faut calculer le temps passŽ depuis la derniere entrŽe
		if (type.equals("S")){					
			if (lastIn != null){			
				inOrOut.dailyTotal.elapsedSeconds += timeDiff.seconds+60*timeDiff.minutes+3600*timeDiff.hours
				inOrOut.dailyTotal.weeklyTotal.elapsedSeconds+=timeDiff.seconds+60*timeDiff.minutes+3600*timeDiff.hours
				inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=timeDiff.seconds+60*timeDiff.minutes+3600*timeDiff.hours
					// le salariŽ est ˆ temps partiel: il faut calculer les heures complŽmentaires
				if (employeeInstance.weeklyContractTime != 35){
					computeComplementaryTime(inOrOut.dailyTotal)
				}else{
					computeSupplementaryTime(inOrOut.dailyTotal)
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
	
	def regularizeTime(String type,String userId,InAndOut inOrOut,Calendar calendar){
		def cal = Calendar.instance
		if (calendar != null){
			cal = calendar
		}
		def dailyTotal
		def currentDate = cal.time
		def employeeInstance = Employee.get(userId)
		def weeklyTotal
		def monthlyTotal
		def criteria
		def lastElement
		def nextElement
		def deltaTime
		def NIT
		def LIT
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).getTime()
	
		// liste les entrees de la journŽe et vŽrifie que cette valeur n'est pas supŽrieure ˆ une valeur statique
		criteria = InAndOut.createCriteria()
		def todayEmployeeEntries = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',cal.get(Calendar.YEAR))
				eq('month',cal.get(Calendar.MONTH)+1)
				eq('day',cal.get(Calendar.DAY_OF_MONTH))
				eq('type','E')
			}
		}
		
		if (todayEmployeeEntries != null && todayEmployeeEntries.size() > Employee.entryPerDay){
			flash.message = "TROP D'ENTREES DANS LA JOURNEE. POINTAGE NON PRIS EN COMPTE"
			redirect(action: "show", id: employeeInstance.id)
			return
		}
		criteria = DailyTotal.createCriteria()
		dailyTotal = criteria.get {
			and {
				eq('employee',employeeInstance)
				eq('year',cal.get(Calendar.YEAR))
				eq('month',cal.get(Calendar.MONTH)+1)
				eq('day',cal.get(Calendar.DAY_OF_MONTH))
			}
		}

		// initialisation
		if (dailyTotal == null) {
			initializeTotals(employeeInstance , cal.time , type,null)
		}else{
			criteria = InAndOut.createCriteria()
			lastElement = criteria.get {
				and {
					eq('employee',employeeInstance)
					eq('day',today.getAt(Calendar.DATE))
					eq('month',today.getAt(Calendar.MONTH)+1)
					eq('year',today.getAt(Calendar.YEAR))
					lt('time',inOrOut.time)
					order('time','desc')
				}
				maxResults(1)
			}
			
			// check if there is a next out
			criteria = InAndOut.createCriteria()
			nextElement = criteria.get {
				and {
					eq('employee',employeeInstance)
					eq('day',today.getAt(Calendar.DATE))
					eq('month',today.getAt(Calendar.MONTH)+1)
					eq('year',today.getAt(Calendar.YEAR))
					gt('time',inOrOut.time)
					order('time','asc')
				}
				maxResults(1)
			}
			
			deltaTime=new TimeDuration( 0, 0, 0, 0) 
		
			// l'employee effectue une sortie: il faut calculer le temps passŽ depuis la derniere entrŽe
			if (type.equals("S")){
				if (lastElement != null && lastElement.type.equals('E')){
					LIT = lastElement.time
					use (TimeCategory){deltaTime=inOrOut.time-LIT}
				}
			}
			// il y a eu une entrŽe: il faut vŽrifier par rapport au temps dŽjˆ dŽcomptŽ
			// c'est un cas spŽcial qui a lieu lors d'une rŽgularisation
			else {
				if (nextElement != null && nextElement.type.equals('S')){
					if (lastElement!=null){
						LIT = lastElement.time
						use (TimeCategory){deltaTime=LIT - inOrOut.time}
					}else{
						use (TimeCategory){deltaTime=nextElement.time - inOrOut.time}
					
					}
				}
			}	

			dailyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
			
			if (dailyTotal.weeklyTotal != null){
				dailyTotal.weeklyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
				dailyTotal.weeklyTotal.dailyTotals.add(dailyTotal)
				if (employeeInstance.weeklyContractTime != 35){
					computeComplementaryTime(dailyTotal)
				}else{
					computeSupplementaryTime(dailyTotal)
				}
			}
			if (dailyTotal.weeklyTotal.monthlyTotal != null){
				dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
				dailyTotal.weeklyTotal.monthlyTotal.weeklyTotals.add(weeklyTotal)
			}														
			employeeInstance.inAndOuts.add(inOrOut)
			//dailyTotal.inAndOuts.add(inOrOut)
			dailyTotal.exitCount=dailyTotal.exitCount+1
			// the new input is the last one. return the 
			if (lastElement != null && nextElement != null){
				use (TimeCategory){deltaTime=nextElement.time-lastElement.time}
			}else{
				if (lastElement != null){
					use (TimeCategory){deltaTime=inOrOut.time-lastElement.time}
					
				}
				if (nextElement != null){
					use (TimeCategory){deltaTime=nextElement.time-inOrOut.time}
				}
			}
		}
		if (type.equals("S")){employeeInstance.status=false}
		else {employeeInstance.status=true}
		return deltaTime
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
	
	def dailyTotalUpdate(TimeDuration timeDiff,Date newDate ,Date oldDate,InAndOut inOrOut,Employee employee,int day, int month){
		if (inOrOut.dailyTotal.elapsedSeconds<0){
			log.error('the daily Total is negative ' + inOrOut.dailyTotal)
			//dailyTotal.elapsedSeconds=0
		}		
		if (timeDiff == null){
			use (TimeCategory){timeDiff=newDate-oldDate}	
			if (inOrOut.type == "E"){
				inOrOut.dailyTotal.elapsedSeconds-=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.elapsedSeconds-=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds-=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			} else {
				inOrOut.dailyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			}
		}else{
			inOrOut.dailyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			inOrOut.dailyTotal.weeklyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
		}
		return inOrOut.dailyTotal
	}

	def trash(){
		
		/*UPDATE weekly_total W
		 JOIN monthly_total_weekly_total M ON
		 W.id=M.weekly_total_id
		SET W.monthly_total_id = M.monthly_total_weekly_totals_id;
		
		UPDATE in_and_out IO
	JOIN daily_total DT ON
		DT.day=IO.day
		AND DT.month=IO.month
		AND DT.year=IO.year
		AND DT.employee_id=IO.employee_id
	SET IO.daily_total_id=DT.id;
	  */
		def eventId=params["id"] as int
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
		
		criteria = DailyTotal.createCriteria()
		def dailyTotal = criteria.get {
			and {
				eq('employee',inOrOut.employee)
				eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
				eq('month',calendar.getAt(Calendar.MONTH)+1)
				eq('year',calendar.getAt(Calendar.YEAR))
			}
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
		dailyTotal.elapsedSeconds+=extraTime.hours*3600+extraTime.minutes*60+extraTime.seconds
		dailyTotal.weeklyTotal.elapsedSeconds+=extraTime.hours*3600+extraTime.minutes*60+extraTime.seconds
		dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=extraTime.hours*3600+extraTime.minutes*60+extraTime.seconds
		
		if (inOrOut.employee.weeklyContractTime != 35){
			computeComplementaryTime(dailyTotal)
		}else{
			computeSupplementaryTime(dailyTotal)
		}
		
		
		log.error('removing entry '+inOrOut)
		def employee = inOrOut.employee
		inOrOut.delete(flush:true)
		
		criteria = InAndOut.createCriteria()
		def inAndOutList = criteria.list {
			and {
				eq('employee',employee)
				eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
				eq('month',calendar.getAt(Calendar.MONTH)+1)
				eq('year',calendar.getAt(Calendar.YEAR))
				order('time','asc')
			}
		}
		
		
		String content = g.render(template:'/common/listInAndOutsTemplate',model:[inAndOutList:inAndOutList])
		render content
	//	render template: "/common/listInAndOutsTemplate", model: inAndOutList
	//	return [inAndOutList:inAndOutList]
	//	[inAndOutList:inAndOutList]
		//redirect(action: "showDay",params:[employeeId: inOrOut.employee.id, month: month, year: year, day: day])
		
		
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
	
	def timeModification(){
		def idList=params["inOrOutId"] 
		def timeList=params["time"]
		def dayList=params["day"] 
		def monthList=params["month"] 
		def yearList=params["year"] 
		def employeeId=params["employee.id"]
		def employee = Employee.get(employeeId)
		def newTimeList=params["cell"]
		def fromRegularize=params["fromRegularize"].equals("true") ? true : false
		
		def deltaUp
		def deltaDown
		def timeDiff
		def criteria
		def calendar = Calendar.instance

		
		def user = springSecurityService.currentUser

		if (idList==null){
			log.error("list is null")
			def retour = report(employee.id as long,month as int,year as int)
			render(view: "report", model: retour)
			
		}
		
		if (idList instanceof String){
			def tmpidList=idList
			def tmpdayList=dayList
			def tmpmonthList=monthList
			def tmpyearList=yearList
			def tmpnewTimeList=newTimeList
			idList=[]
			dayList=[]
			monthList=[]
			yearList=[]
			newTimeList=[]
			idList.add(tmpidList)
			dayList.add(tmpdayList)
			monthList.add(tmpmonthList)
			yearList.add(tmpyearList)
			newTimeList.add(tmpnewTimeList)
		}
		def month=monthList[0]
		def year=yearList[0]
		
		
		for( int p = 0 ; ( p < idList.size() ) ; p++ ) {		
		// l'idŽe: comparer time et newTime
			def inOrOut = InAndOut.get(idList[p])
			if (inOrOut==null){
				log.error("InAndOut with id= "+idList[p]+" cannot be found. exiting timeModification¤")
				def retour = report(employee.id as long,monthList[p] as int,yearList[p] as int)
				render(view: "report", model: retour)
				return
			}	
						
			def newDate = new Date().parse("d/M/yyyy H:m", dayList[p]+"/"+monthList[p]+"/"+yearList[p]+" "+newTimeList[p])
			def newCalendar = Calendar.instance
			newCalendar.time=newDate
			def oldCalendar = Calendar.instance
			oldCalendar.time=inOrOut.time
			newCalendar.set(Calendar.SECOND,oldCalendar.get(Calendar.SECOND))
			
			if (newCalendar.time!=oldCalendar.time){
				def toCompare
				if (newCalendar.time>oldCalendar.time){
					toCompare=newCalendar.time
				}else{
					toCompare=oldCalendar.time
				}
				toCompare.set(minutes:toCompare.minutes+1)
					
				// get next inOrOut for this user
				calendar.time=oldCalendar.time
				criteria = InAndOut.createCriteria()
				def nextInOrOut = criteria.get {
					and {
						eq('employee',employee)
						eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
						eq('month',calendar.getAt(Calendar.MONTH)+1)
						eq('year',calendar.getAt(Calendar.YEAR))
						gt('id',inOrOut.id)
						gt('time',toCompare)
						order('time','asc')
					}
					maxResults(1)	
				}
				
				if (newCalendar.time>oldCalendar.time){
					toCompare=oldCalendar.time
				}else{
					toCompare=newCalendar.time
				}
				toCompare.set(minutes:toCompare.minutes+1)
				
				criteria = InAndOut.createCriteria()
				def previousInOrOut = criteria.get {
					and {
						eq('employee',employee)
						eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
						eq('month',calendar.getAt(Calendar.MONTH)+1)
						eq('year',calendar.getAt(Calendar.YEAR))
						lt('time',toCompare)
						lt('id',inOrOut.id)
						order('time','desc')
					}
					maxResults(1)
				}
			
				if (previousInOrOut != null){
					use (TimeCategory){deltaDown=newCalendar.time-previousInOrOut.time}
				}
				else {
					deltaDown=new Date()
					deltaDown.minutes=0
				}
				
				if (nextInOrOut != null){
					use (TimeCategory){deltaUp=nextInOrOut.time-newCalendar.time}
				}
				else {
					deltaUp=new Date()
					deltaUp.minutes=0
				}
				
				if (deltaUp.minutes < 0 || deltaDown.minutes < 0 || deltaDown.hours < 0 || deltaUp.hours < 0){
					def back = report(employee.id as int)
					flash.message = message(code: 'inAndOut.updateTime.error')
					render(view: "report", model: back)
					return
				}
				inOrOut.time=newCalendar.time
				
				// if the time difference
				if (inOrOut.systemGenerated && previousInOrOut!=null ){
						use (TimeCategory){timeDiff=newCalendar.time-previousInOrOut.time}
				}	
				if (inOrOut.regularization && nextInOrOut !=null && nextInOrOut.systemGenerated){
					use (TimeCategory){timeDiff=nextInOrOut.time - newCalendar.time}
				}		
				def doNothing=false	
				if (inOrOut.type.equals('E') && nextInOrOut !=null && nextInOrOut.type.equals('E')){
					doNothing=true
				}
				if (inOrOut.type.equals('S') && previousInOrOut !=null && previousInOrOut.type.equals('S')){
					doNothing=true
				}
				if(!doNothing){
					dailyTotalUpdate(timeDiff,newCalendar.time,oldCalendar.time,inOrOut,employee,dayList[p] as int,month as int)
				}
				if (employee.weeklyContractTime != 35){
					computeComplementaryTime(inOrOut.dailyTotal)
				}else{
					computeSupplementaryTime(inOrOut.dailyTotal)
				}
				
				inOrOut.regularizationType=fromRegularize ? InAndOut.MODIFIEE_SALARIE : InAndOut.MODIFIEE_ADMIN
				
				inOrOut.systemGenerated=false			
				
				if (user!=null){
					inOrOut.modifyingUser=user
					inOrOut.modifyingTime=new Date()
					log.error("user "+user?.username+" modified "+inOrOut)
				}
			}
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
				def elapsedTime = computeHumanTime(weeklyTotal.elapsedSeconds)
				mapByWeek.put(calendar.getAt(Calendar.WEEK_OF_YEAR), elapsedTime)		
			}else{
				mapByWeek.put(calendar.getAt(Calendar.WEEK_OF_YEAR), computeHumanTime(0))
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
				weeklyTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), computeHumanTime(dailyTotal.weeklyTotal.elapsedSeconds))
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
					dailyTotalMap.put(tmpDate, computeHumanTime(dailyTotal.elapsedSeconds))
					dailySupTotalMap.put(tmpDate, computeHumanTime(dailyTotal.supplementarySeconds))
				}else {
					dailyTotalMap.put(tmpDate, computeHumanTime(0))
					dailySupTotalMap.put(tmpDate, computeHumanTime(0))
				}
				mapByDay.put(tmpDate, entriesByDay)
			}
			else{
				dailyTotalMap.put(tmpDate, computeHumanTime(0))
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
			monthlyTotalTimeByEmployee.put(employee, computeHumanTime(monthlyTotal.elapsedSeconds))
		}
		try {
			if (userId != null){
			def cartoucheTable = cartouche(userId,year,month+1)
			def openedDays = computeMonthlyHours(year,month)
			def monthTheoritical = computeHumanTime(cartoucheTable.get(8))
			def yearlyTheoritical = computeHumanTime(cartoucheTable.get(14))
			def yearlyPregnancyCredit = computeHumanTime(cartoucheTable.get(15))
			def yearlyActualTotal = computeHumanTime(cartoucheTable.get(16))
			
			def pregnancyCredit = computeHumanTime(cartoucheTable.get(9))
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
				weeklySuppTotalTime.put(key,computeHumanTime(value))
			};
			weeklyCompTotalTime.each() { key, value ->
				//log.error(value)
				monthlyCompTime += value
				weeklyCompTotalTime.put(key,computeHumanTime(value))
			};
			
			def payableSupTime = computeHumanTime(monthlySupTime)
			def payableCompTime = computeHumanTime(monthlyCompTime)
			
			
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
				weeklyTotalTime.put(weekName+calendarLoop.getAt(Calendar.WEEK_OF_YEAR), computeHumanTime(dailyTotal.weeklyTotal.elapsedSeconds))			
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
					dailyTotalMap.put(tmpDate, computeHumanTime(dailyTotal.elapsedSeconds))
					dailySupTotalMap.put(tmpDate, computeHumanTime(dailyTotal.supplementarySeconds))
				}else {
					dailyTotalMap.put(tmpDate, computeHumanTime(0))
					dailySupTotalMap.put(tmpDate, computeHumanTime(0))
				}		
				mapByDay.put(tmpDate, entriesByDay)
			}	
			else{
				dailyTotalMap.put(tmpDate, computeHumanTime(0))
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
			monthlyTotalTimeByEmployee.put(employee, computeHumanTime(monthlyTotal.elapsedSeconds))
		}	
		try {
			if (userId != null){
			def cartoucheTable = cartouche(userId,year,month+1)
			def openedDays = computeMonthlyHours(year,month)
			def monthTheoritical = computeHumanTime(cartoucheTable.get(8))
			def yearlyTheoritical = computeHumanTime(cartoucheTable.get(14))
			def yearlyPregnancyCredit = computeHumanTime(cartoucheTable.get(15))
			def yearlyActualTotal = computeHumanTime(cartoucheTable.get(16))
			
			def pregnancyCredit = computeHumanTime(cartoucheTable.get(9))
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
				weeklySuppTotalTime.put(key,computeHumanTime(value))
			};
			weeklyCompTotalTime.each() { key, value ->
				//log.error(value)
				monthlyCompTime += value
				weeklyCompTotalTime.put(key,computeHumanTime(value))
			};
			
			def payableSupTime = computeHumanTime(monthlySupTime)
			def payableCompTime = computeHumanTime(monthlyCompTime)
			
			[siteId:siteId,yearInf:yearInf,yearSup:yearSup,userId:userId,workingDays:cartoucheTable.get(3),holiday:cartoucheTable.get(4),rtt:cartoucheTable.get(5),sickness:cartoucheTable.get(6),sansSolde:cartoucheTable.get(7),yearlyActualTotal:yearlyActualTotal,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTheoritical:yearlyTheoritical,yearlyHoliday:cartoucheTable.get(11),yearlyRtt:cartoucheTable.get(12),yearlySickness:cartoucheTable.get(13),yearlySansSolde:cartoucheTable.get(17),yearlyTheoritical:yearlyTheoritical,period:calendar,monthlyTotal:monthlyTotalTimeByEmployee,weeklyTotal:weeklyTotalTimeByEmployee,weeklySupTotal:weeklySupTotalTimeByEmployee,weeklyCompTotal:weeklyCompTotalTimeByEmployee,dailySupTotalMap:dailySupTotalMap,dailyTotalMap:dailyTotalMap,month:month,year:year,period:calendarLoop.getTime(),dailyTotalMap:dailyTotalMap,holidayMap:holidayMap,weeklyAggregate:weeklyAggregate,employee:employee,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
			
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
	

	
	def computeMonthlyHours(int year,int month){
		def openedDays = 0
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		
		// discard sundays from opened days
		while(calendar.getAt(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			if (calendar.getAt(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				openedDays += 1
			}
			
			if (calendar.getAt(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		def bankHolidays = BankHoliday.findAllByYearAndMonth(year,month+1)
		// discard bank holidays
		if (bankHolidays!= null){
			openedDays -= bankHolidays.size()
		}
		return openedDays
	}
	
	def regularize(){
		
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
			
			 
			
			//inAndOutsCriteria = InAndOut.createCriteria()
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
					totalByDay.put(tmpCalendar.time, computeHumanTime(lastDailyTotal.elapsedSeconds))
				}else{
					totalByDay.put(tmpCalendar.time, computeHumanTime(0))
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
				humanTime = computeHumanTime(dailyTotal.elapsedSeconds)
				dailySupp = computeHumanTime(dailyTotal.supplementarySeconds)
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
	

	def computeHumanTime(long inputSeconds){
		def diff=inputSeconds
		long hours=TimeUnit.SECONDS.toHours(diff);
		diff=diff-(hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff=diff-(minutes*60);
		long seconds=TimeUnit.SECONDS.toSeconds(diff);
		return [hours,minutes,seconds]
	}
	
}
