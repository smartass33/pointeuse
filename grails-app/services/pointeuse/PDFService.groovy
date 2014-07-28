package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

class PDFService {
	def timeManagerService
	def pdfRenderingService
	
	def generateSiteMonthlyTimeSheet(Date myDate,Site site,String folder){
		def fileNameList=[]
		def filename
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		File file
		
		def employeeList = Employee.findAllBySite(site)
		for (Employee employee:employeeList){
			log.error('method pdf siteMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
			def modelReport=timeManagerService.getReportData((site.id).toString(),employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
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
		def filename
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		File file

		log.error('method pdf generateUserMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
		def modelReport=timeManagerService.getReportData(employee.site.id as String,employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
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
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeEcartPDFTemplate', model: modelEcart)
		filename = period.year.toString()+'-'+site.name +'-ecartReport' +'.pdf'
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
		filename = (currentDate.format('yyyy-mm-dd')).toString()+'-'+site.name +'-dailyReport' +'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
}
