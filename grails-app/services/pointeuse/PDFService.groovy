package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

class PDFService {
	def timeManagerService
	def pdfRenderingService
	
	def generateSiteMonthlyTimeSheet(Date myDate,Site site,String folder){
		def bytesMap=[:]
		def fileNameList=[]
		def filename
		def userId
		def siteId
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		File file
		
		def employeeList = Employee.findAllBySite(site)
		for (Employee employee:employeeList){
			log.error('method pdf siteMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
			def modelReport=timeManagerService.getReportData((site.id).toString(),employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
			// Get the bytes
			ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/common/completeReportTemplate', model: modelReport)
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
	
	
	def generateEcartSheet(Date myDate,Site site,String folder){
		def bytesMap=[:]
		def fileNameList=[]
		def filename
		def userId
		def siteId
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		File file
		
		def employeeList = Employee.findAllBySite(site)
		for (Employee employee:employeeList){
			log.error('method pdf siteMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
			def modelReport=timeManagerService.getReportData((site.id).toString(),employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
			// Get the bytes
			ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/common/ecartPDFTemplate', model: modelReport)
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
	
}
