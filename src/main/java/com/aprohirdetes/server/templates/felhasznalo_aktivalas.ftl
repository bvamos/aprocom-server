<#include "html_header.ftl.html"/>
	<div class="container">
<#include "page_header_banner.ftl.html"/>
<#include "page_header.ftl.html"/>
		<div class="row">
			<h3>Felhasználói fiók aktiválása</h3>
			
				<#if hibaUzenet?? >
				<div class="alert alert-danger">${hibaUzenet}</div>
				</#if>
				<#if uzenet?? >
				<div class="alert alert-success">${uzenet}</div>
				</#if>
				
			</form>
		</div>
<#include "page_footer.ftl.html"/>
	</div>
<#include "html_footer.ftl.html"/>
