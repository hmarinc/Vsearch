package com.integratedGI;

import java.awt.*;
import java.util.*;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.integration.*;
import com.integration.tree.*;
import com.clustering.*;
import com.process.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//public class TabbedPaneDemo extends JPanel {
public class InterfazUnifiedWQI extends JFrame implements ActionListener{
   JButton BtnBuscar;
   JButton BtnReset;
   
   JComboBox criteriaPRank;
   JTextField textValuePRank;
   JButton BtnFilter;
  
   NodeSchema tree;
   Map<NodeCluster,LinkedList<Clusterizable>> idClusters;
   LinkedList<LinkedList<JComponent>> uis;
   Map<NodeCluster,LinkedList<JComponent>> fields;
   Map<Integer,String>urls;
   Map<Integer,Integer>pRanks;
   Map<Integer,String> webQuery;
   //Map<String, String> eventos;
   String mainDir;
   
   LinkedList<String> validURLtoConnect;
   LinkedList<Integer> pRankList;
   
   JPanel containerResults;
   int validResults;                 /* Numero de resultados en JPanel*/
   
   class ActionListenerBoton implements ActionListener{
      String uri;
      ActionListenerBoton(String uri){
         this.uri = uri;
      }
      public void actionPerformed(ActionEvent e){
         try{
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(uri));
         }
         catch(Exception e2){
            System.out.println("Unable to open browser: " + e2.toString());
         }
      }
   }
      
   public InterfazUnifiedWQI(NodeSchema tree,Map<NodeCluster,LinkedList<Clusterizable>> idClusters, Map<Integer,String>urls,Map<Integer,Integer>pRanks,String dir) {
      super("Integrated WQI");
      mainDir = dir;
      this.idClusters = idClusters;
      this.tree = tree;
      this.urls= urls;
      this.pRanks = pRanks;
      validResults = 0;    //no hay resultados validos aun
      uis = new LinkedList();
      fields = new HashMap<NodeCluster,LinkedList<JComponent>>(); 
             
      showTree(tree, 0);
      
      /*  
      BtnBuscar = new JButton("BUSCAR", new ImageIcon(
         "com/bvertical/imagenes/buscar.jpg"));
      BtnReset = new JButton("RESET", new ImageIcon(
         "com/bvertical/imagenes/restart.jpg"));
       */     
      BtnBuscar = new JButton("BUSCAR");
      BtnReset = new JButton("RESET");
         
      criteriaPRank = new JComboBox();
      textValuePRank = new JTextField("0",2);
      
      criteriaPRank.addItem("<");
      criteriaPRank.addItem(">");
      criteriaPRank.addItem("=");
   
      BtnFilter = new JButton("Filter");
      
   //agregar el vector de UIs para despliege
      Container container = getContentPane();
      JPanel main = new JPanel();
      main.setLayout(new GridLayout(0,1));
      
      container.setLayout(new GridLayout(0,1));
      for(LinkedList<JComponent> list : uis){
         JPanel row = new JPanel();
         row.setLayout(new FlowLayout());
      //int i = 0;
         for(JComponent ui : list){
         //if(i == 0)
            //container.add(ui);
         //else
            row.add(ui);
         //i++;
         }
         main.add(row);
      }
      
      /*
      BtnBuscar.setPreferredSize(new Dimension(80, 40));
      BtnReset.setPreferredSize(new Dimension(80, 40));
      */
      
      JPanel botones = new JPanel();
      botones.setLayout(new FlowLayout());
         
      botones.add(BtnBuscar);
      botones.add(BtnReset);
      main.add(botones);
      
      JPanel pRankSelectPanel = new JPanel();
      pRankSelectPanel.setLayout(new FlowLayout());
         
      pRankSelectPanel.add(new JLabel("Filter by page rank: "));
      pRankSelectPanel.add(criteriaPRank);
      pRankSelectPanel.add(textValuePRank);
      pRankSelectPanel.add(BtnFilter);
      pRankSelectPanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 
      
      main.add(pRankSelectPanel);
   
      BtnBuscar.addActionListener(this);
      BtnReset.addActionListener(this);
      BtnFilter.addActionListener(this);
      
      containerResults = new JPanel();
      containerResults.setLayout(new BorderLayout());
      
      JScrollPane scrollPane = new JScrollPane( containerResults );
      containerResults.setPreferredSize(new Dimension(400,400));
      container.add(main);
      container.add(scrollPane);
      
      setBounds(10,10,500,800);
      //pack();
      setVisible(true);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
   }

   public void showTree(NodeSchema root, int espacios){
    
      if(root == null) 
         return;
   
      String simbol = root.getSymbol();
      String newLabel = MapaGlobal.getLabel(simbol);
      LinkedList<NodeSchema> childs = root.getChilds();   
   
      for(int j = 0; j< espacios; j++) System.out.print(" "); 
   
      if(newLabel != null){ 
         NodeCluster nc = MapaGlobal.getNodeMap(newLabel); 
         LinkedList<Clusterizable> cluster = (LinkedList<Clusterizable>)idClusters.get(nc);
      
      //System.out.println(simbol + " -> " + newLabel);
         String inf = nc.infinito();     //text, check
         FiniteUI fui = nc.getFinite();  //radio o select 
         int idx = nc.getIndexNode();    //ind si es compo o grupo
      
         if(idx != -1){
            Clusterizable clus = cluster.get(idx);
         //este clusterizable es o un campo compuesto o un grupo
            System.out.println(clus.show());    
            LinkedList row = new LinkedList();
            JLabel lb=new JLabel(); 
            if(clus instanceof UIcompuesto){
               UIcompuesto uic = (UIcompuesto)clus;
               lb = new JLabel("COMPO-" + newLabel);
               row.add(lb);     
               JTextField ui = new JTextField(""+(uic.getValue()).get(0),14);
               row.add(ui);
               
            }                   
            else if(clus instanceof GroupElement){
               GroupElement g= (GroupElement)clus;
               lb = new JLabel("GPO-" + newLabel);
               row.add(lb);
            
               LinkedList<UIcontent> UIchilds = g.getChilds();
               for (int j = 0; j < UIchilds.size(); j++) {
                  UIcontent uic= UIchilds.get(j);
                  String lab = uic.getLabel();
                  if(!lab.equals("no label")){
                     lb = new JLabel(lab);
                     row.add(lb);
                  }
                  String typeUI = uic.getType();
                  LinkedList valueField = uic.getValue();
                  if(typeUI.equals("select")){
                     JComboBox ui2 = new JComboBox();
                     for(int i = 0; i < valueField.size(); i++){
                        ui2.addItem(valueField.get(i));
                     }
                     row.add(ui2);  
                  }
                  else if(typeUI.equals("text")){
                     JTextField ui = new JTextField("",14);
                     row.add(ui);
                     
                  }
                  else if(typeUI.equals("checkbox")){
                     JCheckBox ui = new JCheckBox();
                     row.add(ui);
                     
                  }
                  else if(typeUI.equals("radio")){
                  //crea un radio para cada valor
                     ButtonGroup bG = new ButtonGroup();
                     JRadioButton opt;
                     for(int i = 0; i < valueField.size(); i++){
                        opt = new JRadioButton(""+valueField.get(i));
                        bG.add(opt);
                     }                     
                     row.add(bG);
                     //fields.add((String)opt.getSelected());
                  }
                  System.out.println(typeUI+", "+uic);   
               }
            }  
            if(row.size() > 0){
               uis.add(row); 
               //fields.put(lb.getText(),row);
               fields.put(nc,row);
            }	
         }
         else{
            System.out.println("infinito = " + inf);
            System.out.println("finito = " + fui);
         
            LinkedList row = new LinkedList();
         
            JLabel lb = new JLabel(newLabel);
            row.add(lb);
         
            if(inf != null){
               if(inf.equals("text")){
                  JTextField ui = new JTextField("",14);
                  row.add(ui);
                
               }
               else if (inf.equals("checkbox")){
                  JCheckBox ui = new JCheckBox();
                  row.add(ui);
                 
               }
            }
            if(fui != null){
               String type = fui.getType();
               if(type.equals("select")){
                  JComboBox ui2 = new JComboBox();
                  LinkedList valueField = new LinkedList(fui.getValues());
                  for(int i = 0; i < valueField.size(); i++){
                     ui2.addItem(valueField.get(i));
                  }
                  row.add(ui2);  
               }
               else if (type.equals("radio")){
               //crea un radio para cada valor
                  ButtonGroup bG = new ButtonGroup();
                  LinkedList valueField = new LinkedList(fui.getValues());
                  for(int i = 0; i < valueField.size(); i++){
                     JRadioButton opt = new JRadioButton(""+valueField.get(i));
                     bG.add(opt);
                  }                     
                  row.add(bG);   
               }
            }  
            uis.add(row);
            //fields.put(lb.getText(),row);
            fields.put(nc,row);
         }      
      }
      else
         System.out.println(simbol);
   
      if(childs != null){   
         for(int i = 0; i < childs.size(); i++)
            showTree(childs.get(i), espacios+3);
      }
   }

   public void actionPerformed(ActionEvent evt) {
      Object obj = evt.getSource();
      
      if (obj == BtnReset) {
         containerResults.removeAll();
         containerResults.validate();
         containerResults.repaint();
         validResults = 0;
      
         for(LinkedList<JComponent> list : uis){
            for(JComponent c: list){
               if(c instanceof JTextField)
                  ((JTextField) c).setText("");
                  
               if(c instanceof JComboBox)
                  ((JComboBox) c).setSelectedIndex(0);
            
               if(c instanceof JCheckBox)
                  ((JCheckBox) c).setSelected(false);
                  
               if(c instanceof JRadioButton)	
                  ((JRadioButton) c).setSelected(false);  
            		
            }
         }
         //modelo.setDataVector(new Vector(), nombresColumnas);
         return;
      }
      else if (obj == BtnBuscar) {
      
      //el evento viene del boton buscar
      
         containerResults.removeAll();
         containerResults.validate();
         containerResults.repaint();
         validResults = 0;
      /*      
      Iterator<NodeCluster> keySetIterator = fields.keySet().iterator();
      
      while(keySetIterator.hasNext()){
         NodeCluster key = keySetIterator.next();
         LinkedList<JComponent> list= fields.get(key);
         //for(LinkedList<JComponent> list : uixs){
         for(JComponent c: list){
            if(c instanceof JTextField)
               System.out.println("key: " +key.getLabel()+ " , value: " + ((JTextField)c).getText());
            if(c instanceof JComboBox)
               System.out.println("key: " +key.getLabel()+ " , value: " + (String) (((JComboBox)c).getSelectedItem()));
                  
            if(c instanceof JCheckBox)
               if(((JCheckBox)c).isSelected())
                  System.out.println("key: " +key.getLabel()+ ", value: " + ((JCheckBox)c).getText());
            if(c instanceof JRadioButton)	
               System.out.println("key: " +key.getLabel()+ ", value: " + ((JRadioButton)c).getText());
            
         }   
      }
      */
      //medir el tiempo
      //Crea el archivo de salida, si este no existe
         try{
            File f = new File(mainDir + "/searchTime.txt");
            if(!f.exists()){
               PrintWriter outFileTime = new PrintWriter(mainDir + "/searchTime.txt"); 
               outFileTime.println("Query time $ Total URLs $ URL that accepted the connection");
               outFileTime.close();
            }
         }
         catch(Exception e){
         }
         
         long startTime = System.nanoTime();
      // 1.- crear mediador.
         Mediator mediador = new Mediator();
      // 2.- realizar consulta global y esperar resultados.         
         webQuery = mediador.startProcess(fields,idClusters);
      // 3. Formar el conjunto de URLs validas y hacer un test de cada URL y medir el tiempo.
         
         createResults();
         
         long endTime = System.nanoTime();
         
         try{
            
            PrintWriter outFileTime = new PrintWriter(new FileWriter(mainDir + "/searchTime.txt", true)); 
            outFileTime.println("" + ((double)(endTime - startTime)/1000000000.0) + "$" + urls.size() + "$" + validURLtoConnect.size());
            outFileTime.close();
            
         }
         catch(Exception e){
         }
             
      // 4.- mostrar los resultados en la interfaz   
         addResults("",0);
      
      //fillResults();
         return;
      }      
      else if (obj == BtnFilter) {
      
         if(validResults == 0)
            return;
      
      //obtiene el criterio de mostrar
         String criterio = (String)criteriaPRank.getSelectedItem();
         String val = textValuePRank.getText();
         int valor = -1;
         try{
            valor = Integer.parseInt(val);
         }
         catch(Exception e){
            valor = -1;
         }
         
         if (valor == -1) 
            return;
            
         containerResults.removeAll();
         containerResults.validate();
         containerResults.repaint();
         validResults = 0;
            
         addResults(criterio,valor);
      
         return;
      }
      else
         return;
          	
   }     
   
   public void createResults(){
   //si ya se tienen los resultados, se crea la lista de URLs validas que despues serán desplegadas en 
   //la interfaz
      validURLtoConnect = new LinkedList<String>();
      pRankList = new LinkedList<Integer>();
      int numURLs = urls.size();
      
       
      for (Map.Entry entry : urls.entrySet())  {
         String uri = "";
         int key = (Integer)entry.getKey();   
         int pageRank = pRanks.get(key);
        
         String values = webQuery.get(key);
         uri = entry.getValue() + "?" + values;
         //uri = URLEncoder.encode(myUrl, "UTF-8");
         uri = conv2Html(uri);
                     
         boolean ok = tryConnection(uri);
         if(ok){
            validURLtoConnect.add(uri);
            pRankList.add(pageRank);
            System.out.println("!!! VALID URI: " + uri + ", PRANK = " + pageRank);
         }
         else
            System.out.println("!!! NOT VALID URI: " + uri + ", PRANK = " + pageRank);
                   
      }
   
   
   
   }
   public boolean validPageRank(String criterio,int val,int pageRank){
      if(criterio.equals("<")){
         if(pageRank < val) 
            return true;
         else 
            return false;
      }
      else if(criterio.equals(">")){
         if(pageRank > val) 
            return true;
         else 
            return false;
      }
      else if(criterio.equals("=")){
         if(pageRank == val) 
            return true;
         else 
            return false;
      }
      else
         return true;
   }
 
   public boolean tryConnection(String uri){
      try
      {
         URL url = new URL(uri);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      
      // just want to do an HTTP GET here
         connection.setRequestMethod("GET");          
      // give it 10 seconds to respond
         //connection.setReadTimeout(10*1000);
         connection.connect();
         if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return true;
         } 
         else {
            return false;
         }
      }
      catch (Exception e)
      {
         return false;
      }
   }
   public void addResults(String criterio, int val){
      int counter = 0;
      String boton = "btn";
      JPanel urlPanel = new JPanel();
      JPanel pRankPanel = new JPanel();
      urlPanel.setLayout(new GridLayout(0,1));
      pRankPanel.setLayout(new GridLayout(0,1));
      JPanel bottonsPanel = new JPanel();
      bottonsPanel.setLayout(new GridLayout(0,1));
      
      JLabel head1 = new JLabel("URL List");
      head1.setForeground(Color.blue);
      JLabel head2 = new JLabel("Page Rank",SwingConstants.CENTER);
      head2.setForeground(Color.blue);
      
      urlPanel.add(head1);   
      pRankPanel.add(head2);
      bottonsPanel.add(new JLabel(" "));
          
      for (int index = 0; index < pRankList.size(); index++)  {
         int pageRank = pRankList.get(index);
         
         if(!validPageRank(criterio,val,pageRank))
            continue;
         
         String uri = validURLtoConnect.get(index);
           
         boton = boton + counter;   
               
         JLabel jl = new JLabel(findLabel(uri) + "\t");
         JButton btn = new JButton("View page");
         btn.addActionListener(new ActionListenerBoton(uri));
                      
         urlPanel.add(jl);
         pRankPanel.add(new JLabel(""+pageRank,SwingConstants.CENTER));
         bottonsPanel.add(btn);   
         validResults++;
      }
   
      containerResults.add(urlPanel,BorderLayout.WEST); 
      containerResults.add(pRankPanel,BorderLayout.CENTER);
      containerResults.add(bottonsPanel,BorderLayout.EAST);   
      containerResults.validate();
      containerResults.repaint();                
   }

   public void addResults(){
      String u;
      String form="";
      System.out.println("URLs: " + urls.size());
      System.out.println("WebQuery: " + webQuery.size());
     
      int counter = 0;
      String boton = "btn";
      JPanel urlPanel = new JPanel();
      JPanel pRankPanel = new JPanel();
      urlPanel.setLayout(new GridLayout(0,1));
      pRankPanel.setLayout(new GridLayout(0,1));
      JPanel bottonsPanel = new JPanel();
      bottonsPanel.setLayout(new GridLayout(0,1));
      /*
      for (Map.Entry entry : urls.entrySet())  {
         String uri = "";
         String cadena = "";
         for (Map.Entry entry2 : webQuery.entrySet()) {
            int int1 = (Integer)entry2.getKey();
            int int2 = (Integer)entry.getKey();
            if(int1 == int2){
               uri = entry.getValue() +"?" + entry2.getValue();
               uri = conv2Html(uri);
               System.out.println("URI: " + uri);
               boton = boton + counter;   
               String str1 = (String)entry.getValue();
               
               JLabel jl = new JLabel(findLabel(str1));
               JButton btn = new JButton("View page");
               btn.addActionListener(new ActionListenerBoton(uri));
               
               //JPanel any = new JPanel();
               //any.setLayout(new FlowLayout());
               urlPanel.add(jl);
               bottonsPanel.add(btn);   
               break;
            }
         }
         
      }
       */
       
      urlPanel.add(new JLabel("URL List"));
      pRankPanel.add(new JLabel("Page Rank",SwingConstants.CENTER));
      bottonsPanel.add(new JLabel(" "));
         
       
      for (Map.Entry entry : urls.entrySet())  {
         String uri = "";
         String cadena = "";
         
         int key = (Integer)entry.getKey();
         String values = webQuery.get(key);
         uri = entry.getValue() + "?" + values;
         uri = conv2Html(uri);
         int pageRank = pRanks.get(key);
         System.out.println("URI: " + uri + ", PRANK = " + pageRank);
            
         boton = boton + counter;   
         String str1 = (String)entry.getValue();
               
         JLabel jl = new JLabel(findLabel(str1) + "\t");
         JButton btn = new JButton("View page");
         btn.addActionListener(new ActionListenerBoton(uri));
               
               //JPanel any = new JPanel();
               //any.setLayout(new FlowLayout());
         urlPanel.add(jl);
         pRankPanel.add(new JLabel(""+pageRank,SwingConstants.CENTER));
         bottonsPanel.add(btn);   
         validResults++;
      }
   
      containerResults.add(urlPanel,BorderLayout.WEST); 
      containerResults.add(pRankPanel,BorderLayout.CENTER);
      containerResults.add(bottonsPanel,BorderLayout.EAST);   
      containerResults.validate();
      containerResults.repaint();                
   }
   
   public String conv2Html(int i) {
      //if (i == '&')
         //return "&amp;";
      if (i == '<')
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


   public String findLabel(String str){
      int index = 0;
      int numSlash = 0;
   
      for(int i = 0; i < str.length(); i++){
         if(str.charAt(i) == '/') numSlash ++;
         if(numSlash == 3)
            return str.substring(0,i);
      }
      
      return str;
   }
   
   public void fillResults(){
      String u;
      String form="";
      //Map<Integer,String> completeURL=new HashMap<Integer,String>();
      System.out.println("URLs: " + urls.size());
      System.out.println("WebQuery: " + webQuery.size());
     
      try{
         PrintWriter printWriter = new PrintWriter ("results.html");
         printWriter.println ("<html><body>");
      
      
         for (Map.Entry entry : urls.entrySet())  {
            String uri = "";
            String cadena = "";
            for (Map.Entry entry2 : webQuery.entrySet()) {
               int int1 = (Integer)entry2.getKey();
               int int2 = (Integer)entry.getKey();
               System.out.println("INT1 = " + int1 + " INT2 = " + int2);
               if(int1 == int2){
                  uri = entry.getValue() +"?" + entry2.getValue();
                  System.out.println("URI: " + uri);   
                  cadena = "<p><a href=\""+uri+"\"> " + entry.getValue() + "</a><p>";
                  printWriter.println (cadena);
                  break;
               }
            }
         
         }
         printWriter.println ("</body></html>");
         printWriter.close (); 
      }
      catch(Exception e){} 
        
   }
   
   
}