package pointeuse

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import java.text.Normalizer

class SiteController {
	def authenticateService
	def springSecurityService
	def geocoderService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

	
	@Secured(['ROLE_ADMIN'])
	def map(Integer max) {
		def mapData = []
		def siteTable =[]
		
		for (Site site:Site.findAll()){
			siteTable =[]
			if (site.latitude!=0 && site.longitude!=0){
				siteTable[0]=site.latitude
				siteTable[1]=site.longitude
				siteTable[2]='<a href="show/'+site.id+'">'+site.name+'</a>'+'<BR>'+site.address +'<BR>'+site.postCode +', '+site.town
				mapData.add(siteTable)
			}
		}
		def mapColumns = [['number', 'Lat'], ['number', 'Lon'], ['string', 'Name']]
		[mapData:mapData,mapColumns:mapColumns]
	}
	
	
	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		params.sort='name'

		def siteInstanceList = Site.list(params)

	
        [siteInstanceList: siteInstanceList, siteInstanceTotal: Site.count()]
    }

    def create() {
        [siteInstance: new Site(params)]
    }

    def save() {
		def user = springSecurityService.currentUser
		def result
        def siteInstance = new Site(params)
		
		result = geocoderService.geocodeAddress(params["address"],params["town"])
		siteInstance.latitude = result.lat
		siteInstance.longitude = result.lng
		
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

        [siteInstance: siteInstance,employeeInstanceList:siteInstance.employees,employeeInstanceTotal:siteInstance.employees.size()]
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
		params.each{i->
			log.error(i);
}
		def address = params["address"]
		address = Normalizer.normalize(address, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
		
		def result = geocoderService.geocodeAddress(address)
		render result as String
	  }
	
}
