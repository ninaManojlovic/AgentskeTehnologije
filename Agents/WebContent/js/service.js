app.factory('service',['$http', function($http){
	var k={};
	
	k.startAgent=function(name,type){
		console.log("usao u func u servisu: "+name+type);
		return $http.get('/Agents/rest/agent/startAgent/'+type+'/'+name);
	}
	
	return k;
}]);