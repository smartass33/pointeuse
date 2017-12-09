package pointeuse

import java.security.MessageDigest
import javax.servlet.http.HttpServletRequest
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.ui.strategy.UserStrategy;

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import uk.co.desirableobjects.ajaxuploader.exception.FileUploadException

class UserController extends grails.plugin.springsecurity.ui.UserController {
	
	
	UserStrategy uiUserStrategy
	def springSecurityService
	
		def save() {
			doSave uiUserStrategy.saveUser(params, roleNamesFromParams(), params.password)
		}
	
		def update() {
			params.each{i->log.error('parameter of list: '+i)}
			
			def allSites = Site.findAll()
			def userSites = []
			if (params['siteId'] instanceof String){
				userSites.add(Site.get(params['siteId']))
			}else{
				params['siteId'].each {
					userSites.add(Site.get(it))
				}
			
			}
			
			
			doUpdate { user ->
				user.properties = params
				
				

				allSites.each { currentSite ->
					if (userSites.contains(currentSite)){
						log.error('user has authorization: '+currentSite)
						if (currentSite.users == null){
							currentSite.users = []
						}
						currentSite.users.add(user)
					}else{
						log.error('user not authorized: '+currentSite)
						currentSite.users.remove(user)
					}
					currentSite.save()
				}
							/*
								params['siteId'].each {
									log.error('siteId: '+it)
									site = Site.get(it)
									if (site.users == null){
										site.users = []
									}
									site.users.add(user)
									site.save()
								  }
								  */
						
				
				if (params['password'] != null){					
					user.password = springSecurityService.encodePassword(params.password, user.username)

				}
				uiUserStrategy.updateUser params, user, roleNamesFromParams()
			}
		}
	
	/*
	def ajaxUploaderService
	def index() {
		redirect(action: "list", params: params)
	}

	@Secured(['ROLE_ADMIN'])
	def list(Integer max) {
		params.max = Math.min(max ?: 10, 100)
		def userInstanceList = User.list(params)
		def sites = Site.list()
		def userSiteMap = [:]
		def userInstanceTotal = User.count()
		for (User user : userInstanceList){
			def userSiteList = []
			for (Site site : sites){
				if (site.users.contains(user)){
					userSiteList.add(site)
				}
			}
			userSiteMap.put(user,userSiteList)
		}
		[userInstanceList: userInstanceList, userInstanceTotal: userInstanceTotal,userSiteMap:userSiteMap]
	}

	def create() {
		[userInstance: new User(params)]
	}
	
	@Secured(['ROLE_SUPER_ADMIN'])
	def save() {
		def role = params["role"].get('id')
		def userInstance = new User(params)
		if (!userInstance.save(flush: true)) {
			render(view: "create", model: [userInstance: userInstance])
			return
		}
		if (role != null){
			def roleObject = Role.get(role)
			UserRole.create(userInstance,roleObject)
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.firstName+' '+userInstance.lastName])
		redirect(action: "show", id: userInstance.id)
	}

	@Secured(['ROLE_ADMIN'])
	def show(Long id) {
		def userInstance = User.get(id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
			redirect(action: "list")
			return
		}

		[userInstance: userInstance]
	}
	@Secured(['ROLE_SUPER_ADMIN'])
	def edit(Long id) {
		def userInstance = User.get(id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
			redirect(action: "list")
			return
		}
		[userInstance: userInstance]
	}

	@Secured(['ROLE_SUPER_ADMIN'])
	def update(Long id, Long version) {
		def site
		def userInstance = User.get(id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
			redirect(action: "list")
			return
		}

		if (version != null) {
			if (userInstance.version > version) {
				userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'user.label', default: 'User')] as Object[],
						  "Another user has updated this User while you were editing")
				render(view: "edit", model: [userInstance: userInstance])
				return
			}
		}

		userInstance.properties = params

		if (params['siteId'] instanceof String){
			site = Site.get(params['siteId'])
			if (site.users == null){
				site.users = []
			}
			site.users.add(userInstance)
			site.save()
		}else{
			params['siteId'].each {
				log.error('siteId: '+it)
				site = Site.get(it)
				if (site.users == null){
					site.users = []
				}
				site.users.add(userInstance)
				site.save()
			  }
		}

		if (!userInstance.save(flush: true)) {
			render(view: "edit", model: [userInstance: userInstance])
			return
		}

		flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
		redirect(action: "show", id: userInstance.id)
	}

	@Secured(['ROLE_SUPER_ADMIN'])
	def delete(Long id) {
		def userInstance = User.get(id)
		if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
			redirect(action: "list")
			return
		}

		try {
			userInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
			redirect(action: "show", id: id)
		}
	}
		
	def importUserList = {
		log.error('importUserList called')
		def lastName
		def user
		def site
		def settings = [separatorChar:';','charset':'windows-1252']
		def criteria
		try {
			File uploaded = File.createTempFile('grails', 'ajaxupload')
			InputStream inputStream = selectInputStream(request)

			ajaxUploaderService.upload(inputStream, uploaded)
			uploaded.toCsvReader(settings).eachLine { tokens ->
				log.error("tokens: "+tokens)
				lastName = tokens[0]
				log.error("lastName: "+lastName)
				
				user = User.findByLastName(lastName)
				log.error("tokens[3]: "+tokens[3])
				
				if (user != null && tokens[3].equals('1')){
					log.error('user and admin are not null')
					site = Site.findByName(tokens[2])
					log.error('tokens[2]: '+tokens[2])
					log.error('site: '+site)
					if (site != null){
						if (site.users == null){
							site.users = []
						}
						log.error(site.users)
						if (!site.users.contains(user)){
							site.users.add(user)
							site.save()
						}
					}
					user.email = tokens[4]
					user.hasMail = true
					user.reportSendDay=1
					user.save()
				}
				log.error("user: "+user)
			}
			return render(text: [success:true] as JSON, contentType:'text/json')
		} catch (FileUploadException e) {
			log.error("Failed to upload file.", e)
			return render(text: [success:false] as JSON, contentType:'text/json')
		}
	}

	private InputStream selectInputStream(HttpServletRequest request) {
		if (request instanceof MultipartHttpServletRequest) {
			MultipartFile uploadedFile = ((MultipartHttpServletRequest) request).getFile('qqfile')
			return uploadedFile.inputStream
		}
		return request.inputStream
	}

	
	def isAuthenticated(){
		def username = params["username"]
		def password = params["password"]
		byte[] hash
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		def hashAsString
		def user
		def retour = false
		response.status = 404;
		
				
		if (username == null){
			log.error('authentication failed!')
		} else{
			user = User.findByUsername(username)
		}
		
		if (password == null){
			log.error('authentication failed!')
		}
		else{
			hash = digest.digest(password.getBytes("UTF-8"));
			hashAsString = hash.encodeHex()
		}
		
		if (user != null){
			if (!user.password.equals(hashAsString.toString())){
				log.error('authentication failed!')
			}else{
				retour = true
				response.status = 200;
			}
		}
		log.error("retour is= "+retour)
		render retour
		return retour
	}
	
	*/
}
