<#include "html_header.ftl.html"/>
	<div class="container">
<#include "page_header_banner.ftl.html"/>
<#include "page_header.ftl.html"/>
		<div class="row">
			<div class="col-sm-3 col-md-2">
		
<#include "profil_menu.ftl.html"/>
				
			</div>
			<div class="col-sm-9 col-md-10">
			
				<h3>Kedvenc Hirdetéseim (${hirdetesList?size})</h3>
					
				<#if uzenet?? >
				<div class="alert alert-success">${uzenet}</div>
				</#if>
				<#if hibaUzenet?? >
				<div class="alert alert-danger">${hibaUzenet}</div>
				</#if>
					
				<table class="table table-striped">
					<thead>
					<tr>
						<th>Cím</th>
						<th>Kategória</th>
						<th>Ár</th>
						<th>Feladva</th>
						<th>Műveletek</th>
					</tr>
					</thead>
					<tbody>
				<#list hirdetesList as hirdetes>
					<tr>
						<td><a href="${app.contextRoot}/hirdetes/${hirdetes.id}" target="_blank">${hirdetes.cim}</a></td>
						<td>${(hirdetes.egyebMezok.kategoria)!''}</td>
						<td align="right" nowrap>${hirdetes.ar} Ft</td>
						<td><small>${(hirdetes.egyebMezok.feladvaSzoveg)!''}</small></td>
						<td><a href="javascript:torolKedvenc('${hirdetes.id}');"><i class="fa fa-trash-o"></i> Törlés</a>
						</td>
					</tr>
				</#list>
					</tbody>
				</table>
			</div>
		</div>
<#include "page_footer.ftl.html"/>
	</div>
<#include "html_footer.ftl.html"/>
