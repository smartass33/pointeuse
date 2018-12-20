package pointeuse

import java.util.Date;

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfCopyFields
import groovy.time.TimeDuration
import groovy.time.TimeCategory
import groovyx.gpars.*
import org.hibernate.exception.LockAcquisitionException
import org.hibernate.StaleObjectStateException

class PDFService {
	def timeManagerService
	def paymentService
	def itineraryService
	def mileageService
	def pdfRenderingService
	def grailsApplication
	
	
	def generateItineraryMonthlyReportByItinerary(def viewType, def itinerary, def currentCalendar, def folder){
		log.error('generateItineraryMonthlyReportByItinerary called for itinerary: '+itinerary.name+' and date: '+currentCalendar.time)
		def model
		def filename
		OutputStream outputStream
		File file
		def theoriticalActionsList = []
		def theoriticalSaturdayActionsList = []
		
		model = itineraryService.getActionMap(viewType, itinerary, currentCalendar, null)
		theoriticalActionsList         = itineraryService.getTheoriticalActionList(itinerary,false)
		theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(itinerary,true)
	
		model << [
			itinerary : itinerary,
			currentDate : currentCalendar.time,
			theoriticalActionsList:theoriticalActionsList,
			theoriticalSaturdayActionsList:theoriticalSaturdayActionsList
		]

		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeItineraryReportTemplate', model: model)
		filename = currentCalendar.get(Calendar.YEAR).toString()+ '-' + (currentCalendar.get(Calendar.MONTH)+1).toString()+'-'+itinerary.name+'-itineraire.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	def generateItineraryMonthlyReportBySite(def viewType, def itinerary, def currentCalendar, def site, def folder){
		log.error('generateItineraryMonthlyReportBySite called for site: '+site.name+' and date: '+currentCalendar.time)
		def model = [:]
		def filename
		OutputStream outputStream
		File file
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def serviceResponse
		def actionIterList
		def orderedActionList = []
		def theoriticalListRef = []
		def actionListMap = [:]
		
		serviceResponse = itineraryService.getActionMap(viewType, itinerary, currentCalendar, site)
		
		theoriticalActionsMap         = itineraryService.getTheoriticalActionMap(site,false)
		theoriticalSaturdayActionsMap = itineraryService.getTheoriticalActionMap(site,true)
		theoriticalActionsList         = itineraryService.getTheoriticalActionList(site,false)
		theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(site,true)
		
		currentCalendar.set(Calendar.DAY_OF_MONTH,1)

		
		for (int j = 1;j < currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;j++){
			log.debug("date: "+currentCalendar.time)
			
			theoriticalListRef = (currentCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) ? theoriticalActionsList.collect() : theoriticalSaturdayActionsList.collect()
			actionIterList = serviceResponse.get('actionListMap').get(currentCalendar.time)
			
			// compare to theoriticalActionsList
			if (theoriticalListRef != null && actionIterList != null && theoriticalListRef.size() == actionIterList.size()){
				orderedActionList = itineraryService.orderList(theoriticalListRef, actionIterList, [])
			}else{
				orderedActionList = actionIterList
			}
			actionListMap.put(currentCalendar.time,orderedActionList)
			orderedActionList = []
			currentCalendar.roll(Calendar.DAY_OF_MONTH,1)
		}
		

		model << [
			actionListMap:actionListMap,
			currentDate : currentCalendar.time,
			site:site,
			theoriticalActionsMap:theoriticalActionsMap,
			theoriticalSaturdayActionsMap:theoriticalSaturdayActionsMap,
			theoriticalSaturdayActionsList:theoriticalSaturdayActionsList,
			theoriticalActionsList:theoriticalActionsList
		]

		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeItineraryReportBySiteTemplate', model: model)
		filename = currentCalendar.get(Calendar.YEAR).toString()+ '-' + (currentCalendar.get(Calendar.MONTH)+1).toString()+'-'+site.name+'-itineraire.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	def generateSiteMonthlyTimeSheet(Date myDate,Site site,String folder){
		log.error('generateSiteMonthlyTimeSheet called for site: '+site.name+' and date: '+myDate)		
		def fileNameList=[]
		def filename
		PdfCopyFields finalCopy
		Calendar calendar = Calendar.instance
		calendar.time = myDate
		log.debug('calendar MONTH: '+calendar.get(Calendar.MONTH))
		log.debug('calendar YEAR: '+calendar.get(Calendar.YEAR))
		OutputStream outputStream
		File file	

		def employeeList = Employee.findAllBySite(site)
		try {
			for (Employee employee:employeeList){
				log.error('method pdf siteMonthlyTimeSheet with parameters: site: '+site.name+',  last name='+employee.lastName+', first name= '+employee.firstName+', year= '+calendar.get(Calendar.YEAR)+', month= '+(calendar.get(Calendar.MONTH)+1))
				def modelReport = timeManagerService.getReportData((site.id).toString(),employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,false)
				modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,false)
				ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeReportTemplate', model: modelReport)
				filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString() +'-'+employee.lastName +'-'+employee.firstName+'.pdf'
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
			site.lastReportDate=calendar.time
			site.save(flush:true)
		} catch( java.io.IOException ioe){
			log.error(ioe)
		}finally{
			file = new File(folder+'/'+calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-'+site.name+'.pdf')
			return [file.bytes,file.name]
		}
	}
	
	def generateUserMonthlyTimeSheetWithModel(Date myDate,Employee employee,String folder, def reportData, def yearSupTimeData){
		log.error('generateUserMonthlyTimeSheetWithModel called for employee: '+employee.firstName+' '+employee.lastName)
		def filename
		Calendar calendar = Calendar.instance
		calendar.time = myDate
		OutputStream outputStream
		File file
		boolean entityUpdate = true
		log.error('method pdf generateUserMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
		def modelReport = reportData
		modelReport << yearSupTimeData
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeReportTemplate', model: modelReport)
		filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString()+'-'+employee.lastName+'-'+employee.firstName+'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	def generateUserMonthlyTimeSheet(Date myDate,Employee employee,String folder){
		log.error('generateUserMonthlyTimeSheet called for employee: '+employee.firstName+' '+employee.lastName)	
		def filename
		Calendar calendar = Calendar.instance
		calendar.time = myDate
		OutputStream outputStream
		File file
		boolean entityUpdate = true
		log.error('method pdf generateUserMonthlyTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
		def modelReport = timeManagerService.getReportData(employee.site.id as String,employee,myDate,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,false)
		modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,false)
		log.error('getReportData and getYearSupTime finalized with parameters: Last Name='+employee.lastName+', Year= '+calendar.get(Calendar.YEAR)+', Month= '+(calendar.get(Calendar.MONTH)+1))
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeReportTemplate', model: modelReport)
		filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString()+'-'+employee.lastName+'-'+employee.firstName+'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	def generateUserMonthlyMileageSheet(Date minDate, Date maxDate,Employee employee,String folder){
		log.error('generateUserMonthlyMileageSheet called for employee: '+employee.firstName+' '+employee.lastName)
		def filename
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		File file
		boolean entityUpdate = true
		def modelReport = timeManagerService.getMileage(minDate, maxDate, employee)
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeUserMileageTemplate', model: modelReport)
		filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString()+'-Kilometres-'+employee.lastName+'-'+employee.firstName+'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	
	
	def generateYearSiteMileageSheet(def infYear, def supYear, def siteList , folder){
		log.error('generateYearSiteMileageSheet called between year: '+infYear+' and year '+supYear)
		def filename
		Calendar calendar = Calendar.instance
		OutputStream outputStream
		File file
		boolean entityUpdate = true
		def modelReport = mileageService.getAllSitesOverPeriod(siteList,infYear,supYear)
		modelReport << [infYear:infYear,supYear:supYear]
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeYearSiteMileageTemplate', model: modelReport)
		filename = calendar.get(Calendar.YEAR).toString()+ '-' + (calendar.get(Calendar.MONTH)+1).toString()+'-Kilometres-'+infYear+'-'+supYear+'-Sites'+'.pdf'
		outputStream = new FileOutputStream (folder+'/'+filename);
		bytes.writeTo(outputStream)
		if(bytes)
			bytes.close()
		if(outputStream)
			outputStream.close()
		file = new File(folder+'/'+filename)
		return [file.bytes,file.name]
	}
	

	def generateUserAnnualTimeSheet(int year,int month,Employee employee,String folder){
		def filename
		OutputStream outputStream
		File file
		log.error('method pdf generateUserAnnualTimeSheet with parameters: Last Name='+employee.lastName+', Year= '+year+', Month= '+month)
		def modelReport = timeManagerService.getAnnualReportData(year, employee)		
		modelReport << timeManagerService.getOffHoursTimeNoUpdate(employee,year)
		// Get the bytes
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/completeAnnualReportTemplate', model: modelReport)
		filename = year.toString()+'-'+employee.lastName +'-'+employee.firstName +'-annualReport' +'.pdf'
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
		def modelDaily=timeManagerService.getDailyInAndOutsData(site.id,currentDate)
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
	
	def generateDailySheetWithStyle(Site site,String folder,Date currentDate){
		def filename
		OutputStream outputStream
		File file
		def modelDaily=timeManagerService.getDailyInAndOutsData(site,currentDate)
		modelDaily << [site:site]
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/listDailyTimePDFWithStyleTemplate', model: modelDaily)
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
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualSickness = 0
		def siteAnnualMaternite = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0
		def siteAnnualDIF = 0
		def siteAnnualDON = 0
		def siteAnnualPayableSupTime = 0
		def siteAnnualTheoriticalIncludingExtra = 0
		def siteAnnualSupTimeAboveTheoritical = 0
		def siteAnnualGlobalSupTimeToPay = 0	
		def startDate = new Date()
		def employeeList = Employee.findAllBySite(site)
		
		GParsExecutorsPool.withPool {
			 employeeList.iterator().eachParallel {
				 try{
					 log.debug(it)
					 log.debug(' and period: ' + period.year)
					 data = timeManagerService.getAnnualReportData(period.year, it)
					 annualReportMap.put(it,data)
					 if (data != null){
						 siteAnnualEmployeeWorkingDays += data.get('annualEmployeeWorkingDays')
						 siteAnnualTheoritical += data.get('annualTheoritical')
						 siteAnnualTotal += data.get('annualTotal')
						 siteAnnualHoliday += data.get('annualHoliday')
						 siteRemainingCA += data.get('remainingCA')
						 siteAnnualRTT += data.get('annualRTT')
						 siteAnnualCSS += data.get('annualCSS')
						 siteAnnualINJUSTIFIE += data.get('annualINJUSTIFIE')
						 siteAnnualSickness += data.get('annualSickness')
						 siteAnnualMaternite += data.get('annualMaternite')					 
						 siteAnnualDIF += data.get('annualDIF')
						 siteAnnualDON += data.get('annualDON')
						 siteAnnualExceptionnel += data.get('annualExceptionnel')
						 siteAnnualPaternite += data.get('annualPaternite')
						 siteAnnualPayableSupTime += data.get('annualPayableSupTime')
						 siteAnnualTheoriticalIncludingExtra += data.get('annualTheoriticalIncludingExtra') as long
						 siteAnnualSupTimeAboveTheoritical += data.get('annualSupTimeAboveTheoritical') as long
						 siteAnnualGlobalSupTimeToPay += data.get('annualGlobalSupTimeToPay')
				 	}else{
					 	log.debug('data is null for: ')
						log.debug(it)
				 	}
				 }catch (Exception e){
					 log.debug('error with application: '+e.toString())
					 log.debug(it)
				 }

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
			siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
			siteAnnualSickness:siteAnnualSickness,
			siteAnnualMaternite:siteAnnualMaternite,		
			siteAnnualDIF:siteAnnualDIF,
			siteAnnualDON:siteAnnualDON,
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
				def modelReport=timeManagerService.getReportData((site.id).toString(),employee,calendar.time,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,false)
				modelReport << timeManagerService.getYearSupTime(employee,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,false)
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
	
	def generateSiteInfo(Date date,Site site,String folder){
		def filename
		OutputStream outputStream
		File file
		def calendar = Calendar.instance
		calendar.time = date
		def employeeInstanceList = Employee.findAllBySite(site)
		ByteArrayOutputStream bytes = pdfRenderingService.render(template: '/pdf/siteInfoTemplate', model: [employeeInstanceList:employeeInstanceList])	
		filename = calendar.get(Calendar.YEAR).toString()+'-'+(calendar.get(Calendar.MONTH)+1).toString() +'-INFO-'+site.name+'.pdf'
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
