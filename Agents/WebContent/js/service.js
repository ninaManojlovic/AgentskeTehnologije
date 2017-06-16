app.factory('service',['$http', function($http,$scope){
	var k={};
	//k.socket={};
	k.startAgent=function(name,type){
	
		console.log("usao u func u servisu: "+name+type);
		var port = window.location.port;
		var t= $http.get('/Agents/rest/agent/startAgentRest/'+type+'/'+name+'/'+port);
		//console.log("return iz servidsa: "+t);
		return t;
	
	}
	k.posaljiPoruku=function(tipPoruke,sender,receivers,content){
		
		console.log("posalji poruku u servisu");
		console.log(tipPoruke);
		console.log(sender);
		console.log(receivers);
		console.log(content);
	
		//var poruka={"sender":sender, "receivers":receivers, "content":content}
		//console.log(JSON.stringify(poruka));
		//var nesto=JSON.stringify(poruka);
		return $http.post('/Agents/rest/agent/sendMessage/'+tipPoruke+'/'+sender+'/'+receivers,content);
		
	}
	


	
		k.initSocket=function(){
			console.log("usao u initSocket");
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
				    var n=str.split("|");
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
				    
				     
				     
				    }else if(n[0]=="Inform"){
				    
				    	var konz= document.getElementById("konzola");
				    	konz.value+=n[1]
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
	
	k.startAgentWS=function(name,type){
		
			   var imeAgenta=name;//document.getElementById("imeAgenta").value;
			   var selekt=type;//document.getElementById("tipAgenta").value;
			   console.log("Pokupio ime: " + imeAgenta);
			   console.log("Pokupio tip " + selekt);
			   var domain = window.location.hostname;
			   var port = window.location.port;
			   var por="Create;"+imeAgenta+";"+selekt+";"+domain+";"+port;
			   console.log("start agent service: "+por)
			   socket.send(por);
			   return true;
	}	
		
	
	k.posaljiPorukuWS=function(pref,send,rec,con){
	
	
		var domain = window.location.hostname;
		var port = window.location.port;
		
		var poruka="Message;"+pref+";"+send+";"+rec+";"+con;
		socket.send(poruka);
		
	}
	k.getRunning=function(){
		console.log("running service");
		var n=$http.get('/Agents/rest/agent/runningAgents');
		return n;
	}
	return k;
}]);