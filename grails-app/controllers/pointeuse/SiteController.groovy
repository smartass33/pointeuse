package pointeuse

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


class SiteController {
	def authenticateService
	def springSecurityService
	
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		params.sort='name'
		def mapData = []
		def siteTable =[]
		def siteInstanceList = Site.list(params)
		
		for (Site site:Site.findAll()){
			siteTable =[]
			if (site.latitude!=0 && site.longitude!=0){
				siteTable[0]=site.latitude
				siteTable[1]=site.longitude
				siteTable[2]=site.name+'<BR>'+site.address
				mapData.add(siteTable)
			}
		}
		
	/*	
	 mapData = [[44.89156, -0.70898, 'Saint-MŽdard'],
          [44.86548, -0.66613, 'Le Haillan'],
          [44.85891, -0.51520, 'Cenon'],
          [44.88016, -0.52311, 'Lormont'],        
          [44.78527, -0.49689, 'Latresne'],
          [45.19254, -0.74464, 'Pauillac'],
          [44.88395, -0.63419, 'Eysines'],
          [44.83345, -0.52929, 'Floirac'],
          [44.73173, -0.60013, 'Leognan'],
          [44.68911, -0.51577, 'La Brde'],
 		  [44.63227, -1.14408, 'La Teste'],         
          [44.92655, -0.48949, 'Ambares']]
	*/	
	def mapColumns = [['number', 'Lat'], ['number', 'Lon'], ['string', 'Name']]
	
        [siteInstanceList: siteInstanceList, siteInstanceTotal: Site.count(),mapData:mapData,mapColumns:mapColumns]
    }

    def create() {
        [siteInstance: new Site(params)]
    }

    def save() {
		def user = springSecurityService.currentUser
		
        def siteInstance = new Site(params)
		siteInstance.loggingDate=new Date()
		if (user != null){
			siteInstance.user=user
		}
        if (!siteInstance.save(flush: true)) {
            render(view: "create", model: [siteInstance: siteInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'site.label', default: 'Site'), siteInstance.id])
        redirect(action: "show", id: siteInstance.id)
    }

    def show(Long id) {
        def siteInstance = Site.get(id)
        if (!siteInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
            return
        }

        [siteInstance: siteInstance]
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

        if (!siteInstance.save(flush: true)) {
            render(view: "edit", model: [siteInstance: siteInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'site.label', default: 'Site'), siteInstance.id])
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
            siteInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'site.label', default: 'Site'), id])
            redirect(action: "show", id: id)
        }
    }
}
