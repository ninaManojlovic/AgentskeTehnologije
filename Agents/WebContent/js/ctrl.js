app.controller('ctrl', function ($scope,$rootScope,$window, $http, service) {
	
	$scope.startAgent=function(){
		console.log("usao u func u ctrl");
		service.startAgent($scope.name,$scope.type)
		.success(function(data){
   
			$scope.agent=data;
			console.log($scope.agent);
			})
	}
});