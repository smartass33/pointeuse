package pointeuse


import static org.springframework.http.HttpStatus.*

import java.util.Date;
import java.util.Map;

import grails.transaction.Transactional

@Transactional(readOnly = true)
class EmployeeDataListMapController {

	def springSecurityService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	
	@Transactional
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
		
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMapInstance = criteria.get {
			maxResults(1)
		}
		/*
		def increment = 1
		employeeDataListMapInstance.fieldMap.each{k,v->
			def dataRank = new EmployeeDataListRank()
			dataRank.rank = increment
			dataRank.fieldName = k
			increment ++
			dataRank.save(flush:true)
		}
		*/
		def dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")

        [employeeDataListMapInstance: employeeDataListMapInstance,dataListRank:dataListRank]
		
    }

    def show(EmployeeDataListMap employeeDataListMapInstance) {
        respond employeeDataListMapInstance
    }

    def create() {
        respond new EmployeeDataListMap(params)
    }

    @Transactional
    def save(EmployeeDataListMap employeeDataListMapInstance) {
        if (employeeDataListMapInstance == null) {
            notFound()
            return
        }

        if (employeeDataListMapInstance.hasErrors()) {
            respond employeeDataListMapInstance.errors, view:'create'
            return
        }

        employeeDataListMapInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'employeeDataListMapInstance.label', default: 'EmployeeDataListMap'), employeeDataListMapInstance.id])
                redirect employeeDataListMapInstance
            }
            '*' { respond employeeDataListMapInstance, [status: CREATED] }
        }
    }

    def edit(EmployeeDataListMap employeeDataListMapInstance) {
        respond employeeDataListMapInstance
    }

    @Transactional
    def update(EmployeeDataListMap employeeDataListMapInstance) {
        if (employeeDataListMapInstance == null) {
            notFound()
            return
        }

        if (employeeDataListMapInstance.hasErrors()) {
            respond employeeDataListMapInstance.errors, view:'edit'
            return
        }

        employeeDataListMapInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'EmployeeDataListMap.label', default: 'EmployeeDataListMap'), employeeDataListMapInstance.id])
                redirect employeeDataListMapInstance
            }
            '*'{ respond employeeDataListMapInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(EmployeeDataListMap employeeDataListMapInstance) {

        if (employeeDataListMapInstance == null) {
            notFound()
            return
        }

        employeeDataListMapInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'EmployeeDataListMap.label', default: 'EmployeeDataListMap'), employeeDataListMapInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeDataListMapInstance.label', default: 'EmployeeDataListMap'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	@Transactional
	def addNewEmployeeData(){
		def user = springSecurityService.currentUser
		log.error('addNewEmployeeData called')
		params.each{i->log.error('parameter of list: '+i)}
		def fieldName = params['fieldname']
		def rank = params.int('rank')
		def type = params['type']
		def isHiddenField = false
		
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMap = criteria.get {
			maxResults(1)
		}
		
		if (employeeDataListMap == null){
			employeeDataListMap = new EmployeeDataListMap(new Date(),user)
			employeeDataListMap.fieldMap = [:]
			employeeDataListMap.hiddenFieldMap = [:]
		}
		
		if (isHiddenField){
			employeeDataListMap.hiddenFieldMap.put(fieldName,type)
		}else{
			employeeDataListMap.fieldMap.put(fieldName,type)		
		}
		employeeDataListMap.modificationUser = user
		employeeDataListMap.lastModification = new Date()
		employeeDataListMap.save(flush:true)
		
		def employeeDataListRank = new EmployeeDataListRank()
		employeeDataListRank.fieldName = fieldName
		employeeDataListRank.rank = rank 
		employeeDataListRank.save()
		log.error('employeeDataListMap saved')
		redirect(action: "index")		
	}
	
	@Transactional
	def trashEmployeeData(){
		def user = springSecurityService.currentUser
		log.error('removeNewEmployeeData called')
		params.each{i->log.error('parameter of list: '+i)}
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMap = criteria.get {
			maxResults(1)
		}
		(employeeDataListMap.fieldMap).remove(params['fieldMap'])
		def rank= EmployeeDataListRank.findByFieldName(params['fieldMap'])
		employeeDataListMap.save flush:true
		rank.delete flush:true
		log.error(params["fieldMap"]+' removed from employeeDataListMap')
		flash.message = message(code: 'default.deleted.message', args: [message(code: 'employeeDataListMap.label'), params["fieldMap"]])
		render template: "/employeeDataListMap/template/employeeDataListTable",model:[employeeDataListMapInstance:employeeDataListMap,flash:flash]
		//return
	}
	
	@Transactional
	def modify(){
		log.error('modify called')
		params.each{i->log.error('parameter of list: '+i)}
		def extraData
		def parameters = params.oldKey.split(' value=')
		def oldKey = parameters[0]
		def newKey = params['newKey']
		def employeeList = Employee.findAll()
		for (Employee employee : employeeList){
			extraData = employee.extraData 
			def value = extraData.get(oldKey)
			if (value != null){
				log.error(value)
				extraData.remove(oldKey)
				extraData.put(newKey,value)
				employee.save()
			}
		}
		def criteria = EmployeeDataListMap.createCriteria()
		def employeeDataListMap = criteria.get {
			maxResults(1)
		}
		def oldFieldValue = (employeeDataListMap.fieldMap).get(oldKey)
		(employeeDataListMap.fieldMap).remove(oldKey)
		(employeeDataListMap.fieldMap).put(newKey,oldFieldValue)
		log.error('done')

	}
	
	@Transactional
	def changeRank(){
		params.each{i->log.error('parameter of list: '+i)}
		def elements = params['Element[]']
		def dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		
		def i = 1
		def listShifted = []
		elements.each{element ->
			listShifted.add((element as int) + 1)
		}

		listShifted.each{ele_rank->
			def dataRank = dataListRank.get(ele_rank-1)
			dataRank.rank=i
			dataRank.save(flush:true)
			i++
		}

	}
}
