package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields
import groovy.time.TimeDuration
import groovy.time.TimeCategory

class PDFService {
	def timeManagerService
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
		log.error('getReportData and getYearSupTime finalized')
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
		def modelSiteTotal=timeManagerService.getSiteData(site,period)
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
	//	return [file.bytes,file.name]
	}

	
}
