<form id="adv_search_container" role="form" method="get">

<div class="form-group">
	<label class="control-label">Ár</label>
	<div class="form-group">
		<div class="input-group">
			<input type="number" class="form-control input-sm" id="ar_min" name="ar_min" value="${ar_min!''}" placeholder="-tól" min="0">
			<span class="input-group-addon">Ft</span>
		</div>
	</div>
</div>

<div class="form-group">
	<div class="input-group">
		<input type="number" class="form-control input-sm" id="ar_max" name="ar_max" value="${ar_max!''}" placeholder="-ig" min="0">
		<span class="input-group-addon">Ft</span>
	</div>
</div>
	
${egyebAttributumokHtml!''}

<button type="button" class="btn btn-success" id="btnAdvKereses">Keresés</button>
						
</form>
<script type="text/javascript">
$('#btnAdvKereses').click(function(){
	$('#btnKereses').click();
});
</script>