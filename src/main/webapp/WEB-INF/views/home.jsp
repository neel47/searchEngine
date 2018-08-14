
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">

<head>
<%@ page isELIgnored="false" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">
<title>Inventory</title>
<!-- Jquery UI  CSS-->
<link href="resources/vendor/jquery/jquery-ui.min.css"
	rel="stylesheet">
<!-- Bootstrap core CSS-->
<link href="resources/vendor/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">
<!-- Custom fonts for this template-->
<link href="resources/vendor/font-awesome/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">
<!-- Page level plugin CSS-->
<link href="resources/vendor/datatables/dataTables.bootstrap4.css"
	rel="stylesheet">
<!-- Custom styles for this template-->
<link href="resources/css/sb-admin.css" rel="stylesheet">

<!-- Bootstrap core JavaScript-->
<script src="resources/vendor/jquery/jquery.min.js"></script>
<script src="resources/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- Core plugin JavaScript-->
<script src="resources/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="resources/vendor/jquery/jquery-ui.min.js"></script>




			<!-- Page level plugin JavaScript-->
			<script src="resources/vendor/chart.js/Chart.min.js"></script>
			<script src="resources/vendor/datatables/jquery.dataTables.js"></script>
			<script src="resources/vendor/datatables/dataTables.bootstrap4.js"></script>
			<!-- Custom scripts for all pages-->
			<script src="resources/js/sb-admin.min.js"></script>
			<!-- Custom scripts for this page-->
			<script src="resources/js/sb-admin-datatables.min.js"></script>

<style>
.bg-dark {
	background: url("resources/images/photo_bg.jpg") no-repeat center center
		fixed;
	background-size: cover;
	font-size: 16px;
	font-family: 'Lato', sans-serif;
	font-weight: 300;
	margin: 0;
	color: white;
	
}

h1#title {
	font-family: 'Roboto Slab', serif;
	font-weight: 300;
	font-size: 3.2em;
	color: white;
	text-shadow: 0 0 10px rgba(0, 0, 0, 0.8);
	margin: 0 auto;
	padding-top: 20px;
	max-width: 400px;
	text-align: center;
	position: relative;
	top: 0px;
}

.card-header {
	background-color: #665851;
}

.btn-primary:hover {
	color: #fff;
	background-color: #847973;
	border-color: #847973;
}

.btn-primary {
	color: #fff;
	background-color: #665851;
	border-color: #665851;
}
table.dataTable {
background-color: rgba(0, 0, 0, 0.5);
}


.ui-autocomplete { position: absolute; cursor: default; background:#CCC }   

/* workarounds */
html .ui-autocomplete { width:1px; } /* without this, the menu expands to 100% in IE6 */
.ui-menu {
    list-style:none;
    padding: 2px;
    margin: 0;
    display:block;
    float: left;
}
.ui-menu .ui-menu {
    margin-top: -3px;
}
.ui-menu .ui-menu-item {
    margin:0;
    padding: 0;
    zoom: 1;
    float: left;
    clear: left;
    width: 100%;
}
.ui-menu .ui-menu-item a {
    text-decoration:none;
    display:block;
    padding:.2em .4em;
    line-height:1.5;
    zoom:1;
}
.ui-menu .ui-menu-item a.ui-state-hover,
.ui-menu .ui-menu-item a.ui-state-active {
    font-weight: normal;
    margin: -1px;
}
.form-control-extended {
width: 90%;
padding: .375rem .75rem;
font-size: 1rem;
line-height: 1.5;
color: #495057;
background-color: #fff;
background-clip: padding-box;
border: 1px solid #ced4da;
border-radius: .25rem;
transition: border-color .15s ease-in-out,box-shadow .15s ease-in-out;

}


</style>


<script>

$(document).ready(function() {
	var from = '${from}';
	if("results"==from)
		document.getElementById("tableToShow").style.display="block";
	
	 $(".clickable-row").click(function() {
		 getFile($(this).data("href"));
	    });
})



	function submitForm() {
		
		return true;
	}

	function callError(id) {
		document.getElementById(id).innerHTML = "Hello JavaScript!";
	}
	
	function getFile(fileName)
	{
		
		$.ajax({
			type : "Get",
			/* contentType : "application/json", */
			url : "${pageContext.servletContext.contextPath}/getFile",
			data : {
			    "filename" : fileName
			    },
			    async: true,
			/* data : JSON.stringify(data), */
			/* dataType : 'json', */
			timeout : 100000,
			success : function(data) {
				var u=decodeURIComponent(data)
				window.open(u,"winName");
				console.log("DONE->"+u);
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
			}
		});
		
		
		
		
	}
	
	function wordSuggestionInterface()
	{
		var word=document.getElementById("inputData").value;
		
		if(word.length<1)
			{
			return false;
			}
		
		
		$.ajax({
			type : "Get",
			/* contentType : "application/json", */
			url : "${pageContext.servletContext.contextPath}/wordSuggestion",
			data : {
			    "word" : word.trim()
			    },
			    async: true,
			/* data : JSON.stringify(data), */
			/* dataType : 'json', */
			timeout : 100000,
			success : function(data) {
				$( "#inputData" ).autocomplete({
				      source: data.split(',')
				    });
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
			}
		});
	}
</script>

</head>

<body class="bg-dark" onsubmit="return submitForm();" id="page-top">
	<h1 id="title" class="hidden">
		<span id="logo">Search Engine<span>
	</h1>
	<div class="container" >
		<div class="">
			<div class="card-body" >
				<form:form action="submitForm" method="post"
					modelAttribute="userform" >
						<form:input path="inputData" class="form-control-extended" id="inputData"
							type="text" placeholder="Search" required="required" onkeypress="wordSuggestionInterface()"/>
							&nbsp;
							<button class="btn btn-primary">Submit</button>
				</form:form>
				
				<br><br>
				
				
				
				
				
				
				<div class="table-responsive" id="tableToShow" style="display: none">
						<table class="table table-bordered" id="dataTable" width="100%"
							cellspacing="0">
							<thead>
								<tr>
									<th>Page Name</th>
									<th>Occurrences</th>
								</tr>
							</thead>
							<tfoot>
								<tr>
									<th>Page Name</th>
									<th>Occurrences</th>
								</tr>
							</tfoot>
							<tbody>


								<c:forEach var="map" items="${documentWordFrequency}">
									<tr>

										<td class='clickable-row' data-href='${map.key}'>${map.key}</td>
										<td>${map.value}</td>
									</tr>
								</c:forEach>



							</tbody>
						</table>
					</div>
				
				
				
					<!-- Scroll to Top Button-->
			<a class="scroll-to-top rounded" href="#page-top"> <i
				class="fa fa-angle-up"></i>
			</a>
				
			</div>
		</div>
	</div>

</body>

</html>
