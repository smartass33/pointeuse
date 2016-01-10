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
		
		def employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")
		def dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		def employeeList = Employee.findAll()
		def hasEmployeeMap = [:]
		dataListRank.each{rank->
			hasEmployeeMap.put(rank.fieldName,0)
			for (Employee employee : employeeList){
				def extraData = employee.extraData
				if ((employee.extraData).get(rank.fieldName)){
					hasEmployeeMap.put(rank.fieldName,hasEmployeeMap.get(rank.fieldName) + 1)
					
				}
			}
		}
	
        [employeeDataListMapInstance: employeeDataListMap,dataListRank:dataListRank,hasEmployeeMap:hasEmployeeMap]
		
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
		def rank = 1
		def type = params['type']
		def isHiddenField = false
		
		def employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")

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
		params.each{i->log.debug('parameter of list: '+i)}
		def employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")

		(employeeDataListMap.fieldMap).remove(params['fieldMap'])
		def rank= EmployeeDataListRank.findByFieldName(params['fieldMap'])
		employeeDataListMap.save flush:true
		rank.delete flush:true
		log.error(params["fieldMap"]+' removed from employeeDataListMap')
		def dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")
		
		
		def dataListRankCount = dataListRank.size()
		def j = 1
		// recompute the ranks to avoid holes
		for (EmployeeDataListRank rankTmp in dataListRank){
			rankTmp.rank = j
			rankTmp.save(flush:true)
			j++
		}
		dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		
		def employeeList = Employee.findAll()
		def extraData
		for (Employee employee : employeeList){
			extraData = employee.extraData
			def value = extraData.get(params['fieldMap'])
			if (value != null){
				extraData.remove(params['fieldMap'])
				employee.save()
			}
		}
		def hasEmployeeMap = [:]
		dataListRank.each{i->
			hasEmployeeMap.put(i.fieldName,0)
			for (Employee employee : employeeList){
				if ((employee.extraData).get(i.fieldName)){
					hasEmployeeMap.put(i.fieldName,hasEmployeeMap.get(i.fieldName) + 1)
					
				}
			}
		}
		flash.message = message(code: 'default.deleted.message', args: [message(code: 'employeeDataListMap.label'), params["fieldMap"]])
		render template: "/employeeDataListMap/template/employeeDataListTable",model:[employeeDataListMapInstance:employeeDataListMap,dataListRank:dataListRank,hasEmployeeMap:hasEmployeeMap,flash:flash]
		return
		//return
	}
	
	@Transactional
	def modify(){
		log.error('modify called')
		params.each{i->log.error('parameter of list: '+i)}
		def extraData
		def type = params['type']
		def fieldName = params['fieldName']
		def oldFieldName = params['oldFieldName']
		def employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")
		def oldFieldValue = (employeeDataListMap.fieldMap).get(oldFieldName)
		(employeeDataListMap.fieldMap).remove(oldFieldName)
		(employeeDataListMap.fieldMap).put(fieldName,type)
		employeeDataListMap.save(flush:true)
		def dataListRank= EmployeeDataListRank.findByFieldName(oldFieldName)
		dataListRank.fieldName = fieldName
		dataListRank.save(flush:true)
		
		

		def employeeList = Employee.findAll()
		for (Employee employee : employeeList){
			extraData = employee.extraData 
			def value = extraData.get(oldFieldName)
			if (value != null){
				log.error(value)
				extraData.remove(oldFieldName)
				extraData.put(fieldName,value)
				employee.save()
			}
		}
		employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")
		dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")
		
		log.error('done')
		render template: "/employeeDataListMap/template/employeeDataListTable",model:[employeeDataListMapInstance:employeeDataListMap,dataListRank:dataListRank,flash:flash]
		return

	}
	
	@Transactional
	def changeRank(){
		params.each{i->log.debug('parameter of list: '+i)}
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
		dataListRank= EmployeeDataListRank.findAll("from EmployeeDataListRank as e order by rank asc")

		def hasEmployeeMap = [:]
		def employeeList = Employee.findAll()
		dataListRank.each{rank->
			hasEmployeeMap.put(rank.fieldName,0)
			for (Employee employee : employeeList){
				if ((employee.extraData).get(rank.fieldName)){
					hasEmployeeMap.put(rank.fieldName,hasEmployeeMap.get(rank.fieldName) + 1)
					
				}
			}
		}
		def employeeDataListMap = EmployeeDataListMap.find("from EmployeeDataListMap")
		render template: "/employeeDataListMap/template/employeeDataListTable",model:[employeeDataListMapInstance:employeeDataListMap,dataListRank:dataListRank,hasEmployeeMap:hasEmployeeMap,flash:flash]
		return

	}
}
