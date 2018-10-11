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
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.codehaus.groovy.grails.core.io.ResourceLocator

import pl.touk.excel.export.WebXlsxExporter

import java.util.concurrent.*

import groovyx.gpars.GParsConfig
import groovyx.gpars.GParsPool

import groovy.time.TimeCategory;

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

//@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class ItineraryController {
	def springSecurityService
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
	
	@Secured(['ROLE_ADMIN'])
	def trash(){
		log.error('trash called')
		params.each{i->log.error('parameter of list: '+i)}
		def action = Action.get(params["actionItemId"])
		def itinerary = action.itinerary
		action.delete flush:true
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
	
	@Secured(['ROLE_ADMIN'])
	def addTheoriticalAction(){
		log.error('addAction called')
		params.each{i->log.error('parameter of list: '+i)}
		def site
		def itinerary
		def actionType
		def employee
		if (params['employeeId'] != null)
			employee = Employee.get(params.int('employeeId'))
		if (params['siteId'] != null)
			site = Site.get(params.int('siteId'))
		if (params['itineraryId'] != null)
			itinerary = Itinerary.get(params.int('itineraryId'))

		def currentDate =  new Date().parse("kk:mm", params['time_picker'])
			
		actionType = params['nature']
		if (actionType != null){
			
			actionType = actionType.equals('ARRIVEE') ? ItineraryNature.ARRIVEE : ItineraryNature.DEPART
		}
		def action = new Action(itinerary, currentDate, site, itinerary.deliveryBoy, actionType)
		action.isTheoritical = true
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
		
	}
	

	def showItineraryActions(){
		log.error('showItineraryActions called')
	//	params.each{i->log.error('parameter of list: '+i)}
		def itinerary
		def currentCalendar = Calendar.instance
		def criteria 
		def actionsList
		def theoriticalActionsList
		def date_picker = params['date_picker'] 
		def viewType = params['id']
		def timeDiff
		def timeDiffMap = [:]
		def actionListMap = [:]
		def calendar = Calendar.instance
		def monthCalendar = Calendar.instance
		def i = 0
		def hasDiscrepancy = false
		
		if (params['itineraryId'] != null)
			itinerary = Itinerary.get(params.int('itineraryId'))	
		if (date_picker != null && date_picker.size() > 0)
			currentCalendar.time = new Date().parse("dd/MM/yyyy", date_picker)
		
		criteria = Action.createCriteria()

		switch (viewType) {
				case 'dailyView':
					actionsList = criteria.list {
						and {
							eq('itinerary',itinerary)
							eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',currentCalendar.get(Calendar.MONTH) + 1)
							eq('year',currentCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
						}
					}
					break
				case 'monthlyView':
				/*
					actionsList = criteria.list {
						and {
							eq('itinerary',itinerary)
							eq('month',currentCalendar.get(Calendar.MONTH) + 1)
							eq('year',currentCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
						}
					}		
				*/
					
					monthCalendar = currentCalendar
					monthCalendar.set(Calendar.DAY_OF_MONTH,1)
					def lastDay = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					
					for (int j = 1;j < lastDay + 1;j++){
						
						criteria = Action.createCriteria()
						actionsList = criteria.list {
							and {
								eq('itinerary',itinerary)
								eq('day',monthCalendar.get(Calendar.DAY_OF_MONTH))
								eq('month',monthCalendar.get(Calendar.MONTH) + 1)
								eq('year',monthCalendar.get(Calendar.YEAR))
								eq('isTheoritical',false)
							}
						}
						actionListMap.put(monthCalendar.time,actionsList)
						monthCalendar.roll(Calendar.DAY_OF_MONTH,1)
					}
					
								
					break
				default:
					actionsList = criteria.list {
						and {
							eq('itinerary',itinerary)
							eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',currentCalendar.get(Calendar.MONTH) + 1)
							eq('year',currentCalendar.get(Calendar.YEAR))
							eq('isTheoritical',false)
						}
					}
					break
			}
		
		criteria = Action.createCriteria()
		actionsList = criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('day',currentCalendar.get(Calendar.DAY_OF_MONTH))
				eq('month',currentCalendar.get(Calendar.MONTH) + 1)
				eq('year',currentCalendar.get(Calendar.YEAR))
				eq('isTheoritical',false)			
			}
		}		

		
		criteria = Action.createCriteria()
		theoriticalActionsList= criteria.list {
			and {
				eq('itinerary',itinerary)
				eq('isTheoritical',true)
				order('date','asc')
			}
		}
		hasDiscrepancy = (actionsList != null && actionsList.size() != theoriticalActionsList.size()) ? true : false
		
		log.error("actionsList.size():" +actionsList.size())
		log.error("theoriticalActionsList.size():" +theoriticalActionsList.size())
		
		if (actionsList != null && actionsList.size() != theoriticalActionsList.size()){
			hasDiscrepancy = true
		}
		
		if (actionsList != null && actionsList.size() > 0){
			for (Action action in theoriticalActionsList){
				calendar.setTime(action.date)
				calendar.set(Calendar.DAY_OF_MONTH,currentCalendar.get(Calendar.DAY_OF_MONTH))		
				calendar.set(Calendar.MONTH,currentCalendar.get(Calendar.MONTH))
				calendar.set(Calendar.YEAR,currentCalendar.get(Calendar.YEAR))
			//	action.date = calendar.time
				
				if (actionsList.size() > i && actionsList.get(i) != null){
					use (TimeCategory){timeDiff = calendar.time - actionsList.get(i).date}
					timeDiffMap.put(i, timeDiff)
				}else{
					timeDiffMap.put(i, 0)
				}
				i++
			}
		}
		
		render template: "/itinerary/template/itineraryReportTemplate", model: [
			itineraryInstance:itinerary,
			actionsList:actionsList,
			theoriticalActionsList:theoriticalActionsList,
			hasDiscrepancy:hasDiscrepancy,
			actionListMap:actionListMap,
			timeDiffMap:timeDiffMap,
			viewType:viewType,
			timeDiffMap:timeDiffMap
			]
		return
	}
	
    @Transactional
    def save(Itinerary itineraryInstance) {
		def user = springSecurityService.currentUser
		
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
	
	
	def changeDeliveryBoy(){
			
		log.error('changeDeliveryBoy called')
		params.each{i->log.error('parameter of list: '+i)}
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
	 //  [itineraryInstance:itinerary,theoriticalActionsList:theoriticalActionsList,employeeList:employeeList]

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
