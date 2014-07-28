function setKategoria(kategoria) {
	var kform = document.forms.KeresesForm;
	selectOption(kform.hirdetesKategoria, kategoria);
	$('#btnKereses').click();
}

function selectOption(select, option) {
	for(var i=0; i<select.options.length; i++) {
		if(select.options[i].value == option) {
			select.options[i].selected = true;
			break;
		}
	}
}
