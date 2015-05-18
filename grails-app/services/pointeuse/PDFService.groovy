package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields
import groovy.time.TimeDuration
import groovy.time.TimeCategory
import groovyx.gpars.*

class PDFService {
	def timeManagerService
	def paymentService
	def pdfRenderingService
	def grailsApplication
	
	
	def generateSiteMonthlyTimeSheet(Date myDate,Site site,String folder){
		def fileNameList=[]
		def filename
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		calendar.time = myDate
		OutputStream outputStream
		File file
		
		def employeeList = Employee.findAllBySite(site)
		for (Employee employee:employeeList){
			log.error('method pdf siteMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', First Name= '+employee.firstName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
			def modelReport=timeManagerService.getReportData((site.id).toString(),employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
			modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
			//log.error('getReportData and getYearSupTime finalized')		
			// Get the bytes
			ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeReportTemplate', model: modelReport)
			filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+employee.lastName + '.pdf'
			fileNameList.add(filename)
			outputStream = new FileOutputStream (folder+'/'+filename);
			bytes.writeTo(outputStream)
			if(bytes)
				bytes.close()
			if(outputStream)
				outputStream.close()
		}
		finalCopy = new PdfCopyFields(new FileOutputStream(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf'));
		finalCopy.open()
		for (String tmpFile:fileNameList){
			PdfReader pdfReader = new PdfReader(folder+'/'+tmpFile)
			finalCopy.addDocument(pdfReader)	
		}
		finalCopy.close();		
		file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')		
		return [file.bytes,file.name]
	}
	

	def generateUserMonthlyTimeSheet(Date myDate,Employee employee,String folder){
		log.error('generateUserMonthlyTimeSheet called')
		
		def filename
		Calendar calendar = Calendar.instance
		calendar.time = myDate
		OutputStream outputStream
		File file

		log.error('method pdf generateUserMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
		def modelReport=timeManagerService.getReportData(employee.site.id as String,employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
		modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
		log.error('getReportData and getYearSupTime finalized with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeReportTemplate', model: modelReport)
		filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+employee.lastName + '.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+employee.lastName+'.pdf')
		return [file.bytes,file.name]
	}

	def generateUserAnnualTimeSheet(int year,int month,Employee employee,String folder){
		def filename
		OutputStream outputStream
		File file

		log.error('method pdf generateUserAnnualTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+year+', Month= '+month)

		def modelReport = timeManagerService.getAnnualReportData(year, employee)

		//def offHoursTime = 
		
		modelReport << timeManagerService.getOffHoursTime(employee,year)
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeAnnualReportTemplate', model: modelReport)
		filename = year.toString()+'-'+employee.lastName +'-annualReport' +'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}


	def generateEcartSheet(Site site,String folder,def monthList,def period){
		def filename
		OutputStream outputStream
		File file
		def modelEcart=timeManagerService.getEcartData(site, monthList, period)
		modelEcart << [site:site]
		def siteName = (site.name).replaceAll("\\s","").trim()
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeEcartPDFTemplate', model: modelEcart)
		filename = period.year.toString()+'-'+siteName +'-ecartReport' +'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}

	def generateDailySheet(Site site,String folder,Date currentDate){
		def filename
		OutputStream outputStream
		File file
		def modelDaily=timeManagerService.getDailyInAndOutsData(site,currentDate)
		modelDaily << [site:site]
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/listDailyTimePDFTemplate', model: modelDaily)
		def siteName = (site.name).replaceAll("\\s","").trim()
		filename = (currentDate.format('yyyy-mm-dd')).toString()+'-'+siteName +'-dailyReport' +'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	def generateSiteTotalSheet(Site site,String folder,Period period){
		def filename
		OutputStream outputStream
		File file
		def modelSiteTotal = [:]
		def executionTime
		def data
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
		def startDate = new Date()
		def employeeList = Employee.findAllBySite(site)
		
		GParsExecutorsPool.withPool {
			 employeeList.iterator().eachParallel {
				 println it
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
				 siteAnnualPayableSupTime += data.get('annualPayableSupTime')
				 siteAnnualTheoriticalIncludingExtra += data.get('annualTheoriticalIncludingExtra') as long
				 siteAnnualSupTimeAboveTheoritical += data.get('annualSupTimeAboveTheoritical') as long
				 siteAnnualGlobalSupTimeToPay += data.get('annualGlobalSupTimeToPay')
			 }
		 }
		
		modelSiteTotal << [
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
		
		modelSiteTotal << [site:site,period2:period]
		def siteName = (site.name).replaceAll("\\s","").trim()
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeSiteTotalPDFTemplate', model: modelSiteTotal)
		filename = siteName+'-'+period.year+'-'+(period.year+1)+'-report' +'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		def endDate = new Date()
		use (TimeCategory){executionTime=endDate-startDate}
		log.error('execution time for generateSiteTotalSheet: '+executionTime)
		return [file.bytes,file.name]
	}
	
	def generateAllSiteTotalSheet(String folder,Period period){
		def filename
		def fileNameList=[]
		PdfCopyFields finalCopy
		OutputStream outputStream
		File file
		
		def sites = Site.findAll();
		
		for (Site site:sites){
			def modelSiteTotal=timeManagerService.getSiteData(site,period)
			modelSiteTotal << [site:site,period2:period]
			def siteName = (site.name).replaceAll("\\s","").trim()
			
			// Get the bytes
			ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeSiteTotalPDFTemplate', model: modelSiteTotal)
			filename = siteName+'-'+period.year+'-'+(period.year+1)+'-report' +'.pdf'
			fileNameList.add(filename)
			outputStream = new FileOutputStream (folder+'/'+filename);
			bytes.writeTo(outputStream)
			if(bytes)
				bytes.close()
			if(outputStream)
				outputStream.close()
		}
		finalCopy = new PdfCopyFields(new FileOutputStream(folder+'/'+period.year+'-'+(period.year+1) +'-allSites'+'.pdf'));
		finalCopy.open()
		for (String tmpFile:fileNameList){
			PdfReader pdfReader = new PdfReader(folder+'/'+tmpFile)
			finalCopy.addDocument(pdfReader)	
		}
		finalCopy.close();		
		file = new File(folder+'/'+period.year+'-'+(period.year+1) +'-allSites'+'.pdf')		
		return [file.bytes,file.name]
	}
	
	def killThemAll(){
		def folder = grailsApplication.config.pdf.directory
		def fileNameList=[]
		def filename
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		calendar.roll(Calendar.MONTH,-1)
		OutputStream outputStream
		File file
		
		
		def sites = Site.findAll()
		
		for (Site site: sites){
		
			def employeeList = Employee.findAllBySite(site)
			for (Employee employee:employeeList){
				log.error('method pdf siteMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
				def modelReport=timeManagerService.getReportData((site.id).toString(),employee,calendar.time,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
				modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
				log.error('getReportData and getYearSupTime finalized')
				
				// Get the bytes
				ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeReportTemplate', model: modelReport)
				filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+employee.lastName + '.pdf'
				fileNameList.add(filename)
				outputStream = new FileOutputStream (folder+'/'+filename);
				bytes.writeTo(outputStream)
				if(bytes)
					bytes.close()
				if(outputStream)
					outputStream.close()
			}
			finalCopy = new PdfCopyFields(new FileOutputStream(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf'));
			finalCopy.open()
			for (String tmpFile:fileNameList){
				PdfReader pdfReader = new PdfReader(folder+'/'+tmpFile)
				finalCopy.addDocument(pdfReader)
			}
			finalCopy.close();
			file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')
		}
	}

	def generateSitePaymentSheet(def model,String folder){
		def filename		
		OutputStream outputStream
		File file
		def siteName = (model.site.name).replaceAll("\\s","").trim()
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeHSPDFTemplate', model: model)
		filename = model.period.year.toString()+'-'+siteName +'-HSPaymentReport' +'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	
	def generateAllSitesPaymentSheet(Period period,String folder){
		log.error('generating paymentPDF for all sites with period: '+period)
		def fileNameList=[]
		def filename
		Calendar calendar = Calendar.instance
		PdfCopyFields finalCopy
		OutputStream outputStream
		File file
		
		for (Site site:Site.findAll()){
			ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeHSPDFTemplate', model: paymentService.getReportData(period,site))
			filename = period.year.toString()+'-'+site.name +'-HSPaymentReport' +'.pdf'
			fileNameList.add(filename)
			outputStream = new FileOutputStream (folder+'/'+filename);
			bytes.writeTo(outputStream)
			if(bytes)
				bytes.close()
			if(outputStream)
				outputStream.close()
		}
		
		def finalCopyName = folder+'/'+period.year+'-'+'paymentReport'+'.pdf'
		finalCopy = new PdfCopyFields(new FileOutputStream(finalCopyName));
		finalCopy.open()
		for (String tmpFile:fileNameList){
			PdfReader pdfReader = new PdfReader(folder+'/'+tmpFile)
			finalCopy.addDocument(pdfReader)
		}
		finalCopy.close();
		file = new File(finalCopyName)
		return [file.bytes,file.name]
	}
}
