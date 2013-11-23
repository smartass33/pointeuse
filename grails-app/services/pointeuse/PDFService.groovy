package pointeuse

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

class PDFService {

	def timeManagerService
	def pdfRenderingService
	
	def siteMonthlyTimeSheet(Date myDate,Site site){
		log.error('method siteMonthlyTimeSheet called')
		
		def folder = grailsApplication.config.pdf.directory
		def bytesMap=[:]
		def fileNameList=[]
		def userId
		def siteId
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		
		def employeeList = Employee.findAllBySite(site)
		for (Employee employee:employeeList){
			log.error('method pdf called with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
			def cartoucheTable = timeManagerService.getCartoucheData(employee as int,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1) 
			def workingDays=cartoucheTable.get(3)
			def holiday=cartoucheTable.get(4)
			def rtt=cartoucheTable.get(5)
			def sickness=cartoucheTable.get(6)
			def sansSolde=cartoucheTable.get(7)
			def monthTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(8))
			def pregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(9))
			def yearlyHoliday=cartoucheTable.get(11)
			def yearlyRtt=cartoucheTable.get(12)
			def yearlySickness=cartoucheTable.get(13)
			def yearlyTheoritical = timeManagerService.computeHumanTime(cartoucheTable.get(14))
			def yearlyPregnancyCredit = timeManagerService.computeHumanTime(cartoucheTable.get(15))
			def yearlyActualTotal = timeManagerService.computeHumanTime(cartoucheTable.get(16))
			def yearlySansSolde=cartoucheTable.get(17)
			def openedDays = timeManagerService.computeMonthlyHours(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
			def yearInf
			def yearSup
			if ((calendar.get(Calendar.MONTH)+1)>5){
				yearInf=calendar.get(Calendar.YEAR)
				yearSup=calendar.get(Calendar.YEAR)+1
			}else{
				yearInf=calendar.get(Calendar.YEAR)-1
				yearSup=calendar.get(Calendar.YEAR)
			}
			def modelCartouche=[weeklyContractTime:employee.weeklyContractTime,matricule:employee.matricule,firstName:employee.firstName,lastName:employee.lastName,yearInf:yearInf,yearSup:yearSup,employee:employee,openedDays:openedDays,workingDays:workingDays,holiday:holiday,rtt:rtt,sickness:sickness,sansSolde:sansSolde,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyHoliday:yearlyHoliday,yearlyRtt:yearlyRtt,yearlySickness:yearlySickness,yearlyTheoritical:yearlyTheoritical,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyActualTotal:yearlyActualTotal,yearlySansSolde:yearlySansSolde]
			def modelReport=report(employee.id as int,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1)
			modelReport<<modelCartouche
			// Get the bytes
			ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/common/completeReportTemplate', model: modelReport)
			

			def filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+employee.lastName + '.pdf'
			fileNameList.add(filename)

			outputStream = new FileOutputStream (folder+'/'+filename);
			bytes.writeTo(outputStream);   
			
			
			
			if(bytes)
			   bytes.close();
	
			if(outputStream)
				outputStream.close();

		}
			PdfCopyFields finalCopy = new PdfCopyFields(new FileOutputStream(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf'));
			finalCopy.open();
			for (String tmpFile:fileNameList){
				PdfReader pdfReader = new PdfReader(folder+'/'+tmpFile)
				finalCopy.addDocument(pdfReader);
				
			}
			finalCopy.close();
			
			File file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')
			
			response.setContentType("application/octet-stream")
			response.setHeader("Content-disposition", "filename=${file.name}")
			response.outputStream << file.bytes
			return
		
	}
	
}
