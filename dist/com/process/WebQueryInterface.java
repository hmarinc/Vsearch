package com.bvertical.webQuery;

import java.io.IOException;
import java.util.Vector;

public interface WebQueryInterface {
	public Vector<Vector<Object>> getQueryResults() throws IOException;
	
}
