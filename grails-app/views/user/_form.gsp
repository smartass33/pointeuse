<%@ page import="pointeuse.User" %>
<%@ page import="pointeuse.Role" %>
<%@ page import="pointeuse.Site" %>


<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'firstName', 'error')} required">
	<label for="firstName">
		<g:message code="user.firstname.label" default="First Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="firstName" required="" value="${userInstance?.firstName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'lastName', 'error')} required">
	<label for="lastName">
		<g:message code="user.lastname.label" default="Last Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="lastName" required="" value="${userInstance?.lastName}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="user.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${userInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
	<label for="password">
		<g:message code="user.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
	<g:passwordField name="password" required="" value="${userInstance?.password}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: userRoleInstance, field: 'role', 'error')} required">
	<label for="role">
		<g:message code="userRole.role.label" default="Role" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="role" name="role.id" from="${Role.list()}" optionKey="id" required="" optionValue="authority" class="many-to-one"/>
</div>


<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'accountExpired', 'error')} ">
	<label for="accountExpired">
		<g:message code="user.accountExpired.label" default="Account Expired" />
		
	</label>
	<g:checkBox name="accountExpired" value="${userInstance?.accountExpired}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'accountLocked', 'error')} ">
	<label for="accountLocked">
		<g:message code="user.accountLocked.label" default="Account Locked" />
		
	</label>
	<g:checkBox name="accountLocked" value="${userInstance?.accountLocked}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'enabled', 'error')} ">
	<label for="enabled">
		<g:message code="user.enabled.label" default="Enabled" />
		
	</label>
	<g:checkBox name="enabled" value="${userInstance?.enabled}"  checked="true"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'passwordExpired', 'error')} ">
	<label for="passwordExpired">
		<g:message code="user.passwordExpired.label" default="Password Expired" />
		
	</label>
	<g:checkBox name="passwordExpired" value="${userInstance?.passwordExpired}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email', 'error')}">
	<label for="email">
		<g:message code="user.email.label" default="Username" />
	</label>
		<g:textField name="email"  value="${userInstance?.email}"/>
		<g:message code="user.receive.site.mail.label" default="Username" />
		<g:checkBox name="hasMail" value="${userInstance.hasMail}" checked="${userInstance.hasMail}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'phoneNumber', 'error')}">
	<label for="phoneNumber">
		<g:message code="user.phone.number.label" default="Username" />
	</label>
	<g:textField name="phoneNumber"  value="${userInstance?.phoneNumber}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'reportSendDay', 'error')}">
	<label for="reportSendDay">
		<g:message code="user.report.send.day.label" default="Username" />
	</label>
	<g:select id="reportSendDay" name="reportSendDay" from="${1..30}" value="${userInstance.reportSendDay}" />
</div>


<BR>
<h1><g:message code="site.list" /></h1>


<table>
	<g:each var="site" in="${Site.list([sort:'name'])}">
		   <tr>
		   	<td class='eventTD'>${site.name}</td>
		   	<g:if test="${site.users.contains(userInstance)}">
		   		<td class='eventTD'><g:checkBox name="siteId" value="${site.id}" checked="${true}"> </g:checkBox></td>
			</g:if>
			<g:else>
				<td class='eventTD'><g:checkBox name="siteId" value="${site.id}" checked="${false}"> </g:checkBox></td>
			</g:else>
		   </tr>
	</g:each>
</table>