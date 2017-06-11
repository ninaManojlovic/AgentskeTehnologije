app.controller('ctrl', function ($scope,$rootScope,$window, $http, service) {
	
	$scope.radiRest=function(){
		$scope.rest=true;
		$scope.websocket=false;
	}
	
	$scope.radiWebsocket=function(){
		$scope.rest=false;
		$scope.websocket=true;
	}
	
	$scope.initSocket=function(){
		service.initSocket();
	}
	
	$scope.startAgent=function(){
		if($scope.rest==true){
		console.log("usao u func u ctrl");
		service.startAgent($scope.name,$scope.type);
		$scope.getRunning();
		}else{
			console.log("ws za start agenta");
			service.startAgentWS($scope.name,$scope.type);
			$scope.getRunning();
		}
	}
	
	$scope.getRunning=function(){
		console.log("refresh");
		service.getRunning()
		.success(function(data){
			$scope.agents=data;
			console.log($scope.agents);
		})
	}
	
	$scope.posaljiPoruku=function(){
		if($scope.rest==true){
		console.log("usao u posalji poruku u kontroleru");
		
		
		service.posaljiPoruku($scope.tipPoruke,$scope.sender,$scope.receivers,$scope.content)
		.success(function(data){
			   
		//	$scope.agents=data;
			//console.log($scope.agents);
			})
			$scope.poruka="";
	}else{
		console.log("ws posalji poruku")//+$scope.receivers.aid.name+" port: "+$scope.receivers.aid.host.port);
		service.posaljiPorukuWS($scope.tipPoruke,$scope.sender,$scope.receivers,$scope.content)
	}
	}
});