<%@ page import="org.codehaus.groovy.grails.plugins.web.taglib.JavascriptValue"%>
<%@ page import="pointeuse.Employee"%>
<%@ page import="pointeuse.InAndOut"%>
<%@ page import="pointeuse.AbsenceType"%>
<%@ page import="pointeuse.MonthlyTotal"%>

<table border="1" style="table-layout: fixed;" id="reportTable">
    <thead>
      <th width="60px" align="center">date</th>
      <th width="90px">total du jour</th>
      <th width="60px">HS</th>
      <th width="60px">absence</th>
      <th align="center" colspan="80">${message(code: 'events.label', default: 'report')}</th>
    </thead>
    <tbody>
        <g:each in="${weeklyAggregate}" status="k" var="week">
          <g:each in="${week}" status="l" var="day">
            <g:each in="${day.value}" status="m" var="entries">
              <g:if test="${entries!=null}">
                <tr>
                  <g:if test="${dailyBankHolidayMap.get(entries.key) }">
	                  <td style="width:140px; color:red;"><i> <modalbox:createLink
	                          controller="employee" action="showDay" id="${employee.id}"
	                          title="modifier des évenements" width="500"
	                          params="[employeeId: employee.id, month: entries.key.format('MM'), year: entries.key.format('yyyy'), day: entries.key.format('dd')]">
	                         <font size="2" style="color:red;"> ${entries.key.format('E dd MMM yyyy')}</font>
	                        </modalbox:createLink>
	                    </i>
	                    </td>
                  </g:if>
                  <g:else>
                  	 <td width="140px"><font size="2"><i> <modalbox:createLink
	                          controller="employee" action="showDay" id="${employee.id}"
	                          title="modifier des évenements" width="500"
	                          params="[employeeId: employee.id, month: entries.key.format('MM'), year: entries.key.format('yyyy'), day: entries.key.format('dd')]">
	                          ${entries.key.format('E dd MMM yyyy')}
	                        </modalbox:createLink>
	                    </i></font>
	                    </td>
                  </g:else>
                  <g:if
                    test="${dailyTotalMap.get(entries.key) !=null && (dailyTotalMap.get(entries.key).get(0)>0 || dailyTotalMap.get(entries.key).get(1)>0 || dailyTotalMap.get(entries.key).get(2)>0)}">

                    <td><font size="2">
                        ${(dailyTotalMap.get(entries.key)).get(0)}:${(dailyTotalMap.get(entries.key)).get(1)}:${(dailyTotalMap.get(entries.key)).get(2)}
                    </font></td>
                  </g:if>
                  <g:else>
                    <td><font size="2">00:00:00</font></td>
                  </g:else>
                  <td><font size="2"> <g:if
                        test="${employee.weeklyContractTime==35}">
                        <g:if
                          test="${weeklySupTotal.get(employee) != null && weeklySupTotal.get(employee).get(day.key) !=null}">
                          <g:if
                            test="${dailySupTotalMap.get(entries.key) !=null && (dailySupTotalMap.get(entries.key).get(0)>0 ||dailySupTotalMap.get(entries.key).get(1)>0)}">
                            ${dailySupTotalMap.get(entries.key).get(0)}H${dailySupTotalMap.get(entries.key).get(1)==0?'':dailySupTotalMap.get(entries.key).get(1)}
                          </g:if>
                        </g:if>
                      </g:if> <g:else>
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
	                      <g:select
	                          onchange="${remoteFunction(action:'modifyAbsence', update:'updateDiv2', params:[updatedSelection:new JavascriptValue('this.value'),employeeId:employee.id,day:entries.key.format('dd/MM/yyyy'),payableSupTime:(payableSupTime.get(0)*3600+payableSupTime.get(1)*60+payableSupTime.get(2)),payableCompTime:(payableCompTime.get(0)*3600+payableCompTime.get(1)*60+payableCompTime.get(2))])}"
	                          name="absenceType" from="${AbsenceType.values()}"
	                          value="${AbsenceType}" optionKey="key"
	                          noSelection="${['-':holidayMap.get(entries.key).type]}" />
	
	                      </font>
	                    </g:if> <g:else>
	                      <font size="2"> <g:select
	                          onchange="${remoteFunction(action:'modifyAbsence', update:'updateDiv2',params:[updatedSelection:new JavascriptValue('this.value'),employeeId:employee.id,day:entries.key.format('dd/MM/yyyy'),payableSupTime:(payableSupTime.get(0)*3600+payableSupTime.get(1)*60+payableSupTime.get(2)),payableCompTime:(payableCompTime.get(0)*3600+payableCompTime.get(1)*60+payableCompTime.get(2))] )}"
	                          name="absenceType" from="${AbsenceType.values()}"
	                          value="${AbsenceType}" optionKey="key"
	                          noSelection="${['':'-']}" />
	                      </font>
	                   </g:else>
                  </g:if>
				<g:else>
					<g:if test="${holidayMap.get(entries.key) != null}">
                  	${holidayMap.get(entries.key).type}
                  	</g:if>
                  	<g:else>
                  	-
                  	</g:else>
                  </g:else>
                  </td>
                  <g:each in="${entries.value}" var="inOrOut">
                    <font size="2"> <g:if
                        test="${inOrOut.type.equals('E')}">
                        <td bgcolor="98FB98" style="height: 1px;text-align:center;">
                      </g:if> <g:else>
                        <td bgcolor="#FFC0CB" style="height: 1px;text-align:center;">
                      </g:else> <font size="2"> <g:if
                          test="${inOrOut.regularizationType!=0 || inOrOut.systemGenerated}">
                          <g:if test="${inOrOut.systemGenerated}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('H:mm')}" align="center"
                             style="font-weight: bold" />
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==1}">
                            <g:textField id="myinput" name="cell"
                             value="${inOrOut.time.format('H:mm')}" align="center"
                              style="color : red;font-weight: bold;" />
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==2}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('H:mm')}" align="center"
                              style="color : blue;font-weight: bold;" />
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==3}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('H:mm')}" align="center"
                              style="color : green;font-weight: bold;" />
                          </g:if>
                          <g:if test="${inOrOut.regularizationType==4}">
                            <g:textField id="myinput" name="cell"
                              value="${inOrOut.time.format('H:mm')}" align="center"
                              style="color : orange;font-weight: bold;" />
                          </g:if>
                        </g:if> <g:else>
                          <g:textField id="myinput" name="cell"
                            value="${inOrOut.time.format('H:mm')}" align="center" />
                        </g:else>
                    </font>
                    </font>
                    <g:hiddenField name="inOrOutId" value="${inOrOut.id}" />
                    <g:hiddenField name="time" value="${inOrOut.time.format('yyyy-M-d H:mm:ss')}" /> 
                    <g:hiddenField name="day" value="${inOrOut.day}" /> 
                    <g:hiddenField name="month" value="${inOrOut.month}" /> 
                    <g:hiddenField name="year" value="${inOrOut.year}" /> 
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
                <th colspan="34" scope="colgroup">Total fin de semaine: 0H</TH>
              </g:else>
            </tr>  
          </g:each>
        </g:each>
        <g:hiddenField name="employee.id" value="${employeeId}" />
      </tbody>
    </table>