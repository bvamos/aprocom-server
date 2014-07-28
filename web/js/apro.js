function submitKereses() {
	var kform = document.forms.KeresesForm;
	if(typeof window.p === 'undefined') window.p = 1;
	
	var kulcsszo = kform.hirdetesKulcsszo.value;
	var tipus = kform.hirdetesTipus.value;
	var helyseg = kform.hirdetesHelyseg.value;
	var kategoria = kform.hirdetesKategoria.value;
	
	kform.action = appContext + '/kereses/' + tipus + '/' + helyseg + '/' + kategoria + '/?';
	if(kulcsszo.length>0) kform.action = kform.action + 'q=' + kulcsszo;
	if(p>1) kform.action = kform.action + '&p=' + window.p;
	if(this.s>0) kform.action = kform.action + '&s=' + window.s;
	if(kform.keresesArMin.value.length>0) kform.action = kform.action + '&arMin=' + kform.keresesArMin.value;
	if(kform.keresesArMax.value.length>0) kform.action = kform.action + '&arMax=' + kform.keresesArMax.value;
	
	window.location = kform.action;
	return false;
}

function setKategoria(kategoria) {
	var kform = document.forms.KeresesForm;
	selectOption(kform.hirdetesKategoria, kategoria);
	submitKereses();
}

function selectOption(select, option) {
	for(var i=0; i<select.options.length; i++) {
		if(select.options[i].value == option) {
			select.options[i].selected = true;
			break;
		}
	}
}
