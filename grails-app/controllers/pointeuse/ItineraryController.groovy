package pointeuse

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.core.io.Resource
import org.springframework.dao.DataIntegrityViolationException
import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable

import groovy.json.JsonSlurper
import groovy.time.TimeDuration
import groovy.time.TimeCategory
import grails.converters.JSON

import org.apache.commons.logging.LogFactory
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.codehaus.groovy.grails.core.io.ResourceLocator

import pl.touk.excel.export.WebXlsxExporter
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.CellStyle

import java.util.concurrent.*

import groovyx.gpars.GParsConfig
import groovyx.gpars.GParsPool

import groovy.time.TimeCategory;

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

import java.text.ParseException;

//@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
class ItineraryController {
	def springSecurityService
	def itineraryService
	def PDFService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Itinerary.list(params), model:[itineraryInstanceCount: Itinerary.count()]
    }

	def addAction(){
		log.error('addAction called')
		
	}
	
	def show(Itinerary itineraryInstance) {
        respond itineraryInstance
    }

    def create() {
		def isExpanded = false
		def coursierFunction = Function.findByName('Coursier')
		def employeeList = Employee.findAllByFunction(coursierFunction)
		[employeeList:employeeList,checked:isExpanded]
    }
	
	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
	def itineraryPDF(){
		log.error('itinerarySitePDF called')
		params.each{i->log.error('parameter of list: '+i)}
		def folder = grailsApplication.config.pdf.directory
		def itinerary
		def viewType
		def siteTemplate
		def site
		def date_picker
		def serviceResponse
		def retour
		def currentCalendar = Calendar.instance
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('id')){
				viewType = params[name]
				siteTemplate = viewType.contains('BySite') ? true : false
			}
			if (name.contains('siteId'))
				site = Site.get(params.int(name))
			if (name.contains('date_picker')){
				date_picker = params[name]
				if (date_picker != null && date_picker.size() > 0){
					currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
					log.debug('currentCalendar.time: '+currentCalendar.time)
				}
			}
		}	
		
		if (siteTemplate){
			retour = PDFService.generateItineraryMonthlyReportBySite(viewType, itinerary, currentCalendar, site, folder)
		}else{
			retour = PDFService.generateItineraryMonthlyReportByItinerary(viewType, itinerary, currentCalendar, folder)
		}
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "filename=${retour[1]}")
		response.outputStream << retour[0]
	}
	
	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
	def trash(){
		log.error('trash called')
		params.each{i->log.debug('parameter of list: '+i)}
		def actionsList
		def criteria
		def action = Action.get(params["actionItemId"])
		def itinerary = action.itinerary
		action.delete flush:true
		criteria = Action.createCriteria()
		actionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		render template: "/itinerary/template/theoriticalActionTable", model: [
			itineraryInstance:itinerary,theoriticalActionsList:actionsList
		]
		return
	}
	
	@Secured(['ROLE_ADMIN','ROLE_TOURNEE'])
	def addTheoriticalAction(){
		log.error('addAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def itinerary
		def actionType
		def employee
		def currentDate
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('employeeId'))
				employee = Employee.get(params.int(name))
			if (name.contains('siteId'))
				site = Site.get(params.int(name))
			if (name.contains('time_picker')){		
				try {
				currentDate =  new Date().parse("dd/MM/yyyy HH:mm", params[name])
				} catch (ParseException e) {
					currentDate =  new Date().parse("HH:mm", params[name])
				}
			}
			if (name.contains('action.type'))
				actionType = (params[name]).equals('ARRIVEE') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
		}
		
		def action = new Action(itinerary, currentDate, site, itinerary.deliveryBoy, actionType)
		action.isTheoritical = true
		action.isSaturday = itinerary.isSaturday
		action.save flush:true
		def actionsList
		def criteria = Action.createCriteria()
		actionsList= criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		render template: "/itinerary/template/theoriticalActionTable", model: [
			itineraryInstance:itinerary,theoriticalActionsList:actionsList
			]
		return
	}
	
	def itineraryReport(){
		params.each{i->log.error('parameter of list: '+i)}
		def itinerary
		def itineraryId
		params.each { name, value ->
			if (name.contains('itineraryId')){
				itinerary = Itinerary.get(params.int(name))
				itineraryId = params.int(name)
			}
		}
		if (itinerary != null){
			[itineraryIdFromIndex:itinerary.id,itinerary:itinerary,itineraryId:itineraryId]
		}else{
			[itineraryId:itineraryId]
		}
	}
	
	def itinerarySiteView(){
		
	}

	def showAnomalies(){
		log.error('showAnomalies called')
		params.each{i->log.debug('parameter of list: '+i)}
		
		def itinerary
		def site
		def date_picker
		def criteria
		def actionsList = []
		def currentCalendar = Calendar.instance
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('siteId'))
				site = Site.get(params.int(name))
			if (name.contains('date_picker')){
				date_picker = params[name]
				if (date_picker != null && date_picker.size() > 0){
					currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
					log.debug('currentCalendar.time: '+currentCalendar.time)
				}
			}
		}
		
		criteria = Action.createCriteria()
		actionsList = criteria.list {
			and {
				eq('month',currentCalendar.get(Calendar.MONTH) + 1)
				eq('year',currentCalendar.get(Calendar.YEAR))
				eq('isTheoritical',false)
				eq('site',site)
				order('date','asc')
			}
		}
		
	}
	
	def showItineraryActions(){
		log.error('showItineraryActions called')
		params.each{i->log.error('parameter of list: '+i)}
		
		def itinerary
		def currentCalendar = Calendar.instance
		def criteria 
		def actionsList
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def date_picker
		def viewType
		def timeDiff
		def timeDiffMap = [:]
		def actionListMapUpdated = [:]
		def actionListNotOrderedMap = [:]
		def calendar = Calendar.instance
		def i = 0
		def hasDiscrepancy = false
		def serviceResponse 
		def site
		def siteTemplate
		def retour
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def actionThOrderList
		def actionIterList
		def orderedActionList = []
		def theoriticalListRef = []
		
		params.each { name, value ->
			if (name.contains('itineraryIdFromIndex') && value.size() > 0)
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('id')){
				viewType = params[name]
				siteTemplate = viewType.contains('BySite') ? true : false
			}
			if (name.contains('siteId'))
				site = Site.get(params.int(name))	
			if (name.contains('date_picker')){
				date_picker = params[name]
				if (date_picker != null && date_picker.size() > 0){
					currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
					log.debug('currentCalendar.time: '+currentCalendar.time)
				}
			}
		}
		if (params['itineraryId'] != null){
			itinerary = Itinerary.get(params.int('itineraryId'))
		}
		serviceResponse = itineraryService.getActionMap(viewType, itinerary, currentCalendar, site)
		
		if (siteTemplate){
			theoriticalActionsMap        	= itineraryService.getTheoriticalActionMap(site,false) 
			theoriticalSaturdayActionsMap  	= itineraryService.getTheoriticalActionMap(site,true) 
			theoriticalActionsList         	= itineraryService.getTheoriticalActionList(site,false)
			theoriticalSaturdayActionsList 	= itineraryService.getTheoriticalActionList(site,true)			
		}else{
			theoriticalActionsList         = itineraryService.getTheoriticalActionList(itinerary,false)
			theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(itinerary,true)
		}
		
		currentCalendar.set(Calendar.DAY_OF_MONTH,1)

		for (int j = 1;j < currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;j++){
			log.debug("date: "+currentCalendar.time)
			def notOrderedActionList = serviceResponse.get('actionListMap').get(currentCalendar.time).collect()
			actionListNotOrderedMap.put(currentCalendar.time, notOrderedActionList)
			theoriticalListRef = (currentCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) ? theoriticalActionsList.collect() : theoriticalSaturdayActionsList.collect()
			actionIterList = serviceResponse.get('actionListMap').get(currentCalendar.time)
			
			// compare to theoriticalActionsList
			if (theoriticalListRef != null && actionIterList != null && theoriticalListRef.size() == actionIterList.size()){
				orderedActionList = itineraryService.orderList(theoriticalListRef, actionIterList, [])
			}else{
				orderedActionList = actionIterList
			}
			actionListMapUpdated.put(currentCalendar.time,orderedActionList)
			orderedActionList = []
			currentCalendar.roll(Calendar.DAY_OF_MONTH,1)
		}
	
		if (siteTemplate){
			render template: "/itinerary/template/itinerarySiteReportTemplate", 
				model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:actionListMapUpdated,
				actionListNotOrderedMap:actionListNotOrderedMap,
				dailyActionMap:serviceResponse.get('dailyActionMap'),
				theoriticalActionsList:theoriticalActionsList,
				theoriticalSaturdayActionsList:theoriticalSaturdayActionsList,
				theoriticalActionsMap:theoriticalActionsMap,
				theoriticalSaturdayActionsMap:theoriticalSaturdayActionsMap,
				hasDiscrepancy:hasDiscrepancy,
				timeDiffMap:timeDiffMap,
				viewType:viewType,
				siteTemplate:siteTemplate,
				site:site,
				timeDiffMap:timeDiffMap
				]
			return
		}else{
			render template: "/itinerary/template/itineraryReportTemplate", 
				model: [
				itineraryInstance:itinerary,
				actionsList:serviceResponse.get('actionsList'),
				actionListMap:actionListMapUpdated,
				actionListNotOrderedMap:actionListNotOrderedMap,
				dailyActionMap:serviceResponse.get('dailyActionMap'),
				theoriticalActionsList:theoriticalActionsList,
				theoriticalSaturdayActionsList:theoriticalSaturdayActionsList,
				hasDiscrepancy:hasDiscrepancy,
				timeDiffMap:timeDiffMap,
				viewType:viewType,
				siteTemplate:siteTemplate,
				timeDiffMap:timeDiffMap
				]
			return
		}
	}
	
    @Transactional
    def save(Itinerary itineraryInstance) {
		def user = springSecurityService.currentUser
		params.each{i->log.error('parameter of list: '+i)}
		def deliveryBoy = Employee.get(params.int('deliveryBoyId'))
		itineraryInstance.deliveryBoy = deliveryBoy
		itineraryInstance.creationUser = user
		itineraryInstance.creationDate = new Date()
		itineraryInstance.validate()
        if (itineraryInstance == null) {
            notFound()
            return
        }

        if (itineraryInstance.hasErrors()) {
            respond itineraryInstance.errors, view:'create'
            return
        }

        itineraryInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), itineraryInstance.name])

                redirect itineraryInstance
            }
            '*' { respond itineraryInstance, [status: CREATED] }
        }
    }
	
	def itinerarySiteExcel(){
		log.error('itinerarySiteExcel called')		
		def folder = grailsApplication.config.pdf.directory
		def itinerary
		def currentCalendar = Calendar.instance
		def criteria
		def actionsList
		def theoriticalActionsList
		def theoriticalSaturdayActionsList
		def date_picker
		def viewType
		def timeDiff
		def timeDiffMap = [:]
		def actionListMap = [:]
		def calendar = Calendar.instance
		def i = 0
		def hasDiscrepancy = false
		def serviceResponse
		def site
		def siteTemplate
		def retour
		def theoriticalActionsMap = [:]
		def theoriticalSaturdayActionsMap = [:]
		def actionIterList
		def orderedActionList = []
		def theoriticalListRef = []
		def row = 2
		def col = 1
		def iter = 1
		def actionThOrderList
		XSSFCellStyle myStyle
		XSSFCell cell
		XSSFRow excelRow
		CreationHelper createHelper
		def theoriticalCal = Calendar.instance
		def realCal = Calendar.instance
		def fontDEPART
		def fontARRIVEE
		def computeTimeDiff = false
		
		params.each { name, value ->
			if (name.contains('itineraryId'))
				itinerary = Itinerary.get(params.int(name))
			if (name.contains('id')){
				viewType = params[name]
				siteTemplate = viewType.contains('BySite') ? true : false
			}
			if (name.contains('siteId'))
				site = Site.get(params.int(name))
			if (name.contains('date_picker')){
				date_picker = params[name]
				if (date_picker != null && date_picker.size() > 0){
					currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
					log.debug('currentCalendar.time: '+currentCalendar.time)
				}
			}
		}
				
		serviceResponse = itineraryService.getActionMap(viewType, itinerary, currentCalendar, site)
		
		if (siteTemplate){
			theoriticalActionsMap        	= itineraryService.getTheoriticalActionMap(site,false)
			theoriticalSaturdayActionsMap  	= itineraryService.getTheoriticalActionMap(site,true)
			theoriticalActionsList         	= itineraryService.getTheoriticalActionList(site,false)
			theoriticalSaturdayActionsList 	= itineraryService.getTheoriticalActionList(site,true)
		}else{
			theoriticalActionsList         = itineraryService.getTheoriticalActionList(itinerary,false)
			theoriticalSaturdayActionsList = itineraryService.getTheoriticalActionList(itinerary,true)
		}
		
		currentCalendar.set(Calendar.DAY_OF_MONTH,1)

		
		new WebXlsxExporter(folder+'/tournees.xlsx').with { xlsxReporter ->
			setResponseHeaders(response)
			fontDEPART = xlsxReporter.workbook.createFont()
			fontDEPART.setColor(HSSFColor.BRIGHT_GREEN.index);
			fontARRIVEE = xlsxReporter.workbook.createFont()
			fontARRIVEE.setColor(HSSFColor.RED.index);
			excelRow = xlsxReporter.workbook.getSheetAt(0).createRow(0)
			theoriticalActionsList.each{actionItem->
				myStyle = xlsxReporter.workbook.createCellStyle()			
				if (actionItem.nature.equals(ItineraryNature.DEPART)){
					myStyle.setFont(fontDEPART)
				}else{
					myStyle.setFont(fontARRIVEE)
				}
				cell = excelRow.createCell(0)
				if (siteTemplate)
					cell.setCellValue(message(code: 'itinerary.WEEK'))
				cell = excelRow.createCell(iter)		
				myStyle.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY)
				if (actionItem != null){
					if (siteTemplate){
						cell.setCellValue(actionItem.itinerary.name+'\n'+actionItem.date.format('HH:mm'))
					}else{
						cell.setCellValue(actionItem.site.name+'\n'+actionItem.date.format('HH:mm'))
					}
				}
				cell.setCellStyle(myStyle)
				iter ++
			}
			iter = 1
			excelRow = xlsxReporter.workbook.getSheetAt(0).createRow(1)
			theoriticalSaturdayActionsList.each{actionItem->
				myStyle = xlsxReporter.workbook.createCellStyle()
				
				if (actionItem.nature.equals(ItineraryNature.DEPART)){
					myStyle.setFont(fontDEPART)
				}else{
					myStyle.setFont(fontARRIVEE)
				}
				cell = excelRow.createCell(0)
				if (siteTemplate)
					cell.setCellValue(message(code: 'itinerary.SATURDAY'))
				cell = excelRow.createCell(iter)				
				if (actionItem != null){
					if (siteTemplate){
						cell.setCellValue(actionItem.itinerary.name+'\n'+actionItem.date.format('HH:mm'))
					}else{
						cell.setCellValue(actionItem.site.name+'\n'+actionItem.date.format('HH:mm'))
					}
				}
				
				myStyle.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY)
				cell.setCellStyle(myStyle)
				iter ++
			}
			
			for (int j = 1;j < currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1;j++){
				theoriticalListRef = (currentCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) ? theoriticalActionsList.collect() : theoriticalSaturdayActionsList.collect()
				actionIterList = serviceResponse.get('actionListMap').get(currentCalendar.time)
				
				// compare to theoriticalActionsList
				if (theoriticalListRef != null && actionIterList != null && theoriticalListRef.size() == actionIterList.size()){
					orderedActionList = itineraryService.orderList(theoriticalListRef, actionIterList, [])
				}else{
					orderedActionList = actionIterList
				}		
				col = 1
				excelRow = xlsxReporter.workbook.getSheetAt(0).createRow(row)
				cell = excelRow.createCell(0)
				cell.setCellValue(currentCalendar.time)
				myStyle = xlsxReporter.workbook.createCellStyle()
				createHelper = xlsxReporter.workbook.getCreationHelper()
				myStyle.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy"))
				cell.setCellStyle(myStyle)
				
				for (Action actionItem : orderedActionList){ 				
					cell = excelRow.createCell(col);
					myStyle = xlsxReporter.workbook.createCellStyle();
					if (actionItem.nature.equals(ItineraryNature.DEPART)){
						myStyle.setFont(fontDEPART)					
					}else{
						myStyle.setFont(fontARRIVEE)
					}	
					theoriticalCal.time = actionItem.date
					realCal.time = actionItem.date	
					
					
					if (actionItem.date.getAt(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
						if (theoriticalActionsList.size >= orderedActionList.size()){
							theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalActionsList[col - 1].date.getAt(Calendar.HOUR_OF_DAY))
							theoriticalCal.set(Calendar.MINUTE,theoriticalActionsList[col - 1].date.getAt(Calendar.MINUTE))
							computeTimeDiff = true
						}else{
							computeTimeDiff = false
						}
					}else{
						if (theoriticalSaturdayActionsList.size >= orderedActionList.size()){
							theoriticalCal.set(Calendar.HOUR_OF_DAY,theoriticalSaturdayActionsList[col - 1].date.getAt(Calendar.HOUR_OF_DAY))
							theoriticalCal.set(Calendar.MINUTE,theoriticalSaturdayActionsList[col - 1].date.getAt(Calendar.MINUTE))
							computeTimeDiff = true
							}else{
								computeTimeDiff = false
							}
					}
					if (computeTimeDiff){
						use (TimeCategory){timeDiff = realCal.time - theoriticalCal.time}
						
						if ((timeDiff.minutes + timeDiff.hours*60) > 15){
							myStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(254, 251, 0)));
							myStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
						}
						if ((timeDiff.minutes + timeDiff.hours*60) > 30){
							myStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(116,243,254)));						
							myStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
						}
						if ((timeDiff.minutes + timeDiff.hours*60) > 60){
							myStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(255,142,121)));							
							myStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
						}
					}
					cell.setCellValue(actionItem.date.format('HH:mm').toString());
					cell.setCellStyle(myStyle);
					col ++	
				}
				row ++
				actionListMap.put(currentCalendar.time,orderedActionList)
				orderedActionList = []
				currentCalendar.roll(Calendar.DAY_OF_MONTH,1)
			}
			save(response.outputStream)
		}	
	}
	
	def changeDeliveryBoy(){		
		log.error('changeDeliveryBoy called')
		params.each{i->log.debug('parameter of list: '+i)}
		def itinerary = Itinerary.get(params.int('itineraryInstanceId'))
		def deliveryBoy = Employee.get(params.int('deliveryBoyId'))
		itinerary.deliveryBoy = deliveryBoy
		itinerary.save flush:true
		def coursierFunction = Function.findByName('Coursier')
		def employeeList = Employee.findAllByFunction(coursierFunction)
		def theoriticalActionsList
		def criteria = Action.createCriteria()
		theoriticalActionsList= criteria.list {
			and {
				eq('itinerary',itineraryInstance)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		return
	}
	
	
    def edit(Itinerary itineraryInstance) {
		def coursierFunction = Function.findByName('Coursier')
		def employeeList = Employee.findAllByFunction(coursierFunction)
		def theoriticalActionsList
		def criteria = Action.createCriteria()
		theoriticalActionsList= criteria.list {
			and {
				eq('itinerary',itineraryInstance)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
       [itineraryInstance:itineraryInstance,theoriticalActionsList:theoriticalActionsList,employeeList:employeeList]
    }

    @Transactional
    def update(Itinerary itineraryInstance) {
		
		params.each{i->log.debug('parameter of list: '+i)}
        if (itineraryInstance == null) {
            notFound()
            return
        }

        if (itineraryInstance.hasErrors()) {
            respond itineraryInstance.errors, view:'edit'
            return
        }

        itineraryInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), itineraryInstance.name])
                redirect itineraryInstance
            }
            '*'{ respond itineraryInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Itinerary itineraryInstance) {

        if (itineraryInstance == null) {
            notFound()
            return
        }

        itineraryInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), itineraryInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'itinerary.label', default: 'Itinerary'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
