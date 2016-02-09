<#macro keresesStatusz kod>
	<#if kod=1>Aktív</#if>
	<#if kod=2>Inaktív</#if>
	<#if kod=99>Törölve</#if>
</#macro>

<#macro keresesGyakorisag kod>
	<#if kod=1>Soha</#if>
	<#if kod=11>Azonnal</#if>
	<#if kod=21>Naponta</#if>
	<#if kod=22>Hetente</#if>
	<#if kod=23>Havonta</#if>
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
			
				<h3>Mentett kereséseim</h3>
				
				<ul class="nav nav-tabs">
					<li role="presentation" class="<#if keresesAktiv==true>active</#if>"><a href="${app.contextRoot}/felhasznalo/keresesek/aktiv">Aktív kereséseim<#if keresesAktiv=true> (${keresesList?size})</#if></a></li>
					<li role="presentation" class="<#if keresesAktiv==false>active</#if>"><a href="${app.contextRoot}/felhasznalo/keresesek/inaktiv">Inaktív kereséseim<#if keresesAktiv=false> (${keresesList?size})</#if></a></li>
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
						<th>Név</th>
						<th>Küldés gyakorisága</th>
						<th>Utolsó küldés</th>
						<!--th>Feltételek</th-->
						<th>Műveletek</th>
					</tr>
					</thead>
					<tbody>
				<#list keresesList as kereses>
					<tr>
						<td><a href="${app.contextRoot}${kereses.url!''}" target="_blank">${kereses.nev}</a></td>
						<td><@keresesGyakorisag kod=kereses.kuldesGyakorisaga /></td>
						<td><#if (kereses.kuldesGyakorisaga>1)>${kereses.utolsoKuldes?datetime}</#if></td>
						<!--td></td-->
						<td><a href="${app.contextRoot}/felhasznalo/kereses/${kereses.id}/torol"><i class="fa fa-trash-o"></i> Törlés</a><br>
							<#if keresesAktiv==true && (kereses.kuldesGyakorisaga>1)><span style="white-space:nowrap"><a href="${app.contextRoot}/felhasznalo/kereses/${kereses.id}/kikapcsol"><i class="fa fa-recycle"></i> Kikapcsolás</a></span></#if>
							<#if keresesAktiv!=true && (kereses.kuldesGyakorisaga>1)><span style="white-space:nowrap"><a href="${app.contextRoot}/felhasznalo/kereses/${kereses.id}/bekapcsol"><i class="fa fa-recycle"></i> Bekapcsolás</a></span></#if>
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
