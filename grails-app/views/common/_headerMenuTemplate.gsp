<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>

<ul>
	<li><a class="home" href="${createLink(uri: '/')}"><g:message
				code="default.home.label" /></a></li>
	<li><g:link class="list" action="list"
			params="${[isAdmin:isAdmin]}">
			<g:message code="default.list.label" args="[entityName]" />
		</g:link></li>
	<g:if test="${username != null}">
		<li><g:link class="create" action="create">
				<g:message code="default.new.label" args="[entityName]" />
			</g:link></li>
		<li><g:link class="list" action="" controller="logout">
				${message(code: 'default.button.logout', default: 'Logout')}
			</g:link></li>
	</g:if>
	<g:if test="${params.action == 'list'}">
		<input type="button" id='detailSelector'    value="Masquer les détails"  onclick="		document.getElementById('employee-table').className='showDetail';document.getElementById('detailSelector').style.display = 'none';document.getElementById('principalSelector').style.display = 'block'";" />
		<input type="button" id='principalSelector' value="Afficher les détails" onclick="document.getElementById('employee-table').className='hideDetail';document.getElementById('detailSelector').style.display = 'block';document.getElementById('principalSelector').style.display = 'none'" />

	</g:if>
</ul>

