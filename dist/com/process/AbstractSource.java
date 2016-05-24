package com.process;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

//import com.bvertical.webQuery.WebQueryInterface;

public class AbstractSource{
	/* Some database connection arguments. */
	final static String url = "localhost";
	final static String dbname = "test";
	final static String user = "SYSTEM";
	final static String password = "MANAGER";

	// Next are common fields found in all sources
	//campos de la interfaz integrada
	//protected String keywords, author, title, isbn, publisher, subject;

	/*public void AbstractSource() {
		this.keywords = keywords;
		this.author = author;
		this.title = title;
		this.isbn = isbn;
		this.publisher = publisher;
		this.title = title;
		this.subject = subject;
	}*/
public void processSources()
	public String conv2Html(int i) {
		if (i == '&')
			return "&amp;";
		else if (i == '<')
			return "&lt;";
		else if (i == '>')
			return "&gt;";
		else if (i == '"')
			return "&quot;";
		else if (i == ' ')
			return "+";
		else
			return "" + (char) i;
	}

	public String conv2Html(String st) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < st.length(); i++) {
			buf.append(conv2Html(st.charAt(i)));
		}
		return buf.toString();
	}

	protected void accumulateResults(Vector<Vector<Object>> global,
			Vector<Vector<Object>> current) {
		for (Iterator<Vector<Object>> iterator = current.iterator(); iterator
				.hasNext();) {
			Vector<Object> vector = (Vector<Object>) iterator.next();
			global.add(vector);
		}
	}

	//abstract void clearFiles();

	/*protected abstract String getStringRequest(String keywords, String author,
			String title, String isbn, String publisher, String subject);

	abstract Vector<Vector<Object>> performQuery()
			throws FileNotFoundException, IOException;

	abstract boolean hasMorePages() throws IOException;
	*/
}