<%@ page import="pointeuse.Period" %>

<div id='year' >
	<label for="year">
		<g:message code="period.year.label" default="Year" />
		<span class="required-indicator">*</span>
	</label>
	<input type="number" 
  			onchange="${remoteFunction(action:'changeValue', update:'periodBox',params:'\'yearAsString=\'+ this.value')}"         		
           	name="year" 
           	value="${toto}" 
			min="2012" />  
</div>
<div id='periodBox'>
	<g:periodBox/>
</div>