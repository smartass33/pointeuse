<html>
	<head>
		<meta name="layout" content="${layoutUi}"/>
		<s2ui:title messageCode='spring.security.ui.user.search'/>
	</head>
	<body>
		<div>
			<s2ui:formContainer type='search' beanType='user'>
				<s2ui:searchForm colspan='4'>
					<tr>
						<td><g:message code='user.username.label' default='Username'/>:</td>
						<td colspan="3"><g:textField name='username' size='50' maxlength='255' autocomplete='off' value='${username}'/></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td style='text-align:center;'><g:message code='spring.security.ui.search.true'/></td>
						<td style='text-align:center;'><g:message code='spring.security.ui.search.false'/></td>
						<td style='text-align:center;'><g:message code='spring.security.ui.search.either'/></td>
					</tr>
					<tr>
						<td><g:message code='user.enabled.label' default='Enabled'/>:</td>
						<g:radioGroup name='enabled' labels="['','','']" values='[1,-1,0]' value='${enabled ?: 0}'>
						<td style='text-align:center;'><%=it.radio%></td>
						</g:radioGroup>
					</tr>
					<tr>
						<td><g:message code='user.accountExpired.label' default='Account Expired'/>:</td>
						<g:radioGroup name='accountExpired' labels="['','','']" values='[1,-1,0]' value='${accountExpired ?: 0}'>
						<td style='text-align:center;'><%=it.radio%></td>
						</g:radioGroup>
					</tr>
					<tr>
						<td><g:message code='user.accountLocked.label' default='Account Locked'/>:</td>
						<g:radioGroup name='accountLocked' labels="['','','']" values='[1,-1,0]' value='${accountLocked ?: 0}'>
						<td style='text-align:center;'><%=it.radio%></td>
						</g:radioGroup>
					</tr>
					<tr>
						<td><g:message code='user.passwordExpired.label' default='Password Expired'/>:</td>
						<g:radioGroup name='passwordExpired' labels="['','','']" values='[1,-1,0]' value='${passwordExpired ?: 0}'>
						<td style='text-align:center;'><%=it.radio%></td>
						</g:radioGroup>
					</tr>
				</s2ui:searchForm>
			</s2ui:formContainer>
			<g:if test='${searched}'>
			<div class="list">
			<table>
				<thead>
				<tr>
					<s2ui:sortableColumn property='username' titleDefault='Username'/>
					<s2ui:sortableColumn property='enabled' titleDefault='Enabled'/>
					<s2ui:sortableColumn property='accountExpired' titleDefault='Account Expired'/>
					<s2ui:sortableColumn property='accountLocked' titleDefault='Account Locked'/>
					<s2ui:sortableColumn property='passwordExpired' titleDefault='Password Expired'/>
				</tr>
				</thead>
				<tbody>
				<g:each in='${results}' status='i' var='user'>
				<tr  class="${(i % 2) == 0 ? 'odd' : 'even'}">
					<td style='text-align:center;'><g:link action='edit' id='${user.id}'>${uiPropertiesStrategy.getProperty(user, 'username')}</g:link></td>
					<td style='text-align:center;'>
						<g:if test="${user.enabled}"><g:img dir="images" file="skin/tick.png" width="14" height="14"/></g:if>
						<g:else><g:img dir="images" file="skin/cross.png" width="14" height="14"/></g:else>
						
					</td>				
					<td style='text-align:center;'>
						<g:if test="${user.accountExpired}"><g:img dir="images" file="skin/tick.png" width="14" height="14"/></g:if>
						<g:else><g:img dir="images" file="skin/cross.png" width="14" height="14"/></g:else>						
					</td>							
					<td style='text-align:center;'>
						<g:if test="${user.accountLocked}"><g:img dir="images" file="skin/tick.png" width="14" height="14"/></g:if>
						<g:else><g:img dir="images" file="skin/cross.png" width="14" height="14"/></g:else>						
					</td>					
					<td style='text-align:center;'>
						<g:if test="${user.passwordExpired}"><g:img dir="images" file="skin/tick.png" width="14" height="14"/></g:if>
						<g:else><g:img dir="images" file="skin/cross.png" width="14" height="14"/></g:else>
						
					</td>

				</tr>
				</g:each>
				</tbody>
			</table>
			</div>
			<s2ui:paginate total='${totalCount}'/>
			</g:if>
		</div>
		<s2ui:ajaxSearch paramName='username'/>
	</body>
</html>
