<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>
<%@ page import="java.util.Calendar"%>

<table  border="1" style="table-layout: fixed;" id="reportTable">
    <thead>
      <th width="60px" align="center">${message(code: 'report.table.date.label', default: 'report')}</th>
      <th width="90px">${message(code: 'report.table.daily.total.label', default: 'report')}</th>
      <th width="60px">${message(code: 'report.table.HS.label', default: 'report')}</th>
      <th width="60px">${message(code: 'report.table.absence.label', default: 'report')}</th>
      <th align="center" colspan="80">${message(code: 'events.label', default: 'report')}</th>
    </thead>
    <tbody>
        <g:each in="${weeklyAggregate}" status="k" var="week">
          <g:each in="${week}" status="l" var="day">
            <g:each in="${day.value}" status="m" var="entries">
              <g:if test="${entries!=null}">
                <tr>
                  <g:if test="${dailyBankHolidayMap.get(entries.key) }">
	                  <td style="width:140px; color:red;"><i><font size="2" style="color:red;"> ${entries.key.format('E dd MMM yyyy')}</font></i></td>
                  </g:if>
                  <g:else>
						<g:if test="${entries.key.getAt(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY}">
							<td style="width:140px;"><i><font size="2" style="width:140px; font-weight:bold;"> ${entries.key.format('E dd MMM yyyy')}</font></i></td>
						</g:if>
						<g:else>
							<td style="width:140px;"><font size="2"><i>${entries.key.format('E dd MMM yyyy')}</i></font></td>						
	                   </g:else>
                  </g:else>
                  <g:if
                    test="${dailyTotalMap.get(entries.key) !=null && (dailyTotalMap.get(entries.key).get(0)>0 || dailyTotalMap.get(entries.key).get(1)>0)}">
                    <td><font size="2">
                       	<g:if test='${(dailyTotalMap.get(entries.key)).get(0)<10}'>0${(dailyTotalMap.get(entries.key)).get(0)}</g:if>
		                <g:else> ${(dailyTotalMap.get(entries.key)).get(0)}</g:else>
		                <g:if test='${(dailyTotalMap.get(entries.key)).get(1)<10}'> : 0${(dailyTotalMap.get(entries.key)).get(1)}</g:if>
		                <g:else>: ${(dailyTotalMap.get(entries.key)).get(1)}</g:else>
                    </font></td>
                  </g:if>
                  <g:else>
                    <td><font size="2">00 : 00</font></td>
                  </g:else>
                  <td><font size="2"> 
                  <g:if test="${employee.weeklyContractTime==35}">
                        <g:if
                          test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
                          <g:if
                            test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
                            ${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}
                          </g:if>
                        </g:if>
                      </g:if> 
                      <g:else>
                        <g:if
                          test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
                          ${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}
                        </g:if>
                      </g:else>
                  </font></td>             
                  <td>
                    <g:if test="${entries == null || entries.value ==null}">           
	                  	<g:if test="${holidayMap.get(entries.key) != null}">
	                      <font size="2"> 
	                      <g:select width="200px"
	                          onchange="${remoteFunction(action:'modifyAbsence', update:'updateDiv2', 
								  params:'\'employeeId=' + employee.id 						  
								  + '&day=' + entries.key.format("dd/MM/yyyy")
								  + '&monthlyTotalRecap=' + (monthlyTotalRecap.get(0)*3600+monthlyTotalRecap.get(1)*60+monthlyTotalRecap.get(2))
								  + '&payableSupTime=' + (payableSupTime.get(0)*3600+payableSupTime.get(1)*60+payableSupTime.get(2))
								  + '&payableCompTime=' + (payableCompTime.get(0)*3600+payableCompTime.get(1)*60+payableCompTime.get(2))
								  + '&updatedSelection=\' + this.value'								  )}"
	                          name="absenceType" from="${AbsenceType.values()}"
	                          value="${AbsenceType}" optionKey="key"
	                          noSelection="${['-':holidayMap.get(entries.key).type]}" />
	
	                      </font>
	                    </g:if> <g:else>
	                      <font size="2"> <g:select width="200px"
	                          onchange="${remoteFunction(action:'modifyAbsence', update:'updateDiv2',		  
								  params:'\'employeeId=' + employee.id 						  
								  + '&day=' + entries.key.format("dd/MM/yyyy")
								  + '&monthlyTotalRecap=' + (monthlyTotalRecap.get(0)*3600+monthlyTotalRecap.get(1)*60+monthlyTotalRecap.get(2))
								  + '&payableSupTime=' + (payableSupTime.get(0)*3600+payableSupTime.get(1)*60+payableSupTime.get(2))
								  + '&payableCompTime=' + (payableCompTime.get(0)*3600+payableCompTime.get(1)*60+payableCompTime.get(2))
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
	                      <g:select width="200px"
	                          onchange="${remoteFunction(action:'modifyAbsence', update:'updateDiv2', 
								  params:'\'employeeId=' + employee.id 						  
								  + '&day=' + entries.key.format("dd/MM/yyyy")
								  + '&monthlyTotalRecap=' + (monthlyTotalRecap.get(0)*3600+monthlyTotalRecap.get(1)*60+monthlyTotalRecap.get(2))
								  + '&payableSupTime=' + (payableSupTime.get(0)*3600+payableSupTime.get(1)*60+payableSupTime.get(2))
								  + '&payableCompTime=' + (payableCompTime.get(0)*3600+payableCompTime.get(1)*60+payableCompTime.get(2))
								  + '&updatedSelection=\' + this.value'								  )}"
	                          name="absenceType" from="${AbsenceType.values()}"
	                          value="${AbsenceType}" optionKey="key"
	                          noSelection="${['-':holidayMap.get(entries.key).type]}" />	
	                      </font>
	                </g:if> 
	                <g:else>
	                      <g:select width="200px"
	                          onchange="${remoteFunction(action:'modifyAbsence', update:'updateDiv2', 
								  params:'\'employeeId=' + employee.id 						  
								  + '&day=' + entries.key.format("dd/MM/yyyy")
								  + '&monthlyTotalRecap=' + (monthlyTotalRecap.get(0)*3600+monthlyTotalRecap.get(1)*60+monthlyTotalRecap.get(2))
								  + '&payableSupTime=' + (payableSupTime.get(0)*3600+payableSupTime.get(1)*60+payableSupTime.get(2))
								  + '&payableCompTime=' + (payableCompTime.get(0)*3600+payableCompTime.get(1)*60+payableCompTime.get(2))
								  + '&updatedSelection=\' + this.value'								  )}"
	                          name="absenceType" from="${['-',AbsenceType.GROSSESSE]}"
	                          noSelection="${['':'-']}" />
	                 </g:else>
                  </g:else>
                  </td>
                  <g:each in="${entries.value}" var="inOrOut">
                    <font size="2"> 
                    	<g:if test="${inOrOut.type.equals('E')}">
                        	<td bgcolor="98FB98" style="height: 1px;text-align:center;">
                      	</g:if> 
                      	<g:else>
                        	<td bgcolor="#FFC0CB" style="height: 1px;text-align:center;">                        
                      	</g:else> 
                      	<table class="reportTable">
                      	<tr>
                      	<td style='width:"50px"'>
                      	<font size="2"> 
                      	<g:if test="${inOrOut.regularizationType!=InAndOut.INIT || inOrOut.systemGenerated}">
                        	<g:if test="${inOrOut.systemGenerated}">
                            	<g:textField id="myinput" name="cell" value="${inOrOut.time.format('HH:mm')}" align="center" style="font-weight: bold" />
                          	</g:if>
                          	<g:if test="${inOrOut.regularizationType==InAndOut.INITIALE_SALARIE}">    
  	          	            	<g:remoteLink action="validate" id="link_${inOrOut.id}"			  				
	                         		before="if(!confirm('${message(code: 'inAndOut.validate.confirmation', default: 'Create')}')) return false"
	                         		onComplete="document.getElementById(${inOrOut.id}).style.color='green';"
	                         		params="[employeeId: employee.id,inOrOutId:inOrOut.id]">
	                         		<a id="tooltipText" href="#" title="heure de saisie: ${inOrOut.loggingTime.format('HH:mm dd/MM/yyyy')}">
		                          		<g:textField name="cell" class="myinput" id='${inOrOut.id}'	                          	
		                            	value="${inOrOut.time.format('HH:mm')}" align="center"
		                              	style="color : red;font-weight: bold;	border: 0; align: center; width: 45px; margin: -8px;" />
	                              	</a><richui:tooltip id="logging_time" />      
	                          </g:remoteLink>
	                         
	                          
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==InAndOut.INITIALE_ADMIN}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('HH:mm')}" align="center"
                              style="color : blue;font-weight: bold;" />
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==InAndOut.MODIFIEE_ADMIN}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('HH:mm')}" align="center"
                              style="color : green;font-weight: bold;" />
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==InAndOut.MODIFIEE_SALARIE}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('HH:mm')}" align="center"
                              style="color : orange;font-weight: bold;" />

                          </g:if>
                        </g:if> <g:else>    
                          <g:textField id="myinput" name="cell"
                            value="${inOrOut.time.format('HH:mm')}" align="center" />
                        </g:else>
                    </font>
                    </font>
                    <g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
                    <g:hiddenField name="time" value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" /> 
                    <g:hiddenField name="day" value="${inOrOut.day}" /> 
                    <g:hiddenField name="month" value="${inOrOut.month}" /> 
                    <g:hiddenField name="year" value="${inOrOut.year}" /> 
                    </td>
                    <td style='width:"10px"'>
	                    <g:remoteLink action="trash" controller="employee" id="${entries.value}" params="[inOrOutId:inOrOut.id]"
	                    update="updateDiv3"
	                    onLoading="document.getElementById('spinner').style.display = 'inline';"
	                    onComplete="document.getElementById('spinner').style.display = 'none';"
	                    before="if(!confirm('${message(code: 'inAndOut.delete.confirmation', default: 'Create')}')) return false">
	                    	<g:img dir="images" file="skin/trash.png" width="14" height="14"/>
	                    </g:remoteLink>	
                    </td>
                    </tr>
                    </table>
                    </td>
                  </g:each>
                </tr>
              </g:if>
            </g:each>
            <tr>
              <th>${day.key}</th>
              <g:if test="${weeklyTotal.get(employee) != null && weeklyTotal.get(employee).get(day.key) !=null && (weeklyTotal.get(employee).get(day.key).get(2)>0 || weeklyTotal.get(employee).get(day.key).get(1)>0 || weeklyTotal.get(employee).get(day.key).get(0)>0)}">
                <th colspan="34" scope="colgroup">
                  ${message(code: 'weekly.total.label', default: 'Report')} ${(weeklyTotal.get(employee).get(day.key)).get(0)}H${(weeklyTotal.get(employee).get(day.key)).get(1)==0?'':(weeklyTotal.get(employee).get(day.key)).get(1)}
                  <g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null && ((weeklySupTotal.get(employee).get(day.key)).get(0) > 0 || (weeklySupTotal.get(employee).get(day.key)).get(1) >0)}">
                   dont ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee).get(day.key)).get(0)}H${(weeklySupTotal.get(employee).get(day.key)).get(1)==0?'':(weeklySupTotal.get(employee).get(day.key)).get(1)}    
                  </g:if>
                </th>                                                        
              </g:if>
              <g:else>
                <th colspan="34" scope="colgroup">Total fin de semaine: 0H
               		<g:if test="${weeklySupTotal != null && weeklySupTotal.get(employee) != null  && weeklySupTotal.get(employee).get(day.key) != null && ((weeklySupTotal.get(employee).get(day.key)).get(0) > 0 || (weeklySupTotal.get(employee).get(day.key)).get(1) >0)}">
                   dont ${message(code: 'which.sup.time', default: 'Report')} ${(weeklySupTotal.get(employee).get(day.key)).get(0)}H${(weeklySupTotal.get(employee).get(day.key)).get(1)==0?'':(weeklySupTotal.get(employee).get(day.key)).get(1)}  
                  </g:if>
                </th>
              </g:else>
            </tr>  
          </g:each>
        </g:each>
        <g:hiddenField name="employee.id" value="${userId}" />
      </tbody>
    </table>