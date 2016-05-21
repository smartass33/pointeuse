package pointeuse

import java.util.Calendar
import java.util.Date;

import org.apache.commons.logging.LogFactory

import pointeuse.PDFService
import pointeuse.Site
import groovy.time.TimeDuration
import groovy.time.TimeCategory

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields

import grails.util.Holders


class SiteMonthlyPDFJob {
	private static final log = LogFactory.getLog(this)
	
	//def employeeService
	//def PDFService
	def timeManagerService
	
	static triggers = {
		// fire trigger every day of the month at 3AM
		cron name: 'myTrigger', startDelay: 60000, cronExpression: "0 0 3 * * ?"

	}
	def group = "MyGroup"

/*
	def execute(context) {
		log.error "SiteMonthlyPDFJob is running!"	
		def registryCalendar = Calendar.instance
		def timeDifference	
		def folder = context.mergedJobDataMap.get('folder')//grailsApplication.config.pdf.directory
		def retour
		Date startDate = new Date()
		Calendar calendar= Calendar.instance
		log.error("createAllSitesPDF called at: "+calendar.time)
		calendar.roll(Calendar.MONTH,-1)
		def currentDate = calendar.time
		def sites = Site.findAll()
		def threads = []	
		def thr = sites.each{ site ->		
			if (site.id != 16){
				def th = new Thread({								
					log.error('generating PDF for site: '+site.name)
					retour = PDFService.generateSiteMonthlyTimeSheet(currentDate,site,folder as String)
				})
				println "putting thread in list"
				threads << th
			}
		}	
		threads.each { it.start() }
		threads.each { it.join() }
			
		def thTime = Thread.start{
			
			def endDate = new Date()
			log.error('end time= '+endDate)
			use (TimeCategory){timeDifference = endDate - startDate}
			log.error("le rapport a pris: "+timeDifference)
		}
		thTime.join()
			
		if (registryCalendar.get(Calendar.DAY_OF_YEAR) == registryCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			registryCalendar.roll(Calendar.YEAR,1)
			registryCalendar.set(Calendar.DAY_OF_YEAR,1)
		}else{
			registryCalendar.roll(Calendar.DAY_OF_YEAR,1)
		}
		registryCalendar.set(Calendar.HOUR_OF_DAY,3)
		registryCalendar.set(Calendar.MINUTE,0)
		log.error 'registring SiteMonthlyPDFJob at '+registryCalendar.time

		this.schedule(registryCalendar.time,[folder:folder])
		
	}
	*/
	
	/*
	def execute(context){
		def registryCalendar = Calendar.instance
		
		//employeeService.createAllSitesPDF()
		pdfService.killThemAll()
		if (registryCalendar.get(Calendar.DAY_OF_YEAR) == registryCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			registryCalendar.roll(Calendar.YEAR,1)
			registryCalendar.set(Calendar.DAY_OF_YEAR,1)
		}else{
			registryCalendar.roll(Calendar.DAY_OF_YEAR,1)
		}
		registryCalendar.set(Calendar.HOUR_OF_DAY,3)
		registryCalendar.set(Calendar.MINUTE,0)
		log.error 'registring SiteMonthlyPDFJob at '+registryCalendar.time

		this.schedule(registryCalendar.time)
	}
	
	*/
	
	
	
	def execute(context){
		Calendar calendar= Calendar.instance
		
		log.error("createAllSitesPDF called at: "+calendar.time)
		
		def timeDifference
		def folder = context.mergedJobDataMap.get('folder')
		def retour
		Date startDate = new Date()
		calendar.roll(Calendar.MONTH,-1)
		def currentDate = calendar.time
		
		//log.error('start time= '+myDate)
		def sites = Site.findAll()

		
		def threads = []
	
		def thr = sites.each{ site ->
			
			if (site.id != 16){
				def th = new Thread({
									
					log.error('generating PDF for site: '+site.name)
					retour = generateSiteMonthlyTimeSheet(currentDate,site,folder)
				})
				println "putting thread in list"
				threads << th
			}
		}
	
		threads.each { it.start() }
		threads.each { it.join() }
			
		def thTime = Thread.start{
			
			def endDate = new Date()
			log.error('end time= '+endDate)
			use (TimeCategory){timeDifference = endDate - startDate}
			log.error("le rapport a pris: "+timeDifference)
		}
		thTime.join()
	}

		
		
	void generateSiteMonthlyTimeSheet(Date myDate,Site site,String folder){
		def fileNameList=[]
		def filename
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		calendar.time = myDate
		OutputStream outputStream
		File file
		//def timeManagerService = Holders.grailsApplication.mainContext.getBean 'timeManagerService'
		
		
		def employeeList = Employee.findAllBySite(site)
		for (Employee employee:employeeList){
			log.error('method pdf siteMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
			def modelReport = timeManagerService.getReportData((site.id).toString(),employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,true)
			modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,true)
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
	//	return [file.bytes,file.name]
	}
	
}
