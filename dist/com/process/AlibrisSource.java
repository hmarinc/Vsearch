package com.bvertical.process.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.ispras.sedna.driver.DatabaseManager;
import ru.ispras.sedna.driver.DriverException;
import ru.ispras.sedna.driver.SednaConnection;
import ru.ispras.sedna.driver.SednaSerializedResult;
import ru.ispras.sedna.driver.SednaStatement;

import com.chilkatsoft.CkHtmlToXml;

public class AlibrisSource extends AbstractSource {
	private static final String FILE_NAME = "alibris";
	private static final String SOURCE_NAME = "Alibris.com";
	private static final String ID_PRODUCTS_START = "works";
	private static final String ISBN_START_RESULTS = "copies";
	private static final String DIV_PAGINATION_CLASS = "pagination";
	private static final String NEXT_PAGE_LITERAL = "Next";
	private static final int MAX_RESULTS = 50;

	private int contResults;
	private String currentXQueryString;

	private static final String XQUERY_1_STRING = "for $a in doc('"
			+ FILE_NAME
			+ ".xml')/root/li "

			+ "let $titulo:=$a/div[@class='left']/h2/a/text/text() "
			+ "let $autor:=$a/div[@class='left']/h3/a/text/text() "
			+ "let $precio:=$a/div[@class='left']/div/div[2]/ul/li[2]/span/text/text() "
			+ "return <libro>" + "^" + "{$titulo}" + "^" + "{$autor}" + "^"
			+ "{$precio}" + "^" + "</libro>";

	private static final String XQUERY_2_STRING = "for $a in doc('" + FILE_NAME
			+ ".xml')/root/li "
			+ "let $autor:=$a/div[2]/div/div/h3/text/text()"
			+ "let $titulo:=$a/div[2]/div/div/h2/a/text/text()"
			+ "let $precio:=$a/div[2]/div/div[2]/p/span[1]/text/text() "
			+ "return <libro>" + "^" + "{$titulo}" + "^" + "{$autor}" + "^"
			+ "{$precio}" + "^" + "</libro>";

	public AlibrisSource(String keywords, String author, String title,
			String isbn, String publisher, String subject) {
		super(keywords, author, title, isbn, publisher, subject);
	}

	@Override
	/***
	 * Method employed to delete the temporal files created by previous executions of the web query process
	 */
	public void clearFiles() {

		// Delete the (possibly) existing full HTML file.
		// This file is the one received from the webserver "as is", with wild
		// HTML code. It includes
		// an extra bunch of information that is not needed for the current
		// processing
		File file = new File(FILE_NAME + "_full.html");
		if (file.exists()) {
			file.delete();
			System.out.println("existing " + FILE_NAME
					+ " FULL HTML file has been deleted");
		}

		// Delete the (possibly) existing HTML file
		// This file is created after a simple "cleaning" of the wild html file
		// (html_full.html), it contains only the section with information about
		// the books.
		file = new File(FILE_NAME + ".html");
		if (file.exists()) {
			file.delete();
			System.out.println("existing " + FILE_NAME
					+ " HTML file has been deleted");
		}

		// Delete the (possibly) existing XHTML file
		// This file is the result of converting the "cleaned" html file to a
		// well formed XML document. This document is the one passed to the XML
		// engine to perform queries over it.
		file = new File(FILE_NAME + ".xml");
		if (file.exists()) {
			file.delete();
			System.out.println("Existing " + FILE_NAME
					+ " XML file has been deleted");
		}
	}

	@Override
	/*
	 * Method used to gather the information from the remote web server
	 * associated to this "wrapper".
	 */
	public Vector<Vector<Object>> getQueryResults() throws IOException {
		Vector<Vector<Object>> global = new Vector<Vector<Object>>();
		boolean continueSearch = true;
		int pageCounter = 1;
		contResults = 0;
		while (continueSearch) {
			clearFiles();
			writeWebResults(getStringRequest(conv2Html(keywords),
					conv2Html(author), conv2Html(title), conv2Html(isbn),
					conv2Html(publisher), conv2Html(subject))
					+ "&page=" + pageCounter);
			if (!existResults())
				continueSearch = false;
			else {
				Vector<Vector<Object>> currentAnswer = performQuery();
				accumulateResults(global, currentAnswer);
				pageCounter++;

				contResults += currentAnswer.size();

				// Analyse if there are more "next" pages or if the MAX_RESULT
				// limit
				// has been reached...
				if (contResults > MAX_RESULTS || !hasMorePages())
					continueSearch = false;
			}
		}
		return global;
	}

	@Override
	boolean hasMorePages() throws IOException {
		if (currentXQueryString.equals(XQUERY_2_STRING))
			return false;
		// File input = new File(FILE_NAME + "_full.html");
		File input = new File(FILE_NAME + ".html");
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		Element pagination = doc.getElementsByClass(DIV_PAGINATION_CLASS)
				.get(0);
		// Get the las "<a>" child
		Elements linkElements = pagination.getElementsByTag("a");
		if (linkElements.size() == 0)
			return false;
		Element lastLink = linkElements.last();
		if (lastLink.html().contains(NEXT_PAGE_LITERAL))
			return true;
		return false;
	}

	/*
	 * Method used to connect with web server and write the result of the
	 * request to disk. A small cleaning process of the original and wild HTML
	 * response file is made.
	 */
	private void writeWebResults(String url) throws IOException {

		PrintWriter pw_full = new PrintWriter(new FileWriter(FILE_NAME
				+ "_full.html", true));

		URL urlOfWebRequest = new URL(url);

		URLConnection connection = urlOfWebRequest.openConnection();

		// Connect to the specific URL query for this service
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			pw_full.println(inputLine);
			inputLine = inputLine.trim();

		}

		pw_full.close();
		in.close();
		System.out.println(FILE_NAME + "_full.html has been written");
		File input = new File(FILE_NAME + "_full.html");
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		Element e = doc.getElementById(ID_PRODUCTS_START);
		// If we have the "works" element present in the page, then it is a NON
		// ISBN search, proceed normally
		if (e != null) {
			PrintWriter pw_cleaned = new PrintWriter(new FileWriter(FILE_NAME
					+ ".html", true));
			pw_cleaned.write(e.html());

			Element pagination = doc.getElementsByClass(DIV_PAGINATION_CLASS)
					.get(0);
			pw_cleaned.write(pagination.outerHtml());
			pw_cleaned.close();

			currentXQueryString = XQUERY_1_STRING;
		}
		// ... Else, we have an ISBN search
		else {
			// ... or empty results
			PrintWriter pw_cleaned = new PrintWriter(new FileWriter(FILE_NAME
					+ ".html", true));
			Element breadcrumb = doc.getElementById("breadcrumb");
			if (breadcrumb != null) { // empty results!
				pw_cleaned.write("<vacio></vacio>");
			} else { // An ISBN search

				e = doc.getElementById(ISBN_START_RESULTS);

				pw_cleaned.write(e.html());

				currentXQueryString = XQUERY_2_STRING;
			}
			pw_cleaned.close();
		}

		// Next is to convert the clean HTML file to XML
		CkHtmlToXml htmlConv = new CkHtmlToXml();
		htmlConv.UnlockComponent("anything for 30-day trial");

		htmlConv.put_XmlCharset("utf-8");
		htmlConv.ConvertFile(FILE_NAME + ".html", FILE_NAME + ".xml");
		System.out.println("XHTML file generated... ");
	}

	/***
	 * Simple method that identifies if there are results from the web server
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean existResults() throws IOException {
		File input = new File(FILE_NAME + ".xml");
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		Elements vacio = doc.getElementsByTag("vacio");
		if (vacio.size() != 0)
			return false;

		return true;
	}

	protected Vector<Vector<Object>> performQuery()
			throws FileNotFoundException, IOException {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		SednaConnection con = null;
		System.out.println("Client has been started ...");

		try {
			/* Get a connection */
			con = DatabaseManager.getConnection(url, dbname, user, password);

			/* Begin a new transaction */
			con.begin();

			/* Create statement */
			SednaStatement st = con.createStatement();

			/* Load XML into the database */
			System.out.println("Loading data ...");
			boolean call_res = st.execute("LOAD '"
					+ System.getProperty("user.dir") + "/" + FILE_NAME
					+ ".xml' '" + FILE_NAME + ".xml'");

			/* If call_res is false the statement was an update */
			if (!call_res) {
				System.out.println("Document '" + FILE_NAME
						+ ".xml' has been loaded successfully");
				System.out
						.println("==================================================\n");
			}

			/* Execute query */
			System.out.println("Executing query");
			call_res = st.execute(currentXQueryString);

			/*
			 * If call_res is true the statement was not an update and we can
			 * use SednaSerializedResult object.
			 */
			if (call_res) {
				String item = null;
				int count = 1;
				SednaSerializedResult pr = st.getSerializedResult();
				while ((item = pr.next()) != null) {
					// item contains the value for every register in the
					// result set

					Vector<Object> row = new Vector<Object>();
					row.addElement(SOURCE_NAME);
					String datos[] = item.split("\\^");
					for (int i = 1; i < datos.length - 1; i++) {
						row.addElement(datos[i]);
					}

					data.addElement(row);
					System.out.println(count + " item: " + item);
					System.out
							.println("==================================================");
					count++;
				}
			}

			/* Remove document */
			System.out.println("Removing document ...");
			call_res = st.execute("DROP DOCUMENT '" + FILE_NAME + ".xml'");

			if (!call_res) {
				System.out.println("Document '" + FILE_NAME
						+ "' has been dropped successfully");
				System.out
						.println("==================================================\n");
			}
			/* Commit current transaction */
			con.commit();
		} catch (DriverException e) {
			e.printStackTrace();
		} finally {
			/* Properly close connection */
			try {
				if (con != null)
					con.close();
			} catch (DriverException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	@Override
	protected String getStringRequest(String keywords, String author,
			String title, String isbn, String publisher, String subject) {

		String url = "http://www.alibris.com/booksearch?keyword=" + keywords
				+ "&author=" + author + "&qpub=" + publisher + "&title="
				+ title + "&isbn=" + isbn;
		return url;
	}
}