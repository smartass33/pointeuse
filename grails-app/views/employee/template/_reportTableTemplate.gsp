<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.Site"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>
<%@ page import="java.util.Calendar"%>

<form method="POST" >
	<g:actionSubmit value="appliquer" action="modifyTime" class="listButton" style="position: absolute; left: -9999px" />
	<table  border="1" style="table-layout: fixed;" id="reportTable" >
	    <thead>
	      <th>${message(code: 'report.table.date.label', default: 'report')}</th>
	      <th>${message(code: 'report.table.location', default: 'report')}</th>
	      <th>${message(code: 'report.table.daily.total.label', default: 'report')}</th>
	      <th>${message(code: 'report.table.HS.label', default: 'report')}</th>
	      <th>
	      	${message(code: 'report.table.absence.label', default: 'report')}    
             <g:select width="50px"
                    onchange="${remoteFunction(action:'modifyAllAbsence', update:'cartouche_div', 
					onLoading: "document.getElementById('spinner').style.display = 'inline';",
					onComplete: "document.getElementById('spinner').style.display = 'none';",
	  				params:'\'employeeId=' + employee.id 	
					+ '&period=' + period.format('dd/MM/yyyy')
	  				+ '&updatedSelection=\' + this.value'								  
	  				)}"
                    name="absenceType" from="${AbsenceType.values()}"
                    value="${AbsenceType}"
                    noSelection="${['-':'']}" />	
	      	</th>
	      <th colspan="80" class="principal">${message(code: 'events.label')}</th>
	      <th class="admin">
	      	${message(code: 'mileage.label')} 
			<g:if test="${monthlyPeriodValue}">
				 : ${monthlyPeriodValue}KM (${mileageMinDate.format('dd/MM/yyyy')} - ${mileageMaxDate.format('dd/MM/yyyy')}) 
	        </g:if>	    
	        <g:else>
	        	 : ${0}KM (${mileageMinDate.format('dd/MM/yyyy')} - ${mileageMaxDate.format('dd/MM/yyyy')}) 
	        </g:else>  		
	      </th>
	    </thead>
	    <tbody>
	        <g:each in="${weeklyAggregate}" status="k" var="week">
	          <g:each in="${week}" status="l" var="day">
	            <g:each in="${day.value}" status="m" var="entries">
	              <g:if test="${entries!=null}">
	                <tr class="${((departureDate != null && entries.key > departureDate) || employee.arrivalDate > entries.key) ? 'outTR' :''}">
	                  <g:if test="${dailyBankHolidayMap.get(entries.key) }">
		                  <td class="eventTD" style="width:120px; padding: 2px 2px; color:red;" ><i><font size="2" style="color:red;"> ${entries.key.format('E dd MMM')}</font></i></td>
	                  </g:if>
	                  <g:else>
							<g:if test="${entries.key.getAt(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY}">
								<td style="width:120px;" class="eventTD"><i><font size="2" style="width:140px; font-weight:bold;"> ${entries.key.format('E dd MMM')}</font></i></td>
							</g:if>
							<g:else>
								<td style="width:120px;" class="eventTD"><font size="2"><i>${entries.key.format('E dd MMM')}</i></font></td>						
		                   </g:else>
	                  </g:else>
	                  <g:if test="${dailyTotalMap.get(entries.key) != null}">
	                    <g:if test="${dailyTotalMap.get(entries.key).site != null}">
	                    	<td class="eventTD">                		
	                    		<g:select style='width:110px'
			                          onchange="${remoteFunction(action:'modifySite', update:'report_table_div', 
												  params:'\'employeeId=' + employee.id 						  
												  + '&day=' + entries.key.format("dd/MM/yyyy")	
												  +	'&month=' + entries.key.format("dd/MM/yyyy")	
												  + '&year=' + entries.key.format("dd/MM/yyyy")
												  + '&updatedSite=\' + this.value'								  
												  )}"
			                          name="dailySite" from="${Site.list()}"
			                          noSelection="${['':(dailyTotalMap.get(entries.key)).site]}" />
	                    	</td>
	                    </g:if>
	                    <g:else>
	                    	<td class="eventTD">
       			                   <g:select style='width:110px'
			                          onchange="${remoteFunction(action:'modifySite', update:'report_table_div', 
												  params:'\'employeeId=' + employee.id 						  
												  + '&day=' + entries.key.format("dd/MM/yyyy")	
												  +	'&month=' + entries.key.format("dd/MM/yyyy")	
												  + '&year=' + entries.key.format("dd/MM/yyyy")
												  + '&updatedSite=\' + this.value'								  
												  )}"
			                          name="dailySite" from="${Site.list()}"
			                          noSelection="${['':employee.site]}" />
	                    	</td>	                    
	                    </g:else>
	                    <td class="eventTD"><font size="2"><my:humanTimeTD id="dailyTotalTime"  name="dailyTotalTime" value="${dailyTotalMap.get(entries.key).elapsedSeconds}"/></font></td>
	                  </g:if>
	                  <g:else>
	                  	<td class="eventTD">
                 			 <g:select style='width:110px'
								onchange="${remoteFunction(action:'modifySite', update:'report_table_div', 
												  params:'\'employeeId=' + employee.id 						  
												  + '&day=' + entries.key.format("dd/MM/yyyy")	
												  +	'&month=' + entries.key.format("dd/MM/yyyy")	
												  + '&year=' + entries.key.format("dd/MM/yyyy")
												  + '&updatedSite=\' + this.value'								  
												  )}"
							  name="dailySite" from="${Site.list()}"
	                          noSelection="${['':employee.site]}" />
	                  	</td>	
	                    <td class="eventTD"><font size="2"><my:humanTimeTD id="dailyTotalTime"  name="dailyTotalTime" value="0"/></font></td>
	                  </g:else>
	                  <td class="eventTD"><font size="2"> 
	                  <g:if test="${employee.weeklyContractTime==35}">
	                        <g:if
	                          test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
	                          <g:if test="${dailySupTotalMap.get(entries.key) !=null}">
	                          	<my:humanTimeTD id="dailySupTotalMap"  name="dailySupTotalMap" value="${dailySupTotalMap.get(entries.key)}"/>    
	                          </g:if>
	                        </g:if>
	                      </g:if> 
	                      <g:else>
	                        <g:if test="${dailySupTotalMap.get(entries.key) !=null}">
	                          	<my:humanTimeTD id="dailySupTotalMap"  name="dailySupTotalMap" value="${dailySupTotalMap.get(entries.key)}"/>    
	                        </g:if>
	                      </g:else>
	                  </font></td>             
	                  <td class="eventTD">	                  
	                  <g:if test="${departureDate != null && entries.key > departureDate}">
	                  	-
	                  </g:if>     
	                  <g:else>
		                  	<g:if test="${employee.arrivalDate > entries.key}">
			                  	<g:if test="${holidayMap.get(entries.key) != null }">
			                  		${holidayMap.get(entries.key).type}
			                  	</g:if>
			                  	<g:else>
			                  		-
			                  	</g:else>	                  	
		                  	</g:if>
	                  		<g:else>
		   	                    <g:if test="${entries == null || entries.value == null}">           	   	                    
				                  	<g:if test="${holidayMap.get(entries.key) != null}">
				                      <font size="2"> 
				                      <g:select width="50px"
				                          onchange="${remoteFunction(action:'modifyAbsence', update:'cartouche_div', 
											  params:'\'employeeId=' + employee.id 						  
											  + '&day=' + entries.key.format("dd/MM/yyyy")						
											  + '&updatedSelection=\' + this.value'	)}"
				                          name="absenceType" from="${AbsenceType.values()}"
				                          value="${AbsenceType}" optionKey="key"
				                          noSelection="${['-':holidayMap.get(entries.key).type]}" />	
				                      </font>
				                    </g:if>
				                    <g:else>
				                      <font size="2"> <g:select width="50px"
				                          onchange="${remoteFunction(action:'modifyAbsence', update:'cartouche_div',		  
											  params:'\'employeeId=' + employee.id 						  
											  + '&day=' + entries.key.format("dd/MM/yyyy")				
											  + '&updatedSelection=\' + this.value'
											   )}"
				                          name="absenceType" from="${AbsenceType.values()}"
				                          value="${AbsenceType}" optionKey="key"
				                          noSelection="${['':'-']}" />
				                      </font>
				                   </g:else>
			                  	</g:if>
								<g:else>
									<g:if test="${holidayMap.get(entries.key) != null}">
					                      <font size="2"> 
					                      <g:if test="${holidayMap.get(entries.key).type == AbsenceType.MALADIE || holidayMap.get(entries.key).type == AbsenceType.GROSSESSE || holidayMap.get(entries.key).type == AbsenceType.FORMATION}">
					                    	<g:select width="50px"
					                          onchange="${remoteFunction(action:'modifyAbsence', update:'cartouche_div', 
												  params:'\'employeeId=' + employee.id 						  
												  + '&day=' + entries.key.format("dd/MM/yyyy")					
												  + '&updatedSelection=\' + this.value'								  
												  )}"
					                          name="absenceType" from="${['-',AbsenceType.GROSSESSE,AbsenceType.MALADIE,AbsenceType.FORMATION]}"
					                          noSelection="${['':holidayMap.get(entries.key).type]}" />
					                      </g:if>
					                      <g:else>
						                      <g:select width="50px"
						                          onchange="${remoteFunction(action:'modifyAbsence', update:'cartouche_div', 
													  params:'\'employeeId=' + employee.id 						  
													  + '&day=' + entries.key.format("dd/MM/yyyy")				
													  + '&updatedSelection=\' + this.value'								  
													  )}"
						                          name="absenceType" from="${AbsenceType.values()}"
						                          value="${AbsenceType}"
						                          noSelection="${['-':holidayMap.get(entries.key).type]}" />	
					                          </g:else>
					                      </font>
					                </g:if> 
					                <g:else>
					                      <g:select width="50px"
					                          onchange="${remoteFunction(action:'modifyAbsence', update:'cartouche_div', 
												  params:'\'employeeId=' + employee.id 						  
												  + '&day=' + entries.key.format("dd/MM/yyyy")					
												  + '&updatedSelection=\' + this.value'								  
												  )}"
					                          name="absenceType" from="${['-',AbsenceType.GROSSESSE,AbsenceType.MALADIE,AbsenceType.FORMATION]}"
					                          noSelection="${['':'-']}" />
					                 </g:else>
			                  	</g:else>
		                  	</g:else>
	                  	</g:else>                  	
	                  </td>	    
		                  <g:each in="${entries.value}" var="inOrOut" >
		                   	<g:if test="${inOrOut.type.equals('E')}">
		                       	<td bgcolor="98FB98" style="height: 1px;text-align:center;" class="eventTDEntry" >
		                       	   <g:if test="${inOrOut.regularizationType!=InAndOut.INIT || inOrOut.systemGenerated}">
		                       		<g:if test="${inOrOut.systemGenerated}">
		                           		<g:textField id="myinput" name="cell" value="${inOrOut.time.format('HH:mm')}" align="center" style="font-weight: bold;background-color:#98FB98;" />
		                         	</g:if>
		                          	<g:if test="${inOrOut.regularizationType==InAndOut.INITIALE_SALARIE}">    
		  	          	            	<g:remoteLink action="validate" id="link_${inOrOut.id}"			  				
			                         		before="if(!confirm('${message(code: 'inAndOut.validate.confirmation', default: 'Create')}')) return false"
			                         		onComplete="document.getElementById(${inOrOut.id}).style.color='green';"
			                         		params="[employeeId: employee.id,inOrOutId:inOrOut.id]">
			                         		<a style="text-decoration: none;" id="tooltipText" href="#" title="heure de saisie: ${inOrOut.loggingTime.format('HH:mm dd/MM/yyyy')}">
				                          		<g:textField name="cell" class="myinput" id='${inOrOut.id}'	                          	
				                            	value="${inOrOut.time.format('HH:mm')}" align="center"
				                              	style="color : red;font-weight: bold;	border: 0; align: center; width: 45px; margin: -8px;background-color:#98FB98;" />
			                              	</a><richui:tooltip id="logging_time" />      
			                          </g:remoteLink>                                               
		                          </g:if>
		                          <g:if test="${inOrOut.regularizationType==InAndOut.INITIALE_ADMIN}">
		                            <g:textField id="myinput" name="cell" 
		                              value="${inOrOut.time.format('HH:mm')}" align="center"
		                              style="color : blue;font-weight: bold; background-color:#98FB98;" />
		                          </g:if>
		                          <g:if test="${inOrOut.regularizationType==InAndOut.MODIFIEE_ADMIN}">
		                            <g:textField id="myinput" name="cell" 
		                              value="${inOrOut.time.format('HH:mm')}" align="center"
		                              style="color : green;font-weight: bold;background-color:#98FB98;" />
		                          </g:if>
		                          <g:if test="${inOrOut.regularizationType==InAndOut.MODIFIEE_SALARIE}">
		                            <g:textField id="myinput" name="cell" 
		                              value="${inOrOut.time.format('HH:mm')}" align="center"
		                              style="color : orange;font-weight: bold;background-color:#98FB98;" />
		                          </g:if>
		                       	</g:if> 
		                       	<g:else>    
		                          <g:textField id="myinput" name="cell" value="${inOrOut.time.format('HH:mm')}" align="center" style="background-color:#98FB98;" />
		                       	</g:else>
			                    <g:remoteLink action="trash" controller="employee" id="${entries.value}" params="[inOrOutId:inOrOut.id]"
			                    	update="report_table_div"
			                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
			                    	onComplete="document.getElementById('spinner').style.display = 'none';"
			                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
			                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
			                    </g:remoteLink>			                    
				                <g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
			                    <g:hiddenField name="time" value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" /> 
			                    <g:hiddenField name="day" value="${inOrOut.day}" /> 
			                    <g:hiddenField name="month" value="${inOrOut.month}" /> 
			                    <g:hiddenField name="year" value="${inOrOut.year}" /> 	
		                       	</td>
		                     </g:if> 
		                     <g:else>
		                       	<td bgcolor="#FFC0CB" style="height: 1px;text-align:center;" class="eventTDExit" >      
		                       	   <g:if test="${inOrOut.regularizationType!=InAndOut.INIT || inOrOut.systemGenerated}">
		                        	<g:if test="${inOrOut.systemGenerated}">
		                            	<g:textField id="myinput" name="cell" value="${inOrOut.time.format('HH:mm')}" align="center" style="font-weight: bold;background-color:#FFC0CB;" />
		                          	</g:if>
		                          	<g:if test="${inOrOut.regularizationType==InAndOut.INITIALE_SALARIE}">    
		  	          	            	<g:remoteLink action="validate" id="link_${inOrOut.id}"			  				
			                         		before="if(!confirm('${message(code: 'inAndOut.validate.confirmation', default: 'Create')}')) return false"
			                         		onComplete="document.getElementById(${inOrOut.id}).style.color='green';"
			                         		params="[employeeId: employee.id,inOrOutId:inOrOut.id]">
			                         		<a style="text-decoration: none;" id="tooltipText" href="#" title="heure de saisie: ${inOrOut.loggingTime.format('HH:mm dd/MM/yyyy')}">
				                          		<g:textField name="cell" class="myinput" id='${inOrOut.id}'	                          	
				                            	value="${inOrOut.time.format('HH:mm')}" align="center"
				                              	style="color : red;font-weight: bold;	border: 0; align: center; width: 45px; margin: -8px;background-color:#FFC0CB;" />
			                              	</a><richui:tooltip id="logging_time" />      
			                          </g:remoteLink>                                               
		                          </g:if>
		                          <g:if test="${inOrOut.regularizationType==InAndOut.INITIALE_ADMIN}">
		                            <g:textField id="myinput" name="cell"
		                              value="${inOrOut.time.format('HH:mm')}" align="center"
		                              style="color : blue;font-weight: bold;background-color:#FFC0CB;" />
		                          </g:if>
		                          <g:if test="${inOrOut.regularizationType==InAndOut.MODIFIEE_ADMIN}">
		                            <g:textField id="myinput" name="cell"
		                              value="${inOrOut.time.format('HH:mm')}" align="center"
		                              style="color : green;font-weight: bold;background-color:#FFC0CB;" />
		                          </g:if>
		                          <g:if test="${inOrOut.regularizationType==InAndOut.MODIFIEE_SALARIE}">
		                            <g:textField id="myinput" name="cell"
		                              value="${inOrOut.time.format('HH:mm')}" align="center"
		                              style="color : orange;font-weight: bold;background-color:#FFC0CB;" />
		                          </g:if>
		                       	</g:if> 
		                       	<g:else>    
		                          <g:textField id="myinput" name="cell" value="${inOrOut.time.format('HH:mm')}" align="center"  style="background-color:#FFC0CB;"/>
		                       	</g:else>
			                    <g:remoteLink action="trash" controller="employee" id="${entries.value}" params="[inOrOutId:inOrOut.id]"
			                    	update="report_table_div"
			                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
			                    	onComplete="document.getElementById('spinner').style.display = 'none';"
			                    	before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
			                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
			                    </g:remoteLink>			                    
				                <g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
			                    <g:hiddenField name="time" value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" /> 
			                    <g:hiddenField name="day" value="${inOrOut.day}" /> 
			                    <g:hiddenField name="month" value="${inOrOut.month}" /> 
			                    <g:hiddenField name="year" value="${inOrOut.year}" /> 	
		                       	</td>    
		                       	</g:else>             
		                  </g:each>
		                 <form method="POST" >
	                  <g:if test="${mileageMapByDay.get(entries.key) != null}">
	                    	<td class='mileageTD'>
								<g:textField id="${entries.key.format('yyyyMd')}" name="mileage" class='mileageTextField' value="${mileageMapByDay.get(entries.key).value ?: '0'}" align="center" style="vertical-align: middle;"/>
				                <g:remoteLink action="modifyMileage" 
				                		controller="mileage" 
				                		params="MyJSClass.dynamicParams"
				                    	update="report_table_div"
				                    	before="MyJSClass.setParams(${entries.key.format('yyyyMd')},${employee.id},${entries.key.format('yyyyMMd')},${true})"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';
				                    				document.getElementById('reportTable').className='showDetail';
													document.getElementById('detailSelector').style.display = 'block';
													document.getElementById('principalSelector').style.display = 'none';
													document.getElementById('detail').value = '1';">
				                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
				                    </g:remoteLink>	
									
								</td>
	                  </g:if>
	                  <g:else>
	                  		<td class='mileageTD'>
								<g:textField id="${entries.key.format('yyyyMd')}" name="mileage" class='mileageTextField' value="0" align="center" style="vertical-align: middle;"/>
				                <g:remoteLink action="modifyMileage" controller="mileage" 
				                		params="MyJSClass.dynamicParams"
				                    	update="report_table_div"
				                    	before="MyJSClass.setParams(${entries.key.format('yyyyMd')},${employee.id},${entries.key.format('yyyyMMd')},${true})"
				                    	onLoading="document.getElementById('spinner').style.display = 'inline';"
				                    	onComplete="document.getElementById('spinner').style.display = 'none';
				                    		    	document.getElementById('reportTable').className='showDetail';
													document.getElementById('detailSelector').style.display = 'block';
													document.getElementById('principalSelector').style.display = 'none';
													document.getElementById('detail').value = '1';">
				                    	<g:img dir="images" file="skin/refresh.png" width="16" height="16" style="vertical-align: middle;"/>
				                    </g:remoteLink>								
							</td>
	                  </g:else>
	                  </form>
	                </tr>
	              </g:if>
	            
	            </g:each>
	            
	            <tr>
	              <th>${day.key}</th>
	              <g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null}">
	                <th colspan="34" scope="colgroup">
	                  ${message(code: 'weekly.total.label', default: 'Report')} ${weeklyTotal.get(employee).get(day.key)}
	                  <g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null && !(weeklySupTotal.get(employee).get(day.key)).equals('00:00')}">
	                  	dont ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee)).get(day.key)}	                   
	                  </g:if>
	                </th>                                                        
	              </g:if>
	              <g:else>
	                <th colspan="34" scope="colgroup"> ${message(code: 'weekly.total.label', default: 'Report')} 00:00
	               		<g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null  && weeklySupTotal.get(employee).get(day.key) != null && !(weeklySupTotal.get(employee).get(day.key)).equals('00:00')}">
	                   dont ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee)).get(day.key)}  
	                  </g:if>
	                </th>
	              </g:else>
	            </tr>  
	          </g:each>
	        </g:each>
	        <g:hiddenField name="employee.id" value="${userId}" />
	        <g:hiddenField name="userId" value="${userId}" />  
	      </tbody>
	</table>
</form>