package org.csi.yucca.storage.datamanagementapi.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.csi.yucca.storage.datamanagementapi.dao.MongoDBDatasetDAO;
import org.csi.yucca.storage.datamanagementapi.model.DatasetCollectionItem;

import com.mongodb.MongoClient;

@WebServlet(description = "Api dataset Servlet", urlPatterns = { "/api/dataset/*" }, asyncSupported = false)
public class ApiDatasetServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(ApiDatasetServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// select
		String id = request.getParameter("id");
		if (id == null || "".equals(id)) {
			throw new ServletException("id missing");
		}
		System.out.println("DatasetItem requested with id=" + id);
		MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");
		DatasetCollectionItem datasetCollectionItem = new DatasetCollectionItem();
		datasetCollectionItem.setId(id);
		datasetCollectionItem = datasetDAO.readDatasetCollectionItem(datasetCollectionItem);

		PrintWriter out = response.getWriter();

		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String jsonOut = datasetCollectionItem.toJson();
		out.println(jsonOut);
		out.close();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// update
		String id = request.getParameter("id"); // keep it non-editable in UI
		if (id == null || "".equals(id)) {
			throw new ServletException("id missing for edit operation");
		}

		String datasetJSONParam = request.getParameter("dataset");

		MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");
		DatasetCollectionItem datasetCollectionItem = new DatasetCollectionItem(datasetJSONParam);
		datasetCollectionItem.setId(id);
		datasetDAO.updateDatasetCollectionItem(datasetCollectionItem);
		System.out.println("Person edited successfully with id=" + id);
		PrintWriter out = response.getWriter();

		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String jsonOut = datasetCollectionItem.toJson();
		out.println(jsonOut);
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// insert
		String datasetJSONParam = request.getParameter("dataset");

		MongoClient mongo = (MongoClient) request.getServletContext().getAttribute("MONGO_CLIENT");
		MongoDBDatasetDAO datasetDAO = new MongoDBDatasetDAO(mongo, "smartlab", "provaAle");

		DatasetCollectionItem datasetCollectionItem = new DatasetCollectionItem(datasetJSONParam);
		datasetCollectionItem = datasetDAO.createDatasetCollectionItem(datasetCollectionItem);
		PrintWriter out = response.getWriter();

		response.setContentType("application/json; charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		String jsonOut = datasetCollectionItem.toJson();
		out.println(jsonOut);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// delete
		super.doDelete(req, resp);
	}

}
