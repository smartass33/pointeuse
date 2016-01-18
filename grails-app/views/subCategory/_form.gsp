<%@ page import="pointeuse.SubCategory" %>

<div class="fieldcontain ${hasErrors(bean: subCategoryInstance, field: 'category', 'error')} required">
	<label for="category">
		<g:message code="category.label" default="Category" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="category" name="category.id" from="${pointeuse.Category.list()}" optionKey="id" optionValue='name' required="" value="${subCategoryInstance?.category?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: subCategoryInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="subCategory.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${subCategoryInstance?.name}"/>
</div>


