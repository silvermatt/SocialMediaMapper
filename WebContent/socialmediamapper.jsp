<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Social Media Mapper</title>


<script type="text/javascript"
    src="http://maps.google.com/maps/api/js?sensor=false">
</script>

<script type="text/javascript">
 
function getXMLObject()  //XML OBJECT
{
   var xmlHttp = false;
   try {
     xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")  // For Old Microsoft Browsers
   }
   catch (e) {
     try {
       xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")  // For Microsoft IE 6.0+
     }
     catch (e2) {
       xmlHttp = false   // No Browser accepts the XMLHTTP Object then false
     }
   }
   if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
     xmlHttp = new XMLHttpRequest();        //For Mozilla, Opera Browsers
   }
   return xmlHttp;  // Mandatory Statement returning the ajax object created
}
 
var xmlhttp = new getXMLObject();	//xmlhttp holds the ajax object

function initMap() {
	if (document.myForm.mode.checked) {
		if (xmlhttp) {
			//get from myFrom
			var str = "query?mode=init";
			xmlhttp.open("POST",str,true);
			xmlhttp.onreadystatechange = handleMapResponse;
			xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		    xmlhttp.send(null);
		}	
	}
}

function ajaxLoadMap() {
	if (xmlhttp) {
		//get from myFrom
		var str = "query?queryStr="+document.myForm.queryStr.value;
		if (document.myForm.mode.checked)
			str+="&mode=map";
		str+= "&services=";
		if (document.myForm.services[0].checked)
			str+="twitter&services=";
		if (document.myForm.services[1].checked)
			str+="flickr";
		xmlhttp.open("POST",str,true);
		xmlhttp.onreadystatechange = handleMapResponse;
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	    xmlhttp.send(null);
	}
} 

function handleMapResponse() {
	if (xmlhttp.readyState == 4) {
		if (xmlhttp.status == 200) {
		       document.getElementById("map_canvas").innerHTML=xmlhttp.responseText; //Update the HTML Form element 
		       
		       //display map
		       if (document.myForm.mode.checked) {
			       var ob = document.getElementsByTagName("script");
			       for(var i=0; i<ob.length-1; i++){
			       		if(ob[i+1].text!=null) eval(ob[i+1].text);
			       }
		       }
		}
	     else {
	        alert("Error during AJAX call. Please try again");
	     }
	}
}

</script>

</head>

<body onload="initMap()" >



<p>Hello! Enter your search term: 


 <form name="myForm">
    <div><textarea name="queryStr" rows="1" cols="60"></textarea></div><br />
    <div>
    	<input type="checkbox" name="services" value="twitter" checked="checked" /> Search Twitter<br />
		<input type="checkbox" name="services" value="flickr" checked="checked" /> Search Flickr <br /> 
    </div>
    <div>
    	<input type="checkbox" name="mode" value="map" checked="checked" />Display on Map <br />
    </div>
  </form>
  <form>
    <div><input type="button" value="Search" onClick="javascript:ajaxLoadMap()"/></div>
  </form>


  <div id="map_canvas" style="width:600px; height:480px"></div>

  
  </body>
</html>