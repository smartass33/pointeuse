<%@ page import="pointeuse.Itinerary" %>

<script>		
	$(document).ready(
	   function() {
	       $("#newActionButton").click(function() {
	           $("#newActionForm").toggle();
	       });
	      	$("#cancelAction").click(function() {
	           $("#newActionForm").toggle();
	       });
	   });
</script>
<div id="spinner" class="spinner" style="display: none;"><img src="${createLinkTo(dir:'images',file:'spinner.gif')}"  width="16" height="16" /><g:message code="spinner.loading.label"/></div>

<div id="actionTheoriticalTableDiv">
	<div id="newActionButton"><a href="#"><g:message code="itinerary.add.item"/></a></div>
	<div id="newActionForm">
		<table>
			<tr>
				<td><g:select name="nature" from="${pointeuse.ItineraryNature?.values()}" keys="${pointeuse.ItineraryNature.values()*.name()}" required="" /></td>
				<td>	
					<input type="text" name="time_picker" id="time_picker" /> 
					<script type="text/javascript">
						datePickerLaunch();
					</script>
				</td>
				<td><g:select id="site" name="siteId" from="${pointeuse.Site.list()}" optionKey="id" required=""  class="many-to-one"/></td>
			    <td><a href="#" id="cancelAction">Annuler</a></td>
			    <td>
			    	<g:submitToRemote class="listButton"
						onLoading="document.getElementById('spinner').style.display = 'inline';"
	            		onComplete="document.getElementById('spinner').style.display = 'none';"						
						update="theoriticalActionTableDiv"
					url="[controller:'itinerary', action:'addTheoriticalAction']" value="Ajouter un element itineraire">
					</g:submitToRemote>
			    </td>	    
			</tr>
		</table>
	</div>
</div>
