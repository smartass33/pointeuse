package pointeuse

import grails.plugins.springsecurity.Secured

import org.apache.log4j.Logger
import org.springframework.dao.DataIntegrityViolationException
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter

import pl.touk.excel.export.WebXlsxExporter
import pl.touk.excel.export.XlsxExporter

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar

import groovy.time.TimeCategory
import groovy.sql.Sql
import groovy.time.TimeDuration

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

import grails.converters.JSON


class EmployeeController {
	def PDFService
	def utilService
	def mailService
	def pdfRenderingService
	def searchableService
	def authenticateService
	def springSecurityService
	def timeManagerService 
	def employeeService
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

	def modifyAbsenceCounter(){
		def year=params["year"]
		def userId=params["userId"]
		Employee employee = Employee.get(userId)
		AbsenceCounter absenceCount = AbsenceCounter.findByEmployeeAndYear(employee,year)
		[absenceCount:absenceCount]
	}
	
	@Secured(['ROLE_ADMIN'])
	def dailyReportAjax(){
		def employeeMap = [:]
		def dailyMap = [:]
		def dailySupMap = [:]
		def dailyInAndOutMap = [:]
		def day=params["day"]
		def siteId=params["site.id"]
		def currentDate = params["currentDate"]
		def dailyTotal
		def inAndOutList
		def criteria
		def site
		def elapsedSeconds
		def employeeInstanceList
		def calendar = Calendar.instance
		if (currentDate!=null)
		calendar.time=currentDate

		
		if (siteId!=null && !siteId.equals("")){
			 site = Site.get(params["site.id"] as int)
			 employeeInstanceList = Employee.findAllBySite(site)
		}
		
		else{
			employeeInstanceList = Employee.findAll([sort:'site',order:'asc'])
			
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
			
			dailyInAndOutMap.put(employee, inAndOutList)
			elapsedSeconds = timeManagerService.getDailyTotal(dailyTotal)
			if (elapsedSeconds > DailyTotal.maxWorkingTime){
				dailySupMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds-DailyTotal.maxWorkingTime))
			}else{
				dailySupMap.put(employee,timeManagerService.computeHumanTime(0))
			}
			dailyMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds))
		}

		render template: "/common/listDailyTimeTemplate", model:[dailyMap: dailyMap,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap]
		return	

	}
	
	@Secured(['ROLE_ADMIN'])
	def dailyReport(){
		def employeeMap = [:]
		def dailyMap = [:]
		def dailySupMap = [:]
		def dailyInAndOutMap = [:]
		def day=params["day"]
		def siteId=params["site.id"]
		def currentDate = params["currentDate"]
		def dailyTotal
		def inAndOutList
		def criteria
		def site
		def elapsedSeconds
		def employeeInstanceList
		def calendar = Calendar.instance
		if (currentDate!=null)
		calendar.time=currentDate

		
		if (siteId!=null && !siteId.equals("")){
			 site = Site.get(params["site.id"] as int)
			 employeeInstanceList = Employee.findAllBySite(site)
		}
		
		else{
			employeeInstanceList = Employee.findAll()
			
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
			
			dailyInAndOutMap.put(employee, inAndOutList)
			elapsedSeconds = timeManagerService.getDailyTotal(dailyTotal)
			if (elapsedSeconds > DailyTotal.maxWorkingTime){
				dailySupMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds-DailyTotal.maxWorkingTime))	
			}else{
				dailySupMap.put(employee,timeManagerService.computeHumanTime(0))
			}
			dailyMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds))
		}
		
		if (site!=null){
		render template: "/common/listDailyTimeTemplate", model:[dailyMap: dailyMap,site:site,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap]
		return	}
		[dailyMap: dailyMap,site:site,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap]

	}
	
	
	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
		log.error('browser version: '+request.getHeader('User-Agent') )
		def employeeInstanceList
		def employeeInstanceTotal
		def site
		def siteId=params["site"]
		boolean back = (params["back"] != null && params["back"].equals("true")) ? true : false
		
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
		
		
		if (params["site"]!=null && !params["site"].equals('')){
			site = Site.get(params["site"] as int)
			siteId=site.id
			
		}	
		if (params["siteId"]!=null && !params["siteId"].equals("")){
			site = Site.get(params["siteId"] as int)
			siteId=site.id
			
		}		
		
		def user = springSecurityService.currentUser 
		def username = user?.getUsername()
        params.max = Math.min(max ?: 20, 100)
		if (site!=null && !back){
				employeeInstanceList = Employee.findAllBySite(site)
				employeeInstanceTotal = employeeInstanceList.size()
				render template: "/common/listEmployeeTemplate", model:[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceList.size(),username:username,isAdmin:isAdmin,siteId:siteId,site:site]
				return
			
		}
		if (params["site"].equals('') && !back){			
			employeeInstanceList=Employee.list(params)
			employeeInstanceTotal = employeeInstanceList.totalCount		
			render template: "/common/listEmployeeTemplate", model:[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceTotal,username:username,isAdmin:isAdmin,siteId:null,site:null]
			return
		}
		
		
		if (back){
			if (site!=null){
				employeeInstanceList=Employee.findAllBySite(site)
			}else {
				employeeInstanceList=Employee.findAll()	
				employeeInstanceTotal = employeeInstanceList.size()
			}
		}else{
			employeeInstanceList=Employee.list(params)
			employeeInstanceTotal = employeeInstanceList.totalCount
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
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false
		def service = params["employee.service.id"]
		def site = params["employee.site.id"]
		def function = params["employee.function.id"]	
		def employeeInstance =new Employee(params)
		employeeInstance.service=Service.get(service)
		employeeInstance.site=Site.get(site)
		employeeInstance.function=Function.get(function)
		
		
        if (!employeeInstance.save(flush: true)) {
            render(view: "create", model: [employeeInstance: employeeInstance])
            return
        }

		utilService.initiateVacations(employeeInstance)
		
        flash.message = message(code: 'default.created.message', args: [message(code: 'employee.label', default: 'Employee'), employeeInstance.id])
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
			render template: "/common/listEmployeeTemplate", model:[employeeInstanceList: employeeList, employeeInstanceTotal: employeeList.size(),isAdmin:isAdmin]
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

	def vacationFollowup(Integer max){
		def year = params["year"]
		def myDate=params["myDate"]
		def site
		def siteId
		def employeeInstanceList
		def employeeVacations
		def period
		def employeeMap = [:]
		def vacationMap = [:]
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
		def takenSickness
		def takenRTT
		def takenCA
		def takenCSS
		def takenAutre
		def employeeInstanceTotal

		if (params["site.id"]!=null && !params["site.id"].equals("")){
			def tmpSite = params["site.id"]
			if (tmpSite instanceof String[]){
				if (tmpSite[0]!=""){
					tmpSite=tmpSite[0].toInteger()
					
				}else{
					tmpSite=tmpSite[1].toInteger()
				
				}
			}else {
				tmpSite=tmpSite.toInteger()
			}
			site = Site.get(tmpSite)
			
			
			siteId=site.id
			
		}
		if (year!=null && !year.equals("")){
			if (year instanceof String[]){
				if (year[0]!=""){
					year=year[0].toInteger()
					
				}else{
					year=year[1].toInteger()
				
				}
			}else {
				year=year.toInteger()	
			}
		}
		
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.HOUR_OF_DAY,00)
		startCalendar.set(Calendar.MINUTE,00)
		startCalendar.set(Calendar.SECOND,00)
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,6)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		if (year){
			period = Period.get(year)
			startCalendar.set(Calendar.YEAR,period.year)
			endCalendar.set(Calendar.YEAR,(period.year+1))
		}else{
			period = Period.findByYear(startCalendar.getAt(Calendar.YEAR))
			endCalendar.set(Calendar.YEAR,startCalendar.getAt(Calendar.YEAR)+1)
		}

		
		if (site){
			employeeInstanceList = Employee.findAllBySite(site,params)
			
		}else{
			employeeInstanceList = Employee.findAll(params)		
		}
		
		// for each employee, retrieve absences
		for (Employee employee: Employee.list(params)){
			// step 1: fill initial values
			//CA
			criteria = Vacation.createCriteria()
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
		}
		employeeInstanceTotal=employeeInstanceList.size()
		log.error("done")
		[period:period,employeeInstanceTotal:employeeInstanceTotal,site:site,employeeInstanceList:employeeInstanceList,takenCSSMap:takenCSSMap,takenAutreMap:takenAutreMap,takenSicknessMap:takenSicknessMap,takenRTTMap:takenRTTMap,takenCAMap:takenCAMap,initialCAMap:initialCAMap,initialRTTMap:initialRTTMap,remainingRTTMap:remainingRTTMap,remainingCAMap:remainingCAMap]
		
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
		def holidayCounter = 0
		// starting calendar: 1 of June of the period
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.HOUR_OF_DAY,00)
		startCalendar.set(Calendar.MINUTE,00)
		startCalendar.set(Calendar.SECOND,00)
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,6)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		for (Period period:Period.findAll([sort:'year',order:'asc'])){
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
        def employeeInstance = Employee.get(id)
		def criteria
		def takenCA=[]
		def takenRTT=[]
		def yearMap=[:]
		def initialCAMap=[:]
		def remainingCAMap=[:]
		def initialRTTMap=[:]
		def remainingRTTMap=[:]
		def holidayCounter = 0
		// starting calendar: 1 of June of the period
		def startCalendar = Calendar.instance		
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.HOUR_OF_DAY,00)
		startCalendar.set(Calendar.MINUTE,00)
		startCalendar.set(Calendar.SECOND,00)
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,6)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		def orderedVacationList=[]
		def periodList= Period.findAll(sort:'year',order:'asc')
		for (Period period:periodList){
			def vacations = Vacation.findAllByEmployeeAndPeriod(employeeInstance,period,[sort:'type',order:'asc'])
			for (Vacation vacation:vacations){
				orderedVacationList.add(vacation)
			}
			
		}
		
		def previousContracts = Contract.findAllByEmployee(employeeInstance,[sort:'date',order:'desc'])
		
		def siteId=params["siteId"]
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }
		def arrivalDate = employeeInstance.arrivalDate
		[previousContracts:previousContracts,arrivalDate:arrivalDate,orderedVacationList:orderedVacationList,orderedVacationListfromSite:fromSite,employeeInstance: employeeInstance,isAdmin:isAdmin,siteId:siteId]		
	}

	def cartouche(long userId,int year,int month){
		def employeeInstance = Employee.get(userId)
		return timeManagerService.getCartoucheData(employeeInstance,year,month)
	}
	
	
	def modifyAbsence(){
		def employee = Employee.get(params["employeeId"] as int)
		def day = params["day"]
		def supTime=params["payableSupTime"] as int
		def compTime=params["payableCompTime"] as int
		def monthlyTotal=params["monthlyTotalRecap"] as int
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
		def cartoucheTable = timeManagerService.getCartoucheData(employee,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1)	
		def workingDays=cartoucheTable.getAt('workingDays')
		def holiday=cartoucheTable.getAt('holidays')
		def rtt=cartoucheTable.getAt('rtt')
		def sickness=cartoucheTable.getAt('rtt')
		def sansSolde=cartoucheTable.getAt('sansSolde')
		def monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.getAt('monthTheoritical'))
		def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.getAt('pregnancyCredit'))
		def yearlyHoliday=cartoucheTable.getAt('yearlyHolidays')
		def yearlyRtt=cartoucheTable.getAt('yearlyRtt')
		def yearlySickness=cartoucheTable.getAt('yearlySickness')
		def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.getAt('yearlyTheoritical'))
		def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.getAt('yearlyPregnancyCredit'))
		def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.getAt('yearlyActualTotal'))
		def yearlySansSolde=cartoucheTable.getAt('yearlySansSolde')
		def payableSupTime=timeManagerService.computeHumanTime(supTime)
		def payableCompTime=timeManagerService.computeHumanTime(compTime)
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
		monthlyTotal=timeManagerService.computeHumanTime(monthlyTotal)
		
		def model=[period:cal.time,firstName:employee.firstName,lastName:employee.lastName,weeklyContractTime:employee.weeklyContractTime,matricule:employee.matricule,monthlyTotalRecap:monthlyTotal,yearInf:yearInf,yearSup:yearSup,employee:employee,openedDays:openedDays,workingDays:workingDays,holiday:holiday,rtt:rtt,sickness:sickness,sansSolde:sansSolde,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyHoliday:yearlyHoliday,yearlyRtt:yearlyRtt,yearlySickness:yearlySickness,yearlyTheoritical:yearlyTheoritical,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyActualTotal:yearlyActualTotal,yearlySansSolde:yearlySansSolde,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
		render template: "/common/cartoucheTemplate", model:model
		return
	}
	
	def addingEventToEmployee(Long id){
		def cal = Calendar.instance	
		def type = params["type"].equals("Entrer") ? "E" : "S" 
		def isOutSideSite=params["isOutSideSite"].equals("true") ? true : false
		Employee employeeInstance = Employee.get(params["userId"] as int)		
		def currentDate = cal.time
		def criteria
		def entranceStatus=false
		def timeDiff		
		def flashMessage=true
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).time
			
		
		log.error('trying to add event to employee '+employeeInstance.firstName+' '+employeeInstance.lastName+' with type: '+type)
		// liste les entrees de la journ�e et v�rifie que cette valeur n'est pas sup�rieure � une valeur statique
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
		
		def humanTime = timeManagerService.computeHumanTime(timeManagerService.getDailyTotal(inAndOuts))
		def dailySupp = timeManagerService.computeHumanTime(Math.max(timeManagerService.getDailyTotal(inAndOuts)-DailyTotal.maxWorkingTime,0))

		employeeInstance.status = type.equals("S") ? false : true
		
		if (type.equals("E")){
			if (flashMessage)
				flash.message = message(code: 'inAndOut.create.label', args: [message(code: 'inAndOut.entry.label', default: 'exit'), cal.time])
		}else{
			if (flashMessage)		
				flash.message = message(code: 'inAndOut.create.label', args: [message(code: 'inAndOut.exit.label', default: 'exit'), cal.time])
		}

		def model=[employee: employeeInstance,humanTime:humanTime, dailySupp:dailySupp,entranceStatus:entranceStatus,inAndOuts:inAndOuts]
		render template: "/common/currentDayTemplate", model:model
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
		
		def function = params["employee.function.id"]
		if (function!=null && !function.equals('')){
			function = Function.get(function)
			employeeInstance.function=function
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
		def criteria = InAndOut.createCriteria()
		def calendar = Calendar.instance
		calendar.time=inOrOut.time
		log.error('removing entry '+inOrOut)
		inOrOut.delete(flush:true)
		
		criteria = InAndOut.createCriteria()
		def inAndOutList = criteria.list {
			and {
				eq('employee',inOrOut.employee)
				eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('year',calendar.get(Calendar.YEAR))
				order('time','asc')
			}
		}
		render template: "/common/listInAndOutsTemplate", model: [inAndOutList:inAndOutList,day:calendar.get(Calendar.DAY_OF_MONTH),month:calendar.get(Calendar.MONTH)+1,year:calendar.get(Calendar.YEAR)]		
	}
	
	def showDay(){
		def day=params["day"] as int
		def month=params["month"] as int
		def year=params["year"] as int
		def employee = Employee.get(params["employeeId"])
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

		try{

			def month=monthList[0]
			def year=yearList[0]
			
			if (idList==null){
				log.error("list is null")
				def retour = report(employee.id as long,month as int,year as int)
				render(view: "report", model: retour)
			}
			
		timeManagerService.timeModification( idList, timeList, dayList, monthList, yearList, employee, newTimeList, fromRegularize)
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
				def currentMonth=params["myDate_month"] as int
				def currentYear=params["myDate_year"] as int	
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
		Employee employee = Employee.get(userId)
		
		if (params["myDate_year"] != null && !params["myDate_year"].equals('')){
			year = params["myDate_year"] as int
		}else{
			year = calendar.get(Calendar.YEAR)
		}
		if (params["myDate_month"] != null && !params["myDate_month"].equals('')){
			month = params["myDate_month"] as int
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

				
		def model = timeManagerService.getAnnualReportData(year,month, employee)
		if (isAjax){
			render template: "/common/annualReportTemplate", model:	model
			return
		}
		else{
			model
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
		def monthlyTotalTimeByEmployee = [:]
		def weeklyAggregate = [:]
		def dailyTotalMap = [:]
		def dailyBankHolidayMap = [:]
		def dailySupTotalMap = [:]
		def holidayMap = [:]
		def previousDay
		def employee
		def mapByDay = [:]
		def dailyTotalId=0
		def myDate = params["myDate"]
		def monthlySupTime = 0
		def monthlyTotalTime = 0
		def monthlyCompTime = 0				
		def criteria
		def dailySeconds = 0
		def weeklySupTime
		def currentWeek
		
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
			def currentDay=calendarLoop.time
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
			// permet de r�cup�rer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId){
				dailySeconds = timeManagerService.getDailyTotal(dailyTotal)
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
			def openedDays = timeManagerService.computeMonthlyHours(year,month)
			def workingDays=cartoucheTable.get('workingDays')
			def holiday=cartoucheTable.get('holidays')
			def rtt=cartoucheTable.get('rtt')
			def sickness=cartoucheTable.get('sickness')
			def sansSolde=cartoucheTable.get('sansSolde')
			def monthTheoritical = cartoucheTable.get('monthTheoritical')
			def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get('pregnancyCredit'))
			def yearlyHoliday=cartoucheTable.get('yearlyHolidays')
			def yearlyRtt=cartoucheTable.get('yearlyRtt')
			def yearlySickness=cartoucheTable.get('yearlySickness')
			def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyTheoritical'))
			def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyPregnancyCredit'))
			def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyActualTotal'))
			def yearlySansSolde=cartoucheTable.get('yearlySansSolde')
			def payableSupTime = timeManagerService.computeHumanTime(Math.round(monthlySupTime))
			def payableCompTime = timeManagerService.computeHumanTime(0)
			if (employee.weeklyContractTime!=35){
				if (monthlyTotalTime > monthTheoritical){
					payableCompTime = timeManagerService.computeHumanTime(Math.max(monthlyTotalTime-monthTheoritical-monthlySupTime,0))
				}
			}
			
			monthlyTotalTimeByEmployee.put(employee, timeManagerService.computeHumanTime(monthlyTotalTime))
			def monthlyTotal=timeManagerService.computeHumanTime(monthlyTotalTime)
			monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get('monthTheoritical'))
			
			def yearInf
			def yearSup
			if (month>5){
				yearInf=year
				yearSup=year+1
			}else{
				yearInf=year-1
				yearSup=year
			}

			def bankList = BankHoliday.findByCalendar(calendar);
			def model=[isAdmin:false,siteId:siteId,yearInf:yearInf,yearSup:yearSup,userId:userId,yearlyActualTotal:yearlyActualTotal,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTheoritical:yearlyTheoritical,yearlyTheoritical:yearlyTheoritical,period:calendar,monthlyTotal:monthlyTotalTimeByEmployee,weeklyTotal:weeklyTotalTimeByEmployee,weeklySupTotal:weeklySupTotalTimeByEmployee,dailySupTotalMap:dailySupTotalMap,dailyTotalMap:dailyTotalMap,month:month,year:year,period:calendarLoop.getTime(),dailyTotalMap:dailyTotalMap,holidayMap:holidayMap,weeklyAggregate:weeklyAggregate,employee:employee,payableSupTime:payableSupTime,payableCompTime:payableCompTime]
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
		def weekName="semaine "
		def myDate = params["myDate"]
		def employee

		if (myDate != null && myDate instanceof String){
			SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)			
		}

		if (userId==null && params["userId"] != null ){
			employee = Employee.get(params["userId"])
		}else{
			employee = Employee.get(userId)
		}
		//get last day of the month
		if (employee)
			return timeManagerService.getReportData(siteId, employee,  myDate, monthPeriod, yearPeriod)
	}

	def getUser(){
		def username = params["userName"]
		def cal = Calendar.instance
		Employee employeeInstance = Employee.findByUserName(username)
		def currentDate = cal.time
		def criteria
		def inOrOut
		def entranceStatus=false
		def timeDiff
		def flashMessage=true
		def status="E"
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).time
			
		
		// liste les entrees de la journ�e et v�rifie que cette valeur n'est pas sup�rieure � une valeur statique
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
			status = lastIn.type.equals("S") ? "E" : "S"
			
			use (TimeCategory){timeDiff=currentDate-LIT}
			//empecher de represser le bouton pendant 30 seconds
			if ((timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600)<30){
				flash.message = message(code: 'employee.overlogging.error')
				flashMessage=false
			}
		}
		
		// initialisation
		if (flashMessage){
			inOrOut = timeManagerService.initializeTotals(employeeInstance,currentDate,status,null,false)
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
		
		def humanTime = timeManagerService.computeHumanTime(timeManagerService.getDailyTotal(inAndOuts))
		def dailySupp = timeManagerService.computeHumanTime(Math.max(timeManagerService.getDailyTotal(inAndOuts)-DailyTotal.maxWorkingTime,0))

		employeeInstance.status = entranceStatus
		def result = new JSONEmployee()
		result.firstName=employeeInstance.firstName
		result.lastName=employeeInstance.lastName
		result.status=employeeInstance.status
		
		render result as JSON
		}
	
	
	def pointage(Long id){		
		def headerNames = request.getHeaderNames()
		def requestTT = request
		def clientIP=request.getHeader("X-Forwarded-For");
		def msg = "The Requestor IP: " + request.getRemoteAddr() + " Requestor Host name: " + request.getRemoteHost()
		log.error(msg)
		try {	
			def username = params["username"]
			def employee
			def entranceStatus
			def mapByDay=[:]
			def totalByDay=[:]
			def dailyCriteria
			def timeDiff
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
			while(tmpCalendar.get(Calendar.DAY_OF_YEAR) < (calendar.get(Calendar.DAY_OF_YEAR) -1)){
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
				elapsedSeconds = timeManagerService.getDailyTotal(lastInAndOuts)
				totalByDay.put(tmpCalendar.time, timeManagerService.computeHumanTime(elapsedSeconds))
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
			def humanTime = timeManagerService.computeHumanTime(timeManagerService.getDailyTotal(dailyTotal))
			def dailySupp = timeManagerService.computeHumanTime(Math.max(timeManagerService.getDailyTotal(dailyTotal)-DailyTotal.maxWorkingTime,0))
			
			
			if (inAndOuts!=null){
				def max = inAndOuts.size()
				if (max>0){
				def  lastEvent = inAndOuts.get(max-1)
				entranceStatus = lastEvent.type.equals("S") ? false : true
				}else{
				entranceStatus=false
				
				}
			}else{
				entranceStatus=false
			}
			
			[employee: employee,inAndOuts:inAndOuts,dailyTotal:dailyTotal,humanTime:humanTime, dailySupp:dailySupp,mapByDay:mapByDay,entranceStatus:entranceStatus,totalByDay:totalByDay]
		}
		catch (Exception e){
			log.error('error with application: '+e.toString())		
			flash.message = message(code: 'employee.not.found.label',args:[params["username"]])
			redirect(uri:'/')

		}
	}	

	
	def ecartPDF(){
		def siteId=params["site.id"]
		def myDate=params["myDate"]
		def year = params["year"]
		def folder = grailsApplication.config.pdf.directory
		def calendar = Calendar.instance
		def refCalendar = Calendar.instance
		def currentMonth=6
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
		def userId
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
	
	def userPDF(){
		def myDate = params["myDate"]
		def userId = params["userId"]
		Employee employee = Employee.get(userId as int)
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
		def year
		def month
		def calendar = Calendar.instance
		boolean isAjax = params["isAjax"].equals("true") ? true : false
		Employee employee = Employee.get(userId)
		def folder = grailsApplication.config.pdf.directory
		
		
		if (params["myDate_year"] != null && !params["myDate_year"].equals('')){
			year = params["myDate_year"] as int
		}else{
			year = calendar.get(Calendar.YEAR)
		}
		if (params["myDate_month"] != null && !params["myDate_month"].equals('')){
			month = params["myDate_month"] as int
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

		
		def retour = PDFService.generateUserAnnualTimeSheet(year,month,employee,folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]

	}
	
	def dailyTotalPDF(){
		log.error('method dailyTotalPDF called with parameters:')
		def employeeMap = [:]
		def dailyMap = [:]
		
		def day=params["day"]
		def siteId=params["site.id"]
		def currentDate = params["currentDate"]
		def dailyTotal
		def criteria 
		def elapsedSeconds
		def site
		def dailyInAndOutMap = [:]
		def dailySupMap = [:]
		def inAndOutList
		def employeeInstanceList
		def filename
		def calendar = Calendar.instance
		if (currentDate!=null)
		calendar.time=currentDate

		if (siteId!=null && !siteId.equals("")){
			site = Site.get(params["site.id"] as int)
			employeeInstanceList = Employee.findAllBySite(site)
	   }
	   
	   else{
		   employeeInstanceList = Employee.findAll([sort:'site',order:'asc'])	   
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
			   }		   
		   }
		   
		   dailyInAndOutMap.put(employee, inAndOutList)
		   elapsedSeconds = timeManagerService.getDailyTotal(dailyTotal)
		   if (elapsedSeconds > DailyTotal.maxWorkingTime){
			   dailySupMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds-DailyTotal.maxWorkingTime))
		   }else{
			   dailySupMap.put(employee,timeManagerService.computeHumanTime(0))
		   }
		   dailyMap.put(employee,timeManagerService.computeHumanTime(elapsedSeconds))
	   }
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/common/listDailyTimePDFTemplate', model: [dailyMap: dailyMap,site:site,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap,site:site,currentDate:calendar.time])
		OutputStream outputStream;	
		def folder = grailsApplication.config.pdf.directory
		if (site!=null)
			filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+(calendar.get(Calendar.DAY_OF_MONTH)).toString() +'-'+site.name+'.pdf'
		else
			filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+(calendar.get(Calendar.DAY_OF_MONTH)).toString() + '.pdf'
		
		try {
			outputStream = new FileOutputStream (folder+'/'+filename);
			bytes.writeTo(outputStream);
			return
		}finally {
			if(bytes)
			   bytes.close();

			if(outputStream)
			outputStream.close();
			File file = new File(folder+'/'+filename)
			
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "filename=${file.name}")
			response.outputStream << file.bytes
			return
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
	
	 def sendMail(){
		 def employeeList = Employee.list()
		 Calendar calendar = Calendar.instance
		 //find orphans
		 
		// for (Employee employee:employeeList){
		//	 employee
		 //}		 
		 
		 mailService.sendMail {
			 to "henri.martin@gmail.com"
			 from "pointeuse.biolab33@gmail.com"
			 subject "Hello John"
			 body 'this is some text'
		  }
	 }

	 def recomputeDailyTotals(){
		 def tmpInOrOut
		 def dailyDelta=0
		 def timeDiff
		 def criteria
		 def employeeList = Employee.findAll()
		//def employee = Employee.get(3)
		 for (Employee employee:employeeList){
			 print employee.firstName + " "+employee.lastName
			 criteria = DailyTotal.createCriteria()
			 def dailyTotalList = DailyTotal.findAllByEmployee(employee)
			 
			 for (DailyTotal dailyTotal: dailyTotalList){
				 tmpInOrOut=null
				 dailyDelta=0
				 criteria=InAndOut.createCriteria()
				 def inOrOutList = criteria.list {
					 and {
						 eq('employee',employee)
						 eq('year',dailyTotal.year)
						 eq('month',dailyTotal.month)
						 eq('day',dailyTotal.day)
					 }
					 order('time','asc')
				 }
				 
				 for (InAndOut inOrOut:inOrOutList){
					 if (inOrOut.type.equals("E")){
						 tmpInOrOut=inOrOut
					 }else{
						 if (tmpInOrOut!=null){
							 use (TimeCategory){timeDiff=inOrOut.time-tmpInOrOut.time}
							 dailyDelta+=timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600
						 }
					 }
				 }
				 dailyTotal.elapsedSeconds=dailyDelta
				 dailyTotal.weeklyTotal.elapsedSeconds+=dailyDelta
				 dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=dailyDelta
			 }
		 }
		 print "I am done!!"
		 
	 }
	 
	 
	 
	 def execute() {
		 log.error "Job run!"
		 
		 def inOrOut
		 //trouver toutes les entr�es du jour courant
		 //trouver, pour chaque employ� le plus r�cent
		 //Si ce dernier est une Entr�e, rajouter une sortie
		 // ajouter dans un nouveau champs de InAndOut que cette 'sortie' est g�n�r�e automatiquement pour la resortir en erreur dans les rapprts.
 
		 def employeeList = Employee.get(53)//Employee.findAll()
		 def calendar = Calendar.instance
		 for (employee in employeeList){
			 def lastIn = InAndOut.findByEmployee(employee,[max:1,sort:"time",order:"desc"])
			 if (lastIn != null && lastIn.type == "E"){
				 log.error "we have a problem: user "+employee.lastName +" did not log out"
				 inOrOut = new InAndOut(employee, calendar.time,"S",false)
				 inOrOut.dailyTotal=lastIn.dailyTotal
				 inOrOut.systemGenerated=true
				 employee.inAndOuts.add(inOrOut)
				 employee.status=false
				 employee.hasError=true
				 log.error "creating inOrOut: "+inOrOut
			 }
			 
			 //def inAndOutList = InAndOut.findAllBySystemGeneratedAndEmployee(true,employee)
			 def criteria = InAndOut.createCriteria()
			 def inAndOutList = criteria.list {
				 or{
					 and {
						 eq('employee',employee)
						 eq('systemGenerated',true)
					 }
					and {
						eq('employee',employee)
						eq('regularizationType',InAndOut.INITIALE_SALARIE)						
					} 
				 }
				
			 }
			 
			 if (inAndOutList != null && inAndOutList.size()>0){
				 log.error("there still "+inAndOutList.size() +" errors for employee "+employee.id + " : " +employee.lastName)
				 employee.hasError=true
			 }else {
			 	employee.hasError=false
			 }
			 
		 }
		 
	 }
	 
	 def excel(){
		 log.error('calling function excel')
		 def employeeList = Employee.getAll()
		 
		 OutputStream outputStream;
		 
		 
		 def year
		 def month
		 def criteria
		 def dailySeconds
		 def monthlyTotalTime
		 def monthlySupTotalTime
		 def yearMonthMap = [:]
		 def yearTotalMap = [:]
		 def yearSupMap = [:]
		 def yearMonthlySupTime = [:]
		 def yearMonthlyCompTime = [:]
		 def yearMap = [:]
		 def cartoucheTable=[]
		 def firstWeekOfMonth
		 def lastWeekOfMonth
		 def weeklySupTime
		 def payableCompTime
		 def payableSupTime
		 def annualTheoritical = 0
		 def annualTotal = 0
		 def annualHoliday = 0
		 def annualRTT = 0
		 def annualCSS = 0
		 def annualSickness = 0
		 def annualPayableSupTime = 0
		 def annualPayableCompTime = 0
		 def annualWorkingDays = 0
		 def annualEmployeeWorkingDays = 0
		 def annualTotalIncludingHS = 0
		 def calendar = Calendar.instance
		 
		 def userId=2
		 
		 if (params["myDate_year"] != null && !params["myDate_year"].equals('')){
			 year = params["myDate_year"] as int
		 }else{
			 year = calendar.get(Calendar.YEAR)
		 }
		 if (params["myDate_month"] != null && !params["myDate_month"].equals('')){
			 month = params["myDate_month"] as int
		 }else{
			 month = calendar.get(Calendar.MONTH)+1
		 }
		 
		 boolean isAjax = params["isAjax"].equals("true") ? true : false
		 
		 
		 Employee employee = Employee.get(userId)
		 
		 if (userId==null){
			 log.error('userId is null. exiting')
			 return
		 }
 
		 if (month < 6){
			 year = year - 1
		 }
		 
		 new WebXlsxExporter().with {
			 setResponseHeaders(response)
			 
			 fillRow(["aaa", "bbb", 13, new Date()], 1)
			 fillRow(["ccc", "ddd", 87, new Date()], 2)
			 putCellValue(3, 3, "Now I'm here")
			 save(response.outputStream)
		 }	 
			 
		 for (int lastYearMonth = 6 ;lastYearMonth <13 ; lastYearMonth++){
			 yearMap.put(lastYearMonth, year)
			 cartoucheTable=cartouche(userId,year,lastYearMonth)				 
			 yearMonthMap.put(lastYearMonth, cartoucheTable)
			 monthlyTotalTime = 0
			 monthlySupTotalTime = 0
			 calendar.set(Calendar.MONTH,lastYearMonth-1)
			 calendar.set(Calendar.YEAR,year)
			 calendar.set(Calendar.DAY_OF_MONTH,1)
			 firstWeekOfMonth = calendar.get(Calendar.WEEK_OF_YEAR)
			 calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
			 lastWeekOfMonth = calendar.get(Calendar.WEEK_OF_YEAR)			 
			 criteria = DailyTotal.createCriteria()
			 def dailyTotalList = criteria.list {
				 and {
					 eq('employee',employee)
					 eq('month',lastYearMonth)
					 eq('year',year)
				 }
			 }
			 dailySeconds = 0
			 for (DailyTotal dailyTotal:dailyTotalList){
				 dailySeconds = timeManagerService.getDailyTotal(dailyTotal)
				 monthlyTotalTime += dailySeconds
				 annualEmployeeWorkingDays += 1
			 }
			 yearTotalMap.put(lastYearMonth, timeManagerService.computeHumanTime(monthlyTotalTime))
			 // iterate over weeks of the given month to get supplementary time
			 for (int currentWeek = firstWeekOfMonth; currentWeek <= lastWeekOfMonth; currentWeek++){
				 monthlySupTotalTime += timeManagerService.computeSupplementaryTime(employee,currentWeek, year)
			 }
			 yearMonthlySupTime.put(lastYearMonth,timeManagerService.computeHumanTime(monthlySupTotalTime))
			 def monthTheoritical = cartoucheTable.get(8)
			 if (employee.weeklyContractTime!=35){
				 if (monthlyTotalTime > monthTheoritical){
					 payableCompTime = Math.max(monthlyTotalTime-monthTheoritical-monthlySupTotalTime,0)
					 yearMonthlyCompTime.put(lastYearMonth, timeManagerService.computeHumanTime(payableCompTime))
				 }else{
					 payableCompTime = 0
					 yearMonthlyCompTime.put(lastYearMonth, timeManagerService.computeHumanTime(0))
				 }
			 }else{
			 	payableCompTime = 0 
			 }
			 
			 annualTheoritical += cartoucheTable.get(8)
			 annualHoliday += cartoucheTable.get(4)
			 annualRTT += cartoucheTable.get(5)
			 annualCSS += cartoucheTable.get(7)
			 annualSickness += cartoucheTable.get(6)
			 annualWorkingDays += cartoucheTable.get(3)	 
			 annualPayableSupTime += monthlySupTotalTime
			 annualPayableCompTime += payableCompTime
			 annualTotal += monthlyTotalTime		 
		 }		 

		 def model=[annualTotalIncludingHS:timeManagerService.computeHumanTime(annualTotalIncludingHS),annualEmployeeWorkingDays:annualEmployeeWorkingDays,	annualTheoritical:timeManagerService.computeHumanTime(annualTheoritical),annualHoliday:annualHoliday,annualRTT:annualRTT,annualCSS:annualCSS,annualSickness:annualSickness,annualWorkingDays:annualWorkingDays,annualPayableSupTime:timeManagerService.computeHumanTime(annualPayableSupTime),annualPayableCompTime:timeManagerService.computeHumanTime(annualPayableCompTime),annualTotal:timeManagerService.computeHumanTime(annualTotal),
 lastYear:year,thisYear:year+1,yearMap:yearMap,yearMonthlyCompTime:yearMonthlyCompTime,yearMonthlySupTime:yearMonthlySupTime,yearTotalMap:yearTotalMap,yearMonthMap:yearMonthMap,userId:userId,employee:employee]

		 def withProperties = ['firstName', 'lastName']
		 new XlsxExporter('/Users/henri/Documents/myReportFile.xlsx').add(employeeList, withProperties).save()
	 }
	 
	 def ecartFollowup(){
		 def siteId=params["site.id"]
		 def myDate=params["myDate"]
		 def year = params["year"]		 
		 def employeeInstanceList
		 def employeeInstanceTotal
		 def calendar = Calendar.instance
		 def refCalendar = Calendar.instance 
		 def currentMonth=6
		 def site
		 def period
		 def monthList=[]
		 def fromIndex=params['fromIndex'].equals('true')?true:false
		 
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
		def retour = [site:site,fromIndex:fromIndex,period:period,employeeInstanceTotal:employeeInstanceTotal,monthList:monthList,employeeInstanceList:employeeInstanceList]
	 	retour << ecartData
		return retour
	}
	 
	def appendContract(){
		log.error('appendContract called')
		params.each{i->log.error(i)}
	}
	
	def someAction = {
		def DATE_PATTERN = 'date_'
		def CONTRACT_PATTERN='contract_info'
		log.error('someAction called')
		def contractList =[]
		def contractDate 
		def contractLength
		params.each{i->		
			if ((i.key).contains(CONTRACT_PATTERN)){
				log.error('param value: '+i.value)
				def contractInfos = i.value
				def length = contractInfos.size()
				for (int j = 0 ;j <length ; j++){
					if (j%2==0){
						Contract contract = new Contract()
						contract.date = new Date().parse("d/M/yyyy",  contractInfos[j])
						contract.year=contract.date.getAt(Calendar.YEAR)
						contract.month=(contract.date.getAt(Calendar.MONTH))+1
						contract.period=contract.month<6?Period.findByYear(contract.year-1):Period.findByYear(contract.year)
						contract.weeklyLength = contractInfos[j+1].toFloat()
						contract.employee = Employee.get(params.id)
						
						contract.save()
						contractList.add(contract)
					}		
				}
			}
		}
		Long userId = params.id
		redirect(action: "edit", params: [id:params.id,isAdmin:false])		
	  }
	 
}
