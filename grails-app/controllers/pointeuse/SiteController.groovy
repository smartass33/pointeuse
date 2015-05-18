package pointeuse

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured
import groovy.time.TimeDuration;
import groovy.time.TimeCategory;

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

    def create() {
        [siteInstance: new Site(params),fromSite:true]
    }

    def save() {
		def user = springSecurityService.currentUser
		def result
        def siteInstance = new Site(params)
		
		result = geocoderService.geocodeAddress(params["address"],params["town"])
		siteInstance.latitude = result.lat
		siteInstance.longitude = result.lng
		if (result.postCode)
			siteInstance.postCode= result.postCode

		
		siteInstance.loggingDate=new Date()
		if (user != null){
			siteInstance.user=user
		}
        if (!siteInstance.save(flush: true)) {
            render(view: "create", model: [siteInstance: siteInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'site.label', default: 'Site'), siteInstance.name])
        redirect(action: "show", id: siteInstance.id)
    }

    def show(Long id) {
        def siteInstance = Site.get(id)
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }

        [siteInstance: siteInstance,employeeInstanceList:siteInstance.employees,employeeInstanceTotal:siteInstance.employees.size(),fromSite:true]
    }

    def edit(Long id) {
        def siteInstance = Site.get(id)
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }

        [siteInstance: siteInstance]
    }

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
	
	def siteTotalTime = {
		Date currentDate = new Date()
		def month = currentDate.getAt(Calendar.MONTH) + 1
		def year = currentDate.getAt(Calendar.YEAR)
		Period period = (month>5)?Period.findByYear(year):Period.findByYear(year-1)		
		[period2:period,year:year,month:month]
	}
	
	
	def completeSiteReport(){
		log.error('entering monthlyTotalTime method')
		//params.each{i->log.error('parameter of list: '+i)}
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
		def siteAnnualSickness = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0
		def siteAnnualDIF = 0
		def siteAnnualPayableSupTime = 0
		def siteAnnualTheoriticalIncludingExtra = 0
		def siteAnnualSupTimeAboveTheoritical = 0
		def siteAnnualGlobalSupTimeToPay = 0
		
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
		
		GParsExecutorsPool.withPool {		 
			 employeeList.iterator().eachParallel {
				 data = timeManagerService.getAnnualReportData(period.year, it)
				 annualReportMap.put(it,data)			 
				 siteAnnualEmployeeWorkingDays += data.get('annualEmployeeWorkingDays')
				 siteAnnualTheoritical += data.get('annualTheoritical')
				 siteAnnualTotal += data.get('annualTotal')
				 siteAnnualHoliday += data.get('annualHoliday')
				 siteRemainingCA += data.get('remainingCA')
				 siteAnnualRTT += data.get('annualRTT')
				 siteAnnualCSS += data.get('annualCSS')
				 siteAnnualSickness += data.get('annualSickness')
				 siteAnnualDIF += data.get('annualDIF')
				 siteAnnualExceptionnel += data.get('annualExceptionnel')
				 siteAnnualPaternite += data.get('annualPaternite')
				 siteAnnualPayableSupTime += data.get('annualPayableSupTime') as long
				 siteAnnualTheoriticalIncludingExtra += data.get('annualTheoriticalIncludingExtra') as long
				 siteAnnualSupTimeAboveTheoritical += data.get('annualSupTimeAboveTheoritical') as long
				 siteAnnualGlobalSupTimeToPay += data.get('annualGlobalSupTimeToPay')
			 }
		 }
		
		def endDate = new Date()
		use (TimeCategory){executionTime=endDate-startDate}
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
			siteAnnualSickness:siteAnnualSickness,
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
