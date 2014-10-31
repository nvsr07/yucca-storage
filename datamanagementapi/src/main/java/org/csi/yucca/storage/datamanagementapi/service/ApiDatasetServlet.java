package org.csi.yucca.storage.datamanagementapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBDatasetDAO;
import org.csi.yucca.storage.datamanagementapi.model.Dataset;

import com.mongodb.MongoClient;

@WebServlet(description = "Api dataset Servlet", urlPatterns = { "/api/dataset" }, asyncSupported = false)
public class ApiDatasetServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(ApiDatasetServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// select
		log.debug("[ApiDatasetServlet::doGet] - START");
		String id = request.getParameter("id");
		if (id == null || "".equals(id)) {
			throw new ServletException("id missing");
		}
		System.out.println("DatasetItem requested with id=" + id);
		MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");
		Dataset dataset = new Dataset();
		dataset.setId(id);
		dataset = datasetDAO.readDataset(dataset);

		PrintWriter out = response.getWriter();

		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String jsonOut = dataset.toJson();
		out.println(jsonOut);
		out.close();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// update
		log.debug("[ApiDatasetServlet::doPut] - START");
		String id = request.getParameter("id"); // keep it non-editable in UI
		if (id == null || "".equals(id)) {
			throw new ServletException("id missing for edit operation");
		}

		String datasetJSONParam = request.getParameter("dataset");

		MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");
		Dataset dataset = Dataset.fromJson(datasetJSONParam);
		dataset.setId(id);
		datasetDAO.updateDataset(dataset);
		System.out.println("Person edited successfully with id=" + id);
		PrintWriter out = response.getWriter();

		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String jsonOut = dataset.toJson();
		out.println(jsonOut);
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// insert
		log.debug("[ApiDatasetServlet::doPost] - START");
		StringBuffer inBodyRequest = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				inBodyRequest.append(line);
				log.debug("[ApiDatasetServlet::doPost] - request body: " + line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		Dataset dataset = Dataset.fromJson(inBodyRequest.toString());
		dataset = datasetDAO.createDataset(dataset);
		PrintWriter out = response.getWriter();

		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String jsonOut = dataset.toJson();
		out.println(jsonOut);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// delete
		log.debug("[ApiDatasetServlet::doDelete] - START");
		super.doDelete(req, resp);
	}

}
