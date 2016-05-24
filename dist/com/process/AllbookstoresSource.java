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

public class AllbookstoresSource extends AbstractSource {
	private static final String FILE_NAME = "allbookstores";
	private static final String SOURCE_NAME = "Allbookstores.com";
	private static final String ID_PRODUCTS_START = "content";
	private static final String CLASS_PRODUCTS = "searchresult";

	private static final String DIV_PAGINATION_CLASS = "paging";
	private static final String NEXT_PAGE_LITERAL = "Next";
	private static final int MAX_RESULTS = 50;

	private int contResults;
	private String currentXQueryString;

	private static final String XQUERY_1_STRING = "for $a in doc('" + FILE_NAME
			+ ".xml')/root/registro " + "let $titulo:=$a/titulo/a/text/text() "
			+ "let $autor:=$a/autor/text/text() "
			+ "let $precio:=$a/precio/text/text() " + "return <libro>" + "^"
			+ "{$titulo}" + "^" + "{$autor}" + "^" + "{$precio}" + "^"
			+ "</libro>";

	private static final String XQUERY_2_STRING = "for $a in doc('"
			+ FILE_NAME
			+ ".xml')/root/registro "
			+ "let $titulo:=$a/div/h2/span/text/text() "
			// + "let $autor:=$a/div[3]/table/tbody/tr/td[2]/a/text/text() "
			+ "let $autor:=$a/div[3]/table/tbody/tr/td[2]//text/text() "
			+ "let $precio:=$a/div[3]/table/tbody/tr[7]/td[2]/text/text() "
			+ "return <libro>" + "^" + "{$titulo}" + "^" + "{$autor}" + "^"
			+ "{$precio}" + "^" + "</libro>";

	public AllbookstoresSource(String keywords, String author, String title,
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
		file = new File(FILE_NAME + ".xhtml");
		if (file.exists()) {
			file.delete();
			System.out.println("Existing " + FILE_NAME
					+ " XML file has been deleted");
		}
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

		File input = new File(FILE_NAME + "_full.html");
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		Element e = doc.getElementById(ID_PRODUCTS_START);

		Element divBgB = e.getElementsByClass("bg-brown").get(0);
		// If the node contains the word "Details", from "Book Details", then it
		// is a ISBN search
		if (divBgB.html().contains("Details")) {

			System.out.println("HERE");
			Element divBoB = e.getElementsByClass("border-brown").get(0);
			System.out.println("HERE-2");

			// anonymous node
			Element infoNode = divBoB.getElementsByTag("div").get(1);
			System.out.println("HERE-3");
			infoNode = infoNode.getElementsByTag("div").get(1);
			System.out.println("info node " + infoNode.html());

			StringBuffer sb = new StringBuffer();
			sb.append("<registro>").append(infoNode.html())
					.append("</registro>");

			PrintWriter pw_cleaned = new PrintWriter(new FileWriter(FILE_NAME
					+ ".html", true));

			pw_cleaned.write(sb.toString());
			pw_cleaned.close();

			currentXQueryString = XQUERY_2_STRING;
		} else // If contains the word Tips, then it is an empty search
		if (divBgB.html().contains("Tips")) {
			PrintWriter pw_cleaned = new PrintWriter(new FileWriter(FILE_NAME
					+ ".html", true));

			pw_cleaned.write("<vacio></vacio>");
			pw_cleaned.close();
		}
		// Otherwise, it should proceeds as a normal search
		else {
			Elements list = e.getElementsByClass(CLASS_PRODUCTS);

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				Element singleResult = list.get(i);

				sb.append("<registro>");
				Element divImage = singleResult.getElementsByTag("div").get(1);
				sb.append("<fotografia>").append(divImage.html())
						.append("</fotografia>");

				Element divInformation = singleResult.getElementsByTag("div")
						.get(2);

				Element elementName = divInformation.getElementsByTag("h2")
						.get(0);
				sb.append("<titulo>").append(elementName.html())
						.append("</titulo>");

				Element elementAuthorsAndPrice = divInformation
						.getElementsByTag("div").get(1);

				Elements authorNames = elementAuthorsAndPrice
						.getElementsByTag("a");

				// sb.append("<autores>");
				// for (int j = 0; j < authorNames.size() - 1; j++) {
				// sb.append("<autor>").append(authorNames.get(j).html())
				// .append("</autor>");
				// }
				// sb.append("</autores>");

				sb.append("<autor>");
				for (int j = 0; j < authorNames.size() - 1; j++) {
					sb.append(authorNames.get(j).html());
					if (j != authorNames.size() - 2)
						sb.append(",");
				}
				sb.append("</autor>");

				String strPriceWrapper = elementAuthorsAndPrice.ownText();

				String price = "";
				if (strPriceWrapper.contains("$")) {
					String tmp[] = strPriceWrapper.split("\\$");
					price = "$" + tmp[tmp.length - 1];
				} else
					price = "$ Not defined";
				sb.append("<precio>").append(price).append("</precio>");

				sb.append("</registro>");
			}

			PrintWriter pw_cleaned = new PrintWriter(new FileWriter(FILE_NAME
					+ ".html", true));

			pw_cleaned.write(sb.toString());

			// Find pagination element and write to clean file
			Element divPaging = doc.getElementsByClass(DIV_PAGINATION_CLASS)
					.get(0);

			// Element pagingNode = divPaging.getElementsByClass("div").get(0);
			Element pagingNode = divPaging.getElementsByAttributeValue("align",
					"center").get(0);

			sb = new StringBuffer();

			sb.append("<" + DIV_PAGINATION_CLASS + ">");
			for (Element element : pagingNode.getElementsByTag("a")) {
				sb.append(element.outerHtml());
			}
			sb.append("</" + DIV_PAGINATION_CLASS + ">");
			pw_cleaned.write(sb.toString());

			pw_cleaned.close();

			currentXQueryString = XQUERY_1_STRING;

		}

		// Next is to convert the clean HTML file to XML
		CkHtmlToXml htmlConv = new CkHtmlToXml();
		htmlConv.UnlockComponent("anything for 30-day trial");

		htmlConv.put_XmlCharset("utf-8");
		htmlConv.ConvertFile(FILE_NAME + ".html", FILE_NAME + ".xml");
		System.out.println("XHTML file generated... ");

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
					// System.out.println(count + " item: " + item);
					// System.out
					// .println("==================================================");
					// count++;
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

		String url = "http://www.allbookstores.com/search?type=advance&author="
				+ author
				+ "&title="
				+ title
				+ "&isbn="
				+ isbn
				+ "&status=&image.x=38&image.y=17&binding=&dateyear_operator=during&dateyear=";
		return url;
	}

	@Override
	boolean hasMorePages() throws IOException {
		if (currentXQueryString.equals(XQUERY_2_STRING))
			return false;
		// If the last link or the link before the last item have the "Next"
		// word, then there are more pages
		File input = new File(FILE_NAME + ".html");
		Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		Element pagination = doc.getElementsByTag(DIV_PAGINATION_CLASS).get(0);
		// Get the las "<a>" child
		Elements linkElements = pagination.getElementsByTag("a");
		if (linkElements.size() == 0)
			return false;
		Element lastLink = linkElements.last();
		Element beforeLastLink = linkElements.get(linkElements.size() - 2);

		if (lastLink.html().contains(NEXT_PAGE_LITERAL)
				|| beforeLastLink.html().contains(NEXT_PAGE_LITERAL))
			return true;
		return false;
	}
}