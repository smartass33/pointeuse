package pointeuse

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.core.io.Resource
import org.springframework.dao.DataIntegrityViolationException
import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable

import groovy.json.JsonSlurper
import groovy.time.TimeDuration
import groovy.time.TimeCategory
import grails.converters.JSON

import org.apache.commons.logging.LogFactory
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.codehaus.groovy.grails.core.io.ResourceLocator

import pl.touk.excel.export.WebXlsxExporter

import java.util.concurrent.*

import groovyx.gpars.GParsConfig
import groovyx.gpars.GParsPool



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
	ResourceLocator assetResourceLocator
	
	@Secured(['ROLE_ADMIN'])
    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
	def modifyAbsenceCounter(){
		def year=params["year"]
		def userId=params["userId"]
		Employee employee = Employee.get(userId)
		AbsenceCounter absenceCount = AbsenceCounter.findByEmployeeAndYear(employee,year)
		[absenceCount:absenceCount]
	}
	
	@Secured(['ROLE_ADMIN'])
	def expandList(){
		log.error('expandList called')
		params.each{i->log.error('parameter of list: '+i)}
		def employeeList
		
		def isExpanded = params.boolean('value')
		if (isExpanded){
			employeeList =	Employee.list([sort: "lastName", order: "asc"])	
		}else{
			def function = Function.findByName('Coursier')
			employeeList = Employee.findAllByFunction(function)
		}
			log.error('isExpanded:'+isExpanded)
			render template: "/itinerary/form", model:[employeeList : employeeList,checked:isExpanded]

			return		
		}

	@Secured(['ROLE_ADMIN'])
	def dailyReport(){
		def siteId=params["site.id"]
		def currentDate
		def calendar = Calendar.instance
		def fromIndex=params.boolean('fromIndex')
		
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
		}else{
			currentDate = calendar.time
		}
		def model = timeManagerService.getDailyInAndOutsData(siteId, currentDate)
		def startDate=calendar.time
		startDate.putAt(Calendar.HOUR_OF_DAY,6)
		startDate.putAt(Calendar.MINUTE,0)
		
		model << [startDate:"'"+startDate.format('yyyy-MM-dd HH:mm:SS')+"'"]
		if (model.get('site') != null){
			render template: "/employee/template/listDailyTimeTemplate", model:model
			return	
		}
		model
	}
	
	@Secured(['ROLE_ADMIN'])
	def sickLeaveDailyReport(){
		def siteId=params["site.id"]
		def currentDate
		def calendar = Calendar.instance
		def fromIndex=params.boolean('fromIndex')
		
		if (!fromIndex && (siteId == null || siteId.size() == 0)){
			flash.message = message(code: 'sickLeave.site.selection.error')
			params["fromIndex"]=true
			redirect(action: "sickLeaveDailyReport",params:params)
			return
		}
		
		def date_picker =params["date_picker"]
		if (date_picker != null && date_picker.size()>0){
			currentDate =  new Date().parse("dd/MM/yyyy", date_picker)
			calendar.time=currentDate
		}else{
			currentDate = calendar.time
		}
		
		def model = timeManagerService.getDailySickLeaveData(siteId, currentDate,AbsenceType.MALADIE)
		def startDate=calendar.time
		startDate.putAt(Calendar.HOUR_OF_DAY,6)
		startDate.putAt(Calendar.MINUTE,0)
		
		model << [startDate:"'"+startDate.format('yyyy-MM-dd HH:mm:SS')+"'"]
		if (model.get('site') != null){
			render template: "/employee/template/listSickLeaveDailyTemplate", model:model
			return
		}
		model
	}
	
	
	@Secured(['ROLE_ADMIN'])
	def annualSitesReportExcelExport(){
		def folder = grailsApplication.config.pdf.directory
		log.error('entering annualSitesReportExcelExport')
		def siteValue = params.boolean('siteValue')
		def siteFunction = params['siteFunction']
		def currentDate
		def calendar = Calendar.instance
		def fromIndex=params.boolean('fromIndex')
		def funtionCheckBoxesMap = [:]
		def period = Period.get(params.int('periodId'))
		def sites = Site.findAll("from Site")		
		def functions = Function.list([sort: "ranking", order: "asc"])
		def year = (period != null) ? period.year : calendar.get(Calendar.YEAR)
		def annualSiteReportMap = [:]
		if (period == null && year != null){
			period = Period.findByYear(year)
		}
	
		if (params['simpleFuntionCheckBoxesMap']  == null){
			def jsonSlurper = new JsonSlurper()
			funtionCheckBoxesMap = params['funtionCheckBoxesMap'] != null ? jsonSlurper.parseText(params['funtionCheckBoxesMap']) : [:]
		}else{
			def words = []
			words = (params['simpleFuntionCheckBoxesMap']).split("-")
			for (int j = 0 ;j < words.size() ; j++){
				if (j % 2 == 0){
					funtionCheckBoxesMap.put(words[j],(words[j + 1]).toBoolean())
				}
			}
		}
		if (siteFunction != null){
			funtionCheckBoxesMap.put(siteFunction,siteValue)
		}
		sites.each{siteIter ->
			log.debug('computing site: '+siteIter.name)
			annualSiteReportMap.put(siteIter,timeManagerService.getWeeklyReportData(year, siteIter, funtionCheckBoxesMap))
		}
		log.error('annualSitesReportExcelExport - sites retrieved')
				
		def headers = [message(code: 'laboratory.label')]
		functions.each{function ->
			headers.add(function.name)		
		}

		headers.add(message(code: 'weekly.particularism.1.noBR'))
		headers.add(message(code: 'weekly.particularism.2.noBR'))
		headers.add(message(code: 'sub.total'))
		headers.add(message(code: 'sub.total.minutes'))
		headers.add(message(code: 'weekly.case.noBR'))
		headers.add(message(code: 'weekly.home.assistance'))
		headers.add(message(code: 'ratio.employees.cases'))

		WebXlsxExporter webXlsxExporter = new WebXlsxExporter(folder+'/annual_report_template.xlsx').with {exporter ->
			
			def toto = exporter.getCellAt(0, 0)
			setResponseHeaders(response)
			fillHeader(headers)			
			int i = 1			
			annualSiteReportMap.each{site,mileageByEmployeeYear->
				log.debug('siteReport: '+mileageByEmployeeYear)
				def data = []
				data.add(site.name)
				mileageByEmployeeYear.each{employee,employeeMileageYearMap ->	
					employeeMileageYearMap.each{iterYear,employeeYearlyMileage ->
						data.add(employee.lastName)
						data.add(employeeYearlyMileage)
					}
				}
			
				fillRow(data,i)
				i += 1
			}
			save(response.outputStream)
		}
		response.setContentType("application/octet-stream")
	}
	
	@Secured(['ROLE_ADMIN'])
	def annualSitesReport(){
		params.each{i->log.debug('parameter of list: '+i)}
		def siteValue = params.boolean('siteValue')
		def siteFunction = params['siteFunction']
		def period = Period.get(params.int('periodId'))
		def fromWeeklyReport = params.boolean('fromWeeklyReport')
		def calendar = Calendar.instance
		def year = (period != null) ? period.year : calendar.get(Calendar.YEAR)
		def funtionCheckBoxesMap = [:]
		def annualSiteReportMap = [:]
		def model =[:]
		def sites = Site.findAll("from Site")
		def functions = Function.list([sort: "ranking", order: "asc"])
		
		if (!fromWeeklyReport){
			if (params['simpleFuntionCheckBoxesMap']  == null){
				def jsonSlurper = new JsonSlurper()
				funtionCheckBoxesMap = params['funtionCheckBoxesMap'] != null ? jsonSlurper.parseText(params['funtionCheckBoxesMap']) : [:]
			}else{
				def words = []
				words = (params['simpleFuntionCheckBoxesMap']).split("-")
				for (int j = 0 ;j < words.size() ; j++){
					if (j % 2 == 0){
						funtionCheckBoxesMap.put(words[j],(words[j + 1]).toBoolean())
					}
				}
			}
			
			if (siteFunction != null){
				funtionCheckBoxesMap.put(siteFunction,siteValue)
			}
			
			sites.each{siteIter ->
				log.debug('computing site: '+siteIter.name)
				annualSiteReportMap.put(siteIter,timeManagerService.getWeeklyReportData(year, siteIter, funtionCheckBoxesMap))
			}
			log.error('annualSitesReport - sites retrieved')
			
			model << [
				annualSiteReportMap:annualSiteReportMap,
				funtionCheckBoxesMap:funtionCheckBoxesMap,
				siteFunctionList:functions,
				siteList:sites,
				period:period
			]
			render template: "/employee/template/listAnnualSiteTimeTemplate", model:model
			return
		}else{
			[currentDate:calendar.time]
			return
		}
	}
	
	@Secured(['ROLE_ADMIN'])
	def modifyCases(){
		params.each{i->
			log.debug('param: '+i)
			
		}
		def type = params["type"]
		def value = params["value"]
		def site = Site.get(params["siteId"])
		def currentDate = params["myDate"]
		def funtionCheckBoxesMap = [:]
		SimpleDateFormat dateFormat
		def calendar = Calendar.instance
		def criteria
		def weeklyCase

		if (currentDate != null && currentDate instanceof String){
			dateFormat = new SimpleDateFormat('yyyyMMdd');
			currentDate = dateFormat.parse(currentDate)
			calendar.time = currentDate
		}
		def period = Period.get(params.int('periodId'))
		def year = (period != null) ? period.year : calendar.get(Calendar.YEAR)		
		
		if (params['simpleFuntionCheckBoxesMap']  == null){
			def jsonSlurper = new JsonSlurper()
			funtionCheckBoxesMap = params['funtionCheckBoxesMap'] != null ? jsonSlurper.parseText(params['funtionCheckBoxesMap']) : [:]
		}else{
			def words = []
			words = (params['simpleFuntionCheckBoxesMap']).split("-")
			for (int j = 0 ;j < words.size() ; j++){
				if (j % 2 == 0){
					funtionCheckBoxesMap.put(words[j],(words[j + 1]).toBoolean())
				}
			}
		}
		
		criteria = WeeklyCase.createCriteria()
		weeklyCase= criteria.get {
			and {
				eq('year',calendar.get(Calendar.YEAR))
				eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
				eq('site',site)
			}
		}
		if (weeklyCase != null){
			switch (type) {
				case 'cases':
					weeklyCase.cases = value as int
					break
				case 'home_assistance':
					weeklyCase.home_assistance = value as int
					break
				case 'particularity1':
					weeklyCase.particularity1 = timeManagerService.convertStringToTime(value) as int
					break
				case 'particularity2':
					weeklyCase.particularity2 = timeManagerService.convertStringToTime(value) as int
					break
				default:
					weeklyCase.cases = value as int
					break
			}
			weeklyCase.save(flush:true)
		}else{
			switch (type) {
				case 'cases':
					weeklyCase = new WeeklyCase(value as int,currentDate,site)
					break
				case 'home_assistance':
					weeklyCase = new WeeklyCase(0,currentDate,site)
					weeklyCase.home_assistance = value as int
					break
				case 'particularity1':
					weeklyCase = new WeeklyCase(0,currentDate,site)
					weeklyCase.particularity1 = timeManagerService.convertStringToTime(value) as int
					break
				case 'particularity2':
					weeklyCase = new WeeklyCase(0,currentDate,site)
					weeklyCase.particularity2 = timeManagerService.convertStringToTime(value) as int
					break
				default:
					weeklyCase = new WeeklyCase(value as int,currentDate,site)
					break
			}
			weeklyCase.save(flush:true)
		}
		
		
		def model = timeManagerService.getWeeklyReportData(year, site, funtionCheckBoxesMap)
		def weeklyCasesMap = model.get('weeklyCasesMap')
		def weeklyCasesMapParticularity1Text = [:]
		def weeklyCasesMapParticularity2Text = [:]
		weeklyCasesMap.each{k,v->
			def outputString
			outputString = (v != null) ? timeManagerService.computeHumanTimeAsText(v.particularity1) : timeManagerService.computeHumanTimeAsText(0)
			weeklyCasesMapParticularity1Text.put(k,outputString)
			outputString = (v != null) ? timeManagerService.computeHumanTimeAsText(v.particularity2) : timeManagerService.computeHumanTimeAsText(0)
			weeklyCasesMapParticularity2Text.put(k,outputString)
		}
		

		
		model << [
			site:site,
			period:period,
			firstYear:period.year,
			lastYear:period.year+1,
			weeklyCasesMapParticularity1Text:weeklyCasesMapParticularity1Text,
			weeklyCasesMapParticularity2Text:weeklyCasesMapParticularity2Text,
			funtionCheckBoxesMap:funtionCheckBoxesMap
			]

		if (site != null){
			render template: "/employee/template/listWeeklyTimeTemplate", model:model
			return
		}
		
	}
	
	@Secured(['ROLE_ADMIN'])
	def weeklyReportExcelExport(){
		def folder = grailsApplication.config.pdf.directory
		log.error('entering weeklyReportExcelExport')
		def siteValue = params.boolean('siteValue')
		def siteFunction = params['siteFunction']
		def currentDate
		def calendar = Calendar.instance
		def fromIndex=params.boolean('fromIndex')
		def siteId = params['siteId']
		def site = Site.get(siteId)
		def funtionCheckBoxesMap = [:]
		def period = Period.get(params.int('periodId'))
		def year = (period != null) ? period.year : calendar.get(Calendar.YEAR)
		if (period == null && year != null){
			period = Period.findByYear(year)
		}
		def calendarMonday = Calendar.instance
		def calendarSaturday = Calendar.instance
		calendarMonday.set(Calendar.YEAR,year)
		calendarSaturday.set(Calendar.YEAR,year)
		
		if (params['simpleFuntionCheckBoxesMap']  == null){
			def jsonSlurper = new JsonSlurper()
			funtionCheckBoxesMap = params['funtionCheckBoxesMap'] != null ? jsonSlurper.parseText(params['funtionCheckBoxesMap']) : [:]
		}else{
			def words = []
			words = (params['simpleFuntionCheckBoxesMap']).split("-")
			for (int j = 0 ;j < words.size() ; j++){
				if (j % 2 == 0){
					funtionCheckBoxesMap.put(words[j],(words[j + 1]).toBoolean())
				}
			}
		}
		if (siteFunction != null){
			funtionCheckBoxesMap.put(siteFunction,siteValue)
		}
		def model = timeManagerService.getWeeklyReportData(year, site, funtionCheckBoxesMap)
		def headers = [message(code: 'default.week')]		
		for (Employee employee: model.get('employeeInstanceList')){
			headers.add(employee.lastName)
		}
		
		model.get('siteFunctionMap').each{key,value ->
			headers.add(key)
		}
		headers.add(message(code: 'weekly.particularism.1.noBR'))
		headers.add(message(code: 'weekly.particularism.2.noBR'))
		headers.add(message(code: 'sub.total'))
		headers.add(message(code: 'sub.total.minutes'))
		headers.add(message(code: 'weekly.case.noBR'))
		headers.add(message(code: 'weekly.home.assistance'))
		headers.add(message(code: 'ratio.employees.cases'))

		WebXlsxExporter webXlsxExporter = new WebXlsxExporter(folder+'/weekly_report_template.xlsx').with {
			setResponseHeaders(response)
			def weekList = model.get('weekList')
			def employeeInstanceList = model.get('employeeInstanceList')
			def weeklyFunctionTotalMap = model.get('weeklyFunctionTotalMap')
			def siteFunctionMap = model.get('siteFunctionMap')
			def weeklySubTotalsByWeek = model.get('weeklySubTotalsByWeek')
			def weeklyCasesMap = model.get('weeklyCasesMap')
			def yearlyTotalsByFunction = model.get('yearlyTotalsByFunction')
			def yearlyTotalsByEmployee = model.get('yearlyTotalsByEmployee')
			setResponseHeaders(response)
			fillHeader(headers)
			
			int i = 1
			
			for (weekNumber in weekList){
				def data = []
				log.debug('weekNumber: '+weekNumber)
				calendarMonday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)
				calendarMonday.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
				calendarSaturday.set(Calendar.WEEK_OF_YEAR,weekNumber as int)
				calendarSaturday.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY)
						
				if (weekNumber == 1){
					calendarMonday.set(Calendar.YEAR,year + 1)
					calendarSaturday.set(Calendar.YEAR,year + 1)
				}
				
				data.add(calendarMonday.time.format('EE dd/MM/yyyy') + '-' + calendarSaturday.time.format('EE dd/MM/yy'))
				for (Employee currentEmployee in employeeInstanceList){
					log.debug('currentEmployee: '+currentEmployee)
					def weeklyTotalsByWeek = model.get('weeklyTotalsByWeek')
					if (weeklyTotalsByWeek != null && weeklyTotalsByWeek.get(weekNumber) != null && weeklyTotalsByWeek.get(weekNumber).get(currentEmployee) != null ){
						data.add(timeManagerService.writeHumanTime(model.get('weeklyTotalsByWeek').get(weekNumber).get(currentEmployee) as long))
					}else{
						data.add(timeManagerService.writeHumanTime(0))
					}
				}				
				siteFunctionMap.each{key,value ->
					log.debug("siteFunctionMap key: "+key)
					if (weeklyFunctionTotalMap != null && weeklyFunctionTotalMap.get(weekNumber)!= null && weeklyFunctionTotalMap.get(weekNumber).get(value) != null){
						data.add(timeManagerService.writeHumanTime(weeklyFunctionTotalMap.get(weekNumber).get(value) as long))
					}else{
						data.add(timeManagerService.writeHumanTime(0))
					}
				}
				

				//particularity
				if (weeklyCasesMap != null && weeklyCasesMap.get(weekNumber) != null  && (weeklyCasesMap.get(weekNumber)).particularity1 != null){
					data.add(timeManagerService.writeHumanTime(weeklyCasesMap.get(weekNumber).particularity1 as long))
				}else{
					data.add(timeManagerService.writeHumanTime(0))
				}
				if (weeklyCasesMap != null && weeklyCasesMap.get(weekNumber) != null  && (weeklyCasesMap.get(weekNumber)).particularity2 != null){
					data.add(timeManagerService.writeHumanTime(weeklyCasesMap.get(weekNumber).particularity2 as long))
				}else{
					data.add(timeManagerService.writeHumanTime(0))
				}
				
								
				
				if (weeklySubTotalsByWeek != null && weeklySubTotalsByWeek.get(weekNumber)!= null){
					// sous total hebdo
					data.add(timeManagerService.writeHumanTime(weeklySubTotalsByWeek.get(weekNumber) as long))
					// sous total hebdo en minute
					data.add(Math.round((weeklySubTotalsByWeek.get(weekNumber) as long) / 60))
				}else{
					data.add(timeManagerService.writeHumanTime(0))
					data.add(0)
				}
				
				if (weeklyCasesMap != null && weeklyCasesMap.get(weekNumber)!= null){					
					data.add(weeklyCasesMap.get(weekNumber).cases)
					data.add(weeklyCasesMap.get(weekNumber).home_assistance)
				}else{
					data.add(0)
					data.add(0)
				}
				def dataSize = data.size()
				// get subtotal minutes
				if (dataSize > 3){
					def subTotal = data[dataSize - 3]
				// get last item : cases
					def caseNubmers = data.last()
					def home_assistances = data[dataSize - 2]
				//apply ratio (minutes/cases)
					if (caseNubmers > 0 || home_assistances > 0){
						data.add((subTotal / (caseNubmers + home_assistances)).toDouble().round(2))
					}else{
						data.add(0)
					}
				}else{
					data.add(0)
				}
				fillRow(data,i)
				i += 1
			}
			// TOTALS:
			def totalLine = []
			totalLine.add(message(code: 'weekly.report.totals'))
			for (Employee employeeIter in employeeInstanceList){
				if (yearlyTotalsByEmployee != null && yearlyTotalsByEmployee.get(employeeIter) != null){
					totalLine.add(timeManagerService.writeHumanTime(yearlyTotalsByEmployee.get(employeeIter)))
				}else{
					totalLine.add(timeManagerService.writeHumanTime(0))
				}
			}
			siteFunctionMap.each{key,value ->
				if (yearlyTotalsByFunction != null && yearlyTotalsByFunction.get(value) != null){
					totalLine.add(timeManagerService.writeHumanTime(yearlyTotalsByFunction.get(value)))
				}else{
					totalLine.add(timeManagerService.writeHumanTime(0))
				}		
			}
			
			totalLine.add(timeManagerService.writeHumanTime(model.get('yearlyParticularity1')))
			totalLine.add(timeManagerService.writeHumanTime(model.get('yearlyParticularity2')))
			totalLine.add(timeManagerService.writeHumanTime(model.get('yearlySubTotals')))
			totalLine.add(Math.round((model.get('yearlySubTotals') as long) / 60))
			totalLine.add(model.get('yearlyCases'))
			totalLine.add(model.get('yearlyHomeAssistance'))
			
			
			
			
			log.error("(model.get('yearlyCases') + model.get('yearlyHomeAssistance'))": +(model.get('yearlyCases') + model.get('yearlyHomeAssistance')))
			
			if (model.get('yearlyCases') + model.get('yearlyHomeAssistance') != 0 ){
				totalLine.add(((Math.round((model.get('yearlySubTotals') as long) / 60)) / (model.get('yearlyCases') + model.get('yearlyHomeAssistance'))).toDouble().round(2))
			}else{
				totalLine.add((0).toDouble().round(2))
			}
			
			fillRow(totalLine,i)

			
			save(response.outputStream)
		}
		response.setContentType("application/octet-stream")
	}
	
	@Secured(['ROLE_ADMIN'])
	def weeklyReport(){
		log.error('weeklyReport called')
		def siteValue = params.boolean('siteValue')
		def siteFunction = params['siteFunction']
		def currentDate
		def calendar = Calendar.instance
		def fromIndex=params.boolean('fromIndex')
		def site = Site.get(params.int('siteId'))
		def funtionCheckBoxesMap = [:]
		def period = Period.get(params.int('periodId'))
		def year = (period != null) ? period.year : calendar.get(Calendar.YEAR)
		if (period == null && year != null){
			period = Period.findByYear(year)
		}
		if (params['simpleFuntionCheckBoxesMap']  == null){
			def jsonSlurper = new JsonSlurper()
			funtionCheckBoxesMap = params['funtionCheckBoxesMap'] != null ? jsonSlurper.parseText(params['funtionCheckBoxesMap']) : [:]
		}else{
			def words = []
			words = (params['simpleFuntionCheckBoxesMap']).split("-")
			for (int j = 0 ;j < words.size() ; j++){
				if (j % 2 == 0){
					funtionCheckBoxesMap.put(words[j],(words[j + 1]).toBoolean())
				}
			}
		}
	
		if (siteFunction != null){
			funtionCheckBoxesMap.put(siteFunction,siteValue)
		}

		def model = timeManagerService.getWeeklyReportData(year, site, funtionCheckBoxesMap)
		

		if (!fromIndex && site == null){
			flash.message = message(code: 'weeklyTime.site.selection.error')
			params["fromIndex"]=true
			redirect(action: "weeklyReport",params:params)
			return
		}
		
		def weeklyCasesMap = model.get('weeklyCasesMap')
		def weeklyCasesMapParticularity1Text = [:]
		def weeklyCasesMapParticularity2Text = [:]
		weeklyCasesMap.each{k,v->			
			def outputString
			outputString = (v != null) ? timeManagerService.computeHumanTimeAsText(v.particularity1) : timeManagerService.computeHumanTimeAsText(0)
			weeklyCasesMapParticularity1Text.put(k,outputString)
			outputString = (v != null) ? timeManagerService.computeHumanTimeAsText(v.particularity2) : timeManagerService.computeHumanTimeAsText(0)
			weeklyCasesMapParticularity2Text.put(k,outputString)
		}
	
		model << [
			site:site,
			period:period,
			firstYear:period.year,
			lastYear:period.year+1,	
			weeklyCasesMapParticularity1Text:weeklyCasesMapParticularity1Text,
			weeklyCasesMapParticularity2Text:weeklyCasesMapParticularity2Text,
			funtionCheckBoxesMap:funtionCheckBoxesMap
			]

		if (site != null){
			render template: "/employee/template/listWeeklyTimeTemplate", model:model
			return
		}
		[currentDate:calendar.time]
	}
	
	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
		log.error('entering list for employee')
		params.each{i->log.debug('parameter of list: '+i)}
		params.sort='site'
		params.max = Math.min(max ?: 20, 100)
		def offset = params["offset"] != null ? params.int("offset") : 0
		log.debug('browser version: '+request.getHeader('User-Agent') )
		def employeeInstanceList
		def employeeInstanceTotal
		def site
		def siteId=params["site"]
		boolean back = (params["back"] != null && params["back"].equals("true")) ? true : false		
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].equals("true")) ? true : false	
		
		def user = springSecurityService.currentUser
		def username = user?.getUsername()
		def criteria = Employee.createCriteria()

					
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
				employeeInstanceList = Employee.findAllBySite(site,['sort':'lastName','offset':offset])
				employeeInstanceTotal = employeeInstanceList.size()		
				[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceList.size(),username:username,isAdmin:isAdmin,siteId:siteId,site:site]	
			}else {	
				def sites = Site.findAll("from Site")
				employeeInstanceList = []
				sites.each{siteTmp -> 
					employeeInstanceList.addAll(Employee.findAllBySite(siteTmp,['sort':'lastName','offset':offset]))			
				}
	
				employeeInstanceTotal = employeeInstanceList.size()
				[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceList.size(),username:username,isAdmin:isAdmin]		
			}
		}else{
			if (site != null){
				employeeInstanceList = Employee.findAllBySite(site,['sort':'lastName','offset':offset])
				employeeInstanceTotal = employeeInstanceList.size()
				render template: "/employee/template/listEmployeeTemplate", model:[employeeInstanceList: employeeInstanceList, employeeInstanceTotal: employeeInstanceList.size(),username:username,isAdmin:isAdmin,siteId:siteId,site:site]
				return
			}else{
				def sites = Site.findAll("from Site")
				employeeInstanceList = []
				for (Site siteTmp : sites){
					employeeInstanceList.addAll(Employee.findAllBySite(siteTmp,[offset: 0]))				
				}
				employeeInstanceTotal = employeeInstanceList.size()
			}
		}	
		
		log.debug('employee list returned')
		def newList = employeeInstanceList.take(20 + offset)
		def newList2 = newList.drop(offset)
		if (params["offset"] != null){
			render template: "/employee/template/listEmployeeTemplate", model:[
				employeeInstanceList: newList2, 
				employeeInstanceTotal: employeeInstanceTotal,
				username:username
				,isAdmin:isAdmin
				,siteId:siteId
				,site:site
			]
			return
		}
		[
			employeeInstanceList: newList2, 
			employeeInstanceTotal: employeeInstanceTotal,
			username:username,
			isAdmin:isAdmin,
			siteId:siteId,
			site:site
		]	
    }
	
	@Secured(['ROLE_ADMIN'])
    def create() {
		def service = params["employee.service.id"]
		def employeeInstance =new Employee(params)
		employeeInstance.service=Service.get(service)
        [employeeInstance: employeeInstance]
    }

	@Secured(['ROLE_ADMIN'])
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

	@Secured(['ROLE_ADMIN'])
	def search() {
		def isAdmin = (params["isAdmin"] != null && params["isAdmin"].split(" ").getAt(0).equals("true")) ? true : false
		def query = "*"+params.q+"*"
		log.error('query: '+query)
		if(query){
			def srchResults = searchableService.search(query)
			def employeeList = []
			for (Employee employee:srchResults.results){
				def tmpEmployee = Employee.get(employee.id)
				employeeList.add(tmpEmployee)
			}
			render template: "/employee/template/listEmployeeTemplate", model:
			[
				employeeInstanceList: employeeList, 
				employeeInstanceTotal: employeeList.size(),
				isAdmin:isAdmin
				]
			return
		}else{
			redirect(action: "list")
		}
	}

	@Secured(['ROLE_ADMIN'])
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
	

	
	
	
	@Secured(['ROLE_ADMIN'])
	def employeeExcelExport(){
		def folder = grailsApplication.config.pdf.directory
		log.error('entering employeeExcelExport')
		def site = Site.get(params.int("site.id"))
		def calendar = Calendar.instance
		def result
		def authorizations
		def authorization
		def value
		def headers = [
						message(code: 'employee.title.label'),
						message(code: 'employee.firstName.label'),
						message(code: 'employee.lastName.label'),
						message(code: 'employee.birthName.label'),
						message(code: 'employee.username.label'),						
						message(code: 'employee.matricule.label'),							
						message(code: 'employee.function.label'),						
						message(code: 'laboratory.label'),						
						message(code: 'service.label'),							
						message(code: 'employee.weekly.hour.label'),						
						message(code: 'employee.arrivalDate.short.label')]
		int i = 1
		def employeeValue = []	
		def employeeList = (site != null) ? Employee.findAllBySite(site) : Employee.list()	
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")
		def dataListRank = EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		
		def fieldMap = (dataListRank != null) ? dataListRank : [:]
		dataListRank.each{rank->
			headers.add(rank.fieldName)
		}

		def categoryList = Category.findAll()		
		for (Category category : categoryList){
			headers.add(message(code: 'default.authorization.label')+': \n'+category.name)
			headers.add(message(code: 'authorization.startDate.label'))
			headers.add(message(code: 'authorization.endDate.label'))
			def subCategoryList = category.subCategories
			for (SubCategory subCategory:subCategoryList){
				headers.add(message(code: 'default.authorization.label')+': \n'+subCategory.name)
				headers.add(message(code: 'authorization.startDate.label'))
				headers.add(message(code: 'authorization.endDate.label'))
			}
		}
		
		WebXlsxExporter webXlsxExporter = new WebXlsxExporter(folder+'/employee_list_template.xlsx').with {
			setResponseHeaders(response)		
			fillHeader(headers)
			for(employee in employeeList) {			
				employeeValue = []
				employeeValue.add(employee.title != null ? employee.title :'-')
				employeeValue.add(employee.lastName != null ? employee.lastName :'-')
				employeeValue.add(employee.firstName != null ? employee.firstName :'-')
				employeeValue.add((employee.birthName != null && !employee.birthName.equals('') ) ? employee.birthName :'-')				
				employeeValue.add(employee.userName != null ? employee.userName :'-')
				employeeValue.add(employee.matricule != null ? employee.matricule :'-')
				employeeValue.add(employee.function != null ? employee.function.name :'-')
				employeeValue.add(employee.site != null ? employee.site.name :'-')
				employeeValue.add(employee.service != null ? employee.service.name :'-')				
				employeeValue.add(employee.weeklyContractTime != null ? employee.weeklyContractTime :'-')
				employeeValue.add(employee.arrivalDate != null ? employee.arrivalDate.format('dd/MM/yyyy') :'-')
				
				fieldMap.each{rank->
					value = employee.extraData.get(rank.fieldName)	
					if (value == null || value.equals('')){
						value = '-'
					}			
					if ((employeeDataListMap.fieldMap.get(rank.fieldName)).equals('DATE')){
						if (value != null && !value.equals('-')){
							value = (new Date().parse("yyyyMd", value)).format('dd/MM/yyyy')
						}
					}				
					employeeValue.add(value)
				}			
				
				authorizations = Authorization.findByEmployee(employee)
				
				for (Category category : categoryList){
					
					
					authorization = Authorization.findByCategoryAndEmployee(category,employee)
					if (authorization != null){
						employeeValue.add(authorization.isAuthorized)
						employeeValue.add(authorization.startDate != null ? authorization.startDate.format('dd/MM/yyyy') :'-')
						employeeValue.add(authorization.endDate != null ? authorization.endDate.format('dd/MM/yyyy') :'-')
						
					}else{
						employeeValue.add('-')
						employeeValue.add('-')
						employeeValue.add('-')
					}
					
					def subCategoryList = category.subCategories
					for (SubCategory subCategory:subCategoryList){
						authorization = Authorization.findBySubCategoryAndEmployee(subCategory,employee)
						if (authorization != null){
							employeeValue.add(authorization.isAuthorized)
							employeeValue.add(authorization.startDate != null ? authorization.startDate.format('dd/MM/yyyy') :'-')
							employeeValue.add(authorization.endDate != null ? authorization.endDate.format('dd/MM/yyyy') :'-')
							
						}else{
							employeeValue.add('-')
							employeeValue.add('-')
							employeeValue.add('-')
						}
					}
				}
				
				

				fillRow(employeeValue,i)
				i+=1

			}		
			save(response.outputStream)
		}	
	}
	
	@Secured(['ROLE_ADMIN'])
	def vacationExcelExport(){
		params.each{i-> log.debug('param: '+i)}
		log.error('entering vacationExcelExport with params: site: '+params['site'])
		def folder = grailsApplication.config.pdf.directory	
		def siteId = params['siteId']
		def calendar = Calendar.instance	
		def result
		def site = Site.get(params.int("siteId"))
		if (params['myDate_month'] != null && params['myDate_year'] != null){
			calendar.set(Calendar.MONTH,params.int("myDate_month") - 1)
			calendar.set(Calendar.YEAR,params.int("myDate_year"))
		}
		
		if (site != null){
			result = employeeService.getMonthlyPresence(site,calendar.time)
		}
		
		def lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH,1)
		def headers = [message(code: 'laboratory.label'), message(code: 'employee.label')]
		for (int i = 1;i<lastDay + 1;i++){
			headers.add(calendar.time.format('E dd MMM'))
			calendar.roll(Calendar.DAY_OF_MONTH,1)
		}
		
		headers.add(AbsenceType.VACANCE)
		headers.add(AbsenceType.RTT)
		headers.add(AbsenceType.AUTRE)
		headers.add(AbsenceType.EXCEPTIONNEL)
		headers.add(AbsenceType.PATERNITE)
		headers.add(AbsenceType.CSS)
		headers.add(AbsenceType.DIF)
		headers.add(AbsenceType.DON)
		headers.add(AbsenceType.GROSSESSE)	
		headers.add(AbsenceType.MATERNITE)
		headers.add(AbsenceType.MALADIE)				
		def employeeDailyMap = result.get('employeeDailyMap')
		def employeeAbsenceMap = result.get('employeeAbsenceMap')
		def employeeList = result.get('employeeList')
		def dailyMap
		def dailyList = []
		def absenceList = []
		def absenceMap
		int i = 1
			
		new WebXlsxExporter(folder+'/vacation_template.xlsx').with {
			setResponseHeaders(response)
			fillHeader(headers)			
			for(employee in employeeList) {
				dailyList = [employee.site,employee.lastName]
				dailyMap = employeeDailyMap.get(employee)
				dailyMap.each{ k, v -> 
					dailyList.add(v)
				}
				absenceMap = employeeAbsenceMap.get(employee)
				if (absenceMap.get(AbsenceType.VACANCE) != null){
					dailyList.add(absenceMap.get(AbsenceType.VACANCE))
				}else{
					dailyList.add(0)
				}
				
				if (absenceMap.get(AbsenceType.RTT) != null){
					dailyList.add(absenceMap.get(AbsenceType.RTT))
				}else{
					dailyList.add(0)
				}				
				if (absenceMap.get(AbsenceType.AUTRE) != null){
					dailyList.add(absenceMap.get(AbsenceType.AUTRE))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.EXCEPTIONNEL) != null){
					dailyList.add(absenceMap.get(AbsenceType.EXCEPTIONNEL))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.PATERNITE) != null){
					dailyList.add(absenceMap.get(AbsenceType.PATERNITE))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.CSS) != null){
					dailyList.add(absenceMap.get(AbsenceType.CSS))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.DIF) != null){
					dailyList.add(absenceMap.get(AbsenceType.DIF))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.DON) != null){
					dailyList.add(absenceMap.get(AbsenceType.DON))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.GROSSESSE) != null){
					dailyList.add(absenceMap.get(AbsenceType.GROSSESSE))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.MATERNITE) != null){
					dailyList.add(absenceMap.get(AbsenceType.MATERNITE))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.MALADIE) != null){
					dailyList.add(absenceMap.get(AbsenceType.MALADIE))
				}else{
					dailyList.add(0)
				}
				if (absenceMap.get(AbsenceType.FORMATION) != null){
					dailyList.add(absenceMap.get(AbsenceType.FORMATION))
				}else{
					dailyList.add(0)
				}
				fillRow(dailyList,i)
				i+=1
			}
			save(response.outputStream)
		}
	}
	
	@Secured(['ROLE_ADMIN'])
	def monthlyVacationFollowup(){
		log.error('entering monthlyVacationFollowup')		
		def calendar = Calendar.instance
		def thisMonthCalendar = Calendar.instance
		def site
		def month = params.int("myDate_month")
		def year = params.int("myDate_year")
		def result
		def employeeList
		def boolean isCurrentMonth = false
		def criteria
		
		if (params["site.id"] != null && !params["site.id"].equals("")){
			def coco = params["site.id"]
			
			if (params["site.id"] instanceof String[]){
				if (params.("site.id")[0] != ""){
					site = Site.get(params.("site.id")[0])
				}
			}else{
				site = Site.get(params.("site.id"))
			}
		}
		if (site == null && params["siteId"] != null && !params["siteId"].equals("")){
			site = Site.get(params.int("siteId"))			
		}

		if (site == null){
			flash.message = message(code: 'site.cannot.be.null')
			redirect(action: "vacationFollowup")
			return
		}
		
		if (params['myDate_month'] != null && params['myDate_year'] != null){
			calendar.set(Calendar.MONTH,params.int("myDate_month") - 1)
			calendar.set(Calendar.YEAR,params.int("myDate_year"))
		}
		
		//special case: requested month is not over: need to stop at current day
		if (thisMonthCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && thisMonthCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) ){
			isCurrentMonth = true
		}
		if (calendar.get(Calendar.MONTH) >= thisMonthCalendar.get(Calendar.MONTH) || calendar.get(Calendar.YEAR) >= thisMonthCalendar.get(Calendar.YEAR) ){
			isCurrentMonth = true
		}
		
		Period period = (calendar.get(Calendar.MONTH) < 5) ? Period.findByYear(calendar.get(Calendar.YEAR) - 1) : Period.findByYear(calendar.get(Calendar.YEAR))
		def lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		def tmpDay = lastDay
		
		if (site != null){
			result = employeeService.getMonthlyPresence(site,calendar.time)
		}
		return [
			lastDay:tmpDay,
			employeeDailyMap:result.get('employeeDailyMap'),
			employeeAbsenceMap:result.get('employeeAbsenceMap'),			
			siteId:site.id,
			site:site,
			myDate:calendar.time
		]
		
	}

	@Secured(['ROLE_ADMIN'])
	def absenceFollowup(){
		log.error('absenceFollowup called')
		params.each{i->log.error(i)}
		boolean isMonthlyView = false
		boolean isYearlyView = false
		boolean isDailyView = false
		def folder = grailsApplication.config.pdf.directory
		def reportSpan = params["id"]		
		def max = params["max"] != null ? params.int("max") : 20
		def offset = params["offset"] != null ? params.int("offset") : 0
		def siteId
		def site
		def employeeInstanceList
		def criteria
		def absenceList
		def employeeInstanceTotal
		def currentCalendar = Calendar.instance
		def date_picker = params["date_picker"]
		def absenceMapByEmployee = [:]
		def absenceMapByDay = [:]
		def dayList = []
		def headers = ['nom']
		def period = (currentCalendar.get(Calendar.MONTH) >= 5) ? Period.findByYear(currentCalendar.get(Calendar.YEAR)) : Period.findByYear(currentCalendar.get(Calendar.YEAR) - 1)	
		def firstDayOfPeriod = Calendar.instance
		def initialDayOfPeriod = Calendar.instance
		
		def lastDayOfPeriod = Calendar.instance
		firstDayOfPeriod.set(Calendar.YEAR,period.year)
		firstDayOfPeriod.set(Calendar.MONTH,5)
		firstDayOfPeriod.set(Calendar.DAY_OF_MONTH,1)
		firstDayOfPeriod.clearTime()
		
		initialDayOfPeriod.set(Calendar.YEAR,period.year)
		initialDayOfPeriod.set(Calendar.MONTH,5)
		initialDayOfPeriod.set(Calendar.DAY_OF_MONTH,1)
		initialDayOfPeriod.clearTime()
		
		lastDayOfPeriod.set(Calendar.YEAR,period.year + 1)
		lastDayOfPeriod.set(Calendar.MONTH,5)
		lastDayOfPeriod.set(Calendar.DAY_OF_MONTH,1)
		lastDayOfPeriod.clearTime()
		
		if (date_picker != null && date_picker.size() > 0){
			currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
		}
		
		
		
		if (params["site.id"]!=null && !params["site.id"].equals("")){
			site = Site.get(params["site.id"] as long)
			siteId = site.id
		}
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site)
			employeeInstanceTotal = employeeInstanceList.size()
			employeeInstanceList = Employee.findAllBySite(site,[max:max,offset:offset])
			
		}else{
			employeeInstanceList = Employee.findAll()
			employeeInstanceTotal = employeeInstanceList.size()
		}
		switch (reportSpan) {
			// case daily
			case 'dailyView':
				isDailyView = true
				criteria = Absence.createCriteria()
				absenceList = criteria.list {
					and {
						'in'("employee",employeeInstanceList)
						eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
						eq('month',currentCalendar.get(Calendar.MONTH) + 1)
						eq('year',currentCalendar.get(Calendar.YEAR))
					}
				}
				break
			// case monthly
			case 'monthlyView':
				isMonthlyView = true
				
				
				for (Employee employee in employeeInstanceList){
					criteria = Absence.createCriteria()
					absenceList = criteria.list {
						and {
							eq('employee',employee)
							//'in'("employee",employeeInstanceList)
							eq('month',currentCalendar.get(Calendar.MONTH) + 1)
							eq('year',currentCalendar.get(Calendar.YEAR))
						}
					}
					
					absenceMapByDay = [:]
					for (Absence absence in absenceList){
						absenceMapByDay.put(absence.day as int, absence)
					
					}
					
					absenceMapByEmployee.put(employee, absenceMapByDay)
				}
				
				
				
				
				break
			// case yearly
			case 'yearlyView':
				isYearlyView = true
				while(firstDayOfPeriod.get(Calendar.MONTH) <= 11){
					log.debug('refCalendar: '+firstDayOfPeriod.time.format('d-M-yyyy'))
					dayList.add(firstDayOfPeriod.time.format('d-M-yyyy'))
					if (firstDayOfPeriod.get(Calendar.DAY_OF_YEAR) == firstDayOfPeriod.getActualMaximum(Calendar.DAY_OF_YEAR)){
						break
					}
					firstDayOfPeriod.roll(Calendar.DAY_OF_YEAR, 1)
				}
				firstDayOfPeriod.set(Calendar.MONTH,0)
				firstDayOfPeriod.set(Calendar.YEAR,period.year + 1)
				while(firstDayOfPeriod.get(Calendar.DAY_OF_YEAR) <= lastDayOfPeriod.get(Calendar.DAY_OF_YEAR)){
					log.debug('firstDayOfPeriod: '+firstDayOfPeriod.time.format('d-M-yyyy'))
					dayList.add(firstDayOfPeriod.time.format('d-M-yyyy'))
					firstDayOfPeriod.roll(Calendar.DAY_OF_YEAR, 1)
					if (firstDayOfPeriod.get(Calendar.DAY_OF_YEAR) == lastDayOfPeriod.get(Calendar.DAY_OF_YEAR)){
						break
					}
				}
				
				
				for (Employee employee in employeeInstanceList){
					absenceMapByDay = [:]
					criteria = Absence.createCriteria()
					absenceList = criteria.list {
						and {
							eq('employee',employee)
							//'in'("employee",employeeInstanceList)
							ge('date',initialDayOfPeriod.time)
							lt('date',lastDayOfPeriod.time)
						}
					}
					
					for (Absence absence in absenceList){
						absenceMapByDay.put(absence.day+'-'+absence.month+'-'+absence.year, absence)
					}
					
					for (int k = 0;k < dayList.size();k++){
						if (absenceMapByDay.get(dayList[k]) == null){
							absenceMapByDay.put(dayList[k],'-')
							headers.add(dayList[k])
						}
					}
					absenceMapByEmployee.put(employee, absenceMapByDay)
				}
				break

			default:
				break
		}
			
		if (isYearlyView){
			WebXlsxExporter webXlsxExporter = new WebXlsxExporter(folder+'/employee_list_template.xlsx').with {
				def data = []
				int i = 1
				setResponseHeaders(response)
				fillHeader(headers)
				absenceMapByEmployee.each{employeeInstance,absenceMapByDayInstance->
					data.add(employeeInstance)
					absenceMapByDayInstance.each{k,v->
						if (!v.equals('-')){
							data.add(v.type)
						}else{
							data.add('-')
						}
					}
					fillRow(data,i)
					i++
				}
				save(response.outputStream)
				response.setContentType("application/octet-stream")
			}
			
			return
		}
		
		def model = 
			[
				period:period,
				employeeInstanceList:employeeInstanceList,
				isYearlyView:isYearlyView,
				isMonthlyView:isMonthlyView,
				isDailyView:isDailyView,
				dayList:dayList,
				currentDate:currentCalendar.time,
				absenceMapByEmployee:absenceMapByEmployee,
				absenceList:absenceList
			]
		
		if (isDailyView || isMonthlyView || isYearlyView){
			render template: "/employee/template/listAbsenceEmployeeTemplate", model: model
			return 	
		}
		model
		
	}
	
	@Secured(['ROLE_ADMIN'])
	def vacationFollowAnnualExcel(){
		params.each{i->log.error(i)}
		def folder = grailsApplication.config.pdf.directory
		def max = params["max"] != null ? params.int("max") : 20
		def offset = params["offset"] != null ? params.int("offset") : 0
		def siteId
		def site
		def employeeInstanceList
		def criteria
		def absenceList
		def employeeInstanceTotal
		def currentCalendar = Calendar.instance
		def absenceMapByEmployee = [:]
		def absenceMapByDay = [:]
		def dayList = []
		def data = []
		def date_picker = params["date_picker"]
		def headers = ['nom']
		def period = (currentCalendar.get(Calendar.MONTH) >= 5) ? Period.findByYear(currentCalendar.get(Calendar.YEAR)) : Period.findByYear(currentCalendar.get(Calendar.YEAR) - 1)
		def firstDayOfPeriod = Calendar.instance
		def initialDayOfPeriod = Calendar.instance
		
		def lastDayOfPeriod = Calendar.instance
		firstDayOfPeriod.set(Calendar.YEAR,period.year)
		firstDayOfPeriod.set(Calendar.MONTH,5)
		firstDayOfPeriod.set(Calendar.DAY_OF_MONTH,1)
		firstDayOfPeriod.clearTime()
		
		initialDayOfPeriod.set(Calendar.YEAR,period.year)
		initialDayOfPeriod.set(Calendar.MONTH,5)
		initialDayOfPeriod.set(Calendar.DAY_OF_MONTH,1)
		initialDayOfPeriod.clearTime()
		
		lastDayOfPeriod.set(Calendar.YEAR,period.year + 1)
		lastDayOfPeriod.set(Calendar.MONTH,5)
		lastDayOfPeriod.set(Calendar.DAY_OF_MONTH,1)
		lastDayOfPeriod.clearTime()

		if (date_picker != null && date_picker.size() > 0){
			currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
		}
		
		if (params["site.id"]!=null && !params["site.id"].equals("")){
			site = Site.get(params["site.id"] as long)
			siteId = site.id
		}
		
		if (site != null){
			employeeInstanceList = Employee.findAllBySite(site)
			employeeInstanceTotal = employeeInstanceList.size()
			employeeInstanceList = Employee.findAllBySite(site,[max:max,offset:offset])
			
		}else{
			employeeInstanceList = Employee.findAll()
			employeeInstanceTotal = employeeInstanceList.size()
		}
		
		while(firstDayOfPeriod.get(Calendar.MONTH) <= 11){
			log.error('refCalendar: '+firstDayOfPeriod.time.format('d-M-yyyy'))
			dayList.add(firstDayOfPeriod.time.format('d-M-yyyy'))
			headers.add(firstDayOfPeriod.time.format('d-M-yyyy'))
			if (firstDayOfPeriod.get(Calendar.DAY_OF_YEAR) == firstDayOfPeriod.getActualMaximum(Calendar.DAY_OF_YEAR)){
				break
			}
			firstDayOfPeriod.roll(Calendar.DAY_OF_YEAR, 1)
		}
		firstDayOfPeriod.set(Calendar.MONTH,0)
		firstDayOfPeriod.set(Calendar.YEAR,period.year + 1)
		while(firstDayOfPeriod.get(Calendar.DAY_OF_YEAR) <= lastDayOfPeriod.get(Calendar.DAY_OF_YEAR)){
			log.error('firstDayOfPeriod: '+firstDayOfPeriod.time.format('d-M-yyyy'))
			dayList.add(firstDayOfPeriod.time.format('d-M-yyyy'))
			headers.add(firstDayOfPeriod.time.format('d-M-yyyy'))
			firstDayOfPeriod.roll(Calendar.DAY_OF_YEAR, 1)
			if (firstDayOfPeriod.get(Calendar.DAY_OF_YEAR) == lastDayOfPeriod.get(Calendar.DAY_OF_YEAR)){
				break
			}
		}
		
		for (Employee employee in employeeInstanceList){
			absenceMapByDay = [:]
			criteria = Absence.createCriteria()
			absenceList = criteria.list {
				and {
					eq('employee',employee)
					ge('date',initialDayOfPeriod.time)
					lt('date',lastDayOfPeriod.time)
				}
			}
			
			for (Absence absence in absenceList){
				absenceMapByDay.put(absence.day+'-'+absence.month+'-'+absence.year, absence)
			}
			
			for (int k = 0;k < dayList.size();k++){
				if (absenceMapByDay.get(dayList[k]) == null){
					absenceMapByDay.put(dayList[k],'-')
				}
			}
			absenceMapByEmployee.put(employee, absenceMapByDay)
		}
		
		WebXlsxExporter webXlsxExporter = new WebXlsxExporter(folder+'/employee_list_template.xlsx').with {	
			int i = 1
			setResponseHeaders(response)
			fillHeader(headers)
			absenceMapByEmployee.each{employeeInstance,absenceMapByDayInstance->
				data = []
				data.add(employeeInstance.lastName+' '+employeeInstance.firstName)
				
				for (int l = 0;l < dayList.size();l++){
					if (!(absenceMapByDayInstance.get(dayList[l])).equals('-')){
						data.add(absenceMapByDayInstance.get(dayList[l]).type)
					}else{
						data.add('-')
					}
				}

				fillRow(data,i)
				i++
			}
			save(response.outputStream)
			response.setContentType("application/octet-stream")
		}
	}
	
	@Secured(['ROLE_ADMIN'])
	def vacationFollowup(){
		log.error('vacationFollowup called')
		params.each{i->log.error(i)}
		
		def year = params["year"]
		def max = params["max"] != null ? params.int("max") : 20
		def offset = params["offset"] != null ? params.int("offset") : 0	
		def site
		def siteId
		def employeeInstanceList
		def period
		def criteria
		def initialCAMap = [:]
		def remainingCAMap = [:]
		def takenCAMap = [:]
		def initialRTTMap = [:]
		def remainingRTTMap = [:]
		def takenRTTMap = [:]
		def takenSicknessMap = [:]
		def takenMaterniteMap = [:]
		def takenCSSMap = [:]
		def takenAutreMap = [:]
		def takenExceptionnelMap = [:]		
		def takenPaterniteMap = [:]
		def takenDifMap = [:]
		def takenDonMap = [:]
		def formationMap = [:]
		def takenSickness
		def takenRTT
		def takenCA
		def takenCSS
		def takenAutre
		def takenExceptionnel
		def takenPaternite
		def takenMaternite
		def takenDIF	
		def takenDON
		def formation
		def employeeInstanceTotal
		
		if (params["site.id"]!=null && !params["site.id"].equals("")){
			site = Site.get(params["site.id"] as long)
			siteId=site.id	
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
			employeeInstanceList = []
				def statusList = Status.findAllByType(StatusType.ACTIF)
			for (Status status in statusList){
				employeeInstanceList.add(status.employee)
			}
			employeeInstanceTotal = employeeInstanceList.size()
			//employeeInstanceList = Employee.findAllByStatus(StatusType.ACTIF,[max:max,offset:offset])	
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
				
			// MATERNITE
			criteria = Absence.createCriteria()
			takenMaternite = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.MATERNITE)
				}
			}
			if (takenMaternite!=null){
				takenMaterniteMap.put(employee, takenMaternite.size())
			}else{
				takenMaterniteMap.put(employee, 0)
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
			
			//FORMATION
			criteria = Absence.createCriteria()
			formation = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.FORMATION)
				}
			}
			if (formation!=null){
				formationMap.put(employee, formation.size())
			}else{
				formationMap.put(employee, 0)
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
			
			//PATERNITE
			criteria = Absence.createCriteria()
			takenExceptionnel = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.PATERNITE)
				}
			}
			if (takenPaternite!=null){
				takenPaterniteMap.put(employee, takenPaternite.size())
			}else{
				takenPaterniteMap.put(employee, 0)
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
			
			//DON
			criteria = Absence.createCriteria()
			takenDON = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.DON)
				}
			}
			if (takenDON!=null){
				takenDonMap.put(employee, takenDON.size())
			}else{
				takenDonMap.put(employee, 0)
			}
		}
		log.debug("done")
		[
			employeeInstanceTotal:employeeInstanceTotal,
			period:period,
			employeeInstanceTotal:employeeInstanceTotal,
			site:site,
			siteId:siteId,
			employeeInstanceList:employeeInstanceList,
			takenCSSMap:takenCSSMap,
			takenAutreMap:takenAutreMap,
			takenSicknessMap:takenSicknessMap,
			takenRTTMap:takenRTTMap,
			takenExceptionnelMap:takenExceptionnelMap,
			takenPaterniteMap:takenPaterniteMap,
			takenDifMap:takenDifMap,
			takenCAMap:takenCAMap,
			initialCAMap:initialCAMap,
			initialRTTMap:initialRTTMap,
			remainingRTTMap:remainingRTTMap,
			remainingCAMap:remainingCAMap,
			formationMap:formationMap
		]	
	}
	
	@Secured(['ROLE_ADMIN'])
	def vacationDisplay(Long id){		
		def isAdmin = (params["isAdmin"] != null  && params["isAdmin"].equals("true")) ? true : false
		def siteId=params["siteId"]
		def employeeInstance = Employee.get(id)		
		def criteria
		def takenCA=[]
		def takenRTT=[]
		def takenCSS=[]
		def takenAutre=[]
		def formation=[]
		def takenSickness = []
		def takenMaternite = []
		def takenRTTMap=[:]
		def takenCAMap=[:]
		def yearMap=[:]
		def initialCAMap=[:]
		def remainingCAMap=[:]
		def initialRTTMap=[:]
		def remainingRTTMap=[:]
		def takenSicknessMap=[:]
		def takenMaterniteMap=[:]
		def takenCSSMap=[:]
		def takenAutreMap=[:]
		def formationMap=[:]
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
			takenMaternite = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.MATERNITE)
				}
			}
			if (takenMaternite!=null){
				takenMaterniteMap.put(period.year, takenMaternite.size())
			}else{
				takenMaterniteMap.put(period.year, 0)
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
			
			
			criteria = Absence.createCriteria()
			formation = criteria.list {
				and {
					eq('employee',employeeInstance)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.FORMATION)
				}
			}
			
			if (formation!=null){
				formationMap.put(period.year, formation.size())
			}else{
				formationMap.put(period.year, 0)
			}
						
		}
	[
		takenCSSMap:takenCSSMap,
		takenAutreMap:takenAutreMap,
		takenSicknessMap:takenSicknessMap,
		takenRTTMap:takenRTTMap,takenCAMap:takenCAMap,
		employeeInstance: employeeInstance,
		isAdmin:isAdmin,
		siteId:siteId,
		yearMap:yearMap,
		initialCAMap:initialCAMap,
		initialRTTMap:initialRTTMap,
		remainingRTTMap:remainingRTTMap,
		formationMap:formationMap,
		remainingCAMap:remainingCAMap
		]	

	}
	
	@Secured(['ROLE_ADMIN'])
    def edit(Long id) {
		def isAdmin = (params["isAdmin"] != null  && params["isAdmin"].equals("true")) ? true : false
		def fromSite = (params["fromSite"] != null  && params["fromSite"].equals("true")) ? true : false
		def back = (params["back"] != null  && params["back"].equals("true")) ? true : false		
        def employeeInstance = Employee.get(id)
		def myDate = params["myDate"] 
		def siteId=params["siteId"]
		def orderedVacationList=[]
		def orderedCAMap = [:]
		def orderedRTTMap = [:]
		
		def previousContracts
		def previousSickness
		def criteria = Vacation.createCriteria()
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
		
		def periodList = Period.findAll("from Period as p order by year asc")
		
		for (Period period:periodList){
			//get RTT
			criteria = Vacation.createCriteria()
			
			def rtt = criteria.get {
				and {
					eq('employee',employeeInstance)
					eq('period',period)
					eq('type',VacationType.RTT)
				}
			}
			orderedRTTMap.put(period, rtt)
			
			//get CA
			criteria = Vacation.createCriteria()
			def ca = criteria.get {
				and {
					eq('employee',employeeInstance)
					eq('period',period)
					eq('type',VacationType.CA)
				}
			}
			orderedCAMap.put(period, ca)
			
		}				
		

		
		previousContracts = Contract.findAllByEmployee(employeeInstance,[sort:'startDate',order:'desc'])
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), id])
            redirect(action: "list")
            return
        }
		def arrivalDate = employeeInstance.arrivalDate		
		def employeeDataListMap= EmployeeDataListMap.find("from EmployeeDataListMap")
		def dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		def authorizationInstanceList = Authorization.findAllByEmployee(employeeInstance)
	
		def retour = [
			authorizationInstanceList:authorizationInstanceList,
			fromEditEmployee:true,
			back:back,
			myDateFromEdit:myDate,
			previousContracts:previousContracts,
			arrivalDate:arrivalDate,
			orderedCAMap:orderedCAMap,
			orderedRTTMap:orderedRTTMap,
			periodList:periodList,
			employeeInstance: employeeInstance,
			isAdmin:isAdmin,siteId:siteId,
			employeeDataListMapInstance:employeeDataListMap,
			dataListRank:dataListRank
		]		
		return retour
	}
	
	@Secured(['ROLE_ADMIN'])
	def sickLeaveReport(){
		params.each{i-> log.error('param: '+i) }
		
		log.error('sickLeaveReport called')
		def isAdmin = (params["isAdmin"] != null  && params["isAdmin"].equals("true")) ? true : false
		def fromSite = (params["fromSite"] != null  && params["fromSite"].equals("true")) ? true : false
		def back = (params["back"] != null  && params["back"].equals("true")) ? true : false
		def employee = Employee.get(params["employeeId"])
		def myDate = params["myDate"]
		def siteId=params["siteId"]

		def criteria
		def previousSickness
		
		// list sick leave period
		criteria = Absence.createCriteria()
		def sickLeaveList = criteria.list {
			and {
				eq('type',AbsenceType.MALADIE)
				eq('employee',employee)
				order('date','asc')
			}
		}
		
		
		//def periodList = Period.findAll()
		def sickLeaveMap = [:]
		def leaveCouple = [null,null]
		def leaveList = []
		def period
		def sickness
		
		def sicknessCalendar = Calendar.instance
		def previousSicknessCalendar = Calendar.instance
		
		if (sickLeaveList != null && sickLeaveList.size() > 0){
			
			// initialization
			leaveCouple[0] = sickLeaveList.get(0)
			previousSickness = sickLeaveList.get(0)
			
			// now that we have all sick leave, we are going to group them by 2 based on date continuity

			for (int i = 1;i < sickLeaveList.size();i++){
				sickness = sickLeaveList.get(i)				
				sicknessCalendar.time = sickness.date
				previousSicknessCalendar.time = previousSickness.date	
			
				log.error('sickness: '+sickness)
				log.error('sicknessCalendar.time: '+sicknessCalendar.time)
				log.error('previousSicknessCalendar.time: '+previousSicknessCalendar.time)
				if (
					(sicknessCalendar.get(Calendar.DAY_OF_YEAR) == previousSicknessCalendar.get(Calendar.DAY_OF_YEAR) + 1)
					||
					(sicknessCalendar.get(Calendar.DAY_OF_YEAR) == sicknessCalendar.getActualMinimum(Calendar.DAY_OF_YEAR) && previousSicknessCalendar.get(Calendar.DAY_OF_YEAR)  == previousSicknessCalendar.getActualMaximum(Calendar.DAY_OF_YEAR))
					){
					leaveCouple[1] = sickness
					log.error("dates are consecutive")
					if (i == sickLeaveList.size() - 1){
						leaveList.add(leaveCouple)
					}
				}else{
						log.error("dates not are consecutive")
						leaveCouple[1] = previousSickness
						leaveList.add(leaveCouple)
					//	period = ((leaveCouple[0]).date.getAt(Calendar.MONTH) >= 5) ? Period.findByYear((leaveCouple[0]).date.getAt(Calendar.YEAR)) : Period.findByYear((leaveCouple[0]).date.getAt(Calendar.YEAR) - 1)
					//	sickLeaveMap.put(period, leaveList)
						
						leaveCouple = [sickness,null]
				}
				previousSickness = sickness
			}		
		}
			
		 log.error("finalizing list")
		 return [
			 leaveList:leaveList,
			 fromEditEmployee:true,
			 back:back,
			 myDateFromEdit:myDate,
			 employeeInstance: employee,
			 employeeId:employee.id,
			 isAdmin:isAdmin,
			 siteId:employee.site.id
		//	 sickLeaveMap:sickLeaveMap
			 ]

		 
	}
	
	@Secured(['ROLE_ADMIN'])
	def getAjaxSupplementaryTime(Long id) {
		def year = params.int('year')
 		def month = params.int('month')
		 
		log.error("getAjaxSupplementaryTime triggered with params: month="+month+" and year="+year)
		def employee = Employee.get(id)		
		def model
		def currentContract
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month)
	
		if (employee){
			model = timeManagerService.getYearSupTime(employee,year,month,false)
		}
		
		log.error("getAjaxSupplementaryTime has terminated")
		model << [id:id,month:month,year:year]
		render template: "/employee/template/yearSupplementaryTime", model: model
		return 
	}
	
	@Secured(['ROLE_ADMIN'])
	def getAjaxOffHoursTime(Long id){
		log.error('getOffHoursTime called')
		def year = params.int('year')
		def employee = Employee.get(id)
		def data
		def annualBefore7Time = 0
		def annualAfter20Time = 0
		def model = timeManagerService.getOffHoursTime(employee,year)
		log.error("getOffHoursTime has terminated")
		render template: "/employee/template/offHoursTime", model: model
		return 
	}
	
	@Secured(['ROLE_ADMIN'])
	def getSupplementaryTime(Long id) {
		def employeeInstance = Employee.get(id)
		def data
		def period = ((new Date()).getAt(Calendar.MONTH) >= 5) ? Period.findByYear(new Date().getAt(Calendar.YEAR)) : Period.findByYear(new Date().getAt(Calendar.YEAR) - 1)	
		data = supplementaryTimeService.getAllSupAndCompTime(employeeInstance,period)	
		def model = [employeeInstance:employeeInstance]
		model << data 		
		return model
	}
	
	@Secured(['ROLE_ADMIN'])
	def cartouche(long userId,int year,int month){
		def employeeInstance = Employee.get(userId)
		return timeManagerService.getCartoucheData(employeeInstance,year,month)
	}
	
	@Secured(['ROLE_ADMIN'])
	def modifyAllAbsence(){
		log.error('entering modifyAllAbsence')
		
		def employee = Employee.get(params.int('employeeId'))
		def updatedSelection = params["updatedSelection"].toString()
		if (updatedSelection.equals('G'))
			updatedSelection = AbsenceType.GROSSESSE
		if (updatedSelection.equals('-'))
			updatedSelection = AbsenceType.ANNULATION
		if (updatedSelection.equals('M'))
			updatedSelection = AbsenceType.MALADIE
		if (updatedSelection.equals('FO'))
			updatedSelection = AbsenceType.FORMATION
		if (updatedSelection.equals('CE'))
			updatedSelection = AbsenceType.EXCEPTIONNEL
		
		if (updatedSelection.equals('RTT'))
			updatedSelection = AbsenceType.RTT
		
		if (updatedSelection.equals('V'))
			updatedSelection = AbsenceType.VACANCE
		
		if (updatedSelection.equals('R'))
			updatedSelection = AbsenceType.AUTRE
		
		if (updatedSelection.equals('CSS'))
			updatedSelection = AbsenceType.CSS
		
		if (updatedSelection.equals('F'))
			updatedSelection = AbsenceType.FERIE
		
		if (updatedSelection.equals('CP'))
			updatedSelection = AbsenceType.PATERNITE
		
		if (updatedSelection.equals('CM'))
			updatedSelection = AbsenceType.MATERNITE
		
		if (updatedSelection.equals('DIF'))
			updatedSelection = AbsenceType.DIF
		
		if (updatedSelection.equals('DON'))
			updatedSelection = AbsenceType.DON
			
		if (updatedSelection.equals('AI'))
			updatedSelection = AbsenceType.INJUSTIFIE
			
		SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
		Date date = dateFormat.parse(params["period"])
		def calendarLoop= Calendar.instance
		def criteria
		calendarLoop.time=date
		calendarLoop.set(Calendar.DAY_OF_MONTH,1)
		log.error(calendarLoop.time)
		// check if an absence was already logged:			
		while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendarLoop.getActualMaximum(Calendar.DAY_OF_MONTH)){
			if (!updatedSelection.equals('')){
				criteria = Absence.createCriteria()
				// get cumul holidays
				def absence = criteria.get {
					and {
						eq('employee',employee)
						eq('year',calendarLoop.get(Calendar.YEAR))
						eq('month',calendarLoop.get(Calendar.MONTH)+1)
						eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
					}
				}
		
				if (absence != null){
					if (updatedSelection.equals(AbsenceType.ANNULATION.key)){
						// annulation nécessaire: il faut effacer le tupple
						absence.delete(flush: true)
					}else{
						absence.type=updatedSelection
						if (absence.month < 6){
							absence.period=Period.findByYear(absence.year - 1)
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
						absence.day=calendarLoop.get(Calendar.DAY_OF_MONTH)
						absence.month=calendarLoop.get(Calendar.MONTH)+1
						absence.year=calendarLoop.get(Calendar.YEAR)
						absence.type=updatedSelection
						if (absence.month < 6){
							absence.period=Period.findByYear(absence.year - 1)
						}else{
							absence.period=Period.findByYear(absence.year)
						}
						absence.save(flush: true)
					}
				}
			}else{
				flash.message=message(code: 'absence.impossible.update')
			}
			if (calendarLoop.get(Calendar.DAY_OF_MONTH) == calendarLoop.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)	
		}

		def report = timeManagerService.getReportData(null, employee,  null, calendarLoop.get(Calendar.MONTH) + 1, calendarLoop.get(Calendar.YEAR),true)
		def openedDays = timeManagerService.computeMonthlyHours(calendarLoop.get(Calendar.YEAR),calendarLoop.get(Calendar.MONTH) + 1)
		def yearInf
		def yearSup
		if ((calendarLoop.get(Calendar.MONTH) + 1) > 5){
			yearInf=calendarLoop.get(Calendar.YEAR)
			yearSup=calendarLoop.get(Calendar.YEAR) + 1
		}else{
			yearInf=calendarLoop.get(Calendar.YEAR) - 1
			yearSup=calendarLoop.get(Calendar.YEAR)
		}
		Period period = ((calendarLoop.get(Calendar.MONTH) + 1) > 5)?Period.findByYear(calendarLoop.get(Calendar.YEAR)):Period.findByYear(calendarLoop.get(Calendar.YEAR) - 1)
		
		def model=[
			period2:period,
			period:calendarLoop.time,
			firstName:employee.firstName,
			lastName:employee.lastName,
			weeklyContractTime:employee.weeklyContractTime,
			matricule:employee.matricule,
			yearInf:yearInf,
			yearSup:yearSup,
			employee:employee,
			openedDays:openedDays
			]
		model << report
		log.error('modifyAllAbsence finished')
		render template: "/employee/template/reportTableTemplate", model: model
		return
	}
	
	@Secured(['ROLE_ADMIN'])
	def modifyAbsence(){
		def employee = Employee.get(params.int('employeeId'))
		def day = params["day"]
		def supTime=params['payableSupTime']
		def updatedSelection = params["updatedSelection"].toString()
		if (updatedSelection.equals('G'))
			updatedSelection = AbsenceType.GROSSESSE
		if (updatedSelection.equals('-'))
			updatedSelection = AbsenceType.ANNULATION
		if (updatedSelection.equals('M'))
			updatedSelection = AbsenceType.MALADIE
		if (updatedSelection.equals('FO'))
			updatedSelection = AbsenceType.FORMATION
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
					// annulation nécessaire: il faut effacer le tupple
					absence.delete(flush: true)
				}else{
					absence.type=updatedSelection					
					if (absence.month < 6){
						absence.period=Period.findByYear(absence.year - 1)
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
					if (absence.month < 6){
						absence.period=Period.findByYear(absence.year - 1)
					}else{
						absence.period=Period.findByYear(absence.year)
					}			
					absence.save(flush: true)
				}
			}
		}else{
			flash.message=message(code: 'absence.impossible.update')
		}	
		def cartoucheTable = timeManagerService.getReportData(null, employee,  null, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),true)
		def openedDays = timeManagerService.computeMonthlyHours(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1)
		def yearInf
		def yearSup
		if ((cal.get(Calendar.MONTH)+1)>5){
			yearInf=cal.get(Calendar.YEAR)
			yearSup=cal.get(Calendar.YEAR) + 1
		}else{
			yearInf=cal.get(Calendar.YEAR) - 1
			yearSup=cal.get(Calendar.YEAR)
		}
		Period period = ((cal.get(Calendar.MONTH) + 1) > 5)?Period.findByYear(cal.get(Calendar.YEAR)):Period.findByYear(cal.get(Calendar.YEAR) - 1)
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
	
	@Secured(['ROLE_ADMIN'])
	def modifySite(){
		log.error('modifySite called')
		def employee = Employee.get(params.int('employeeId'))
		def siteName = params["updatedSite"].toString()
		def day = params["day"]
		SimpleDateFormat dateFormat = new SimpleDateFormat('dd/MM/yyyy');
		Date date = dateFormat.parse(day)
		def cal = Calendar.instance
		def criteria
		def dailyTotal
		def site
		cal.time = date	
		if (siteName != null && siteName.size() > 0 && !siteName.equals('')){
			site  = Site.findByName(siteName)
			if (site != null){
				criteria = DailyTotal.createCriteria()			
				// get cumul holidays
				dailyTotal = criteria.get {
					and {
						eq('employee',employee)
						eq('year',cal.get(Calendar.YEAR))
						eq('month',cal.get(Calendar.MONTH) + 1)
						eq('day',cal.get(Calendar.DAY_OF_MONTH))
					}
				}
				if (dailyTotal == null){
					timeManagerService.initializeTotals(employee, cal.time,site)
				}else{
					dailyTotal.site = site
					dailyTotal.save(flush: true)
				}
			}
		}
		def report = timeManagerService.getReportData(null, employee,  null, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),true)
		log.error('modifySite ended')		
		render template: "/employee/template/reportTableTemplate", model: report	
		return
	}
	
	@Secured(['ROLE_ANONYMOUS','ROLE_ADMIN'])
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
			if ((timeDiff.seconds + timeDiff.minutes*60 + timeDiff.hours*3600) < 30){
				flash.message = message(code: 'employee.overlogging.error')
				flashMessage=false
			}
		}
		
		// initialisation
		if (flashMessage){
			def inOrOut = timeManagerService.initializeTotals(employeeInstance,currentDate,type,null,isOutSideSite)
			timeManagerService.getDailyTotalWithMonth(inOrOut.dailyTotal)	
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
	
	@Secured(['ROLE_ADMIN'])
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
		log.debug('employee status: '+employeeInstance.status)	
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

		/* dealing with employee extra data*/ 
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMapInstance = EmployeeDataListMap.find("from EmployeeDataListMap")

		employeeDataListMapInstance.fieldMap.each{k,v->
			
			log.error('key: '+k)
			log.error('value: '+v)
			log.error('params[k]: '+params[k])
			if (employeeInstance.extraData == null){
				employeeInstance.extraData = [:]
			}
			if (params[k] != null){
				if (v.equals('DATE')){			
					employeeInstance.extraData.put(k,params[k+'_year']+params[k+'_month']+params[k+'_day'])
				}else{
					employeeInstance.extraData.put(k,params[k])
				}
				if (params[k] == ''){
					employeeInstance.extraData.remove(k)
				}
			}		
		}
		//employeeInstance.extraData
		//employeeInstance.status = employeeStatus		
        if (!employeeInstance.save(flush: true)) {
            render(view: "edit", model: [employeeInstance: employeeInstance])
            return
        }
        flash.message = message(code: 'default.updated.message', args: [message(code: 'employee.label', default: 'Employee'), employeeInstance.userName])
        redirect(action: "edit", id: employeeInstance.id,params: [isAdmin: isAdmin,siteId:siteId])
    }
	
	@Secured(['ROLE_ADMIN'])
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
		
	@Secured(['ROLE_ADMIN'])
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
	
	@Secured(['ROLE_ADMIN'])
    def delete(Long id) {		
        def employeeInstance = Employee.get(id)
		def employeeName = employeeInstance.firstName + ' ' +employeeInstance.lastName
        if (!employeeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'employee.label', default: 'Employee'), employeeName])
            redirect(action: "list")
            return
        }
        try {
			def folder = grailsApplication.config.pdf.directory
			def mysqldump_folder = grailsApplication.config.mysqldump.directory
			def file = folder+'/'+employeeInstance.lastName+'_'+(new Date().format('yyyy-MM-dd'))+'.sql'
			def file1 = folder+'/'+employeeInstance.lastName+'_'+(new Date().format('yyyy-MM-dd'))+'1.sql'
			def file2 = folder+'/'+employeeInstance.lastName+'_'+(new Date().format('yyyy-MM-dd'))+'2.sql'
			
			def threads = []
			def dump1_thread = new Thread({
				log.error('creating thread for mysqldump 1 ')
				(mysqldump_folder+'/mysqldump -u root --no-create-info --where=id='+employeeInstance.id+' -C pointeuse employee --result-file='+file1).execute()
			})
			threads << dump1_thread
			
			def dump2_thread = new Thread({
				log.error('creating thread for mysqldump 2 ')
				(mysqldump_folder+'/mysqldump -u root --no-create-info --where=employee_id='+employeeInstance.id+' --ignore-table=pointeuse.absence_type_config --ignore-table=pointeuse.authorization_nature --ignore-table=pointeuse.authorization_type --ignore-table=pointeuse.bank_holiday --ignore-table=pointeuse.card_terminal --ignore-table=pointeuse.employee_data_list_map --ignore-table=pointeuse.employee_data_list_map_field_map --ignore-table=pointeuse.employee_data_list_map_hidden_field_map --ignore-table=pointeuse.employee_extra_data --ignore-table=pointeuse.event_log --ignore-table=pointeuse.exception_logger --ignore-table=pointeuse.function --ignore-table=pointeuse.period --ignore-table=pointeuse.reason --ignore-table=pointeuse.role --ignore-table=pointeuse.service --ignore-table=pointeuse.site --ignore-table=pointeuse.site_user --ignore-table=pointeuse.status --ignore-table=pointeuse.user --ignore-table=pointeuse.user_role --ignore-table=pointeuse.year --ignore-table=pointeuse.employee --ignore-table=pointeuse.dummy --ignore-table=pointeuse.monthly_total_weekly_total -C pointeuse --result-file='+file2).execute()
			})
			threads << dump2_thread		
			threads.each { it.start() }
			threads.each { it.join() }
			def delete_thread = new Thread({
					new File(file).withWriter { w ->				
					[file1,file2].each{ f ->	
						// Get a reader for the input file
						new File(f).withReader { r ->					
						  // And write data from the input into the output
						 w << r << '\n'
						}
					}
				}
				boolean file1SuccessfullyDeleted =  new File(file1).delete()
				boolean file2SuccessfullyDeleted =  new File(file2).delete()
			})
			delete_thread.sleep((long)(2000))
			delete_thread.start()
				
			employeeInstance.delete()//(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'employee.label', default: 'Employee'), employeeName])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			log.error('error with application: '+e.toString())
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'employee.label', default: 'Employee'), employeeName])
            redirect(action: "show", id: id)
        }
    }
	
	@Secured(['ROLE_ADMIN'])
	def validate(){
		def eventId=params["inOrOutId"]
		def inOrOut = InAndOut.get(eventId)
		log.error('validating entry '+inOrOut)
		inOrOut.regularizationType=InAndOut.MODIFIEE_ADMIN
		inOrOut.save(flush:true)
	}
	
	@Secured(['ROLE_ADMIN'])
	def trash(){
		def eventId = params["inOrOutId"]
		def inOrOut = InAndOut.get(eventId)
		def month = inOrOut.month
		def year = inOrOut.year
		def employee = inOrOut.employee
		log.error('removing entry '+inOrOut)
		inOrOut.delete(flush:true)
		def report = timeManagerService.getReportData(null, employee,  null, month, year,true)			
		render template: "/employee/template/reportTableTemplate", model: report
		return
	}
		
	@Secured(['ROLE_ADMIN'])
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
				//render(view: "index")
				redirect(uri:'/')
				
				return
			}else{
				def retour = report(employee.id as long,month as int,year as int)
				render(view: "report", model: retour)
				return
			}
			
		}catch(NullPointerException e2){
			flash.message = message(code: "appliquer.inutile.message")
			log.error(e2.message)
			if (fromRegularize){
				render(view: "index")
				return
			}else{
				def currentMonth=params.int('myDate_month')
				def currentYear=params.int('myDate_year')	
				def retour = report(employee.id as long,currentMonth as int,currentYear as int)
				render(view: "report", model: retour)
				return
			}
		}
	}
	
	@Secured(['ROLE_ADMIN'])
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
		model << [period:period,siteId:employee.site.id,periodId:period.id]
		if (isAjax){
			render template: "/employee/template/annualReportTemplate", model:	model
			return
		}
		else{
			model
		}
	}
	
	@Secured(['ROLE_ANONYMOUS'])
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
	
	@Secured(['ROLE_ANONYMOUS'])
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
		def mileageMap = [:]
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
			// elimine les dimanches du rapport
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
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), dailySeconds+previousValue)
				}else{
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), dailySeconds)
				}
				
				if (!isSunday && calendarLoop.get(Calendar.WEEK_OF_YEAR)==lastWeekParam.get(0) ){
					weeklySupTime = 0
				}else{
					weeklySupTime = timeManagerService.computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
				}
				weeklySuppTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR),Math.round(weeklySupTime))
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
					dailyTotalMap.put(tmpDate, dailySeconds)
					dailySupTotalMap.put(tmpDate, Math.max(dailySeconds-DailyTotal.maxWorkingTime,0))
				}else {
					dailyTotalMap.put(tmpDate, 0)
					dailySupTotalMap.put(tmpDate, 0)
				}		
				mapByDay.put(tmpDate, entriesByDay)
			}	
			else{
				dailyTotalMap.put(tmpDate, 0)
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
			
			def mileageCriteria = Mileage.createCriteria()
			def dailyMileage = mileageCriteria.get {
				and {
					eq('employee',employee)
					eq('year',calendarLoop.get(Calendar.YEAR))
					eq('month',calendarLoop.get(Calendar.MONTH)+1)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
				}
			}
			mileageMap.put(tmpDate,dailyMileage)
			
			
			weeklyAggregate.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), mapByDay)	
			if (calendarLoop.get(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}	
		def maxDate = calendarLoop.time
		def minCal = Calendar.instance
		minCal.set(Calendar.HOUR_OF_DAY,23)
		minCal.set(Calendar.MINUTE,59)
		minCal.set(Calendar.SECOND,59)
		
		minCal.set(Calendar.DAY_OF_MONTH,1)
		minCal.set(Calendar.YEAR,year)
		minCal.set(Calendar.MONTH,month - 1)
		def minDate = minCal.time
		
		
		
		//def mileageFigures = timeManagerService.getMileage(minDate, maxDate, employee)
			
		try {
			if (userId != null){
				def cartoucheTable = timeManagerService.getCartoucheData(employee,year,month)
				def monthTheoritical = cartoucheTable.get('monthTheoritical')
				def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get('pregnancyCredit') as long)
				def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyTheoritical') as long)
				def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get('yearlyPregnancyCredit') as long)
				def payableSupTime = timeManagerService.computeHumanTime(Math.round(monthlySupTime) as long)
				def payableCompTime = timeManagerService.computeHumanTime(0)
				def currentContract = cartoucheTable.get('currentContract')
							
				if (currentContract.weeklyLength!=35){
					if (monthlyTotalTime > monthTheoritical){
						payableCompTime = timeManagerService.computeHumanTime(Math.max(monthlyTotalTime-monthTheoritical-monthlySupTime,0) as long)
					}
				}
			
				monthlyTotalTimeByEmployee.put(employee, timeManagerService.computeHumanTime(monthlyTotalTime as long))
				monthTheoritical = timeManagerService.getTimeAsText(timeManagerService.computeHumanTime(cartoucheTable.get('monthTheoritical') as long),true)		
	
				if (month>5){
					yearInf=year
					yearSup=year+1
				}else{
					yearInf=year-1
					yearSup=year
				}
	
				def model=[
					mileageMap:mileageMap,
					isAdmin:false,
					siteId:siteId,
					currentContract:currentContract,				
					yearInf:yearInf,
					yearSup:yearSup,
					userId:userId,
					yearlyActualTotal:cartoucheTable.get('yearlyActualTotal'),
					monthTheoritical:monthTheoritical,
					pregnancyCredit:pregnancyCredit,
					yearlyPregnancyCredit:yearlyPregnancyCredit,
					yearlyTheoritical:yearlyTheoritical,
					monthlyTotal:monthlyTotalTimeByEmployee,
					weeklyTotal:weeklyTotalTimeByEmployee,
					weeklySupTotal:weeklySupTotalTimeByEmployee,
					dailySupTotalMap:dailySupTotalMap,
					dailyTotalMap:dailyTotalMap,
					month:month,year:year,
					period:calendarLoop.getTime(),
					holidayMap:holidayMap,
					weeklyAggregate:weeklyAggregate,
					employee:employee,
					payableSupTime:payableSupTime,
					payableCompTime:payableCompTime
				]
				model << cartoucheTable
				return model
			}
		}catch (NullPointerException e){
			log.error('error with application: '+e.toString())
		}	
	}
	

	@Secured(['ROLE_ADMIN'])
	def report(Long userId,int monthPeriod,int yearPeriod){
		params.each{i-> log.error('param: '+i) }
		def siteId=params["siteId"]
		def myDate = params["myDate"]
		def myDateFromEdit = params["myDateFromEdit"]
		SimpleDateFormat dateFormat
		def employee
		def report = [:]
		def currentContract
		def calendar = Calendar.instance

		if (myDate != null && myDate instanceof String){
			dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)			
		}
		if (myDateFromEdit != null && myDateFromEdit instanceof String){
			dateFormat = new SimpleDateFormat('MM/yyyy');
			myDate = dateFormat.parse(myDateFromEdit)
		}	
		
		
		if (userId == null && params["userId"] != null ){
			employee = Employee.get(params["userId"])
		}else{
			employee = Employee.get(userId)
		}
		//get last day of the month
		if (employee){			
			report = timeManagerService.getReportData(siteId, employee,  myDate, monthPeriod, yearPeriod,false)
			currentContract = report.getAt('currentContract')
			if (currentContract == null){
				def lastContract = Contract.findByEmployee(employee,[max: 1, sort: "endDate", order: "desc"])
				if (lastContract != null){
					flash.message = message(code: 'employee.is.gone.error')	
					report = timeManagerService.getReportData(siteId, employee,  null, lastContract.endDate.getAt(Calendar.MONTH)+1, lastContract.endDate.getAt(Calendar.YEAR),false)	
					calendar.set(Calendar.MONTH,lastContract.endDate.getAt(Calendar.MONTH))
					calendar.set(Calendar.YEAR,lastContract.endDate.getAt(Calendar.YEAR))				
				}else{
					flash.message = message(code: 'employee.not.arrived.error')	
					report = timeManagerService.getReportData(siteId, employee,  null, employee.arrivalDate.getAt(Calendar.MONTH)+1, employee.arrivalDate.getAt(Calendar.YEAR),false)
					calendar.set(Calendar.MONTH,employee.arrivalDate.getAt(Calendar.MONTH))
					calendar.set(Calendar.YEAR,employee.arrivalDate.getAt(Calendar.YEAR))				
				}					
			}
		}
		
		if (myDate != null){
			calendar.time = myDate
		}
		report << [period:calendar.time]
		return report			
	}

	def pointage(Long id){	
		log.error('pointage called')
		log.debug('IP address: '+request.getHeader("X-Forwarded-For"))
		log.debug('request.remoteAddr: '+request.remoteAddr)
		def clientIP = request.getHeader("X-Forwarded-For")
		def authorizedIPList = grailsApplication.config.ip.authorized
		def isIPAuthorizationOn = grailsApplication.config.ip.authorization.on
		 
		
		if (isIPAuthorizationOn && !authorizedIPList.contains(clientIP) && !authorizedIPList.contains(request.remoteAddr)){
			log.error('found no matching IP, exiting...')
			flash.message = message(code: 'employee.pointage.wrong.ip')
			redirect(uri:'/')
		}
		
		
		try {	
			def username = params["username"]
			def employee
			def entranceStatus
			def mapByDay=[:]
			def totalByDay=[:]
			def dailyCriteria
			def elapsedSeconds=0
			if (id != null){
				employee = Employee.get(id)		
			}
			if (username != null && !username.equals("")){
				employee = Employee.findByUserName(username)	
			}
			
			if (employee == null){
				throw new NullPointerException("unknown employee("+username+")")
			}else{
				if (employee.status.type.equals(StatusType.SUSPENDU) || employee.status.type.equals(StatusType.TERMINE)){
					throw new NullPointerException("unknown employee("+username+")")
				}
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

	@Secured(['ROLE_ADMIN'])
	def ecartEXCEL(){
		def folder = grailsApplication.config.pdf.directory
		def totalMonthlyTheoritical = params["totalMonthlyTheoritical"] as long
		def totalMonthlyActual = params["totalMonthlyActual"] as long 
		def employeeInstanceListCount = params["employeeInstanceListCount"] as long
		def ecart = totalMonthlyActual - totalMonthlyTheoritical
		def site = Site.get(params["siteId"])		
		def headers = 
			[
				message(code: 'site.unit'),
				message(code: 'laboratory.label'), 
				message(code: 'site.total.theoritical.time'),
				message(code: 'site.total.actual.time'),
				message(code: 'site.total.delta.time'),
				message(code: 'site.total.employee')
			]
		
		new WebXlsxExporter(folder+'/ecart_site.xlsx').with {
			setResponseHeaders(response)
			fillHeader(headers)
			//fillRow([message(code: 'site.unit.seconds'),site.name,totalMonthlyTheoritical,totalMonthlyActual,ecart,employeeInstanceListCount],1)
			fillRow([message(code: 'site.unit.hours'),site.name,totalMonthlyTheoritical/3600,totalMonthlyActual/3600,ecart/3600,employeeInstanceListCount],1)	
			save(response.outputStream)
		}
	}
	
	
	@Secured(['ROLE_ADMIN'])
	def ecartPDF(){
		def siteId=params["site.id"]
		def year = params["year"]
		def folder = grailsApplication.config.pdf.directory
		def calendar = Calendar.instance
		def refCalendar = Calendar.instance
		def site
		def period
		def monthList=[]
		
		
		if (params["site.id"]!=null && !params["site.id"].equals('')){
			site = Site.get(params["site.id"].toInteger())
		}else{
			flash.message = message(code: 'pdf.site.selection.error')
			redirect(action: "ecartFollowup")
			return
		}
		
		
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
	
	@Secured(['ROLE_ADMIN'])
	def siteMonthlyPDF(){
		def myDate = params["myDate"]
		def site
		def siteId
		def timeDifference
		Calendar calendar = Calendar.instance
		calendar.roll(Calendar.MONTH,-1)
		
		def startTime = calendar.time
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
		calendar = Calendar.instance
		def endTime = calendar.time
		use (TimeCategory){timeDifference = endTime - startTime}
		log.error("le rapport a pris: "+timeDifference)

		def file = new File(folder+'/'+retour[1])
		render(file: file, fileName: retour[1],contentType: "application/octet-stream")		
	}
	
	@Secured(['ROLE_ADMIN'])
	def allSiteMonthlyPDF(){
		def myDate = params["myDate"]

		Calendar calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
			
		def retour = PDFService.generateAllSitesMonthlyTimeSheet(calendar.time=myDate,folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	@Secured(['ROLE_ADMIN'])
	def dailyTotalPDF(){
		def site
		def siteId
		Calendar calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
		
		def date_picker =params["date_picker"]
		if (date_picker != null && date_picker.size()>0){
			calendar.time = new Date().parse("dd/MM/yyyy", date_picker)
		}

		if (params["site.id"] != null && !params["site.id"].equals('')){
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
	
	@Secured(['ROLE_ADMIN'])
	def dailyTotalWithStylePDF(){
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
			
		def retour = PDFService.generateDailySheetWithStyle(site,folder,calendar.time)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
		
	}
	
	@Secured(['ROLE_ADMIN'])
	def downloadPdf() {
		String pathToPhantomJS = "/usr/local/bin/phantomjs" //path to your phantom js
		String pathToRasterizeJS = "/usr/local/bin/rasterize.js" //path to your rasterize.js
		String paperSize = "A4"
		String url = "http://localhost:8080/pointeuse/employee/dailyReport?isAdmin=false&max=20&fromIndex=true" //url of your web page to which you want to convert into pdf
		File outputFile = File.createTempFile("sample", ".pdf") //file in which you want to save your pdf
		 		 
		Process process = Runtime.getRuntime().exec(pathToPhantomJS + " " + pathToRasterizeJS + " " + url + " " + outputFile.absolutePath + " " + paperSize);
		int exitStatus = process.waitFor(); //do a wait here to prevent it running for ever
		if (exitStatus != 0) {
		log.error("EXIT-STATUS - " + process.toString());
		}
		response.setContentType("application/octet-stream")
		
		//response.contentType = "application/pdf"
		response.setHeader("Content-Disposition", "filename=sample.pdf");
		response.outputStream << outputFile.bytes
		response.outputStream.flush()
		response.outputStream.close()
		}
	
	@Secured(['ROLE_ADMIN'])
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
		def file = new File(folder+'/'+retour[1])
		render(file: file, fileName: retour[1],contentType: "application/octet-stream")
	}
	
	@Secured(['ROLE_ADMIN'])
	def annualTotalPDF(Long userId){	
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
	
	 
	@Secured(['ROLE_ADMIN'])
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
		 
		 
		def lastMonth = monthList.last() 
		def ecartData = timeManagerService.getEcartData(site, monthList, period)
		def retour = [
			lastMonth:lastMonth,
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
	 
	 
	 @Secured(['ROLE_ADMIN'])
	 def addSickLeave (){
		 params.each{i->log.error(i)}
		 Employee employee = Employee.get(params['employeeId'])
		 def period = Period.get(params['periodId'])
		 def site = Site.get(params['siteId'])
		 def sickStartDate 
		 def sickEndDate
		 def criteria
		 def newSickLeave
		 def previousSickness
		 def nextSickness
		 def type = AbsenceType.MALADIE
		 log.error('sickLeaveReport called')
		 def dateFormat = new SimpleDateFormat('d/M/yyyy');
		 if (params['sickStartDate'] != null)
			 sickStartDate = dateFormat.parse(params['sickStartDate'])
		 if (params['sickEndDate'] != null )
			 sickEndDate = dateFormat.parse(params['sickEndDate'])
			 
		def startCalendar = Calendar.instance
		startCalendar.time = sickStartDate
		log.error('startCalendar: '+startCalendar.time)
		
		def endCalendar = Calendar.instance
		endCalendar.time = sickEndDate
		log.error('endCalendar: '+endCalendar.time)
		
		
		while (startCalendar <= endCalendar){
			log.error("startCalendar: "+startCalendar.time)
			//check if an absence (sickness or other) has been already set. If not, add absence.
			criteria = Absence.createCriteria()
			previousSickness = criteria.get{
				and {
					eq('employee',employee)
					eq('day',startCalendar.get(Calendar.DAY_OF_MONTH))
					eq('month',startCalendar.get(Calendar.MONTH)+1)
					eq('year',startCalendar.get(Calendar.YEAR))
				}
			}
			if (previousSickness == null){ 
				newSickLeave = new Absence(employee, startCalendar.time, type)
				newSickLeave.save(flush: true)
			}
			
			if(startCalendar.get(Calendar.DATE) == endCalendar.get(Calendar.DATE)){
				
				break;
			}
			startCalendar.add(Calendar.DATE,1)
		}

		 
		 redirect(action: "sickLeaveReport", params: [id:employee.id,isAdmin:false,employeeId:employee.id,periodId:params['periodId'],siteId:params['siteId']])
		 
	 }
	 
	@Secured(['ROLE_ADMIN'])
	def addNewContract (){
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
	
	@Secured(['ROLE_ADMIN'])
	def payHS(){
		log.error('payHS called')

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
	
	@Secured(['ROLE_ADMIN'])
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
	
	
	
	@Secured(['ROLE_ADMIN'])
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
	
	@Secured(['ROLE_ADMIN'])
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
		//def jsonInAndOut = new JSONInAndOut(inOrOut,jsonEmployee)
		//jsonEmployee.inOrOuts.add(jsonInAndOut)
		response.contentType = "application/json"
		render jsonEmployee as JSON
		//return 'OK'
	}
	

	@Secured(['ROLE_ANOMYMOUS'])
	def logEmployee(){
		log.error('logEmployee called with param: '+params['username'])
		def cal = Calendar.instance
		def currentDate = cal.time
		def userName = (params['username']).trim()
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
	
	@Secured(['ROLE_ADMIN'])
	def getAllEmployees() {
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
		
		def employeeJSONList = []
		for (Employee employee : employeeInstanceList){
			employeeJSONList.add(new JSONEmployee(employee))
		}
		
		response.contentType = "application/json"
		render employeeJSONList as JSON
	}
	
	@Secured(['ROLE_ADMIN'])
	def searchAllEmployees() {
		params.sort='site'
		def username=params["username"]
		def password=params["password"]
		def name=(params["name"].replaceAll("\\s","")).replaceAll("\\W","")

		def employeeInstanceList
		def site
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
		def criteria = Employee.createCriteria()
		employeeInstanceList = criteria.list {
			or {
				like('firstName','%'+name+'%')
				like('lastName','%'+name+'%')
				}

		}		
		def employeeJSONList = []
		for (Employee employee : employeeInstanceList){
			employeeJSONList.add(new JSONEmployee(employee))
		}
		
		response.contentType = "application/json"
		render employeeJSONList as JSON
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def getEmployee(Long id) {
		def username=params["username"]
		def password=params["password"]
		def employeeLogin = params["employeeLogin"]
		def employee
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
		employee= Employee.get(id)
		if (employeeLogin != null){
			employee=Employee.findByUserName(employeeLogin)
		}
		render employee// as JSON
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def getJSONEmployee() {
		def employee
		def employeeUsername = params["username"].replaceAll("\\s","")
		employeeUsername = employeeUsername.replaceAll("\\W","")
		if (employeeUsername != null){
			employee = Employee.findByUserName(employeeUsername)
		}
		
		render(contentType: 'text/json') {[
			'firstName': employee.firstName,
			'lastName': employee.lastName,
			'userName':employee.userName,
			'status':'OK'
		]}
		return
	}
		
	@Secured(['ROLE_ANONYMOUS'])
	def searchJSONEmployee() {
		def firstName=params["firstName"].replaceAll("\\W","")
		def lastName=params["lastName"].replaceAll("\\W","")
		def employee
		if (lastName != null){
			employee = Employee.findByLastName(lastName)
		}else{
			if (firstName != null){
				employee = Employee.findByFirstName(firstName)
			}
		}
		
		if (employee != null){
			render(contentType: 'text/json') {[

				'status':'NOK'
			]}
			return
		}
		render(contentType: 'text/json') {[
			'firstName': employee.firstName,
			'lastName': employee.lastName,
			'userName':employee.userName,
			'status':'OK'
		]}
		return
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def computeYearSupTime(boolean isLastMonth){
		log.error('computeYearSupTime called')
		def month
		def year
		def calendar = Calendar.instance
		if (isLastMonth){
			month = calendar.get(Calendar.MONTH)
			year = calendar.get(Calendar.YEAR)		
			if (month == 0){
				month = 1
				year = year - 1
			}
		} else{
			month = calendar.get(Calendar.MONTH)+1
			year = calendar.get(Calendar.YEAR)
		}
		
		def employees = Employee.findAll()
		log.error('there are '+employees.size()+' employees found')
		def counter = 1
		for (Employee employee: employees){
			Period period = (month > 5)?Period.findByYear(year):Period.findByYear(year - 1)	
			def data = timeManagerService.getYearSupTime(employee,year,month,false)		
			def criteria = SupplementaryTime.createCriteria()
			def supTime = criteria.get {
				and {
					eq('employee',employee)
					eq('period',period)
					eq('month',month)
				}
				maxResults(1)
			}
			if (supTime == null){
				supTime = new SupplementaryTime( employee, period,  month, data.get('ajaxYearlySupTimeDecimal'))
			}else{
				supTime.value = data.get('ajaxYearlySupTimeDecimal')
			}
			supTime.save(flush: true)		
		}
		log.error('computeYearSupTime ended')
		log.error('recreating job')
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def initializeSupTime(Long id,Long siteId,Long initialMonth,Long initialYear){
		log.error('initializeSupTime called')	
		def calendar = Calendar.instance
		def currentYear = calendar.get(Calendar.YEAR)
		def currentMonth = calendar.get(Calendar.MONTH)+1
		def startTime = calendar.time
		def timeDiff
		def year
		def month
		def loopMonth
		def employees = []
		def counter = 1
		def offset = params["offset"] != null ? params.int("offset") : 0

		if (siteId != null){
			log.error('finding all employees of a given site')
			def site = Site.get(siteId)
			employees = Employee.findAllBySite(site,[offset:offset])
			
		}else{
			if (id != null){
				employees.add(Employee.get(id))
			}else{
				employees = Employee.findAll()
				
			}
		}
		counter = employees.size()
		
		
		// it all starts on June 1st, 2013
		if (initialMonth == null){
			initialMonth = 6
			loopMonth = 5
		}else{
			loopMonth = initialMonth - 1
		}
		if (initialYear == null){
			initialYear = 2014
		}

		log.error('there are '+employees.size()+' employees found')
		for (Employee employee: employees){
			month = initialMonth
			loopMonth = initialMonth - 1
			year = initialYear
			log.error('dealing with employee #'+counter)
				while (year <= currentYear){
					if (loopMonth == 12){
						loopMonth = 1
						year += 1			
					}else{
						loopMonth += 1	
					}
					
					Period period = (loopMonth>5)?Period.findByYear(year):Period.findByYear(year - 1)
					log.error('will compute yearSupTime for month:  '+loopMonth+' and year: '+year)
					def data = timeManagerService.getYearSupTime(employee,year as int,loopMonth as int,true)
					SupplementaryTime supTime = new SupplementaryTime( employee, period,  loopMonth as int, data.get('ajaxYearlySupTimeDecimal'))
					log.error('supTime computed')
					
					supTime.save(flush: true)
					if (loopMonth == currentMonth && year == currentYear){
						break;
					}
				}
				//this will be in its own trasaction
			//since each of these service methods are Transactional
			// } as Callable)
			counter += 1
		}
		calendar = Calendar.instance
		def endTime = calendar.time
		use (TimeCategory){timeDiff=endTime-startTime}	
		log.error('computeYearSupTime executed in '+timeDiff)
	}
	
	@Secured(['ROLE_ANONYMOUS'])
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
	
	@Secured(['ROLE_ANONYMOUS'])
	def displayArchive(){
		def folder = grailsApplication.config.pdf.directory
		def baseDir = new File(folder)
		def archiveList = []
		basedir.eachFileMatch (~/.*.pdf/) { file ->
		  archiveList << file
		
		}
		log.error('archiveList: '+archiveList)
	}
	
	@Secured(['ROLE_ADMIN'])
	def getSitePDF(Long id){	
		def myDate = params["myDate"]
		def site
		def siteId
		def timeDifference
		Calendar calendar = Calendar.instance
		def startTime = calendar.time
		def folder = grailsApplication.config.pdf.directory
		
		if (myDate==null || myDate.equals("")){
			myDate=calendar.time
		}else {
			calendar.time=myDate
		}

		if (params["site.id"]!=null && !params["site.id"].equals('')){
			site = Site.get(params["site.id"].toInteger())
		}else{
			flash.message = message(code: 'pdf.site.selection.error')
			redirect(action: "list")
			return
		}
		def file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')		
		if (!file.exists()){
			flash.message = message(code: 'pdf.site.report.not.available')
			redirect(action: "list")
			return
		}else{
			render(file: file, fileName: file.name,contentType: "application/octet-stream")
		}
	}
	
	@Secured(['ROLE_ADMIN'])
	def getSiteInfoPDF(Long id){
		def myDate = params["myDate"]
		def site
		def siteId
		def timeDifference
		Calendar calendar = Calendar.instance
		def startTime = calendar.time
		def folder = grailsApplication.config.pdf.directory
		
		if (myDate==null || myDate.equals("")){
			myDate=calendar.time
		}else {
			calendar.time=myDate
		}

		if (params["site.id"]!=null && !params["site.id"].equals('')){
			site = Site.get(params["site.id"].toInteger())
		}else{
			flash.message = message(code: 'pdf.site.selection.error')
			redirect(action: "list")
			return
		}
	
		def retour = PDFService.generateSiteInfo(myDate,site,folder)
		def file = new File(folder+'/'+retour[1])
		render(file: file, fileName: retour[1],contentType: "application/octet-stream")

		if (!file.exists()){
			flash.message = message(code: 'pdf.site.report.not.available')
			redirect(action: "list")
			return
		}else{
			render(file: file, fileName: file.name,contentType: "application/octet-stream")
		}
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def testResource() {
		def timeDifference
		def site = Site.get(3)
		def user = User.get(1)
		def calendar = Calendar.instance
		def folder = grailsApplication.config.pdf.directory
		Resource myResource = assetResourceLocator.findAssetForURI(grailsApplication.config.laboratory.logo)
		
		def filename = calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)).toString() +'-'+site.name+'.pdf'
		def file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)).toString() +'-'+site.name+'.pdf')
		
		def logoFile = new File('logo')
		//writeByteArrayToFile(File file, byte[] data)
		def fileUtil = new FileUtils()
		fileUtil.writeByteArrayToFile(logoFile, myResource.getByteArray())
		log.error(logoFile)
		mailService.sendMail {
			multipart true
			to 'henri.martin@gmail.com'
			subject message(code: 'user.email.title')+' '+site.name+' '+message(code: 'user.email.site')+' '+calendar.time.format("MMM yyyy")
			inline grailsApplication.config.laboratory.logo, 'image/png', logoFile
			html g.render(template: "/employee/template/mailTemplate", model:[user:user,site:site,date:calendar.time.format("MMM yyyy")])
			attachBytes filename,'application/pdf', file.readBytes()
		}
		
	}
	
	

	
	@Secured(['ROLE_ANONYMOUS'])
	def createAllSitesPDF() {
		def timeDifference
		def folder = grailsApplication.config.pdf.directory
		def retour
		def siteAdmins
		def userEmail
		def siteNames = []
		Date startDate = new Date()
		Calendar calendar = Calendar.instance
		log.error("createAllSitesPDF called at: "+calendar.time)
		calendar.roll(Calendar.MONTH,-1)
		def currentDate = calendar.time
		if (calendar.get(Calendar.MONTH) == 11){
			log.debug('this is the first month of the year: need to get december instead')
			calendar.roll(Calendar.YEAR,-1)
			log.debug('the new date is:  '+calendar.time)
			currentDate = calendar.time
		}
		
		def sites = Site.findAll()
		def threads = []	
	    def thr = sites.each{ site ->
			if (site.id != 16){
		        def th = new Thread({	            				
					log.debug('generating PDF for site: '+site.name)
					retour = PDFService.generateSiteMonthlyTimeSheet(currentDate,site,folder)
					siteNames.add(retour[1])
		        })
				log.debug('putting thread in list')
		        threads << th
			}
	    }	
	    threads.each { it.start() }
	    threads.each { it.join() }	
		def thTime = Thread.start{			
			def endDate = new Date()
			log.debug('end time= '+endDate)
			use (TimeCategory){timeDifference = endDate - startDate}
			log.error("report createAllSitesPDF execution time"+timeDifference)
		}
		thTime.join()
		
		Resource myResource = assetResourceLocator.findAssetForURI(grailsApplication.config.laboratory.logo)
		def logoFile = new File('logo')
		def fileUtil = new FileUtils()
		def addingLogo = false
		if (myResource != null){ 
			fileUtil.writeByteArrayToFile(logoFile, myResource.getByteArray())
			addingLogo = true
		}
		sites.each { site ->
			def userList = site.users
			def filename = calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf'
			def file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')
			
			userList.each { user ->
				log.debug('userList: '+user)
				log.debug('user.reportSendDay: '+user.reportSendDay)
				log.debug('calendar.time: '+calendar.time)
				log.debug('calendar.get(Calendar.DAY_OF_MONTH): '+calendar.get(Calendar.DAY_OF_MONTH))
				log.debug('user.email: '+user.email)
				if (user.reportSendDay == calendar.get(Calendar.DAY_OF_MONTH) && user.email != null && file.exists()){
					log.error('user reportSendDay has come: will fire report to: '+user.email)
					if (addingLogo) {
						mailService.sendMail {
							multipart true
							to user.email
							subject message(code: 'user.email.title')+' '+site.name+' '+message(code: 'user.email.site')+' '+calendar.time.format("MMM yyyy")
							inline grailsApplication.config.laboratory.logo, 'image/png', logoFile
							html g.render(template: "/employee/template/mailTemplate", model:[user:user,site:site,date:calendar.time.format("MMM yyyy")])
							attachBytes filename,'application/pdf', file.readBytes()
						}
					}else{
						mailService.sendMail {
							multipart true
							to user.email
							subject message(code: 'user.email.title')+' '+site.name+' '+message(code: 'user.email.site')+' '+calendar.time.format("MMM yyyy")
							html g.render(template: "/employee/template/mailTemplate", model:[user:user,site:site,date:calendar.time.format("MMM yyyy")])
							attachBytes filename,'application/pdf', file.readBytes()
						}
					}
				}			
			}
		}
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def sendMonthlySiteMail(){
		def sites = Site.findAll()
		def folder = grailsApplication.config.pdf.directory
		Calendar calendar = Calendar.instance
		
		Resource myResource = assetResourceLocator.findAssetForURI(grailsApplication.config.laboratory.logo)
		def logoFile = new File('logo')
		def fileUtil = new FileUtils()
		def addingLogo = false
		if (myResource != null){
			fileUtil.writeByteArrayToFile(logoFile, myResource.getByteArray())
			addingLogo = true
		}
			
		sites.each { site ->
			def userList = site.users
			def filename = calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf'
			def file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')
			log.debug('filename: '+folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')
			userList.each { user ->
				log.debug('userList: '+user)
				log.debug('user.reportSendDay: '+user.reportSendDay)
				log.debug('calendar.time: '+calendar.time)
				log.debug('calendar.get(Calendar.DAY_OF_MONTH): '+calendar.get(Calendar.DAY_OF_MONTH))
				log.debug('user.email: '+user.email)
				if (user.reportSendDay == calendar.get(Calendar.DAY_OF_MONTH) && user.email != null && file.exists()){
					log.error('user reportSendDay has come: will fire report '+filename+' to: '+user.email )
					if (addingLogo) {
						log.error('user.email: '+user.email)
						mailService.sendMail {
							multipart true
							to 'henri.martin@gmail.com'
							subject message(code: 'user.email.title')+' '+site.name+' '+message(code: 'user.email.site')+' '+calendar.time.format("MMM yyyy")
							inline grailsApplication.config.laboratory.logo, 'image/png', logoFile				
							html g.render(template: "/employee/template/mailTemplate", model:[user:user,site:site,date:calendar.time.format("MMM yyyy")])
							attachBytes filename,'application/pdf', file.readBytes()
						}
					}else{
						mailService.sendMail {
							multipart true
							to 'henri.martin@gmail.com'
							subject message(code: 'user.email.title')+' '+site.name+' '+message(code: 'user.email.site')+' '+calendar.time.format("MMM yyyy")
							html g.render(template: "/employee/template/mailTemplate", model:[user:user,site:site,date:calendar.time.format("MMM yyyy")])
							attachBytes filename,'application/pdf', file.readBytes()
						}
					}
				}
			}
		}
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def computeMonthlyTotals() {
		def timeDifference
		def retour
		Date startDate = new Date()
		Calendar calendar= Calendar.instance
		log.error("computeMonthlyTotals called at: "+calendar.time)
		def currentDate = calendar.time
		def sites = Site.findAll()
		def refCalendar = Calendar.instance	
		def year = refCalendar.get(Calendar.YEAR)
		def period = Period.findByYear(year)
		
		if (refCalendar.get(Calendar.MONTH)<5){
			refCalendar.roll(Calendar.YEAR,- 1)
			year -= 1
			log.debug('changing refCalendar YEAR: '+refCalendar.time)
		}
		def monthList = []			 
		refCalendar.set(Calendar.MONTH,5)		
		if (refCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)){
			while(refCalendar.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)){
				log.debug('refCalendar: '+refCalendar.time)
				monthList.add(refCalendar.get(Calendar.MONTH)+1)
				refCalendar.roll(Calendar.MONTH, 1)
				if (refCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
					monthList.add(refCalendar.get(Calendar.MONTH)+1)
					break
				}
			}
		}else{
			while(refCalendar.get(Calendar.MONTH) <= 11){
				log.debug('refCalendar: '+refCalendar.time)
				monthList.add(refCalendar.get(Calendar.MONTH)+1)
				if (refCalendar.get(Calendar.MONTH)==11){
					monthList.add(refCalendar.get(Calendar.MONTH)+1)
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
					monthList.add(refCalendar.get(Calendar.MONTH)+1)				
					break
				}
			}
		}
		def threads = []
		def thr = sites.each{ site ->		
				def th = new Thread({								
					log.debug('generating computeWeeklyTotals for site: '+site.name)
					def employeeList = Employee.findAllBySite(site)
					for (Employee employee: employeeList){
						employee.attach()
						log.debug('treating employee: '+employee.lastName)
						for (month in monthList){
							try{
								if (month>=6){
									timeManagerService.computeWeeklyTotals( employee,  month,  year,true)									
								}else{
									timeManagerService.computeWeeklyTotals( employee,  month,  year + 1,true)						
								}
							}catch (Exception e){
								log.error('Exception: '+e.message)
							}	
						}
					}
				})
				threads << th			
		}
		threads.each { it.start() }
		threads.each { it.join() }
		def thTime = Thread.start{
			def endDate = new Date()
			log.debug('end time= '+endDate)
			use (TimeCategory){timeDifference = endDate - startDate}
			log.error("report computeMonthlyTotals execution time: "+timeDifference)
		}
		thTime.join()
	}
	
	
	
	@Secured(['ROLE_ADMIN'])
	def reComputeMonthlyTotals() {
		
		def year=params.int("year")
		def day=params.int("day")
		def month=params.int("month")
		def weeklyTotal
		def criteria
		def calendar = Calendar.instance	
		log.error("reComputeMonthlyTotals called at: "+calendar.time)
		calendar.set(Calendar.MONTH,month - 1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.DAY_OF_MONTH,day)
		
		def employeeList = Employee.findAll()
		for (Employee employee: employeeList){
		
		
	
			criteria = DailyTotal.createCriteria()
			def dailyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('day',day)
					eq('month',month)
					eq('year',year)
				}
			}
			
			criteria = InAndOut.createCriteria()
			def entriesByDay = criteria{
				and {
					eq('employee',employee)
					eq('day',day)
					eq('month',month)
					eq('year',year)
					order('time')
					}
			}
			// put in a map in and outs
			if 	(entriesByDay.size() > 0 && dailyTotal == null){
				
				dailyTotal = new DailyTotal(employee,calendar.time)
				
				
				criteria = WeeklyTotal.createCriteria()
				weeklyTotal = criteria.get {
					and {
						eq('employee',employee)
						eq('year',calendar.get(Calendar.YEAR))
						eq('month',calendar.get(Calendar.MONTH)+1)
						eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
					}
				}
				if (weeklyTotal != null){
					log.error('dailyTotal Empty: creating it for employee: '+employee+' and date: '+calendar.time)
					dailyTotal.weeklyTotal = weeklyTotal
					dailyTotal.save(flush:true)
				}
			}		
		}
		log.error('reCompute Ended')
		
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def recreateEmployeeData(Long id){
		params.each{i->log.error(i)}
		def employee = Employee.get(id)
		def criteria = InAndOut.createCriteria()
		def calendar = Calendar.instance
		def executionTime
		def iteratorCal = Calendar.instance
		iteratorCal.clearTime()
		//find first day of work
		def first_in_and_out = criteria.get{
			and {
				eq('employee',employee)
				order('time')
			}
			maxResults(1)
		}
		iteratorCal.set(Calendar.DAY_OF_MONTH,first_in_and_out.day)
		iteratorCal.set(Calendar.MONTH,first_in_and_out.month - 1)
		iteratorCal.set(Calendar.YEAR,first_in_and_out.year)
		log.error('calendar starts at: '+calendar.time)		
		
		def inAndOuts=InAndOut.findAllByEmployee(employee)
		for (InAndOut inAndOut : inAndOuts){
			iteratorCal.set(Calendar.DAY_OF_MONTH,inAndOut.day)
			iteratorCal.set(Calendar.MONTH,inAndOut.month - 1)
			iteratorCal.set(Calendar.YEAR,inAndOut.year)		
			timeManagerService.initializeTotals(employee, iteratorCal.time)
			timeManagerService. recomputeDailyTotals(employee.id as int,inAndOut.day as int,inAndOut.month as int,inAndOut.year as int)
		}
		def endDate = new Date()
		use (TimeCategory){executionTime=endDate-calendar.time}	
		log.error('recreateEmployeeData executed in '+executionTime+' for employee: '+employee.lastName)
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def executeDump(Long id){
		def employeeInstance = Employee.get(id)
		def folder = grailsApplication.config.pdf.directory
		def file = folder+'/'+employeeInstance.lastName+'_'+(new Date().format('yyyy-MM-dd'))+'.sql'
		def file1 = folder+'/'+employeeInstance.lastName+'_'+(new Date().format('yyyy-MM-dd'))+'1.sql'
		def file2 = folder+'/'+employeeInstance.lastName+'_'+(new Date().format('yyyy-MM-dd'))+'2.sql'
		
		log.error('file: '+file)
		log.error('file1: '+file1)
		
		def threads = []
		def dump1_thread = new Thread({
			log.error('creating thread for mysqldump 1 ')
			('/usr/local/mysql/bin/mysqldump -u root --no-create-info --where=id='+employeeInstance.id+' -C pointeuse employee --result-file='+file1).execute()
		})
		threads << dump1_thread
		
		def dump2_thread = new Thread({
			log.error('creating thread for mysqldump 2 ')
			('/usr/local/mysql/bin/mysqldump -u root --no-create-info --where=employee_id='+employeeInstance.id+' --ignore-table=pointeuse.absence_type_config --ignore-table=pointeuse.authorization_nature --ignore-table=pointeuse.authorization_type --ignore-table=pointeuse.bank_holiday --ignore-table=pointeuse.card_terminal --ignore-table=pointeuse.employee_data_list_map --ignore-table=pointeuse.employee_data_list_map_field_map --ignore-table=pointeuse.employee_data_list_map_hidden_field_map --ignore-table=pointeuse.employee_extra_data --ignore-table=pointeuse.event_log --ignore-table=pointeuse.exception_logger --ignore-table=pointeuse.function --ignore-table=pointeuse.period --ignore-table=pointeuse.reason --ignore-table=pointeuse.role --ignore-table=pointeuse.service --ignore-table=pointeuse.site --ignore-table=pointeuse.site_user --ignore-table=pointeuse.status --ignore-table=pointeuse.user --ignore-table=pointeuse.user_role --ignore-table=pointeuse.year --ignore-table=pointeuse.employee --ignore-table=pointeuse.dummy --ignore-table=pointeuse.monthly_total_weekly_total -C pointeuse --result-file='+file2).execute()
		})
		threads << dump2_thread
		

		threads.each { it.start() }
		threads.each { it.join() }
		def delete_thread = new Thread({
				new File(file).withWriter { w ->
				
				[file1,file2].each{ f ->
	
					// Get a reader for the input file
					new File(f).withReader { r ->
				
					  // And write data from the input into the output
					 w << r << '\n'
					}
				}
			}
			boolean file1SuccessfullyDeleted =  new File(file1).delete()
			boolean file2SuccessfullyDeleted =  new File(file2).delete()
		})
		delete_thread.sleep((long)(10000))
		delete_thread.start()
		//delete_thread.join()
		
		/*
			new File( file ).withWriter { w ->
			
			[file1,file2].each{ f ->

				// Get a reader for the input file
				new File( f ).withReader { r ->
			
				  // And write data from the input into the output
				 w << r << '\n'
				}
			}
		}
		boolean file1SuccessfullyDeleted =  new File(file1).delete()
		boolean file2SuccessfullyDeleted =  new File(file2).delete()
*/
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def modifyTitle(){
		log.error('modifyTitle called')
		def base 		
		for (Employee employee:Employee.findAll()){
			base = "https://api.genderize.io/?name="+employee.firstName
			def url = new URL(base)
			def connection = url.openConnection()
			if(connection.responseCode == 200){
				def result = new JsonSlurper().parseText(connection.content.text)
				def gender = result.gender
				log.error("result.gender"+gender+' for employee: '+employee.lastName)
				if (gender.equals('male')){
					employee.title = Title.M
				}else{
					employee.title = Title.MME
				}
				employee.save(flush:true)
			}
		
		}
			log.error('modifyTime done')
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def closeDay() {
		log.error('launching CloseDay')
		
		def inOrOut
		def employeeList = Employee.findAll("from Employee")
		def calendar = Calendar.instance
		for (employee in employeeList){
			def lastIn = InAndOut.findByEmployee(employee,[max:1,sort:"time",order:"desc"])
			if (lastIn != null && lastIn.type == "E"){
				log.error "we have a problem: user "+employee.lastName +" did not log out"
				inOrOut = new InAndOut(employee, calendar.time,"S",false)
				inOrOut.dailyTotal=lastIn.dailyTotal
				inOrOut.systemGenerated=true
				employee.inAndOuts.add(inOrOut)
				employee.hasError=true
				log.error "creating inOrOut: "+inOrOut
			}
			
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
				log.debug("there still "+inAndOutList.size() +" errors for employee "+employee.id + " : " +employee.lastName)
				 employee.hasError=true
			 }else {
				 employee.hasError=false
			 }	
		}	
		return 'CloseDay done!'	
	}
	
	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def randomize(){
		log.error('randomize called')
		
		def employeeList = Employee.findAll("from Employee")
		for (employee in employeeList){
			https://randomuser.me
			def base = "https://randomuser.me/api/?format=json&nat=fr"
			def url = new URL(base)
			def connection = url.openConnection()
			if(connection.responseCode == 200){
				def employeeAnonymizer = new JsonSlurper().parseText(connection.content.text)
				log.error('employeeAnonymizer')
				def last = employeeAnonymizer.results.name.last[0]
				def first = employeeAnonymizer.results.name.first[0]
				log.error(first+' '+last)
				employee.firstName = first
				employee.lastName = last
				def userName = employee.firstName.take(1) + employee.lastName.take(1) + employee.lastName.reverse().take(1).reverse()
				employee.userName = userName
				employee.matricule = userName
				employee.save()
				
			}
		}
		
	}
}
