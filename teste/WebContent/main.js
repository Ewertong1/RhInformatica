$(document).ready(function() {
	 //limpar campos
    $('#limparHorarioBtn').click(function(event) {
        event.preventDefault();
        $('#horarioTbody').empty();
        return;
    });

    $('#limparMarcacaoBtn').click(function(event) {
        event.preventDefault();
        $('#marcacaoTbody').empty();
        return;
    });
    $('#limparResultadosBtn').click(function(event) {
        event.preventDefault();
        $('#atrasoTbody').empty();
        $('#extraTbody').empty();
        return;
    });
    	//adiciona Horario de Trabalho
    $('#addHorarioBtn').click(function(event) {
        event.preventDefault();
        let entrada = $('#entradaHorario').val();
        let saida = $('#saidaHorario').val();

        if (!isValidTimeRange(entrada, saida)) {
            alert("Por favor, insira um intervalo de tempo válido.");
            return;
        }

        if ($('#horarioTbody tr').length >= 3) {
            alert("Você só pode adicionar até 3 horários de trabalho.");
            return;
        }

        $('#horarioTbody').append('<tr><td>' + entrada + '</td><td>' + saida + '</td></tr>');
        $('#entradaHorario').val('');
        $('#saidaHorario').val('');
    });
    //adiciona horario de marcação
    $('#addMarcacaoBtn').click(function(event) {
        event.preventDefault();
        let entrada = $('#entradaMarcacao').val();
        let saida = $('#saidaMarcacao').val();

        if (!isValidTimeRange(entrada, saida)) {
            alert("Por favor, insira um intervalo de tempo válido.");
            return;
        }

        $('#marcacaoTbody').append('<tr><td>' + entrada + '</td><td>' + saida + '</td></tr>');
        $('#entradaMarcacao').val('');
        $('#saidaMarcacao').val('');
    });
    //começa o calculo de atraso e horas extras
    $('#calcularBtn').click(function() {
        let horarios = [];
        let marcacoes = [];

        $('#horarioTbody tr').each(function() {
            let entrada = $(this).find('td').eq(0).text();
            let saida = $(this).find('td').eq(1).text();
            horarios.push({entrada: entrada, saida: saida});
        });

        $('#marcacaoTbody tr').each(function() {
            let entrada = $(this).find('td').eq(0).text();
            let saida = $(this).find('td').eq(1).text();
            marcacoes.push({entrada: entrada, saida: saida});
        });

        $.get("/teste/CalculoServlet", {
            horarios: JSON.stringify(horarios),
            marcacoes: JSON.stringify(marcacoes)
        }, function(response) {
            // Atualizando a tabela de atrasos
            $('#atrasoTbody').empty();
            response.atrasos.forEach(function(interval) {
                $('#atrasoTbody').append('<tr><td>' + interval.inicio + '</td><td>' + interval.fim + '</td></tr>');
            });

            // Atualizando a tabela de horas extras
            $('#extraTbody').empty();
            response.horasExtras.forEach(function(interval) {
                $('#extraTbody').append('<tr><td>' + interval.inicio + '</td><td>' + interval.fim + '</td></tr>');
            });
        });
    });
    

    function isValidTimeRange(entrada, saida) {
        let [eh, em] = entrada.split(":").map(Number);
        let [sh, sm] = saida.split(":").map(Number);

        if (eh > sh) return false;
        if (eh === sh && em >= sm) return false;

        return true;
    }
   
});
