package com.process;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.io.IOException;
import java.util.Vector;
import java.awt.*;
import java.util.*;  
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import com.integration.*;
import com.integration.tree.*;
import com.clustering.*;
import com.process.*;

public class Wrapper {


   private static final String FILE_NAME = "alibris";
   private static final String SOURCE_NAME = "Alibris.com";
   private static final String ID_PRODUCTS_START = "works";
   private static final String ISBN_START_RESULTS = "copies";
   private static final String DIV_PAGINATION_CLASS = "pagination";
   private static final String NEXT_PAGE_LITERAL = "Next";
   private static final int MAX_RESULTS = 50;

   private int contResults;
   private String currentXQueryString;
   
   public LinkedList <String> assignUrl(Map<Integer,String> webquery, Map<Integer,String> urls){
      String u;
      String form="";
      //Map<Integer,String> completeURL=new HashMap<Integer,String>();
      LinkedList <String>listURLs = new LinkedList<String>();
      
      for (Map.Entry entry : urls.entrySet())  {
         String cad="";
         for (Map.Entry entry2 : webquery.entrySet()) {
            if(entry2.getKey()==entry.getKey()){
            //System.out.println(entry.getKey() + ", " + entry.getValue());
               cad = entry.getValue() +"?" + entry2.getValue();
               listURLs.add(cad);
               System.out.println("FORMULARIO"+entry2.getKey() + " URL*" + cad);
               break;
            }
         }
      
      }
      
      return listURLs; 	
   }
	/*
 * Method used to connect with web server and write the result of the
 * request to disk. A small cleaning process of the original and wild HTML
 * response file is made.
 */
   private void writeWebResults(LinkedList <String>listURLs) throws IOException {
      String url = "";		
      Iterator iterator;		
      iterator = listURLs.iterator();
      while (iterator.hasNext()){
         url = (String)iterator.next();
         try{
            URL urlOfWebRequest = new URL(url);
            System.out.println("URL Respuesta" + urlOfWebRequest);
            URLConnection connection = urlOfWebRequest.openConnection();
            
         }
         catch (IOException e) { 
            System.out.println ( "Manejo de la excepción!" ); 
         }                  
      }     
   }
}





