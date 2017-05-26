app.factory('service',['$http', function($http,$scope){
	var k={};
	
	k.startAgent=function(name,type){
	
		console.log("usao u func u servisu: "+name+type);
		var t= $http.get('/Agents/rest/agent/startAgent/'+type+'/'+name);
		console.log("return iz servidsa: "+t);
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
		return $http.post('/Agents/rest/agent/sendMessage/'+tipPoruke+'/'+sender+'/'+receivers+'/'+content);
		
	}
	
/*	return $http({
		method: 'POST',
		url: '/Agents/rest/agent/sendMessage',
		headers: {'Content-Type' : 'application/json'},
		data: nesto,
		dataType:"json"
		
	});*/
		
		k.initSocket=function(){
			document.getElementById("rest").disabled = true;
			 
			 var host = "ws://localhost:8090/Agents/websocket";
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
	
	k.startAgentWS=function(name,type){
		
			   var imeAgenta=name;//document.getElementById("imeAgenta").value;
			   var selekt=type;//document.getElementById("tipAgenta").value;
			   console.log("Pokupio ime: " + imeAgenta);
			   console.log("Pokupio tip " + selekt);
			   var domain = window.location.hostname;
			   var port = window.location.port;
			   var por="Create;"+imeAgenta+";"+selekt+";"+domain+";"+port;
			   socket.send(por);
	}	
		
	
	k.posaljiPorukuWS=function(pref,send,rec,con){
	
	
		var domain = window.location.hostname;
		var port = window.location.port;
		
		var poruka="Message;"+pref+";"+send+";"+rec+";"+con;
		socket.send(poruka);
		
	}
	k.getRunning=function(){
		return $http.get('/Agents/rest/agent/runningAgents')
	}
	return k;
}]);