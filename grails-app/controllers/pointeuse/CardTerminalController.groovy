package pointeuse



import static org.springframework.http.HttpStatus.*

import java.util.Date;

import grails.transaction.Transactional

@Transactional(readOnly = true)
class CardTerminalController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond CardTerminal.list(params), model:[cardTerminalInstanceCount: CardTerminal.count()]
    }

    def show(CardTerminal cardTerminalInstance) {
        respond cardTerminalInstance
    }

    def create() {
        respond new CardTerminal(params)
    }

    @Transactional
    def save(CardTerminal cardTerminalInstance) {
        if (cardTerminalInstance == null) {
            notFound()
            return
        }

        if (cardTerminalInstance.hasErrors()) {
            respond cardTerminalInstance.errors, view:'create'
            return
        }

        cardTerminalInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cardTerminalInstance.label', default: 'CardTerminal'), cardTerminalInstance.id])
                redirect cardTerminalInstance
            }
            '*' { respond cardTerminalInstance, [status: CREATED] }
        }
    }

    def edit(CardTerminal cardTerminalInstance) {
        respond cardTerminalInstance
    }

    @Transactional
    def update(CardTerminal cardTerminalInstance) {
        if (cardTerminalInstance == null) {
            notFound()
            return
        }

        if (cardTerminalInstance.hasErrors()) {
            respond cardTerminalInstance.errors, view:'edit'
            return
        }

        cardTerminalInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'CardTerminal.label', default: 'CardTerminal'), cardTerminalInstance.id])
                redirect cardTerminalInstance
            }
            '*'{ respond cardTerminalInstance, [status: OK] }
        }
    }
	
	

    @Transactional
    def delete(CardTerminal cardTerminalInstance) {

        if (cardTerminalInstance == null) {
            notFound()
            return
        }

        cardTerminalInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'CardTerminal.label', default: 'CardTerminal'), cardTerminalInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }
	@Transactional
	def keepAlive(){
		log.error('keepAlive called with params: ')
		def ip = params["ip"]
		def hostname = params["hostname"]
		log.error('keepAlive called with params: ip= '+ip+' and hostname= '+hostname)
		
		def calendar = Calendar.instance
		//def terminal = CardTerminal.findByHostnameAndIp(hostname,ip)
		def terminal = CardTerminal.findByIp(ip)
		
		if (terminal != null){
			log.error('terminal found')
			
			Date keepAliveTime = calendar.time
			terminal.lastKeepAlive = keepAliveTime
			terminal.save flush:true
			response.status = 200;
			render terminal
			return 'OK'
		}
		else{
			response.status = 404;
			render 'Terminal not found with params IP: '+ip+' and hostname: '+hostname
			return 
			
		}
		
	}

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cardTerminalInstance.label', default: 'CardTerminal'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
	
	@Transactional
	def createAll() {
		def sites = Site.findAll()

		for (Site site: sites){
			
			CardTerminal cardTerminal =  new CardTerminal()
			cardTerminal.creationDate = new Date()
			cardTerminal.hostname = 'locahost'
			cardTerminal.ip = '127.0.0.1'
			cardTerminal.lastKeepAlive = new Date()
			cardTerminal.site = site
			cardTerminal.save flush:true
			log.error('cardTerminal created: '+cardTerminal)
			site.cardTerminal = cardTerminal
			site.save flush:true
		}



	}
	
}
