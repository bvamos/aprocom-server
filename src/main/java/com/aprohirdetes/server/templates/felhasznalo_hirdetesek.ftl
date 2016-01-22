<#macro hirdetesStatusz kod>
	<#if kod=1>Új</#if>
	<#if kod=11>Hitelesítve</#if>
	<#if kod=21>Aktív</#if>
	<#if kod=31>Törölve (Lejárt)</#if>
	<#if kod=32>Inaktív</#if>
	<#if kod=33>Törölve (Eladva)</#if>
</#macro>

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
				
				<ul class="nav nav-tabs">
					<li role="presentation" class="<#if hirdetesAktiv==true>active</#if>"><a href="${app.contextRoot}/felhasznalo/hirdetesek/aktiv">Aktív hirdetéseim<#if hirdetesAktiv=true> (${hirdetesList?size})</#if></a></li>
					<li role="presentation" class="<#if hirdetesAktiv==false>active</#if>"><a href="${app.contextRoot}/felhasznalo/hirdetesek/inaktiv">Inkatív hirdetéseim<#if hirdetesAktiv=false> (${hirdetesList?size})</#if></a></li>
				</ul>
				
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
						<th>Státusz</th>
						<th title="Megjelenések száma">Megj.</th>
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
						<td><@hirdetesStatusz kod=hirdetes.statusz /></td>
						<#if hirdetes.statusz!=1>
						<td align="right">${hirdetes.megjelenes!'0'}</td>
						<#else>
						<td align="right">Nincs aktiválva</td>
						</#if>
						<td><span style="white-space:nowrap"><a href="${app.contextRoot}/felhasznalo/hirdetes/${hirdetes.id}"><i class="fa fa-edit"></i> Szerkesztés</a></span><br>
							<#if hirdetesAktiv==true><a href="${app.contextRoot}/hirdetes/${hirdetes.id}/torol"><i class="fa fa-trash-o"></i> Törlés</a><br></#if>
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
