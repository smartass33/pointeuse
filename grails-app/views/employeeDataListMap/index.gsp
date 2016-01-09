<%@ page import="pointeuse.EmployeeDataListMap" %>
<%@ page import="pointeuse.EmployeeDataType" %>
<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
			#newEmployeeDataForm {
    			display: none;
			}
			
.shoppingList
{
	width: 300px;
	background-color: #fff;
	border: 0px solid #395e7f;
}

.shoppingList .reponse
{
	color: 9f9;
	font-weight: bold;
	width: 200px;
	margin: 5px auto 0;
}

.shoppingList ul
{
	padding: 10px 50px;
}

.shoppingList ul li
{
	list-style: none;
}


.shoppingList tr
{
	padding: 10px 50px;
}

.shoppingList tr td
{
	list-style: none;
}


.count, .item
{
	padding: 2px 5px;
	cursor:pointer;
}


.item:hover
{
	background-color: #93abc1;
	border: 1px solid #33f;
	color: #fff;
}

.shoppingList li.ui-sortable-helper{
	cursor:-moz-grabbing;
	opacity: 0.6;
}

.shoppingList tr.ui-sortable-helper{
	cursor:-moz-grabbing;
	opacity: 0.6;
}





		</style>
		<g:javascript library="jquery" plugin="jquery" />
		<r:require module="report"/>
		<g:javascript src="/jquery/jquery.min.js" />
		<g:javascript src="/jquery/jquery-ui.custom.min.js" />
			

		<resource:include components="autoComplete, dateChooser" autoComplete="[skin: 'default']" />
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'employeeDataListMap.label', default: 'EmployeeDataListMap')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-employeeDataListMap" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<!--li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li-->
			</ul>
		</div>
		<div id="list-employeeDataListMap" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<div id='employeeDataDiv'>

				<g:employeeDataListTable/>
			</div>
			<g:form method="post" >			
				<script>		
					$(document).ready(
					    function() {
					        $("#newEmployeeDataButton").click(function() {
					            $("#newEmployeeDataForm").toggle();
					        });
					       	$("#cancelEmployeeDataCreation").click(function() {
					            $("#newEmployeeDataForm").toggle();
					        });   
					   });		  
					   
					   (function($){
							$.fn.shoppingList = function(options) {
								// Options par defaut
								var defaults = {
								};
										
								var options = $.extend(defaults, options);
								
								this.each(function(){
									
									var obj = $(this);
									
									// Empêcher la sélection des éléments à la sourirs (meilleure gestion du drag & drop)
									var _preventDefault = function(evt) { evt.preventDefault(); };
									$("li").bind("dragstart", _preventDefault).bind("selectstart", _preventDefault);
						
									// Initialisation du composant "sortable"
									$(obj).sortable({
										axis: "y", // Le sortable ne s'applique que sur l'axe vertical
										containment: ".shoppingList", // Le drag ne peut sortir de l'élément qui contient la liste
										handle: ".item", // Le drag ne peut se faire que sur l'élément .item (le texte)
										distance: 10, // Le drag ne commence qu'à partir de 10px de distance de l'élément
										// Evenement appelé lorsque l'élément est relaché
										stop: function(event, ui){
											// Pour chaque item de liste
											$(obj).find("li").each(function(){
												// On actualise sa position
												index = parseInt($(this).index()+1);
												// On la met à jour dans la page
												$(this).find(".count").text(index);
											});
										},
										update: function()
										{
											// On prépare la variable contenant les paramètres
											var order = $(this).sortable("serialize")+'&action=updateListeOrder';
											// $(this).sortable("serialize") sera le paramètre "element", un tableau contenant les différents "id"
											// action sera le paramètre qui permet éventuellement par la suite de gérer d'autres scripts de mise à jour
									 
									 		jQuery.ajax({
										        type:'POST',
										        data : order,
										        url:'${createLink(action: 'changeRank')}',
										        error:function(XMLHttpRequest,textStatus,errorThrown){}
										        });
														
									 
									 
									 		/*
											// Ensuite on appelle notre page updateListe.php en lui passant en paramètre la variable order
											$.post("/employeeDatatListMap/changeRank", order, function(theResponse)
											{
												// On affiche dans l'élément portant la classe "reponse" le résultat du script de mise à jour
												$(".reponse").html(theResponse).fadeIn("fast");
												setTimeout(function()
												{
													$(".reponse").fadeOut("slow");
												}, 2000);
											});
											*/
										}
									});
						
									// Pour chaque élément trouvé dans la liste de départ
									$(obj).find("li").each(function(){
										// On ajoute les contrôles
										addControls($(this));
									});
									
									/*
									* Fonction qui ajoute les contrôles aux items
									* @Paramètres
									*  - elt: élément courant (liste courante)
									*
									* @Return void
									*/
									
			
			
									function addControls(elt)
									{
										// On ajoute en premier l'élément textuel
										$(elt).html("<span class='item'>"+$(elt).text()+"</item>");
										// Puis l'élément de position
										$(elt).prepend('<span class="count">'+parseInt($(elt).index()+1)+'</span>');
						
									}
								});
								// On continue le chainage JQuery
								return this;
							};
						})(jQuery);				
						
						$("#tabs").tabs();

$("tbody").sortable({
    items: "> tr:not(:first)",
    appendTo: "parent",
    helper: "clone"
}).disableSelection();

$("#tabs ul li a").droppable({
    hoverClass: "drophover",
    tolerance: "pointer",
    drop: function(e, ui) {
        var tabdiv = $(this).attr("href");
        $(tabdiv + " table tr:last").after("<tr>" + ui.draggable.html() + "</tr>");

        ui.draggable.remove();
    }
});

						

										   
				</script>
				<table>
					<tbody>	
						<tr>
							<td><div id="newEmployeeDataButton"><a href="#"><g:message code="employeeDataListMap.add.label" default="Field name" /></a></div></td>
						</tr>					
				    	<tr id="newEmployeeDataForm">
				    		<td><input type="text" class="code" id="fieldName"  value="" name="fieldname" /></td>
				    		<td><g:select name="type" from="${EmployeeDataType.values()}"  /></td> 
				    		<td><input type="number" class="code" id="rank"  value="" name="rank" /></td>
				    		<td ><a href="#" id="cancelEmployeeDataCreation">${message(code: 'default.button.cancel.label', default: 'cancel')}</a></td>
				    		<td><input type="submit" class="listButton" value="${message(code: 'default.button.add.label', default: 'cancel')}" name="_action_addNewEmployeeData"></td>
				    	</tr>
					</tbody>		
				</table>
			</g:form>			
		</div>
	</body>
</html>
