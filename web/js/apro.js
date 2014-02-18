function SearchCtrl($scope, $http) {
	$scope.submitKereses = function() {
		$http.get('kereses/'+$scope.hirdetesTipus+'/?q='+$scope.hirdetesKulcsszo);
	};
}

function submitKereses(tipus, helyseg) {
	document.forms.KeresesForm.action = 'kereses/' + tipus + '/' + helyseg + '/';
	document.forms.KeresesForm.submit();
}