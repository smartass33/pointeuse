package pointeuse

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured
import groovy.time.TimeDuration;
import groovy.time.TimeCategory;

import java.text.Normalizer
import java.util.Date;

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
		params.each{i->log.error('parameter of list: '+i)}
		def site 
		def executionTime
		def data
		def employeeList
		def siteId=params["site.id"]
		def periodId = params.int('periodId')
		def period = Period.get(params.int('periodId'))
		def calendar = Calendar.instance
		def year = calendar.get(Calendar.YEAR)
		def annualReportMap =[:]
		def model = [:]
		
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
		data = timeManagerService.getSiteData(site,period)
		def endDate = new Date()
		use (TimeCategory){executionTime=endDate-startDate}
		log.error('execution time: '+executionTime)
		model << data
		model << [flash:flash]
		model << [period2:period,site:site,siteId:siteId]
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
			
			//render template: "/site/template/siteMonthlyTemplate", model:[period2:period,annualReportMap:null,employeeList:null,site:site,siteId:siteId,flash:flash]			
			return
		}
		
		def retour = PDFService.generateSiteTotalSheet(site, folder, period)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
		
	}
	
}
