package pointeuse

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured
import java.text.Normalizer
import java.util.Date;

import org.apache.commons.logging.LogFactory


class SiteController {
	def authenticateService
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
		params.each ={ i-> log.error('param: '+i)}
		
		return
		
	}
	
	def monthlyTotalTime = {
		params.each ={ i-> log.error('param: '+i)}
		def site = params["site.id"]
		def myDate = params["myDate"]
		def siteId = params["siteId"]
		def criteria = Employee.createCriteria()
		def employeeList
		def siteMonthlyTotal = [:]
		def siteMonthlyTheoriticalTotal = [:]
		def tmpTotal = 0
		def tmpTheoriticalTotal = 0
		def data
		def month = myDate.getAt(Calendar.MONTH) + 1
		def year = myDate.getAt(Calendar.YEAR)
		Period period = (month>5)?Period.findByYear(year):Period.findByYear(year-1)

		
		
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
		
		employeeList = (site != null) ? Employee.findAllBySite(site) :Employee.findAll("from Employee")
		
		
		for (Employee employee:employeeList){
			//data = timeManagerService.getCartoucheData(employee,year,month)
			
			
			data = timeManagerService.getMonthlyTotalTime( employee, month, year)
		//	tmpTheoriticalTotal += data.get('monthTheoritical')
			if ( siteMonthlyTotal.get(site) != null ){
				siteMonthlyTotal.put(site, data.get('monthlyTotalTime') + siteMonthlyTotal.get(site))
			}else{
				siteMonthlyTotal.put(site, data.get('monthlyTotalTime'))
			}
			
			//siteMonthlyTheoriticalTotal.put(site, tmpTheoriticalTotal + siteMonthlyTheoriticalTotal.get(site))
			
		}

		log.error('siteMonthlyTheoriticalTotal: ' +siteMonthlyTheoriticalTotal)
		log.error('siteMonthlyTotal: ' +siteMonthlyTotal)
		
		log.error('executed')
		return
	}
	
}
