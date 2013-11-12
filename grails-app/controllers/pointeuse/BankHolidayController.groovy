package pointeuse

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

class BankHolidayController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	def authenticateService
	def springSecurityService
	def utilService
    def index() {
        redirect(action: "list", params: params)
    }

	@Secured(['ROLE_ADMIN'])
    def list(Integer max) {
		params.order = "desc"
		params.sort = "calendar"
		def yearlyCounts=[:]
		def bankHolidayInstanceList = BankHoliday.list(params)
		def bankHolidayInstanceTotal = BankHoliday.count()

		for (holidayInstance in bankHolidayInstanceList){
			if (yearlyCounts.get(holidayInstance.year)==null){
				yearlyCounts.put(holidayInstance.year,BankHoliday.findAllByYear(holidayInstance.year))
			}
		}
        [bankHolidayInstanceTotal:bankHolidayInstanceTotal,yearlyCounts:yearlyCounts]
    }

    def create() {
		def bankHolidayInstance = new BankHoliday(params)
        [bankHolidayInstance:new BankHoliday(params)]
    }

    def save() {
		def user = springSecurityService.currentUser
		def period
        def bankHolidayInstance = new BankHoliday(params)
		if (bankHolidayInstance != null){
			bankHolidayInstance.month=bankHolidayInstance.calendar.get(Calendar.MONTH)+1
			bankHolidayInstance.year=bankHolidayInstance.calendar.get(Calendar.YEAR)
			bankHolidayInstance.day=bankHolidayInstance.calendar.get(Calendar.DAY_OF_MONTH)
			bankHolidayInstance.loggingDate=new Date()
			if (user!=null){
				bankHolidayInstance.user=user
			}
			
			if (bankHolidayInstance.month>=6 && bankHolidayInstance.month<=12){
				if (Period.findByYear(bankHolidayInstance.year)){
					bankHolidayInstance.period=Period.findByYear(bankHolidayInstance.year)
				}
				else{
					Period newPeriod = new Period()
					newPeriod.year=bankHolidayInstance.year	
					newPeriod.save()
					utilService.initiateVacations(newPeriod)
	//				newPeriod.openedDays=utilService.getOpenDays(newPeriod)
					bankHolidayInstance.period=newPeriod	
				}	
			}
			else{
				if (Period.findByYear(bankHolidayInstance.year-1)){
					bankHolidayInstance.period=Period.findByYear(bankHolidayInstance.year-1)
				}
				else{
					Period newPeriod = new Period()
					newPeriod.year=bankHolidayInstance.year-1
					newPeriod.save()
					utilService.initiateVacations(newPeriod)
//					newPeriod.openedDays=utilService.getOpenDays(newPeriod)
					bankHolidayInstance.period=newPeriod
				}				
			}
			
			if (!bankHolidayInstance.save(flush: true)) {
				render(view: "create", model: [bankHolidayInstance: bankHolidayInstance])
				return
			}
	
		}
		//setYearlyOpenDays(bankHolidayInstance.year)
		
        flash.message = message(code: 'default.created.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), bankHolidayInstance.calendar.time.format('EEEE MMM yyyy')])
        redirect(action: "show", id: bankHolidayInstance.id)
    }

    def show(Long id) {
        def bankHolidayInstance = BankHoliday.get(id)
        if (!bankHolidayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), id])
            redirect(action: "list")
            return
        }

        [bankHolidayInstance: bankHolidayInstance]
    }

    def edit(Long id) {
        def bankHolidayInstance = BankHoliday.get(id)
        if (!bankHolidayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), id])
            redirect(action: "list")
            return
        }

        [bankHolidayInstance: bankHolidayInstance]
    }

    def update(Long id, Long version) {
        def bankHolidayInstance = BankHoliday.get(id)
        if (!bankHolidayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (bankHolidayInstance.version > version) {
                bankHolidayInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'bankHoliday.label', default: 'BankHoliday')] as Object[],
                          "Another user has updated this BankHoliday while you were editing")
                render(view: "edit", model: [bankHolidayInstance: bankHolidayInstance])
                return
            }
        }

        bankHolidayInstance.properties = params
		if (bankHolidayInstance != null){
			bankHolidayInstance.month=bankHolidayInstance.calendar.get(Calendar.MONTH)+1
			bankHolidayInstance.year=bankHolidayInstance.calendar.get(Calendar.YEAR)
			bankHolidayInstance.day=bankHolidayInstance.calendar.get(Calendar.DAY_OF_MONTH)
		}

        if (!bankHolidayInstance.save(flush: true)) {
            render(view: "edit", model: [bankHolidayInstance: bankHolidayInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), bankHolidayInstance.id])
        redirect(action: "show", id: bankHolidayInstance.id)
    }

    def delete(Long id) {
        def bankHolidayInstance = BankHoliday.get(id)
        if (!bankHolidayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), id])
            redirect(action: "list")
            return
        }

        try {
			def yearInstance = Period.findByYear(bankHolidayInstance.year)
			bankHolidayInstance.delete(flush: true)
			
			if (yearInstance!=null){
				yearInstance.bankHolidays=BankHoliday.findAllByYear(bankHolidayInstance.year).size()
			}
			
			
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'bankHoliday.label', default: 'BankHoliday'), id])
            redirect(action: "show", id: id)
        }
    }
	/*
	private setYearlyOpenDays(int year){
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.DAY_OF_YEAR,1)
		def yearInstance = Year.findByYear(year)
		
		if ()
		
		if (yearInstance==null){
			yearInstance=new Year()
			yearInstance.year=year
			yearInstance.period=year.toString()+"/"(year+1).toString()
		}
		
		yearInstance.bankHolidays=BankHoliday.findAllByYear(year).size()
		yearInstance.solidarityDays=1
		
		def openedDays = 0
		
		def startCalendar = Calendar.instance
		startCalendar = 
		
		while(calendar.get(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			if (calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				openedDays += 1
			}
			if (calendar.getAt(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
				break
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		yearInstance.openedDays=openedDays
		yearInstance.save()
	}
	*/
}
