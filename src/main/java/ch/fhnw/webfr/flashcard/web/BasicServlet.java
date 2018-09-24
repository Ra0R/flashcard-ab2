package ch.fhnw.webfr.flashcard.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.webfr.flashcard.domain.Questionnaire;
import ch.fhnw.webfr.flashcard.persistence.QuestionnaireRepository;
import ch.fhnw.webfr.flashcard.util.QuestionnaireInitializer;

@SuppressWarnings("serial")
public class BasicServlet extends HttpServlet {
	
	/*
	 * Attention: This repository will be used by all clients, concurrency
	 * could be a problem. THIS VERSION IS NOT PRODUCTION READY!
	 */
	private QuestionnaireRepository questionnaireRepository;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html; charset=utf-8");

		String[] pathElements = request.getRequestURI().split("/");
		
		if (isLastPathElementQuestionnaires(pathElements)) {
			
			handleQuestionnairesRequest(request, response);
			
		} else if(is2ndLastPathElementQuestionnaire(pathElements)) {
            handelQuestionnaireRequest(request,response);

        }else{
			handleIndexRequest(request, response);
		}
	}

	private boolean isLastPathElementQuestionnaires(String[] pathElements) {
		
		String last = pathElements[pathElements.length - 1];
		
		return last.equals("questionnaires");
	}
	private boolean is2ndLastPathElementQuestionnaire(String[] pathElements){
        String last = pathElements[pathElements.length - 2];
        return last.equals("questionnaires");
    }

	private void handleQuestionnairesRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		List<Questionnaire> questionnaires = questionnaireRepository.findAll();
		
		PrintWriter writer = response.getWriter();
		writer.append("<html><head><title>Example</title></head><body>");
		writer.append("<h3>Fragebögen</h3>");
		
		for (Questionnaire questionnaire : questionnaires) {
			
			String url = request.getContextPath() + request.getServletPath();
			url = url + "/questionnaires/" + questionnaire.getId().toString();
			
			writer.append("<p><a href='" + response.encodeURL(url) + "'>" + questionnaire.getTitle() + "</a></p>");
		}
		
		writer.append("</body></html>");
	}

	private void handleIndexRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter writer = response.getWriter();
		
		writer.append("<html><head><title>Example</title></head><body>");
		writer.append("<h3>Welcome</h3>");

		String url = request.getContextPath() + request.getServletPath();
		writer.append("<p><a href='" + response.encodeURL(url) + "/questionnaires'>All Questionnaires</a></p>");
		writer.append("</body></html>");
	}

	private void handelQuestionnaireRequest(HttpServletRequest req, HttpServletResponse res) throws IOException{
        String[] pathElements = req.getRequestURI().split("/");
        Long id = Long.valueOf(pathElements[pathElements.length -1]);
        Questionnaire questionnaire = questionnaireRepository.findById(id);
        PrintWriter writer = res.getWriter();

        writer.append("<h2>" + questionnaire.getTitle() + "</h2>");
        writer.append("<p>" + questionnaire.getDescription() + "</p>");
    }

	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		questionnaireRepository = new QuestionnaireInitializer().initRepoWithTestData();

	}
}
