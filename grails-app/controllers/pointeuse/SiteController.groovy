package pointeuse

import org.springframework.dao.DataIntegrityViolationException
import grails.plugin.springsecurity.annotation.Secured

import grails.plugin.springsecurity.annotation.Secured
import groovy.time.TimeDuration
import groovy.time.TimeCategory

import java.text.Normalizer
import java.util.Date;
import java.util.concurrent.ConcurrentMap

import groovyx.gpars.*

import org.apache.commons.logging.LogFactory


class SiteController {	
	def authenticateService
	def PDFService
	def springSecurityService
	def timeManagerService
	def geocoderService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	private static final log = LogFactory.getLog(this)
	
	@Secured(['ROLE_ADMIN'])
    def index() {
        redirect(action: "list", params: params)
    }

	
	@Secured(['ROLE_ADMIN'])
	def map() {
		def mapData = []
		def siteTable =[]
		
		for (Site site:Site.findAll("from Site")){
			siteTable =[]
			if (site.latitude!=0 && site.longitude!=0){
				siteTable[0]=site.latitude
				siteTable[1]=site.longitude
				siteTable[2]='<a href="show/'+site.id+'">'+site.name+'</a>'+'<BR>'+site.address +'<BR>'+site.postCode +', '+site.town
				mapData.add(siteTable)
			}
		}
		def mapColumns = [['number', 'Lat'], ['number', 'Lon'], ['string', 'Name']]
		[mapData:mapData,mapColumns:mapColumns,fromSite:true]
	}
	
	
	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		params.sort='name'
		def siteInstanceList = Site.list(params)
        [siteInstanceList: siteInstanceList, siteInstanceTotal: Site.count()]
    }

	@Secured(['ROLE_SUPER_ADMIN'])
    def create() {
        [siteInstance: new Site(params),fromSite:true]
    }

	@Secured(['ROLE_SUPER_ADMIN'])
    def save() {
		def user = springSecurityService.currentUser
		def result
        def siteInstance = new Site(params)
		
		result = geocoderService.geocodeAddress(params["address"],params["town"])
		siteInstance.latitude = result.lat
		siteInstance.longitude = result.lng
		if (result.postCode){
			siteInstance.postCode= result.postCode
		}
		
		siteInstance.loggingDate=new Date()
		if (user != null){
			if (siteInstance.users != null){
				siteInstance.users.add(user)
			}else{
				def siteUsers = []
				siteUsers.add(user)
				siteInstance.users = siteUsers
			}
		}
        if (!siteInstance.save(flush: true)) {
            render(view: "create", model: [siteInstance: siteInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'site.label', default: 'Site'), siteInstance.name])
        redirect(action: "show", id: siteInstance.id)
    }
	
	@Secured(['ROLE_ADMIN'])
    def show(Long id) {
        def siteInstance = Site.get(id)
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }

        [siteInstance: siteInstance,employeeInstanceList:siteInstance.employees,employeeInstanceTotal:siteInstance.employees.size(),fromSite:true]
    }

	@Secured(['ROLE_SUPER_ADMIN'])
    def edit(Long id) {
        def siteInstance = Site.get(id)
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }

        [siteInstance: siteInstance]
    }

	@Secured(['ROLE_SUPER_ADMIN'])
    def update(Long id, Long version) {
        def siteInstance = Site.get(id)
		def result
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (siteInstance.version > version) {
                siteInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'site.label', default: 'Site')] as Object[],
                          "Another user has updated this Site while you were editing")
                render(view: "edit", model: [siteInstance: siteInstance])
                return
            }
        }
        siteInstance.properties = params	
		result = geocoderService.geocodeAddress(params["address"],params["town"])
		siteInstance.latitude = result.lat
		siteInstance.longitude = result.lng
		if (result.postCode)
			siteInstance.postCode= result.postCode
	
        if (!siteInstance.save(flush: true)) {
            render(view: "edit", model: [siteInstance: siteInstance])
            return
        }
        flash.message = message(code: 'default.updated.message', args: [message(code: 'site.label', default: 'Site'), siteInstance.name])
        redirect(action: "show", id: siteInstance.id)
    }
	
	@Secured(['ROLE_SUPER_ADMIN'])
    def delete(Long id) {
        def siteInstance = Site.get(id)
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }
        try {
			def siteName = siteInstance.name
            siteInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'site.label', default: 'Site'), siteName])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def geocode = {
		def address = params["address"]
		def result
		address = Normalizer.normalize(address, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		result = geocoderService.geocodeAddress(address)
		render result as String
	  }
	
	@Secured(['ROLE_SUPER_ADMIN'])
	def siteTotalTime(){
		log.error('siteTotalTime called')
		Date currentDate = new Date()
		def month = currentDate.getAt(Calendar.MONTH) + 1
		def year = currentDate.getAt(Calendar.YEAR)
		Period period = (month>5)?Period.findByYear(year):Period.findByYear(year-1)		
		[period2:period,year:year,month:month]
	}

	@Secured(['ROLE_ADMIN'])
	def completeSiteReport(){
		log.error('entering completeSiteReport method')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def executionTime
		def data
		def siteId=params["site.id"]
		def periodId = params.int('periodId')
		def period = Period.get(params.int('periodId'))
		def calendar = Calendar.instance
		def year = calendar.get(Calendar.YEAR)
		def annualReportMap =[:]
		def model = [:]
		def siteAnnualEmployeeWorkingDays = 0
		def siteAnnualTheoritical = 0
		def siteAnnualTotal = 0
		def siteAnnualHoliday = 0
		def siteRemainingCA = 0
		def siteAnnualRTT = 0
		def siteAnnualCSS = 0
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualSickness = 0
		def siteAnnualMaternite = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0
		def siteAnnualDIF = 0
		def siteAnnualPayableSupTime = 0
		def siteAnnualTheoriticalIncludingExtra = 0
		def siteAnnualSupTimeAboveTheoritical = 0
		def siteAnnualGlobalSupTimeToPay = 0
		def annualEmployeeData
		def criteria
		
		if (period != null){
			year = period.year
		} else{
			period = Period.findByYear(year)
		}
		
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
		
		if (site == null){
			flash.message = message(code: 'site.selection.error')
			render template: "/site/template/siteMonthlyTemplate", model:[period2:period,annualReportMap:null,employeeList:null,site:site,siteId:siteId,flash:flash]
			return
		}else{
			flash.message = null
		}
		def startDate = new Date()
		def employeeList = Employee.findAllBySite(site)
		criteria = AnnualEmployeeData.createCriteria()
		def annualEmployeeDataList = criteria.list  {
			and{
				eq('period',period)
				'in'('employee',employeeList)
			}
		}
		def hasEmployeeData = (annualEmployeeDataList.size() == employeeList.size()) ? true : false
		if (hasEmployeeData){
			log.error('data present in database: skipping computation part')
			for (Employee employee:employeeList){			
				// let's check if site data are present in DB
				criteria = AnnualEmployeeData.createCriteria()
				annualEmployeeData = criteria.get {
					and{
						eq('period',period)
						eq('employee',employee)
					}
					maxResults(1)
				}
				def employeeModel = [:]			
				if (annualEmployeeData != null && annualEmployeeData.valueMap != null)	{
					employeeModel << ['yearOpenDays': (annualEmployeeData.valueMap.get('yearOpenDays') != null ? annualEmployeeData.valueMap.get('yearOpenDays').toLong() : 0)]		
					employeeModel << ['annualEmployeeWorkingDays':(annualEmployeeData.valueMap.get('annualEmployeeWorkingDays') != null ? annualEmployeeData.valueMap.get('annualEmployeeWorkingDays').toLong() : 0)]  
					employeeModel << ['annualTotal':(annualEmployeeData.valueMap.get('annualTotal') != null ? annualEmployeeData.valueMap.get('annualTotal').toLong() : 0 )]	
					employeeModel << ['annualTheoritical':(annualEmployeeData.valueMap.get('annualTheoritical') != null ? annualEmployeeData.valueMap.get('annualTheoritical').toLong() : 0)]
					employeeModel << ['annualHoliday':(annualEmployeeData.valueMap.get('annualHoliday') != null ? annualEmployeeData.valueMap.get('annualHoliday').toLong() : 0)]
					employeeModel << ['remainingCA':(annualEmployeeData.valueMap.get('remainingCA') != null ? annualEmployeeData.valueMap.get('remainingCA').toLong() : 0)]
					employeeModel << ['annualRTT':(annualEmployeeData.valueMap.get('annualRTT') != null ? annualEmployeeData.valueMap.get('annualRTT').toLong() : 0)]
					employeeModel << ['annualCSS':(annualEmployeeData.valueMap.get('annualCSS') != null ? annualEmployeeData.valueMap.get('annualCSS').toLong() : 0)]
					employeeModel << ['annualINJUSTIFIE':(annualEmployeeData.valueMap.get('annualINJUSTIFIE') != null && annualEmployeeData.valueMap.get('annualINJUSTIFIE') != 'null' ? annualEmployeeData.valueMap.get('annualINJUSTIFIE').toLong() : 0)]
					employeeModel << ['annualDIF':(annualEmployeeData.valueMap.get('annualDIF') != null ? annualEmployeeData.valueMap.get('annualDIF').toLong() : 0 )  ]
					employeeModel << ['annualSickness':(annualEmployeeData.valueMap.get('annualSickness') != null ? annualEmployeeData.valueMap.get('annualSickness').toLong() : 0)]	
					employeeModel << ['annualMaternite':(annualEmployeeData.valueMap.get('annualMaternite') != null ? annualEmployeeData.valueMap.get('annualMaternite').toLong() : 0)]
					employeeModel << ['annualExceptionnel':(annualEmployeeData.valueMap.get('annualExceptionnel') != null ? annualEmployeeData.valueMap.get('annualExceptionnel').toLong() : 0)]	
					employeeModel << ['annualPaternite':(annualEmployeeData.valueMap.get('annualPaternite') != null ? annualEmployeeData.valueMap.get('annualPaternite').toLong() : 0)]
					employeeModel << ['annualPayableSupTime':(annualEmployeeData.valueMap.get('annualPayableSupTime') != null ? annualEmployeeData.valueMap.get('annualPayableSupTime').toLong() : 0)]
					employeeModel << ['annualTheoriticalIncludingExtra':(annualEmployeeData.valueMap.get('annualTheoriticalIncludingExtra') != null ? annualEmployeeData.valueMap.get('annualTheoriticalIncludingExtra').toLong() : 0)]
					employeeModel << ['annualSupTimeAboveTheoritical':(annualEmployeeData.valueMap.get('annualSupTimeAboveTheoritical') != null ? annualEmployeeData.valueMap.get('annualSupTimeAboveTheoritical').toLong() : 0)]
					employeeModel << ['annualGlobalSupTimeToPay':(annualEmployeeData.valueMap.get('annualGlobalSupTimeToPay') != null ? annualEmployeeData.valueMap.get('annualGlobalSupTimeToPay').toLong() : 0)]
				
					annualReportMap.put(employee,employeeModel)						
					siteAnnualEmployeeWorkingDays +=  (annualEmployeeData.valueMap.get('annualEmployeeWorkingDays') != null ? annualEmployeeData.valueMap.get('annualEmployeeWorkingDays').toLong() : 0)
					siteAnnualTheoritical += (annualEmployeeData.valueMap.get('annualTheoritical') != null ? annualEmployeeData.valueMap.get('annualTheoritical').toLong() : 0)
					siteAnnualTotal += (annualEmployeeData.valueMap.get('annualTotal') != null ? annualEmployeeData.valueMap.get('annualTotal').toLong() : 0)
					siteAnnualHoliday += (annualEmployeeData.valueMap.get('annualHoliday') != null ? annualEmployeeData.valueMap.get('annualHoliday').toLong() : 0)
					siteRemainingCA += (annualEmployeeData.valueMap.get('remainingCA') != null ? annualEmployeeData.valueMap.get('remainingCA').toLong() : 0)
					siteAnnualRTT += (annualEmployeeData.valueMap.get('annualRTT') != null ? annualEmployeeData.valueMap.get('annualRTT').toLong() : 0)
					siteAnnualCSS += (annualEmployeeData.valueMap.get('annualCSS') != null ? annualEmployeeData.valueMap.get('annualCSS').toLong() : 0)
					siteAnnualINJUSTIFIE += (annualEmployeeData.valueMap.get('annualINJUSTIFIE') != null && annualEmployeeData.valueMap.get('annualINJUSTIFIE') != 'null' ? annualEmployeeData.valueMap.get('annualINJUSTIFIE').toLong() : 0)
					siteAnnualSickness += (annualEmployeeData.valueMap.get('annualSickness') != null ? annualEmployeeData.valueMap.get('annualSickness').toLong() : 0)
					siteAnnualMaternite += (annualEmployeeData.valueMap.get('annualMaternite') != null ? annualEmployeeData.valueMap.get('annualMaternite').toLong() : 0)				
					siteAnnualDIF += (annualEmployeeData.valueMap.get('annualDIF') != null ? annualEmployeeData.valueMap.get('annualDIF').toLong() : 0)
					siteAnnualExceptionnel += (annualEmployeeData.valueMap.get('annualExceptionnel') != null ? annualEmployeeData.valueMap.get('annualExceptionnel').toLong() : 0)
					siteAnnualPaternite += (annualEmployeeData.valueMap.get('annualPaternite') != null ? annualEmployeeData.valueMap.get('annualPaternite').toLong() : 0)
					siteAnnualPayableSupTime += (annualEmployeeData.valueMap.get('annualPayableSupTime') != null ? annualEmployeeData.valueMap.get('annualPayableSupTime').toLong() : 0)
					siteAnnualTheoriticalIncludingExtra += (annualEmployeeData.valueMap.get('annualTheoriticalIncludingExtra') != null ? annualEmployeeData.valueMap.get('annualTheoriticalIncludingExtra').toLong() : 0)
					siteAnnualSupTimeAboveTheoritical += (annualEmployeeData.valueMap.get('annualSupTimeAboveTheoritical') != null ? annualEmployeeData.valueMap.get('annualSupTimeAboveTheoritical').toLong() : 0) 
					siteAnnualGlobalSupTimeToPay += (annualEmployeeData.valueMap.get('annualGlobalSupTimeToPay') != null ? annualEmployeeData.valueMap.get('annualGlobalSupTimeToPay').toLong() : 0) 			
					model << [annualReportMap:annualReportMap]
				}
			}			
			model << [
				siteAnnualEmployeeWorkingDays:siteAnnualEmployeeWorkingDays,
				siteAnnualTheoritical:siteAnnualTheoritical,
				siteAnnualTotal:siteAnnualTotal,
				siteAnnualHoliday:siteAnnualHoliday,
				siteRemainingCA:siteRemainingCA,
				siteAnnualRTT:siteAnnualRTT,
				siteAnnualCSS:siteAnnualCSS,
				siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
				siteAnnualSickness:siteAnnualSickness,
				siteAnnualMaternite:siteAnnualMaternite,
				siteAnnualDIF:siteAnnualDIF,
				siteAnnualExceptionnel:siteAnnualExceptionnel,
				siteAnnualPaternite:siteAnnualPaternite,
				siteAnnualPayableSupTime:siteAnnualPayableSupTime,
				siteAnnualTheoriticalIncludingExtra:siteAnnualTheoriticalIncludingExtra,
				siteAnnualSupTimeAboveTheoritical:siteAnnualSupTimeAboveTheoritical,
				siteAnnualGlobalSupTimeToPay:siteAnnualGlobalSupTimeToPay
			]
		}
		model << [
			flash:flash,
			period2:period,
			site:site,
			siteId:siteId,
			employeeList:employeeList
			]
		render template: "/site/template/siteMonthlyTemplate", model:model
		return
		/*
		else{
			GParsExecutorsPool.withPool {		 
				 employeeList.iterator().eachParallel {
					 data = timeManagerService.getAnnualReportDataNOHS(period.year, it)
					 annualReportMap.put(it,data)	
					 if (data != null){
						 siteAnnualEmployeeWorkingDays += (data.get('yearOpenDays') != null ? data.get('yearOpenDays') : 0)
						 siteAnnualTheoritical += (data.get('annualTheoritical') != null ? data.get('annualTheoritical') : 0)
						 siteAnnualTotal += (data.get('annualTotal') != null ? data.get('annualTotal') : 0)
						 siteAnnualHoliday += (data.get('annualHoliday') != null ? data.get('annualHoliday') : 0)
						 siteRemainingCA += (data.get('remainingCA') != null ? data.get('remainingCA') : 0)
						 siteAnnualRTT += (data.get('annualRTT') != null ? data.get('annualRTT') : 0)
						 siteAnnualCSS += (data.get('annualCSS') != null ? data.get('annualCSS') : 0)
						 siteAnnualINJUSTIFIE += (data.get('annualINJUSTIFIE') != null ? data.get('annualINJUSTIFIE') : 0)
						 siteAnnualSickness += (data.get('annualSickness') != null ? data.get('annualSickness') : 0)
						 siteAnnualMaternite += (data.get('annualMaternite') != null ? data.get('annualMaternite') : 0)			 
						 siteAnnualDIF += (data.get('annualDIF') != null ? data.get('annualDIF') : 0)
						 siteAnnualExceptionnel += (data.get('annualExceptionnel') != null ? data.get('annualExceptionnel') : 0)
						 siteAnnualPaternite += (data.get('annualPaternite') != null ? data.get('annualPaternite') : 0)
						 siteAnnualPayableSupTime += (data.get('annualPayableSupTime') != null ? data.get('annualPayableSupTime') as long : 0)
						 siteAnnualTheoriticalIncludingExtra += (data.get('annualTheoriticalIncludingExtra') != null ? data.get('annualTheoriticalIncludingExtra') as long : 0)
						 siteAnnualSupTimeAboveTheoritical += (data.get('annualSupTimeAboveTheoritical') != null ? data.get('annualSupTimeAboveTheoritical') as long : 0)
						 siteAnnualGlobalSupTimeToPay += (data.get('annualGlobalSupTimeToPay') != null ? data.get('annualGlobalSupTimeToPay') : 0)
					 }
				 }
			 }	
			use (TimeCategory){executionTime = new Date() - startDate}
			log.error('execution time: '+executionTime)
			model << [flash:flash]
			model << [period2:period,
				site:site,
				siteId:siteId,
				employeeList:employeeList,
				annualReportMap:annualReportMap,
				siteAnnualEmployeeWorkingDays:siteAnnualEmployeeWorkingDays,
				siteAnnualTheoritical:siteAnnualTheoritical,
				siteAnnualTotal:siteAnnualTotal,
				siteAnnualHoliday:siteAnnualHoliday,
				siteRemainingCA:siteRemainingCA,
				siteAnnualRTT:siteAnnualRTT,
				siteAnnualCSS:siteAnnualCSS,
				siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
				siteAnnualSickness:siteAnnualSickness,
				siteAnnualMaternite:siteAnnualMaternite,
				siteAnnualDIF:siteAnnualDIF,
				siteAnnualExceptionnel:siteAnnualExceptionnel,
				siteAnnualPaternite:siteAnnualPaternite,
				siteAnnualPayableSupTime:siteAnnualPayableSupTime,
				siteAnnualTheoriticalIncludingExtra:siteAnnualTheoriticalIncludingExtra,
				siteAnnualSupTimeAboveTheoritical:siteAnnualSupTimeAboveTheoritical,
				siteAnnualGlobalSupTimeToPay:siteAnnualGlobalSupTimeToPay
			]	
			render template: "/site/template/siteMonthlyTemplate", model:model
			return
		}
		*/
	}
	
	@Secured(['ROLE_ANONYMOUS'])
	def createAllSiteData(){
		def criteria
		def data
		def employeeList 
		def siteList = Site.findAll()
		def annualReportMap =[:]
		def calendar = Calendar.instance
		def executionTime
		def annualEmployeeData
		def threads		
		Period period = (calendar.get(Calendar.MONTH) < 5) ? Period.findByYear(calendar.get(Calendar.YEAR)-1) : Period.findByYear(calendar.get(Calendar.YEAR))
		
		for (Site site:siteList){
			log.error('starting computation for site: '+site.name + 'and period: '+period)
			employeeList = Employee.findAllBySite(site)
			
			threads = []
			employeeList.each{ employee ->
				def th = new Thread({
					log.error('executing query for employee: '+employee)
					data = timeManagerService.getAnnualReportData(period.year, employee)
					annualReportMap.put(employee,data)
				})
				threads << th
			}
			threads.each { it.start() }
			threads.each { it.join() }
			
			for (Employee employee:employeeList){
				criteria = AnnualEmployeeData.createCriteria()
				annualEmployeeData = criteria.get {
					and{
						eq('period',period)
						eq('employee',employee)
					}
					maxResults(1)
				}
				if (annualEmployeeData == null){
					annualEmployeeData = new AnnualEmployeeData(employee,period)
				}
				if (annualEmployeeData.valueMap == null){
					annualEmployeeData.valueMap = [:]
				}
				def valueMap = [:]
				if (annualReportMap != null){
					valueMap << ['yearOpenDays':String.valueOf(annualReportMap.get(employee).get('yearOpenDays'))]
					valueMap << ['annualEmployeeWorkingDays':String.valueOf(annualReportMap.get(employee).get('annualEmployeeWorkingDays'))]
					valueMap << ['annualTotal':String.valueOf(annualReportMap.get(employee).get('annualTotal'))]
					valueMap << ['annualTheoritical':String.valueOf(annualReportMap.get(employee).get('annualTheoritical'))]
					valueMap << ['annualTotal':String.valueOf(annualReportMap.get(employee).get('annualTotal'))]
					valueMap << ['annualHoliday':String.valueOf(annualReportMap.get(employee).get('annualHoliday'))]
					valueMap << ['remainingCA':String.valueOf(annualReportMap.get(employee).get('remainingCA'))]
					valueMap << ['annualRTT':String.valueOf(annualReportMap.get(employee).get('annualRTT'))]
					valueMap << ['annualCSS':String.valueOf(annualReportMap.get(employee).get('annualCSS'))]
					valueMap << ['annualINJUSTIFIE':String.valueOf(annualReportMap.get(employee).get('annualINJUSTIFIE'))]
					valueMap << ['annualDIF':String.valueOf(annualReportMap.get(employee).get('annualDIF'))]
					valueMap << ['annualSickness':String.valueOf(annualReportMap.get(employee).get('annualSickness'))]
					valueMap << ['annualMaternite':String.valueOf(annualReportMap.get(employee).get('annualMaternite'))]
					valueMap << ['annualExceptionnel':String.valueOf(annualReportMap.get(employee).get('annualExceptionnel'))]
					valueMap << ['annualPaternite':String.valueOf(annualReportMap.get(employee).get('annualPaternite'))]
					valueMap << ['annualPayableSupTime':String.valueOf(annualReportMap.get(employee).get('annualPayableSupTime'))]
					valueMap << ['annualTheoriticalIncludingExtra':String.valueOf(annualReportMap.get(employee).get('annualTheoriticalIncludingExtra'))]
					valueMap << ['annualSupTimeAboveTheoritical':String.valueOf(annualReportMap.get(employee).get('annualSupTimeAboveTheoritical'))]
					valueMap << ['annualGlobalSupTimeToPay':String.valueOf(annualReportMap.get(employee).get('annualGlobalSupTimeToPay'))]
				}else{
					log.error('annualReportMap is null for employee: '+employee)
				}
				annualEmployeeData.valueMap = valueMap
				annualEmployeeData.save(flush : true)
			}
			log.error('all loops finished')
			use (TimeCategory){executionTime=new Date()-calendar.time}
			log.error('execution time: '+executionTime)
		}
	}
	
	@Secured(['ROLE_ADMIN'])
	def getAjaxSiteData(){
		log.error('entering getAjaxSiteData method')
		def site = Site.get(params.int('site'))
		def month = params.int('month')
		def year = params.int('year')
		def period
		if (params['period'] != null && params['period'].size() > 0){
			period = Period.get(params.int('period'))
		}else{
			period = (month > 5) ? Period.findByYear(year) : Period.findByYear(year - 1)
		}
		def executionTime
		def data
		def calendar = Calendar.instance
		def annualReportMap =[:]
		def model = [:]
		def siteAnnualEmployeeWorkingDays = 0
		def siteAnnualTheoritical = 0
		def siteAnnualTotal = 0
		def siteAnnualHoliday = 0
		def siteRemainingCA = 0
		def siteAnnualRTT = 0
		def siteAnnualCSS = 0
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualSickness = 0
		def siteAnnualMaternite = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0
		def siteAnnualDIF = 0
		def siteAnnualPayableSupTime = 0
		def siteAnnualTheoriticalIncludingExtra = 0
		def siteAnnualSupTimeAboveTheoritical = 0
		def siteAnnualGlobalSupTimeToPay = 0
		def startDate = new Date()
		def timeDifference
		def employeeList = Employee.findAllBySite(site)
		log.error('nb of employees: '+employeeList.size())		
				
		def  threads = []
		employeeList.each{ employee ->
			def th = new Thread({
				log.error('executing query for employee: '+employee)
				data = timeManagerService.getAnnualReportData(period.year, employee)
				annualReportMap.put(employee,data)
				siteAnnualEmployeeWorkingDays += data.get('annualEmployeeWorkingDays')
				siteAnnualTheoritical += data.get('annualTheoritical')
				siteAnnualTotal += data.get('annualTotal')
				siteAnnualHoliday += data.get('annualHoliday')
				siteRemainingCA += data.get('remainingCA')
				siteAnnualRTT += data.get('annualRTT')
				siteAnnualCSS += data.get('annualCSS')
				siteAnnualINJUSTIFIE += data.get('annualINJUSTIFIE')
				siteAnnualSickness += data.get('annualSickness')
				siteAnnualMaternite += data.get('annualMaternite')			
				siteAnnualDIF += data.get('annualDIF')
				siteAnnualExceptionnel += data.get('annualExceptionnel')
				siteAnnualPaternite += data.get('annualPaternite')
				siteAnnualPayableSupTime += data.get('annualPayableSupTime') as long
				siteAnnualTheoriticalIncludingExtra += data.get('annualTheoriticalIncludingExtra') as long
				siteAnnualSupTimeAboveTheoritical += data.get('annualSupTimeAboveTheoritical') as long
				siteAnnualGlobalSupTimeToPay += data.get('annualGlobalSupTimeToPay')
			})
			threads << th
		}
		threads.each { it.start() }
		threads.each { it.join() }

		log.error('employee loop finished')
		use (TimeCategory){executionTime=new Date()-startDate}
		log.error('execution time: '+executionTime)		
		 
		model << [
			period2:period,
			site:site,
			siteId:site.id,
			employeeList:employeeList,
			annualReportMap:annualReportMap,
			siteAnnualEmployeeWorkingDays:siteAnnualEmployeeWorkingDays,
			siteAnnualTheoritical:siteAnnualTheoritical,
			siteAnnualTotal:siteAnnualTotal,
			siteAnnualHoliday:siteAnnualHoliday,
			siteRemainingCA:siteRemainingCA,
			siteAnnualRTT:siteAnnualRTT,
			siteAnnualCSS:siteAnnualCSS,
			siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
			siteAnnualSickness:siteAnnualSickness,
			siteAnnualMaternite:siteAnnualMaternite,		
			siteAnnualDIF:siteAnnualDIF,
			siteAnnualExceptionnel:siteAnnualExceptionnel,
			siteAnnualPaternite:siteAnnualPaternite,
			siteAnnualPayableSupTime:siteAnnualPayableSupTime,
			siteAnnualTheoriticalIncludingExtra:siteAnnualTheoriticalIncludingExtra,
			siteAnnualSupTimeAboveTheoritical:siteAnnualSupTimeAboveTheoritical,
			siteAnnualGlobalSupTimeToPay:siteAnnualGlobalSupTimeToPay
		]	
		for (Employee employee:employeeList){
			def criteria = AnnualEmployeeData.createCriteria()
			def annualEmployeeData = criteria.get {
				and{
					eq('period',period)
					eq('employee',employee)
				}
				maxResults(1)
			}
			if (annualEmployeeData == null){
				annualEmployeeData = new AnnualEmployeeData(employee,period)
			}
			if (annualEmployeeData.valueMap == null){
				annualEmployeeData.valueMap = [:]
			}
			def valueMap = [:]	
			if (annualReportMap != null && annualReportMap.get(employee) != null){
				//valueMap << ['yearOpenDays':String.valueOf(annualReportMap.get(employee).get('yearOpenDays'))]			
				//valueMap << ['annualEmployeeWorkingDays':String.valueOf(annualReportMap.get(employee).get('annualEmployeeWorkingDays'))]
				valueMap << ['annualTotal':String.valueOf(annualReportMap.get(employee).get('annualTotal'))]
				valueMap << ['annualTheoritical':String.valueOf(annualReportMap.get(employee).get('annualTheoritical'))]
				valueMap << ['annualTotal':String.valueOf(annualReportMap.get(employee).get('annualTotal'))]
				valueMap << ['annualHoliday':String.valueOf(annualReportMap.get(employee).get('annualHoliday'))]
				valueMap << ['remainingCA':String.valueOf(annualReportMap.get(employee).get('remainingCA'))]
				valueMap << ['annualRTT':String.valueOf(annualReportMap.get(employee).get('annualRTT'))]
				valueMap << ['annualCSS':String.valueOf(annualReportMap.get(employee).get('annualCSS'))]
				valueMap << ['annualINJUSTIFIE':String.valueOf(annualReportMap.get(employee).get('annualINJUSITIFIE'))]
				valueMap << ['annualDIF':String.valueOf(annualReportMap.get(employee).get('annualDIF'))]
				valueMap << ['annualSickness':String.valueOf(annualReportMap.get(employee).get('annualSickness'))]
				valueMap << ['annualMaternite':String.valueOf(annualReportMap.get(employee).get('annualMaternite'))]
				valueMap << ['annualExceptionnel':String.valueOf(annualReportMap.get(employee).get('annualExceptionnel'))]
				valueMap << ['annualPaternite':String.valueOf(annualReportMap.get(employee).get('annualPaternite'))]
				valueMap << ['annualPayableSupTime':String.valueOf(annualReportMap.get(employee).get('annualPayableSupTime'))]
				valueMap << ['annualTheoriticalIncludingExtra':String.valueOf(annualReportMap.get(employee).get('annualTheoriticalIncludingExtra'))]
				valueMap << ['annualSupTimeAboveTheoritical':String.valueOf(annualReportMap.get(employee).get('annualSupTimeAboveTheoritical'))]
				valueMap << ['annualGlobalSupTimeToPay':String.valueOf(annualReportMap.get(employee).get('annualGlobalSupTimeToPay'))]
				annualEmployeeData.valueMap = valueMap
				annualEmployeeData.save(flush : true)
			}
		}
		render template: "/site/template/siteDetailTableTemplate", model:model
		return		
	}
	
	@Secured(['ROLE_ADMIN'])
	def completeSiteReportPDF(){
		log.error('entering completeSiteReportPDF method')
		def site 
		def employeeList
		def siteId=params["site.id"]
		def periodId = params.int('periodId')
		def period = Period.get(params.int('periodId'))
		def folder = grailsApplication.config.pdf.directory
		def calendar = Calendar.instance
		def year = calendar.get(Calendar.YEAR)
		def annualReportMap =[:]
		
		if (period != null){
			year = period.year
		} else{
			period = Period.findByYear(year)
		}
		
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
		
		if (site == null){
			flash.message = message(code: 'pdf.site.selection.error')
			redirect(action: "siteTotalTime")	
			return
		}	
		def retour = PDFService.generateSiteTotalSheet(site, folder, period)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]	
	}

}
