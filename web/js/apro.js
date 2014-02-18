function SearchCtrl($scope, $http) {
	$scope.submitKereses = function() {
		$http.get('kereses/'+$scope.hirdetesTipus+'/?q='+$scope.hirdetesKulcsszo);
	};
}

function submitKereses(tipus, helyseg, kategoria) {
	document.forms.KeresesForm.action = 'kereses/' + tipus + '/' + helyseg + '/' + kategoria + '/';
	document.forms.KeresesForm.submit();
}