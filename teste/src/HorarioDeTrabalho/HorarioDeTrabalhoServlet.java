package HorarioDeTrabalho;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HorarioDeTrabalhoServlet extends HttpServlet {
    private static List<String[]> horarios = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entrada = req.getParameter("entrada");
        String saida = req.getParameter("saida");

        getHorarios().add(new String[]{entrada, saida});

        // Retorna uma resposta de sucesso
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"success\"}");
    }

	public static List<String[]> getHorarios() {
		return horarios;
	}

	public static void setHorarios(List<String[]> horarios) {
		HorarioDeTrabalhoServlet.horarios = horarios;
	}
}
