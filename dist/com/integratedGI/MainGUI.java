package com.integratedGI;

import java.awt.*;
import java.util.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import  javax.swing.filechooser.FileNameExtensionFilter;

import com.sources.*;
import com.fieldsIdentify.*;
import com.integration.tree.*;
import com.integration.*;
import com.util.*;
import com.clustering.*;

import java.io.*;

public class MainGUI extends JFrame implements ActionListener{
   JButton BtnNewWQI;
   JButton BtnOpenWQI;
         
   public MainGUI(){
      super("iWQI: Crawling the Deep Web");
   
     
      BtnNewWQI = new JButton("Create new iWQI...", new ImageIcon(
         "com/bvertical/imagenes/new.png"));
      //BtnBuscar.setPreferredSize(new Dimension(10, 10));
      BtnOpenWQI = new JButton("Open an existing iWQI...", new ImageIcon(
         "com/bvertical/imagenes/open.gif"));
            
   
   //agregar el vector de UIs para despliege
      Container container = getContentPane();
      container.setLayout(new BorderLayout());  
   
      container.add(new JLabel("Please, select an option..."),BorderLayout.NORTH);
      
      JPanel main = new JPanel();
             
      main.setLayout(new GridLayout(2,1,8,8));  
   
      main.add(BtnNewWQI);
      main.add(BtnOpenWQI);
       
      container.add(main,BorderLayout.CENTER);
      
      BtnNewWQI.addActionListener(this);
      BtnOpenWQI.addActionListener(this);
      
         
      setBounds(10,10,600,500);
      //pack();
      setVisible(true);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

      
   public void actionPerformed(ActionEvent evt) {
      Object obj = evt.getSource();
      if (obj == BtnNewWQI) {
         newWQI();
         return;
      }
      
      //el evento viene del boton abrir   
      openWQI();
      return;                  	
   }     
   
   public void newWQI(){
      
      //1. Pedir Domain
      //2. Pedir ruta donde se encuentra el archivo XML de donde se extraeran los formularios de consulta
      //3. Crear el archivo settings.txt
      //4. Arrancar los programas hasta la presentación de la interfaz gráfica
      
      Hashtable<String,ArrayList> dictionaries = null;
      
      try{
      //ABRE EL ARCHIVO DONDE SE ENCUENTRAN LOS DICCIONARIOS DISPONIBLES (DOMINIOS)
         FileInputStream fis = new FileInputStream("domainsDictionary.dic");
         ObjectInputStream iis = new ObjectInputStream(fis);
         
         dictionaries = (Hashtable<String,ArrayList>)iis.readObject();
         iis.close();
      }
      catch(Exception e){
         return;
      }
      
      if(dictionaries == null)
         return;
      
      Enumeration e = dictionaries.keys();
      String domain;
      LinkedList<String> lista = new LinkedList<String>();
      while (e.hasMoreElements()) {
         
         lista.add((String)e.nextElement());
      }   
      
      String[] listStr = lista.toArray(new String[lista.size()]) ;
         
      JList list = new JList(listStr);
      JOptionPane.showMessageDialog(
         this, list, "Select a domain:", JOptionPane.PLAIN_MESSAGE);
         
      String domainName = (String)list.getSelectedValue();
   
      /*String domainName = JOptionPane.showInputDialog(this,
         "Enter the domain name:",
         JOptionPane.QUESTION_MESSAGE); 
      */
      
      if(domainName == null) 
         return;
     
     //ABRE EL ARCHIVO XML QUE CONTINE LA LISTA DE URLS DE DONDE SE BUSCARAN FORMULARIOS PERTENECIENTES AL DOMINIO SELECCIOANDO       
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
      chooser.setFileFilter(xmlfilter);
      chooser.setDialogTitle("Open source of HTML files for WQI finding");     
      int ret = chooser.showOpenDialog(this);
      
      if (ret == chooser.APPROVE_OPTION) {
         String dir = chooser.getSelectedFile().getAbsolutePath();
         dir = dir.replace("\\","/");
         
         String mainDir = domainName + "_" + chooser.getSelectedFile().getName();
         //borra el contenido actual del archivo settings.txt y crea el nuevo
         try{
            PrintWriter printWriter = new PrintWriter ("settings.txt");
            printWriter.println ("#htmlForms_source ");
            printWriter.println (dir);
            printWriter.println ("#formsDomain_name ");
            printWriter.println (domainName + "\n");   
            printWriter.println ("#main_dir ");
            printWriter.println (mainDir + "\n");  
            printWriter.close ();
         }
         catch(Exception ex){
            System.out.println(ex.toString());
            return;
         }
      
         //java.util.List<LinkedList> WQIsVectors = TopicalWQI.mainProcess(false);   
         //LA IDENTIFICACION DE FORMULARIOS LA REALIZA UN OBJETO QUE IMPLEMENTA COMO INTERFAZ GRÁFICA UNA BARRA DE PROGRESO
         ProgressBar pBar = new ProgressBar(this,dir); 
         
         //LA LINEA DE CODIGO ANTERIOR SE BLOQUEA HASTA QUE SE HAN OBTENIDO LOS FORMULARIOS INVESTIGABLES, POR LO QUE EN LA SIGUIENTE LINEA LA LISTA DE WQI YA ESTÁN DISPONIBLES.              
         java.util.List<LinkedList> WQIsVectors = pBar.getResults();
      
         if (WQIsVectors == null){//error en la fase uno de obtener las WQIs
            JOptionPane.showMessageDialog(this,"An error occurred while building the WQI repository. Phase 1 of iWQI failed.");
         }
         else{//continua con la fase dos, construye la WQI integrada
            ProgressBarIntegration pBarInt = new ProgressBarIntegration(this);
            NodeSchema ns = pBarInt.getResult();
            
            if (ns == null){//error en la fase dos de obtener las WQIs
               JOptionPane.showMessageDialog(this,"An error occurred while building the WQI repository. Phase 2 of iWQI failed.");
            }
            else{
            //escribe el archivo que ubica las carpetas donde se almacenan los archivos para construir la interfaz integrada
               try{
                  PrintWriter printWriter = new PrintWriter (mainDir + "/" + domainName + ".intgr");
                  printWriter.println ("\n#dataSource \n"+mainDir+"\n");
                  printWriter.close ();  
               }
               catch(Exception ex){
                  System.out.println(ex.toString());
               }  
            
               JOptionPane.showMessageDialog(this,"The integrated WQI was successfully generated and stored as '" + mainDir +"/"+domainName+".intgr");                                
            }
         }
      }
        
   }
   
   public void openWQI(){
   //1. Pedir el dominio
   
   // ".intgr"
   
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("Integrated WQIs (*.intgr)", "intgr");
      chooser.setFileFilter(xmlfilter);
      chooser.setDialogTitle("Open integrated WQI...");
              
      int ret = chooser.showOpenDialog(this);
      
      if (ret == chooser.APPROVE_OPTION) {
         String dir = chooser.getSelectedFile().getAbsolutePath();
         dir = dir.replace("\\","/");
         String mainDir = "";
      
         try{
            File file = new File(dir);
            //open the setting file and look for the path pointing to the list of URls where HTML forms will be extracted from.
            BufferedReader input = new BufferedReader( new FileReader( file ) ); 
            String line = null;	
            while (( line = input.readLine()) != null) {           
               if(line.startsWith("#dataSource")){
                  mainDir = input.readLine();
                  break;
               }
                                 
            }
         }
         catch(Exception e){
            System.out.println("Error ocurred while reading domain name file.");
            return;
         }
         
         boolean guiStarted = false;
         File file = null;
      
         try{
            file = new File(mainDir + "/clustering.clus");
            if(file.exists()){
               file = new File(mainDir + "/unifiedTree.utre");
               if(file.exists()){
                  file = new File(mainDir + "/urls.dat");
                  if(file.exists()){
                     file = new File(mainDir + "/pageRanks.dat");
                     if(file.exists()){
                        if(MapaGlobal.restore(mainDir)){
                        //tengo todos los archivos necesarios para construir la WQI,
                           NodeSchema arbolesIntrg = (NodeSchema)Files.readObject(mainDir + "/unifiedTree.utre", "Unified tree"); 
                           Map<NodeCluster,LinkedList<Clusterizable>> idClusters = (Map<NodeCluster,LinkedList<Clusterizable>>)Files.readObject(mainDir + "/clustering.clus", "Clusters");
                           Map<Integer,String> urls = (Map<Integer,String>)Files.readObject(mainDir + "/urls.dat", "URL List"); 
                           Map<Integer,Integer> pRanks = (Map<Integer,Integer>)Files.readObject(mainDir + "/pageRanks.dat", "Page Ranks List"); 
                           InterfazUnifiedWQI mainFrame = new InterfazUnifiedWQI(arbolesIntrg,idClusters,urls,pRanks,mainDir);   
                           guiStarted = true;
                        }
                     }
                  }
               }          
            }
         }
         catch(Exception e){
            JOptionPane.showMessageDialog(this,"No valid data found in location: '" + mainDir + "'.");
            return;  
         }
      
         if(!guiStarted)
            JOptionPane.showMessageDialog(this,"No valid data found in location: '" + mainDir + "'.");
               
      }
   }   

   public static void main(String args[]){
      new MainGUI();
   }     
}