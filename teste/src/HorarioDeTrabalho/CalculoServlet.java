package HorarioDeTrabalho;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


public class CalculoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Obtendo os hor�rios de trabalho e marca��es  com request...
		String horariosJson = request.getParameter("horarios");
		String marcacoesJson = request.getParameter("marcacoes");
		if (horariosJson == null || marcacoesJson == null) {
			throw new IllegalArgumentException("Hor�rios ou marca��es n�o fornecidos.");
		}

		JSONArray horariosArray = new JSONArray(horariosJson);
		JSONArray marcacoesArray = new JSONArray(marcacoesJson);
		List<Intervalo> horariosDeTrabalho = new ArrayList<>();
		for (int i = 0; i < horariosArray.length(); i++) {
			JSONObject obj = horariosArray.getJSONObject(i);
			horariosDeTrabalho.add(new Intervalo(obj.getString("entrada"), obj.getString("saida")));
		}
		List<Intervalo> marcacoesFeitas = new ArrayList<>();
		for (int i = 0; i < marcacoesArray.length(); i++) {
			JSONObject obj = marcacoesArray.getJSONObject(i);
			marcacoesFeitas.add(new Intervalo(obj.getString("entrada"), obj.getString("saida")));
		}

		List<Intervalo> atrasos = calcularAtrasos(horariosDeTrabalho, marcacoesFeitas);
		List<Intervalo> horasExtras = calcularHorasExtras(horariosDeTrabalho, marcacoesFeitas);

		// Convertendo os resultados para JSON e enviando a resposta
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("atrasos", atrasos);
		jsonResponse.put("horasExtras", horasExtras);

		response.setContentType("application/json");
		response.getWriter().write(jsonResponse.toString());
	}

	private List<Intervalo> calcularAtrasos(List<Intervalo> horariosDeTrabalho, List<Intervalo> marcacoesFeitas) {
	    List<Intervalo> atrasos = new ArrayList<>();

	    for (Intervalo trabalho : horariosDeTrabalho) {
	        Intervalo uncovered = trabalho;
	        for (Intervalo marcacao : marcacoesFeitas) {
	            uncovered = subtractIntervals(uncovered, marcacao);
	            if (uncovered == null) {
	                break;
	            }
	        }
	        if (uncovered != null) {
	            atrasos.add(uncovered);
	        }
	    }

	    return atrasos;
	}

	private List<Intervalo> calcularHorasExtras(List<Intervalo> horariosDeTrabalho, List<Intervalo> marcacoesFeitas) {
	    List<Intervalo> horasExtras = new ArrayList<>();

	    for (Intervalo marcacao : marcacoesFeitas) {
	        boolean isOutsideAllWorkPeriods = true;
	        for (Intervalo trabalho : horariosDeTrabalho) {
	            if (intervalsOverlap(marcacao, trabalho)) {
	                isOutsideAllWorkPeriods = false;
	                break;
	            }
	        }
	        if (isOutsideAllWorkPeriods) {
	            horasExtras.add(marcacao);
	        }
	    }

	    return horasExtras;
	}

	private Intervalo subtractIntervals(Intervalo a, Intervalo b) {
	    int aStart = convertToMinutes(a.getInicio());
	    int aEnd = convertToMinutes(a.getFim());
	    int bStart = convertToMinutes(b.getInicio());
	    int bEnd = convertToMinutes(b.getFim());

	    if (aEnd <= bStart || aStart >= bEnd) {
	        // Os intervalos n�o se sobrep�em
	        return a;
	    }

	    if (aStart < bStart && aEnd > bEnd) {
	        // A marca��o est� completamente dentro do hor�rio de trabalho
	        return null;
	    }

	    if (aStart < bStart) {
	        return new Intervalo(minutesToTime(aStart), minutesToTime(bStart));
	    }

	    if (aEnd > bEnd) {
	        return new Intervalo(minutesToTime(bEnd), minutesToTime(aEnd));
	    }

	    return null;
	}


	private String minutesToTime(int minutes) {
	    int hours = minutes / 60;
	    minutes %= 60;
	    return String.format("%02d:%02d", hours, minutes);
	}




	// M�todos auxiliares
	private int convertToMinutes(String time) {
		if (time == null || time.isEmpty()) {
			throw new IllegalArgumentException("O tempo fornecido � inv�lido: " + time);
		}
		String[] parts = time.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("O formato do tempo � inv�lido: " + time);
		}
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		return hours * 60 + minutes;
	}

	private boolean intervalsOverlap(Intervalo a, Intervalo b) {
		int aStart = convertToMinutes(a.getInicio());
		int aEnd = convertToMinutes(a.getFim());
		int bStart = convertToMinutes(b.getInicio());
		int bEnd = convertToMinutes(b.getFim());

		return aStart < bEnd && bStart < aEnd;
	}
	

	// Classe auxiliar Intervalo
	public static class Intervalo {
		private String inicio;
		private String fim;

		public Intervalo(String inicio, String fim) {
			this.inicio = inicio;
			this.fim = fim;
		}

		public String getInicio() {
			return inicio;
		}

		public void setInicio(String inicio) {
			this.inicio = inicio;
		}

		public String getFim() {
			return fim;
		}

		public void setFim(String fim) {
			this.fim = fim;
		}
	}
}
