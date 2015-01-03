<%@ page import="pointeuse.CardTerminal" %>



<div class="fieldcontain ${hasErrors(bean: cardTerminalInstance, field: 'creationDate', 'error')} required">
	<label for="creationDate">
		<g:message code="cardTerminal.creationDate.label" default="Creation Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="creationDate" precision="day"  value="${cardTerminalInstance?.creationDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: cardTerminalInstance, field: 'hostname', 'error')} ">
	<label for="hostname">
		<g:message code="cardTerminal.hostname.label" default="Hostname" />
		
	</label>
	<g:textField name="hostname" value="${cardTerminalInstance?.hostname}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: cardTerminalInstance, field: 'ip', 'error')} ">
	<label for="ip">
		<g:message code="cardTerminal.ip.label" default="Ip" />
		
	</label>
	<g:textField name="ip" value="${cardTerminalInstance?.ip}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: cardTerminalInstance, field: 'lastKeepAlive', 'error')} required">
	<label for="lastKeepAlive">
		<g:message code="cardTerminal.lastKeepAlive.label" default="Last Keep Alive" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="lastKeepAlive" precision="day"  value="${cardTerminalInstance?.lastKeepAlive}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: cardTerminalInstance, field: 'site', 'error')} required">
	<label for="site">
		<g:message code="cardTerminal.site.label" default="Site" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="site" name="site.id" from="${pointeuse.Site.list()}" optionKey="id" required="" value="${cardTerminalInstance?.site?.id}" class="many-to-one"/>
</div>

