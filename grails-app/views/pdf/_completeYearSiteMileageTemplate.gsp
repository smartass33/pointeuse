<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
		<%@ page import="pointeuse.Employee"%>
		<style  type="text/css">
			@page {
			   size: 210mm 297mm;
			   margin: 5px 5px 5px 5px;
			 }
			table {
			  font: normal 11px verdana, arial, helvetica, sans-serif;
			  color: #363636;
			  background: #f6f6f6;
			  background-color: white;
			  text-align:left;
			  }
			caption {
			  text-align: center;
			  font: bold 16px arial, helvetica, sans-serif;
			  background: transparent;
			  padding:6px 4px 8px 0px;
			  color: #CC00FF;
			  text-transform: uppercase;
			}
			thead, tfoot {
				background:url(bg1.png) repeat-x;
				text-align:left;
				height:20px;
			}
			thead th, tfoot th {
				padding:3px;
			}
			table a {
				color: #333333;
				text-decoration:none;
			}
			table a:hover {
				text-decoration:underline;
			}
			tr.odd {
				background: #f1f1f1;
			}
			tbody td {
			 	text-align:center;
			 	height:5px;
			 	width:90px;
			 	font-size:80%;
			}			
			tbody th{
			 	text-align:center;
			 	height:5px;
			 	width:250px;
			}			
			.cartoucheValues{	
				border-collapse: collapse;
			 	border-width:1px; 
			 	border-style:solid; 
			 	border-color:black;
			}
			.cartoucheValues td{
				width: 100px;
				text-align: center;	
			}

		</style>
	</head>
	<body>
		<h1 style="text-align:center;font-size:130%">${message(code: 'mileage.report.label')} entre ${infYear} et ${supYear}</h1>
		<BR/>
		<div id="mileageFollowupDiv">
			<my:mileageSiteTable/>
		</div>
	</body>
</html>