function soket(){
	
	console.log("usao u novi initSocket");
	document.getElementById("rest").disabled = true;
	 
	 var host = "ws://localhost:"+window.location.port+"/Agents/websocket";
	 var poruka="";
	  try {
	   socket = new WebSocket(host);
	   console.log('connect. Socket Status: ' + socket.readyState + "\n");

	   socket.onopen = function() {
	    console.log('onopen. Socket Status: ' + socket.readyState
	      + ' (open)\n');
	    poruka='onopen. Socket Status: ' + socket.readyState
	    + ' (open)\n'
	   }

		socket.onmessage = function(msg) {
		    //var n=(msg.toString()).split(";");
		    var str=msg.data.toString();
		    var n=str.split(";");
		    console.log("ONMESSAGEEEE " + n[0]);
		   // n.push(msg.split(';'))
		    if(n[0]=="Create"){
		     
		     console.log('onmessage. Received: ' + n[1] + "\n");
		     var currentdate = new Date();
		     var konz= document.getElementById("konzola");
		     konz.value+=currentdate.getHours() + ":" 
		     + currentdate.getMinutes() + ":" + currentdate.getSeconds()+" - " +n[1] + "\n";
		     console.log('KonZOLAaa ' + konz.value);
		    }else if(n[0]=="Refresh"){
		     
		     var lista=n[1];
		     console.log("ONO STO SAM DOBILA: " + lista);
		     
		     return lista;
		     //$scope.agents=lista;
		     //var split=lista.split(',');
		     //console.log("SPLIT: " + split);
		    /* var sel=document.getElementById("posiljalac");
		     var select = sel,
		        opt = document.createElement("option");
		     opt.value = "value";
		     opt.textContent = "text to be displayed";
		     select.appendChild(opt)*/
		     
		     
		    }
		    
		   }

	   socket.onclose = function() {
	    console.log('onsclose. Socket Status: ' + socket.readyState
	      + ' (Closed)\n');
	    socket = null;
	   }

	  } catch (exception) {
	   console.log('Error' + exception + "\n");
	  }
}