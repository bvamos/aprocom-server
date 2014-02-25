function SearchCtrl($scope, $http) {
	$scope.submitKereses = function() {
		$http.get('kereses/'+$scope.hirdetesTipus+'/?q='+$scope.hirdetesKulcsszo);
	};
}
