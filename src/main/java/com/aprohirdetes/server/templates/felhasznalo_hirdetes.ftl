<#include "html_header.ftl.html"/>
	<div class="container">
<#include "page_header_banner.ftl.html"/>
<#include "page_header.ftl.html"/>
		<div class="row">
			<div class="col-sm-3 col-md-2">
		
<#include "profil_menu.ftl.html"/>
				
			</div>
			<div class="col-sm-9 col-md-10">
			
				<h3>Hirdetés szerkesztése</h3>
					
				<#if hibaUzenet?? >
				<div class="alert alert-danger" role="alert">${hibaUzenet}</div>
				</#if>
				<#if uzenet?? >
				<div class="alert alert-success">${uzenet}</div>	
				</#if>
				
				<#if hirdetes?? >
				
				<form class="form-horizontal" method="post">
				
				<h3>Hirdetés adatai</h3>
					<input type="hidden" name="hirdetesId" value="${hirdetes.id}" />
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetesCim">Cím</label>
						<div class="col-sm-8">
							<input type="text" class="form-control" id="hirdetesCim" name="hirdetesCim" autofocus="" required="" value="${hirdetes.cim}" />
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetesHelyseg">Ország, megye, település</label>
						<div class="col-sm-7">
							<select class="form-control" id="hirdetesHelyseg" name="hirdetesHelyseg">
								<#list helysegList as helyseg>
								<#assign selected=''>
								<#if hirdetes.helysegUrlNev==helyseg.urlNev>
									<#assign selected=' selected'>
								</#if>
								<option value="${helyseg.urlNev}"${selected}>${helyseg.nev}</option>
									<#list helyseg.alhelysegList as alhelyseg>
									<#assign selected=''>
									<#if hirdetes.helysegUrlNev==alhelyseg.urlNev>
										<#assign selected=' selected'>
									</#if>
									<option value="${alhelyseg.urlNev}"${selected} style="font-weight: bold;">&nbsp;&nbsp;${alhelyseg.nev}</option>
										<#list alhelyseg.alhelysegList as alalhelyseg>
										<#assign selected=''>
										<#if hirdetes.helysegUrlNev==alalhelyseg.urlNev>
											<#assign selected=' selected'>
										</#if>
										<option value="${alalhelyseg.urlNev}"${selected}>&nbsp;&nbsp;&nbsp;&nbsp;${alalhelyseg.nev}</option>
										</#list>
									</#list>
								</#list>
							</select>
							<span class="help-block small">Válaszd ki a hozzád legközelebb eső települést! Kereséskor a nagyobb földrajzi egység magában foglalja a kisebbeket, tehát pl. Budapestet megadva keresési feltételként, az összes kerületbe feladott hirdetés meg fog jelenni.</span>
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdeteSzoveg">Hirdetés szövege</label>
						<div class="col-sm-10">
							<textarea class="form-control" rows="5" id="hirdetesSzoveg" name="hirdetesSzoveg">${(hirdetes.szoveg)!''}</textarea>
						</div>
					</div>
				
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetesEgyebInfo">Egyéb információ</label>
						<div class="col-sm-10">
							<input type="url" class="form-control" id="hirdetesEgyebInfo" name="hirdetesEgyebInfo" placeholder="http://" value="${(hirdetes.egyebInfo)!''}">
							<span class="help-block small">Pl. egy weboldal címe, ahol további részleteket talál az érdeklődő, vagy egy YouTube link, stb.</span>
						</div>
					</div>
				
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetesAr">Ár</label>
						<div class="col-sm-2">
							<div class="input-group">
								<input type="number" class="form-control" id="hirdetesAr" name="hirdetesAr" required="" value="${(hirdetes.ar)!'0'}">
								<span class="input-group-addon">Ft</span>
							</div>
						</div>
					</div>
					
				<h3>Hirdető adatai</h3>
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetoNev">Név</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" id="hirdetoNev" name="hirdetoNev" value="${(hirdetes.hirdeto.nev)!''}">
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetoTelefon">Telefonszám</label>
						<div class="col-sm-3">
							<input type="text" class="form-control" id="hirdetoTelefon" name="hirdetoTelefon" placeholder="+36-xx-xxxxxxx" value="${(hirdetes.hirdeto.telefon)!''}">
							<span class="help-block small">Telefonszám formátuma: +36-99-1234567</span>
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetoIranyitoSzam">Irányítószám</label>
						<div class="col-sm-2">
							<input type="text" class="form-control" id="hirdetoIranyitoSzam" name="hirdetoIranyitoSzam" value="${(hirdetes.hirdeto.iranyitoSzam)!''}">
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetoTelepules">Település</label>
						<div class="col-sm-4">
							<input type="text" class="form-control" id="hirdetoTelepules" name="hirdetoTelepules" value="${(hirdetes.hirdeto.telepules)!''}">
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetoCim">Cím</label>
						<div class="col-sm-7">
							<input type="text" class="form-control" id="hirdetoCim" name="hirdetoCim" value="${(hirdetes.hirdeto.cim)!''}">
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-2 control-label" for="hirdetoEmail">Email cím</label>
						<div class="col-sm-7">
							<input type="email" class="form-control" id="hirdetoEmail" name="hirdetoEmail" placeholder="@" required="" value="${(hirdetes.hirdeto.email)!''}">
						</div>
					</div>

					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-7">
							<button type="submit" class="btn btn-success btn-lg">Mentés</button>
						</div>
					</div>
				</form>
				
				</#if>
				
				<nav>
					<ul class="pager"><li class="previous"><a href="${app.contextRoot}/felhasznalo/hirdetesek">&larr; Vissza</a></li></ul>
				</nav>
				
			</div>
		</div>
<#include "page_footer.ftl.html"/>
	</div>
<#include "html_footer.ftl.html"/>
