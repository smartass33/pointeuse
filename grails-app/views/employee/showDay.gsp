<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<g:javascript library="prototype" />
		
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta name="layout" content="main" />
		<title>Modifier les évenements du jour</title>
		
		<script type="text/javascript">
		function removeElements()
		{
			alert('toto');
			
			var elem = document.getElementById('elemTable');
			elem.parentNode.removeChild(elem);
			
			}
		
		$('.deleteThisRow').live('click',function(){
		     var rowLength = $('.row').length;
		         //this line makes sure that we don't ever run out of rows.
		      if(rowLength > 1){
		   deleteRow(this);
		  }else{
		   $('.kikoo tr:last').after(appendRow);
		   deleteRow(this);
		  }
		 });
		
		 function deleteRow(currentNode){
		  $(currentNode).parent().parent().remove();
		 }
		 });
		 
		 function someFunction() {
    ${remoteFunction(controller:'employee', action:'trash',update: 'koko'
    )};     
    functionForJQuery();
}
function functionForJQuery(){
    var $someThing = $('.someClassName');
}
		
		</script>
	</head>
	<body>
		<g:listInAndOuts id='koko'/>
	</body>
</html>