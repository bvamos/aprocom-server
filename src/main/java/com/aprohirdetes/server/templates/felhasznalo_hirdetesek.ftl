<#include "html_header.ftl.html"/>
	<div class="container">
<#include "page_header_banner.ftl.html"/>
<#include "page_header.ftl.html"/>
		<div class="row">
			<div class="col-sm-3 col-md-2">
		
<#include "profil_menu.ftl.html"/>
				
			</div>
			<div class="col-sm-9 col-md-10">
			
				<h3>Hirdetéseim (${hirdetesList?size})</h3>
					
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
						<th>Feladva/Lejár</th>
						<th>Megjelent</th>
						<th>Műveletek</th>
					</tr>
					</thead>
					<tbody>
				<#list hirdetesList as hirdetes>
					<tr>
						<td><a href="${app.contextRoot}/hirdetes/${hirdetes.id}" target="_blank">${hirdetes.cim}</a></td>
						<td>${(hirdetes.egyebMezok.kategoria)!''}</td>
						<td align="right" nowrap>${hirdetes.ar} Ft</td>
						<td><small>${(hirdetes.egyebMezok.feladvaSzoveg)!''}<br>${(hirdetes.egyebMezok.lejarSzoveg)!''}</small></td>
						<#if hirdetes.hitelesitve>
						<td align="right">${hirdetes.megjelenes!'0'}</td>
						<#else>
						<td align="right">Nincs aktiválva</td>
						</#if>
						<td><span style="white-space:nowrap"><a href="${app.contextRoot}/felhasznalo/hirdetes/${hirdetes.id}"><i class="fa fa-edit"></i> Szerkesztés</a></span><br>
							<a href="${app.contextRoot}/hirdetes/${hirdetes.id}/torol"><i class="fa fa-trash-o"></i> Törlés</a><br>
							<span style="white-space:nowrap"><a href="${app.contextRoot}/hosszabbitas/${hirdetes.id}"><i class="fa fa-recycle"></i> Hosszabbítás</a></span>
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
