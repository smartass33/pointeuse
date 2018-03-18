package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured
import java.text.SimpleDateFormat
import java.util.Date;
import pl.touk.excel.export.WebXlsxExporter


import org.hibernate.QueryException

@Transactional(readOnly = true)
class MileageController {

	def mileageService
	def springSecurityService
	def timeManagerService
	def PDFService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

	@Secured(['ROLE_ADMIN','ROLE_ANONYMOUS'])
	def addMileage(){
		log.debug('calling addMileage')
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
		def month = calendar.get(Calendar.MONTH) + 1
		def year = calendar.get(Calendar.YEAR)
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
				if (springSecurityService.currentUser != null){
					mileage = new Mileage(employee, period, day, month, year,  value,springSecurityService.currentUser)
				}else {
					mileage = new Mileage(employee, period, day, month, year,  value)
				}
			
				 mileage.save flush:true
			}else{
				mileage.value = value
				mileage.save flush:true
			}
		}catch (QueryException qe){
			log.error('Duplicate entry: '+qe.message)
			return
		}finally{
			return
		}
		
	}
	
	@Secured(['ROLE_ADMIN'])
	def siteMileagePDF(){
		log.error('calling siteMileagePDF')
		def calendar = Calendar.instance
		def infYear = (params['infYear_year'] != null) ? params.int('infYear_year') : calendar.get(Calendar.YEAR)
		def supYear = (params['supYear_year'] != null) ? params.int('supYear_year') : (calendar.get(Calendar.YEAR) - 2)		
		def siteList = []
		def sites = params['sites']
		
		if (sites == null){
			siteList = Site.findAll()
		}
		if (sites != null && sites.length == 0){
			siteList = Site.findAll()
		}
		if (sites != null && sites.length > 0){
			for (def siteName in sites){
				siteList.add(Site.findByName(siteName))
			}
		}
		//def siteList = site == null ? Site.findAll() : [site]				
		def folder = grailsApplication.config.pdf.directory
		def retour = PDFService.generateYearSiteMileageSheet(infYear, supYear, siteList, folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	@Secured(['ROLE_ADMIN'])
	def siteMileageEXCEL(){
		log.error('calling siteMileagePDF')
		def folder = grailsApplication.config.pdf.directory
		def calendar = Calendar.instance
		def infYear = (params['infYear_year'] != null) ? params.int('infYear_year') : calendar.get(Calendar.YEAR)
		def supYear = (params['supYear_year'] != null) ? params.int('supYear_year') : (calendar.get(Calendar.YEAR) - 2)
		//def site = (params['site.id'] != null && !params['site.id'].equals('')) ? Site.get(params['site.id']) : null	
		
		def siteList = []
		def sites = params['sites']
		
		if (sites == null){
			siteList = Site.findAll()
		}
		if (sites != null && sites.length == 0){
			siteList = Site.findAll()
		}
		if (sites != null && sites.length > 0){
			for (def siteName in sites){
				siteList.add(Site.findByName(siteName))
			}
		}
		//def siteList = site == null ? Site.findAll() : [site]		
		def mileageSiteReportMap = [:]
		
		def model = mileageService.getAllSitesOverPeriod(siteList,infYear,supYear)
		def mileageBySiteMap = model.get('mileageBySiteMap')
		def data = []
	
		
		
		def headers = [message(code: 'laboratory.label')]
		for (int k = infYear;k <=supYear;k++){
			headers.add(k)
		}
		WebXlsxExporter webXlsxExporter = new WebXlsxExporter(folder+'/km_report_template.xlsx').with {exporter ->
			setResponseHeaders(response)
			fillHeader(headers)
			int i = 1
			mileageBySiteMap.each{iterSite,mileageByEmployeeYear->
				fillRow([iterSite.name],i)
				i += 1

				mileageByEmployeeYear.each{employee,employeeMileageYearMap->
					data = []
					data.add(employee.lastName)
					employeeMileageYearMap.each{iterYear,employeeYearlyMileage->
						data.add(employeeYearlyMileage)
				
					}
					fillRow(data,i)
					i += 1
				}

			
			}
			save(response.outputStream)
		}
		response.setContentType("application/octet-stream")
		
		
	}
	
	@Secured(['ROLE_ADMIN'])
	def mileagePDF(){
		log.error('calling mileagePDF')
		params.each{i->log.debug('parameter of list: '+i)}
		def employee = Employee.get(params['employeeId'])
		def folder = grailsApplication.config.pdf.directory
		def calendarMax = Calendar.instance
		calendarMax.time = new Date().parse("yyyyMMdd", params["mileageMaxDate"])
		log.debug('calendarMax: '+calendarMax.time)
		def calendarMin = Calendar.instance
		calendarMin.time = new Date().parse("yyyyMMdd", params["mileageMinDate"])
		log.debug('calendarMin: '+calendarMin.time)
		
		def mileageList
		def month = calendarMax.get(Calendar.MONTH) + 1
		def year = calendarMax.get(Calendar.YEAR) + 1
		def monthlyPeriodValue = 0
		
		def criteria = Mileage.createCriteria()

		if (mileageList != null){
			for (Mileage mileageIter : mileageList){
				monthlyPeriodValue += mileageIter.value
			}
		}

		def retour = PDFService.generateUserMonthlyMileageSheet(calendarMin.time, calendarMax.time, employee, folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	
	}
	
	
	@Secured(['ROLE_ADMIN'])
	def modifyMileage(){
		log.error('calling modifyMileage')
		params.each{i->log.debug('parameter of list: '+i)}
		def period = Period.get(params['periodId'])
		def employee = Employee.get(params['employeeId'])
		def fromReport = params.boolean('fromReport')//.split(" ").getAt(0) as boolean
		def value = params.long('value')
		def monthlyPeriodValue = 0
		def calendar = Calendar.instance
		def date_mileage = params["date_mileage_picker"]
		calendar.time = new Date().parse("yyyyMMd", params["date_mileage_picker"])
		log.debug('calendar.time: '+calendar.time)
		def month = calendar.get(Calendar.MONTH) + 1
		def year = calendar.get(Calendar.YEAR)//(month >= 6 ? period.year : period.year + 1)
		if (period == null){
			if (month < 6){
				period=Period.findByYear(year - 1)
			}else{
				period=Period.findByYear(year)
			}
		}
			
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
			if (fromReport){
				def report = timeManagerService.getReportData(null, employee,  null, month, year,true)
				render template: "/employee/template/reportTableTemplate", model: report
				return
			}else{
				redirect(action: "employeeMileage", periodId: period.id,employeeId:employee.id,siteId:employee.site.id)
				return
			}	
		}
	}
	
	@Secured(['ROLE_ADMIN'])
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
	
	@Secured(['ROLE_ADMIN'])
	def employeeMileage(Integer max) {
		log.error('employeeMileage called')
		params.each{i->log.debug('parameter of list: '+i)}
		SimpleDateFormat dateFormat
		def calendar = Calendar.instance
		def criteria
		def monthList=[6,7,8,9,10,11,12,1,2,3,4,5]
		def employeeMileageMap = [:]
		def mileageIDMap = [:]
		def mileageMapByEmployee = [:]
		def milageIDMapByEmployee = [:]
		def period = Period.get(params['periodId'])
		def employee = Employee.get(params['employeeId'])
		def fromIndex=params.boolean('fromIndex')
		
		def fromAnnualReport = (params['fromAnnualReport'] != null) ? params.boolean('fromAnnualReport') :false		
		def site = (params['siteId'] != null && !params['siteId'].equals('') ? Site.get(params['siteId']) : employee.site  )
		
		

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
	
	@Secured(['ROLE_ADMIN'])
	def siteMileage(){
		log.error('entering siteMileage')
		params.each{i->log.debug('parameter of list: '+i)}
		def employeeSiteList
		def calendar = Calendar.instance
		def year = calendar.get(Calendar.YEAR)
		def historyYear = year - 4
		def criteria
		def mileageBySiteMap = [:]
		def siteList = Site.findAll()
		for (Site site in siteList){
			employeeSiteList = site.employees 
			def employeeMileageYearMap = [:]
			def mileageByEmployeeYear = [:]
			for (employee in employeeSiteList){
				criteria = Mileage.createCriteria()		
				for (int i = historyYear; i <= year; i++){
					def employeeYearlyMileage = 0				
					def yearMileageList = Mileage.findAllByEmployeeAndYear(employee,i)
					for (Mileage mileage in yearMileageList){
						employeeYearlyMileage += mileage.value
					}	
					employeeMileageYearMap.put(year,employeeYearlyMileage)
					mileageByEmployeeYear.put(employee,employeeMileageYearMap)
				}
			}
			mileageBySiteMap.put(site,mileageByEmployeeYear)
		}
		return mileageBySiteMap
	}
	
	@Secured(['ROLE_ADMIN'])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		log.error('entering index for mileage')
		params.each{i->log.error('parameter of list: '+i)}
		def fromMileagePage = (params['fromMileagePage'] != null) ? params.boolean('fromMileagePage') : false
		def employeeSiteList
		def calendar = Calendar.instance
		def criteria
		def mileageBySiteMap = [:]
		def employeeMileageYearMap = [:]
		def mileageByEmployeeYear = [:]
		//def site = (params['site.id'] != null && !params['site.id'].equals('')) ? Site.get(params['site.id']) : null	
		
		def siteList = []
		def sites = params['sites']
		
		if (sites == null){
			siteList = Site.findAll()
		}
		if (sites != null && sites.length == 0){
			siteList = Site.findAll()
		}
		if (sites != null && sites.length > 0){
			for (def siteName in sites){
				siteList.add(Site.findByName(siteName))
			}
		}
		//def siteList = site == null ? Site.findAll() : [site]		
		def infYear = (params['infYear_year'] != null) ? params.int('infYear_year') : calendar.get(Calendar.YEAR)
		def supYear = (params['supYear_year'] != null) ? params.int('supYear_year') : (calendar.get(Calendar.YEAR) - 2)
		if (fromMileagePage){
			def model = mileageService.getAllSitesOverPeriod(siteList,infYear,supYear)
			model << [supYear:supYear,infYear:infYear]
			render template:"/mileage/template/mileageFollowup",model:model
			return			
		}else{
			[supYear:supYear,infYear:infYear,isIndex:true]	
		}
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
