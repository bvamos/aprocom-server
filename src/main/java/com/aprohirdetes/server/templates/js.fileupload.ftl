	<script src="${app.contextRoot}/js/vendor/jquery.ui.widget.js"></script>
	<script src="${app.contextRoot}/js/jquery.iframe-transport.js"></script>
	<script src="${app.contextRoot}/js/jquery.fileupload.js"></script>
    <script type="text/javascript">
	$(function () {
	    $('#fileupload').fileupload({
	        dataType: 'json',
	        singleFileUploads: false,
	        /*add: function (e, data) {
	        	$('#progress .bar').css('width', '0%');
	        },*/
	        done: function (e, data) {
	        	$.each(data.result.files, function (index, file) {
	               $('#hirdetesKep' + file.sorszam).attr('src', file.url);
	               $('#uploadMessage').value = 'A kep sikeresen feltöltődött: ' + file.eredetiFileNev;
	            });
	        },
	    	progressall: function (e, data) {
		        var progress = parseInt(data.loaded / data.total * 100, 10);
		        $('#progress .progress-bar').css(
		            'width',
		            progress + '%'
		        );
		    }
	    });
	});
	</script>
