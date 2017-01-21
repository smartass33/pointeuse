package pointeuse

import grails.plugin.springsecurity.ui.ResetPasswordCommand
import grails.plugin.springsecurity.ui.RegistrationCode

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController {
	
	
	def resetPassword(ResetPasswordCommand resetPasswordCommand) {
		
				String token = params.t
		
				def registrationCode = token ? RegistrationCode.findByToken(token) : null
				if (!registrationCode) {
					flash.error = message(code: 'spring.security.ui.resetPassword.badCode')
					redirect uri: successHandlerDefaultTargetUrl
					return
				}
		
				if (!request.post) {
					return [token: token, resetPasswordCommand: new ResetPasswordCommand()]
				}
		
				resetPasswordCommand.username = registrationCode.username
				resetPasswordCommand.validate()
				if (resetPasswordCommand.hasErrors()) {
					return [token: token, resetPasswordCommand: resetPasswordCommand]
				}
		
				def user = uiRegistrationCodeStrategy.resetPassword(resetPasswordCommand, registrationCode)
				if (user.hasErrors()) {
					// expected to be handled already by ErrorsStrategy.handleValidationErrors
				}
		
				flash.message = message(code: 'spring.security.ui.resetPassword.success')
		
				redirect uri: registerPostResetUrl ?: successHandlerDefaultTargetUrl
			}
	
}
