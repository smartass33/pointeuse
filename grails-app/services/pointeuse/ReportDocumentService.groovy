package pointeuse

import org.springframework.cache.annotation.Cacheable
class ReportDocumentService {

	def xhtmlDocumentService

	@Cacheable('reportDocumentCache')
	def getDocument(Serializable serial) {
	    xhtmlDocumentService.createDocument(template: '/report', model: [serial: serial])
	}
}
