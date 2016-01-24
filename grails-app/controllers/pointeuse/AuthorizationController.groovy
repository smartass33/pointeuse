package pointeuse



import static org.springframework.http.HttpStatus.*

import java.util.Date;

import grails.transaction.Transactional

@Transactional(readOnly = true)
class AuthorizationController {
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
		def employeeInstanceId = params.int('employeeInstanceId')
		def employeeInstance = Employee.get(employeeInstanceId)
        params.max = Math.min(max ?: 10, 100)
		respond Authorization.list(params),model:[employeeInstance:employeeInstance,authorizationInstanceCount: Authorization.count(),employeeInstanceId:employeeInstanceId]
    }

    def show(Authorization authorizationInstance) {
        respond authorizationInstance
    }

    def create() {
		def employeeInstanceId = params.int('employeeInstanceId')
		def employeeInstance = Employee.get(employeeInstanceId)
		respond new Authorization(params),model:[employeeInstance:employeeInstance,employeeInstanceId:employeeInstanceId]
    }

    @Transactional
    def save(Authorization authorizationInstance) {
		log.error('saving authorizationInstance')
		def typeId = params.int('type')
		def fromEditEmployee = params.boolean('fromEditEmployee')
		def showEmployee = params.boolean('showEmployee')
		def employeeInstanceId = params.int('employeeInstanceId')
		def employee = Employee.get(employeeInstanceId)
		if (employee == null){
			employee = Employee.get(params.int('employee'))
		}
		if (employee != null){
			authorizationInstance.employee = employee
		}

		
        if (authorizationInstance == null) {
            notFound()
            return
        }
		authorizationInstance.validate()
        if (authorizationInstance.hasErrors()) {
            respond authorizationInstance.errors, view:'create'
            return
        }
        authorizationInstance.save flush:true
		def authorizationInstanceList
		if (fromEditEmployee){
			authorizationInstanceList = Authorization.findAllByEmployee(employee)		
		}else{
			authorizationInstanceList = Authorization.list()	
		}	
		flash.message = message(code: 'default.feminine.created.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), authorizationInstance.type.type])	
		render template: "/authorization/template/authorizationTable", model: [authorizationInstanceList:authorizationInstanceList,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]
		return
    }

    def edit(Authorization authorizationInstance) {
        respond authorizationInstance
    }

    @Transactional
    def update(Authorization authorizationInstance) {
        if (authorizationInstance == null) {
            notFound()
            return
        }

        if (authorizationInstance.hasErrors()) {
            respond authorizationInstance.errors, view:'edit'
            return
        }

        authorizationInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), authorizationInstance.id])
                redirect authorizationInstance
            }
            '*'{ respond authorizationInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Authorization authorizationInstance) {
        if (authorizationInstance == null) {
            notFound()
            return
        }

        authorizationInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), authorizationInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	@Transactional
	def trashAuthorization(){
		log.error('trashAuthorization called')
	//	params.each{i-> log.error(i)}
		def fromEditEmployee = params.boolean('fromEditEmployee')
		def showEmployee = params.boolean('showEmployee')
		def authorization = Authorization.get(params.int('authorizationInstanceId'))
		def employee = authorization.employee
		def oldType = employee.lastName +', ' +authorization.type.type
		authorization.delete flush:true
		def authorizationInstanceList
		if (fromEditEmployee){
			authorizationInstanceList = Authorization.findAllByEmployee(employee)		
		}else{
			authorizationInstanceList = Authorization.list()	
		}		
		flash.message = message(code: 'default.feminine.deleted.message', args: [message(code: 'default.authorization.label', default: 'Authorization'), oldType])	
		render template: "/authorization/template/authorizationTable", model: [authorizationInstanceList:authorizationInstanceList,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]
		return
	}
	
	@Transactional
	def updateTime(){
		params.each{i-> log.error(i)}
		def parameters = params['name'].split('_')
		def categoryType = parameters[1]
		def categoryName = parameters[2]
		def dateType = parameters[3]
		def employeeId = parameters[4]
		def changedType = parameters[5]
		def newValue = params.int('val')
		def employee = Employee.get(employeeId)
		def oldDate
		def authorization 
		def category
		def calendar = Calendar.instance
		if (categoryType.equals('category')){
			category =  Category.findByName(categoryName)
			authorization = Authorization.findByCategoryAndEmployee(category,employee)
			
		}else{
			category = SubCategory.findByName(categoryName)	
			authorization = Authorization.findBySubCategoryAndEmployee(category,employee)
		}
		
		oldDate = dateType.equals('start') ? authorization.startDate : authorization.endDate
		calendar.time = oldDate
	
		if (changedType.equals('day'))
			calendar.set(Calendar.DAY_OF_MONTH,newValue)
		
		if (changedType.equals('month'))
			calendar.set(Calendar.MONTH,newValue - 1)
		
		if (changedType.equals('year'))
			calendar.set(Calendar.YEAR,newValue)
		
		if (dateType.equals('start')){
			authorization.startDate = calendar.time
		}else{
			authorization.endDate = calendar.time
		}
		
		
		authorization.save(flush:true)
		log.error('done')
		render template: "/authorization/template/authorizationCategoryTable", model: [employeeInstance:employee]//,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]
		
		return
	}
	
	
	@Transactional
	def updateAuthorization(){
		log.error('updateAuthorization called')
		params.each{i-> log.error(i)}
		def employee = Employee.get(params.int('employeeInstanceId'))
		
		def isAuthorized = params.boolean('completed')
		def type = params['type']
		def authorization 
		
		if (type.equals('category')){
			def category = Category.get(params.int('id'))
			log.error('category: '+category)
			authorization = Authorization.findByEmployeeAndCategory(employee,category)
			if (authorization == null){
				authorization = new Authorization(new Date(),new Date(), employee)
			}
			authorization.category = Category.get(params.int('id'))
		}else{	
			def subCategory = SubCategory.get(params.int('id'))
			authorization = Authorization.findByEmployeeAndSubCategory(employee,subCategory)
			if (authorization == null){
				authorization = new Authorization(new Date(),new Date(), employee)
			}
			log.error('subCategory: '+subCategory)
			authorization.subCategory = SubCategory.get(params.int('id'))
		}
		authorization.isAuthorized = isAuthorized
		if (!isAuthorized){
			authorization.startDate = null
			authorization.endDate = null
		}else{
			authorization.startDate = new Date()
			authorization.endDate = new Date()
		}
		authorization.save(flush:true)
		render template: "/authorization/template/authorizationCategoryTable", model: [employeeInstance:employee]//,showEmployee:showEmployee,fromEditEmployee:fromEditEmployee]
		
		return
		
	}
}
