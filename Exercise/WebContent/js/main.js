// The root URL for the RESTful services
var rootURL = "http://localhost:8080/exercise/rest/private";

$("#datiClienteForm").on("submit", function (e) {
	console.log('bonificoPrepare');
	e.preventDefault();
	$.ajax({
		type: 'GET',
		url: rootURL + '/cliente/' + $("#idCliente").val() + '/conto/' + $("#idConto").val() + '/bonifico/prepare',
		dataType: "json",
		success: function(data) {
			okBonificoPrepare(data);
			return false;
		},
		error: function(){
			console.error("Errore chiamata REST!");
			return false;
		}
	});
});

function okBonificoPrepare(res) {
	// Change page
	$('#sceltaCliente').hide();
	$('#preparaBonifico').show();
	// Save data
	$("#idClienteDato").text($("#idCliente").val());
	$("#idContoDato").text($("#idConto").val());
	$('#dataBonificoInizio').text(formatDate(res.data.oggi));
	$('#dataBonificoFine').text(formatDate(res.data.dataLimite));
}

$("#preparaBonificoForm").on("submit", function (e) {
	console.log('eseguiBonifico');
	e.preventDefault();
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: rootURL + '/cliente/' + $("#idClienteDato").text() + '/conto/' + $("#idContoDato").text() + '/bonifico/verify',
		dataType: "json",
		data: formToJSON(),
		success: function(data){
			okBonificoVerify(data);
			return false;
		},
		error: function(){
			console.error("Errore chiamata REST!");
			return false;
		}
	});
});

function okBonificoVerify(res) {
	// Change page
	if(res.result.outcome == "SUCCESS") {
		$('#preparaBonifico').hide();
		$('#confermaBonifico').show();
		// Save data
		$('#idImportoDato').text(res.data.totalAmount.amount + " " + res.data.totalAmount.currency);
		$('#idCommissioniDato').text(res.data.commissions.amount + " " + res.data.commissions.currency);
		$('#idDataEsecuzioneDato').text($('#dataEsecuzione').val());
		$('#idBeneficiarioDato').text($('#beneficiario').val());
		$('#idIbanDato').text($('#iban').val());
		$('#idCausaleDato').text($('#causale').val());
		$('#transazione').text(res.transaction.id);
	} else {
		var messages = res.result.messages == null ? [] : (res.result.messages instanceof Array ? res.result.messages : [res.result.messages]);
		$('#listaMessaggiBonificoVerify li').remove();
		$.each(messages, function(index, message) {
			$('#listaMessaggiBonificoVerify').append('<li>' + message + '</li>');
		});
	}
}

$("#confermaBonificoForm").on("submit", function (e) {
	console.log('bonificoExecute');
	e.preventDefault();
	$.ajax({
		type: 'PUT',
		url: rootURL + '/cliente/' + $("#idClienteDato").text() + '/conto/' + $("#idContoDato").text() + '/bonifico/' + $("#transazione").text() + '/execute',
		dataType: "json",
		success: function(data) {
			okBonificoExecute(data);
			return false;
		},
		error: function(){
			console.error("Errore chiamata REST!");
			return false;
		}
	});
});

function okBonificoExecute(res) {
	// Change page
	$('#confermaBonifico').hide();
	$('#bonificoEseguito').show();
	// Save data
	$('#cro').text(res.data.cro);
	var messages = res.result.messages == null ? [] : (res.result.messages instanceof Array ? res.result.messages : [res.result.messages]);
	$('#listaMessaggi li').remove();
	$.each(messages, function(index, message) {
		$('#listaMessaggi').append('<li>' + message + '</li>');
	});
}

function formToJSON() {
	return JSON.stringify({
		"importo": {
			"amount": $('#importo').val(), 
			"currency": $('#valuta').val()
		}, 
		"dataEsecuzione": $('#dataEsecuzione').val(),
		"nominativoBeneficiario": $('#beneficiario').val(),
		"ibanBeneficiario": $('#iban').val(),
		"causaleBonifico": $('#causale').val()
		});
}

function formatDate(data) {
	var dateString = data.substr(0,10).split("-");
	return dateString[2] + "/" + dateString[1] + "/" + dateString[0];
}
