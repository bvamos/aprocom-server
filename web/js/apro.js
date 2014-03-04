	
function submitKereses() {
	var kform = document.forms.KeresesForm;
	if(typeof p === 'undefined') p = 1;
	
	var kulcsszo = kform.hirdetesKulcsszo.value;
	var tipus = kform.hirdetesTipus.value;
	var helyseg = kform.hirdetesHelyseg.value;
	var kategoria = kform.hirdetesKategoria.value;
	
	kform.action = appContext + '/kereses/' + tipus + '/' + helyseg + '/' + kategoria + '/?';
	if(kulcsszo.length>0) kform.action = kform.action + 'q=' + kulcsszo;
	if(p>1) kform.action = kform.action + '&p=' + p;
	
	window.location = kform.action;
	return false;
}

function setKategoria(kategoria) {
	var kform = document.forms.KeresesForm;
	selectOption(kform.hirdetesKategoria, kategoria);
	submitKereses();
}

function nextPage() {
	p = p+1;
	submitKereses();
}

function prevPage() {
	if(p>1) {
		p--;
		submitKereses();
	}
}

function selectOption(select, option) {
	for(var i=0; i<select.options.length; i++) {
		if(select.options[i].value == option) {
			select.options[i].selected = true;
			break;
		}
	}
}