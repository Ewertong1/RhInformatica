package HorarioDeTrabalho;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarcacoesFeitasServlet extends HttpServlet {
    private static List<String[]> marcacoes = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entrada = req.getParameter("entrada");
        String saida = req.getParameter("saida");

        getMarcacoes().add(new String[]{entrada, saida});

        // Retorna uma resposta de sucesso
        resp.setContentType("application/json");
        resp.getWriter().write("{\"status\":\"success\"}");
    }

	public static List<String[]> getMarcacoes() {
		return marcacoes;
	}

	public static void setMarcacoes(List<String[]> marcacoes) {
		MarcacoesFeitasServlet.marcacoes = marcacoes;
	}
}
