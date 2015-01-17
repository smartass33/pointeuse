package pointeuse


import org.springframework.dao.DataIntegrityViolationException
import org.apache.commons.logging.LogFactory

class PeriodController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def utilServic
	private static final log = LogFactory.getLog(this)
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		params.sort='year'
		params.order='asc'
        [periodInstanceList: Period.list(params), periodInstanceTotal: Period.count()]
    }

    def create() {
        [periodInstance: new Period(params)]
    }

    def save() {
        def periodInstance = new Period(params)
        if (!periodInstance.save(flush: true)) {
            render(view: "create", model: [periodInstance: periodInstance])
            return
        }
		utilService.initiateVacations(periodInstance)		
		log.error(periodInstance.toString())
        flash.message = message(code: 'default.created.message', args: [message(code: 'period.label', default: 'Year'), periodInstance.toString()])
        redirect(action: "show", id: periodInstance.id)
    }

    def show(Long id) {
        def periodInstance = Period.get(id)
        if (!periodInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'period.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        [periodInstance: periodInstance]
    }

    def edit(Long id) {
        def periodInstance = Period.get(id)
        if (!periodInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'period.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        [periodInstance: periodInstance]
    }

    def update(Long id, Long version) {
        def periodInstance = Period.get(id)
        if (!periodInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'period.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (periodInstance.version > version) {
                periodInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'period.label', default: 'Year')] as Object[],
                          "Another user has updated this Year while you were editing")
                render(view: "edit", model: [periodInstance: periodInstance])
                return
            }
        }

        periodInstance.properties = params

        if (!periodInstance.save(flush: true)) {
            render(view: "edit", model: [periodInstance: periodInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'period.label', default: 'Year'), periodInstance.id])
        redirect(action: "show", id: periodInstance.id)
    }

    def delete(Long id) {
        def periodInstance = Period.get(id)
        if (!periodInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'period.label', default: 'Year'), id])
            redirect(action: "list")
            return
        }

        try {
            periodInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'period.label', default: 'Year'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'period.label', default: 'Year'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def changeValue(){
		log.error("entering changeValue")
		/*
		params.each{i->
			log.error(i);
		}*/
		def year = params["yearAsString"] as long
		def yearAsString = year.toString()+'/'+(year+1).toString()
		render template: "/common/periodBoxTemplate", model:[yearAsString:yearAsString]
		return
	}
	
	def addPeriodToMonthlyTotals(){
		def monthlyTotals = MonthlyTotal.findAll()
		def month
		def year
		Period period
		
		for (MonthlyTotal monthlyTotal : monthlyTotals){
			month = monthlyTotal.month
			year = monthlyTotal.year
			period = (month>5)?Period.findByYear(year):Period.findByYear(year - 1)
			monthlyTotal.period = period
			monthlyTotal.save(flush: true)
			
			
		}
	}
}
