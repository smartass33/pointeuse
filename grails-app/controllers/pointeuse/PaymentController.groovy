package pointeuse



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import java.text.SimpleDateFormat

@Transactional(readOnly = true)
class PaymentController {

	def timeManagerService
	def PDFService
	def paymentService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Payment.list(params), model:[paymentInstanceCount: Payment.count()]
    }

    def show(Payment paymentInstance) {
        respond paymentInstance
    }

    def create() {
        respond new Payment(params)
    }

    @Transactional
    def save(Payment paymentInstance) {
        if (paymentInstance == null) {
            notFound()
            return
        }

        if (paymentInstance.hasErrors()) {
            respond paymentInstance.errors, view:'create'
            return
        }

        paymentInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'paymentInstance.label', default: 'Payment'), paymentInstance.id])
                redirect paymentInstance
            }
            '*' { respond paymentInstance, [status: CREATED] }
        }
    }

    def edit(Payment paymentInstance) {
        respond paymentInstance
    }

    @Transactional
    def update(Payment paymentInstance) {
        if (paymentInstance == null) {
            notFound()
            return
        }

        if (paymentInstance.hasErrors()) {
            respond paymentInstance.errors, view:'edit'
            return
        }

        paymentInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Payment.label', default: 'Payment'), paymentInstance.id])
                redirect paymentInstance
            }
            '*'{ respond paymentInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Payment paymentInstance) {

        if (paymentInstance == null) {
            notFound()
            return
        }

        paymentInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Payment.label', default: 'Payment'), paymentInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'paymentInstance.label', default: 'Payment'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
	 
	
	
	def getPaymentPDF(){		
		log.error('getPaymentPDF called')
		params.each{i-> log.error(i)}	
		def folder = grailsApplication.config.pdf.directory
		
		def fromIndex=params.boolean('fromIndex')
		def periodId = params['year']
		def isAdmin = params.boolean('isAdmin')

		def calendar = Calendar.instance
		def criteria
		def period 
		
		if (periodId != null && !periodId.equals('')){
			period = Period.get(periodId)
		}else{
			def month =  calendar.get(Calendar.MONTH) + 1
			def year = calendar.get(Calendar.YEAR)
			if (month < 6){
				year -=1
			}
			period = Period.findByYear(year)
		}
			
		def site = (params["siteId"]!=null && params["siteId"]!='') ? Site.get(params["siteId"]) : Site.get(params["site.id"])
		if (site != null){
			def retour = PDFService.generateSitePaymentSheet(paymentService.getReportData(period,site), folder)
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "filename=${retour[1]}")
			response.outputStream << retour[0]
		}	
	}
	
	
	def getAllSitesPaymentPDF(){
		log.error('getAllSitesPaymentPDF called')
		params.each{i-> log.error(i)}
		def folder = grailsApplication.config.pdf.directory
		
		def fromIndex=params.boolean('fromIndex')
		def periodId = params['year']
		def isAdmin = params.boolean('isAdmin')

		def calendar = Calendar.instance
		def criteria
		def period
		
		if (periodId != null && !periodId.equals('')){
			period = Period.get(periodId)
		}else{
			def month =  calendar.get(Calendar.MONTH) + 1
			def year = calendar.get(Calendar.YEAR)
			if (month < 6){
				year -=1
			}
			period = Period.findByYear(year)
		}

		def retour = PDFService.generateAllSitesPaymentSheet(period, folder)
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
		
	}
	
	def paymentReport() {
		params.each{i-> log.error(i)}	
		def fromIndex = (params['fromIndex'] != null) ? params.boolean('fromIndex') :false
		def fromAnnualReport = (params['fromAnnualReport'] != null) ? params.boolean('fromAnnualReport') :false
		def periodId = params["periodId"]
		def employee 
		SimpleDateFormat dateFormat
		def calendar = Calendar.instance
		def criteria
		def site = Site.get(params["siteId"])
		def monthList=[6,7,8,9,10,11,12,1,2,3,4,5]
		def paymentMap = [:]
		def paymentIDMap = [:]
		def paymentMapByEmployee = [:]
		def paymentIDMapByEmployee = [:]
		def month =  calendar.get(Calendar.MONTH) + 1
		def year = (month < 6) ? calendar.get(Calendar.YEAR) - 1 : calendar.get(Calendar.YEAR)
		def myDate = params["myDate"]
		if (myDate != null && myDate instanceof String){
			dateFormat = new SimpleDateFormat('dd/MM/yyyy');
			myDate = dateFormat.parse(myDate)
		}

		def period = (periodId != null) ? Period.get(periodId) : Period.findByYear(year)

		if (site == null){
			site = Site.get(params["site.id"])
		}
		if (site == null && !fromIndex){
			flash.message = message(code: 'site.selection.error')
		}else{
			flash.message = null
		}		
		def model = paymentService.getReportData( period, site)
		model << [fromAnnualReport:fromAnnualReport,myDate:myDate]
		if (fromAnnualReport){
			employee = Employee.get(params["employeeId"])
			model << [employee:employee]	
		}
		if (fromIndex != null && fromIndex){
			model << [fromIndex:false]
			return model
		}
		render template:"/payment/template/paymentTemplate",model:model
		return
	}
	
	@Transactional
	def paymentInitialization(){
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		def periodList = Period.findAll("from Period")
		def employeeList = Employee.findAll("from Employee")
		for (Period period: periodList){
			for (Employee employee:employeeList){
				for (int month in monthList){
					Payment payment = new Payment(employee,period, month,0)
					payment.save flush:true
				}		
			}
		}
	}
	
	
	@Transactional
	def ajaxModifyPayment(){
		params.each{i-> log.error(i)}
		def newValue = params.value as double
		def paymentId = (params["paymentId"].split(" ").getAt(0)) as long	
		def month = (params["month"].split(" ").getAt(0)) as long		
		def periodId = (params["periodId"].split(" ").getAt(0)) as long
		def employeeId = (params["employeeId"].split(" ").getAt(0)) as long
		def siteId = (params["siteId"].split(" ").getAt(0)) as long
		def site = Site.get(siteId)
		def period = Period.get(periodId)
		def employee = Employee.get(employeeId)
		def payment = Payment.get(paymentId)
		
		if (payment != null){
			log.error('updating existing payment with period: '+period+' and month: '+month)
			payment.amountPaid = newValue
		}else{
			log.error('creating new payment with period: '+period+' and month: '+month)
			 payment = new Payment(employee,period, month as int,newValue as double)
		}
		payment.save flush:true
		
		log.error('done')
		def model = [fromIndex:true,siteId:site.id,periodId:period.id,site:site]
		
		model << paymentService.getReportData( period, site)
		
		
		
		render template:"/payment/template/paymentTemplate",model:model
		return
		//redirect(action: "paymentReport",params:[fromIndex:true,siteId:site.id,periodId:period.id])
		//return
	}
	
	
	@Transactional
	def modifyPayment(){
		params.each{i-> log.error(i)}
		def siteId = params["site.id"]
		def site = Site.get(params["siteId"][0])
		def newValuesAsString=params["textField"]		
		def paymentsAsLong=params["payment"]	
		def periodList=params["periodList"]
		def period = Period.findByYear(params["periodList"][0])
		def month
		def paymentIdList=params["paymentIds"]	
		def table = []
		def employeeMap = [:]
		
		for (int j=0;j<newValuesAsString.size();j++){
			newValuesAsString[j] = timeManagerService.getTimeFromText(newValuesAsString[j], false)
		}		
	
		def employeeList = Employee.findAllBySite(site)
		log.error('newValuesAsString.size(): '+newValuesAsString.size())
		def iterator = 0
		for (def i = 0;i<newValuesAsString.size();i++){
			log.error('iterator: '+iterator)
			def employee = employeeList.get(iterator)
			table.add(newValuesAsString)
			if (newValuesAsString[i] as double != paymentsAsLong[i] as double){
				def coc = i%12
				month = (i%12 > 7) ? i%12 - 6 : i%12 + 6 
				if (paymentIdList[i] as long != 0){
					log.error('updating existing payment with period: '+period+' and month: '+month)			
					Payment payment = Payment.get(paymentIdList[i])
				}else{
					 log.error('creating new payment with period: '+period+' and month: '+month)				 
					Payment payment = new Payment(employee,period, month as int,newValuesAsString[i] as double)	
					payment.save flush:true
				}
				log.error('values are different:'+paymentsAsLong[i]+' vs '+newValuesAsString[i])
			}
			log.debug("i: "+i)
			if (i != 0 && i%12 == 0){
				employeeMap.put(employee,table)
				iterator+=1
				table=[]
			}
			
		}
		def payment=params["payment"]
		log.error('done')
		def model = [fromIndex:true,siteId:site.id,periodId:period.id,site:site]
		
		model << paymentService.getReportData( period, site)
		//render template:"/payment/template/paymentTemplate",model:model
		render(view:'paymentReport',model:model)
	//	redirect(action: "paymentReport",params:model)
		return
	}
}
