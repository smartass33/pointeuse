<%@ page import="pointeuse.UserRole" %>
<%@ page import="pointeuse.Role" %>
<%@ page import="pointeuse.User" %>



<div class="fieldcontain ${hasErrors(bean: userRoleInstance, field: 'role', 'error')} required">
	<label for="role">
		<g:message code="userRole.role.label" default="Role" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="role" name="role.id" from="${Role.list()}" optionKey="id" required="" optionValue="authority" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRoleInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="userRole.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${User.list()}" optionKey="id" required="" optionValue="${{it?.firstName+' '+it?.lastName}}" class="many-to-one"/>
</div>

