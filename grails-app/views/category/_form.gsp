<%@ page import="pointeuse.Category" %>

<div class="fieldcontain ${hasErrors(bean: categoryInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="category.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${categoryInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: categoryInstance, field: 'subCategories', 'error')} ">
	<label for="subCategories">
		<g:message code="category.subCategories.label" default="Sub Categories" />
	</label>
	<ul class="one-to-many">
		<g:each in="${categoryInstance?.subCategories?}" var="subCategory">
		    <li><g:link controller="subCategory" action="show" id="${subCategory.id}">${subCategory?.name}</g:link></li>
		</g:each>
		<li class="add">
			<g:link controller="subCategory" action="create" params="['category.id': categoryInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subCategory.label', default: 'SubCategory')])}</g:link>
		</li>
	</ul>
</div>



 