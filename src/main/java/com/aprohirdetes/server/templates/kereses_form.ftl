
<form id="adv_search_container" role="form" method="get">

<div class="form-group">
	<label class="control-label">Ár</label>
	<div class="form-group">
		<div class="input-group">
			<input type="number" class="form-control input-sm" id="ar_min" name="ar_min" value="${ar_min!''}" placeholder="-tól" min="0" onChange="$('#btnKereses').click();">
			<span class="input-group-addon">Ft</span>
		</div>
	</div>
</div>

<div class="form-group">
	<div class="input-group">
		<input type="number" class="form-control input-sm" id="ar_max" name="ar_max" value="${ar_max!''}" placeholder="-ig" min="0" onChange="$('#btnKereses').click();">
		<span class="input-group-addon">Ft</span>
	</div>
</div>
	
${egyebAttributumokHtml!''}

<button type="button" class="btn btn-success" id="btnAdvKereses">Keresés</button>
<#if session?? >
<button type="button" class="btn btn-success" id="btnKeresesMentes" data-toggle="modal" data-target="#keresesMentesModal"><i class="fa fa-save"></i></button>
</#if>
</form>
<script type="text/javascript">
$('#btnAdvKereses').click(function(){
	$('#btnKereses').click();
});
<#if session?? >

function mentKereses() {
	var data = {"nev":$('#keresesNev').val(), "url":window.location.pathname+window.location.search, "kuldesGyakorisaga":$('#kuldesGyakorisaga').val()};
	
	$.ajax({
        type: "POST",
        url: "${app.contextRoot}/api/v1/keresesek",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json"
	}).done(function(data){
		//alert(JSON.stringify(data));
		var id = data.keresesId;
		alert("A keresest sikeresen elmentettük.");
		$('#keresesMentesModal').modal('hide');
    }).fail(function(jqXHR, textStatus) {
        alert(textStatus);
    });
}
</#if>
</script>