/*
* @IntegrationMain.java 23/04/2014
*
* Copyrigth (C) 2013 Heidy Marisol Marin-Castro.
*
* Centro de Investigación y de Estudios Avanzados
* del Instituto Politécnico Nacional - Tamaulipas
* hmarin@tamps.cinvestav.mx
*
* This program uses a set of WQI previously identified and builds an integrated WQI.
*/

package com.fieldsIdentify;
  
import com.clustering.*;
import com.renderTree.*;
import com.preprocessing.*;
import com.integration.*;
import com.integration.tree.*;
import com.util.Files;
import com.integratedGI.*;
import java.io.* ;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.*;

public class IntegrationMain{

   LinkedList<PrunedTree> arboles       = null; //arboles identificados de cada formulario
   LinkedList<Clusterizable> repository = null; //repositorio de campos, campos compuestos  
 
   
   public NodeSchema mainProcess(){
      Map<Integer,String> urls = new HashMap<Integer,String>();
      Map<Integer,Integer> pRanks = new HashMap<Integer,Integer>();
      NodeSchema arbolesIntrg = null;
   //intenta recuperar un clustering previamente almacenado, si no existe corre el proceso nuevamente con el set de WQI dadas en el path
      File file = null;
      String line = null;
      BufferedReader input = null;
      String ruta = "";
      String domainInterest = null;
         
            //1. Verify if a datasource of html forms is available in the settings.txt file 
      try{
         file = new File("settings.txt");
         if(!file.exists()){
            System.out.println("Required file 'settings.txt' not found.");
            return null;
         }
         //open the setting file and look for the path pointing to the list of URls where HTML forms will be extracted from.
         input = new BufferedReader( new FileReader( file ) ); 
         line = null;	
         while (( line = input.readLine()) != null) {           
            if(line.startsWith("#formsDomain_name")){
               domainInterest = input.readLine();
               break;
            }
         }
      }
      catch(Exception e){
         System.out.println("Error ocurred while reading 'settings.txt'.");
         return null;
      }
   
      if(domainInterest == null){
         System.out.println("No any valid path of WQI.");
         return null;
      }
                            
      //2. Calcula el clustering 
      ruta = System.getProperty("user.dir");
      ruta = ruta.replace("\\","/");
      ruta = ruta + "/" + domainInterest + "/";   
      
      arboles = PrunedTree.processByFolder(ruta, false);
      repository  = new LinkedList<Clusterizable>();  //crea repositorio de nodos de los arboles calculado          
      for (PrunedTree prunedTree : arboles) {  
         Collection<Clusterizable> clusterizablesFromTree = prunedTree.getClusterizableList();	          
         for(Clusterizable c : clusterizablesFromTree)
            if(c !=null)
               repository.add(c);          	
      }
        
      Preprocessing.clean(repository);      //el repositorio esta listo, primero se limpia
      ConexionWordNet.process(repository);  //se pasa por Wornet      
      Clustering clustering = new Clustering(repository,0.65);                                                               
      Map<NodeCluster,LinkedList<Clusterizable>> idClusters = Clustering.labelingClusters(clustering.getClusters()); 
      clustering = null;
      Files.storeObject(idClusters, domainInterest + "/clustering.clus", "Clusters");                   
      idClusters = (Map<NodeCluster,LinkedList<Clusterizable>>)Files.readObject(ruta + "clustering.clus", "Clusters");
      
      Clustering.showClusters(idClusters);
      Mapping.upDateTrees(idClusters,arboles);                                      //hace los remplazos para dejar todos los arboles con las mismas etiquetas        
      MapaGlobal.show();
   
    //ahora se crean los arboles de integración, arboles PRUNEDTREE podados
    //y son sobre los cuales se hace el calculo de la matriz   
      LinkedList<IntegrationTree> iTrees = new LinkedList<IntegrationTree>();
      LinkedList<int[][]> allMatrices = new LinkedList<int[][]>(); 
      LinkedList<Vector<String>>allVectors = new LinkedList<Vector<String>>();
              
      for(PrunedTree arbol: arboles){
         //int numTree=arbol.getNumArbol();
         //System.out.println("Numero de Arbol:"+numTree);
         urls.put(arbol.getNumArbol(),arbol.getSource());
         pRanks.put(arbol.getNumArbol(),arbol.getPageRank());
         
         IntegrationTree itree = new IntegrationTree(arbol);
         //itree.showTree();
         //itree.showMatrix();
         iTrees.add(itree); 
         int matriz[][]= itree.getMatrix();
         Vector<String> vector = itree.getVector();
      	
         //***********Quitar matrices y vectores null***********8
      	    
         if((matriz.length!=0)&&(vector.size()!=0)){ 
            itree.showMatrix();
            allMatrices.add(matriz);
            allVectors.add(vector);     
         }            
      }
           
      AVGMat promMat= new AVGMat(allMatrices,allVectors); 
      MatrixToTree matrix = new MatrixToTree();
   //arbol unificado
      arbolesIntrg = matrix.ProcessMatrixToTree(promMat.getUnifiedAVG(), promMat.getAVGVector());
   
      Files.storeObject(arbolesIntrg, domainInterest + "/unifiedTree.utre", "Unified tree"); 
      Files.storeObject(urls, domainInterest + "/urls.dat", "URL List"); 
      Files.storeObject(pRanks, domainInterest + "/pageRanks.dat", "PageRank List"); 
      MapaGlobal.storeStatus(domainInterest);		
      InterfazUnifiedWQI mainFrame = new InterfazUnifiedWQI(arbolesIntrg,idClusters,urls,pRanks,"someDir");   
   
      try{
         PrintWriter printWriter = new PrintWriter (domainInterest + ".intgr");
         printWriter.println ("\n#formsDomain_name\nBooks\n");
         printWriter.close ();  
      }
      catch(Exception e){
         System.out.println(e.toString());
      }  
   /*      
   //matrix.renameLabel(mapa,arbol);
   //matrix.updateElements(idClusters,arbol);
   matrix.displayFullTree(arbol,0);
   int numNodes= matrix.assignarNumNode(arbol,0); 
   System.out.println("NUMERO DE NODOS:"+numNodes); 
   */   	  
      return arbolesIntrg;
   
   }

   public NodeSchema mainProcess(JProgressBar progressBar, JTextArea taskOutput){
      Map<Integer,String> urls = new HashMap<Integer,String>();
      Map<Integer,Integer> pRanks = new HashMap<Integer,Integer>();
      NodeSchema arbolesIntrg = null;
   //intenta recuperar un clustering previamente almacenado, si no existe corre el proceso nuevamente con el set de WQI dadas en el path
      File file = null;
      String line = null;
      BufferedReader input = null;
      String ruta = "";
      String domainInterest = null;
      String mainDir = null;
         
            //1. Verify if a datasource of html forms is available in the settings.txt file 
      try{
         file = new File("settings.txt");
         if(!file.exists()){
            System.out.println("Required file 'settings.txt' not found.");
            return null;
         }
         //open the setting file and look for the path pointing to the list of URls where HTML forms will be extracted from.
         input = new BufferedReader( new FileReader( file ) ); 
         line = null;	
         while (( line = input.readLine()) != null) {           
            if(line.startsWith("#formsDomain_name")){
               domainInterest = input.readLine();
            }
            if(line.startsWith("#main_dir")){
               mainDir = input.readLine();
            }   
         }
      }
      catch(Exception e){
         System.out.println("Error ocurred while reading 'settings.txt'.");
         return null;
      }
   
      if(domainInterest == null){
         System.out.println("No domain valid of WQI dataset.");
         return null;
      }
      
      if(mainDir == null){
         System.out.println("No any valid working directory of WQI dataset.");
         return null;
      }
                   
      //2. Calcula el clustering 
      ruta = System.getProperty("user.dir");
      ruta = ruta.replace("\\","/");
      ruta = ruta + "/" + mainDir + "/";   
      
      progressBar.setValue(5);
      taskOutput.append("\nStarting pruning tree process...");
      taskOutput.setCaretPosition(taskOutput.getDocument().getLength());  
         
      long startTime = System.nanoTime(); //inicia la cuenta del tiempo   
      
      arboles = PrunedTree.processByFolder(ruta, false);
         
      long endTime = System.nanoTime(); 
      
      /*
      try{
         PrintWriter outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));       
         outFileTime.println("\n\nTime Pruned tree generation$" + (double)(endTime - startTime)/1000000000.0);
         outFileTime.close();
      }
      catch(Exception e){
      }
    */
    //COMIENZA CLUSTERING
      startTime = System.nanoTime();
    
      repository  = new LinkedList<Clusterizable>();  //crea repositorio de nodos de los arboles calculado          
      for (PrunedTree prunedTree : arboles) {  
         Collection<Clusterizable> clusterizablesFromTree = prunedTree.getClusterizableList();	          
         for(Clusterizable c : clusterizablesFromTree)
            if(c !=null)
               repository.add(c);          	
      }
      
      progressBar.setValue(25);
      taskOutput.append("\n\t...pruning tree process done!\nStarting clustering process...");
      taskOutput.setCaretPosition(taskOutput.getDocument().getLength());  
               
      Preprocessing.clean(repository);      //el repositorio esta listo, primero se limpia
      ConexionWordNet.process(repository);  //se pasa por Wornet      
      Clustering clustering = new Clustering(repository,0.65);                                                               
      Map<NodeCluster,LinkedList<Clusterizable>> idClusters = Clustering.labelingClusters(clustering.getClusters()); 
      clustering = null;
      Files.storeObject(idClusters, mainDir + "/clustering.clus", "Clusters");                   
      idClusters = (Map<NodeCluster,LinkedList<Clusterizable>>)Files.readObject(mainDir + "/clustering.clus", "Clusters");
      
      Clustering.showClusters(idClusters);
      Mapping.upDateTrees(idClusters,arboles);                                      //hace los remplazos para dejar todos los arboles con las mismas etiquetas        
      MapaGlobal.show();
   
      progressBar.setValue(65);
      taskOutput.append("\n\t...clustering process done!\nStarting integration process...");
      taskOutput.setCaretPosition(taskOutput.getDocument().getLength());  
      
      endTime = System.nanoTime();
   
   /*
      try{
         PrintWriter outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));       
         outFileTime.println("Time WQI classification$" + (double)(endTime - startTime)/1000000000.0);
         outFileTime.close();
      }
      catch(Exception e){
      }
   */
     //COMIENZA LA INTEGRACIÓN  
      startTime = System.nanoTime();
         
    //ahora se crean los arboles de integración, arboles PRUNEDTREE podados
    //y son sobre los cuales se hace el calculo de la matriz   
      LinkedList<IntegrationTree> iTrees = new LinkedList<IntegrationTree>();
      LinkedList<int[][]> allMatrices = new LinkedList<int[][]>(); 
      LinkedList<Vector<String>>allVectors = new LinkedList<Vector<String>>();
              
      for(PrunedTree arbol: arboles){
         //int numTree=arbol.getNumArbol();
         //System.out.println("Numero de Arbol:"+numTree);
         urls.put(arbol.getNumArbol(),arbol.getSource());
         pRanks.put(arbol.getNumArbol(),arbol.getPageRank());
         
         IntegrationTree itree = new IntegrationTree(arbol);
         //itree.showTree();
         //itree.showMatrix();
         iTrees.add(itree); 
         int matriz[][]= itree.getMatrix();
         Vector<String> vector = itree.getVector();
      	
         //***********Quitar matrices y vectores null***********8
      	    
         if((matriz.length!=0)&&(vector.size()!=0)){ 
            itree.showMatrix();
            allMatrices.add(matriz);
            allVectors.add(vector);     
         }            
      }
      
      progressBar.setValue(85);
      taskOutput.append("\n\t...integration process done!\nStarting WQI displaying...");
      taskOutput.setCaretPosition(taskOutput.getDocument().getLength());  
               
      endTime = System.nanoTime();
   
   /*
      try{
         PrintWriter outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));       
         outFileTime.println("Time WQI integration$" + (double)(endTime - startTime)/1000000000.0);
         outFileTime.close();
      }
      catch(Exception e){
      }
     */
      startTime = System.nanoTime();
     
      AVGMat promMat= new AVGMat(allMatrices,allVectors); 
      MatrixToTree matrix = new MatrixToTree();
   //arbol unificado
      arbolesIntrg = matrix.ProcessMatrixToTree(promMat.getUnifiedAVG(), promMat.getAVGVector());
   
      Files.storeObject(arbolesIntrg, mainDir + "/unifiedTree.utre", "Unified tree"); 
      Files.storeObject(urls, mainDir + "/urls.dat", "URL List"); 
      Files.storeObject(pRanks, mainDir + "/pageRanks.dat", "PageRank List"); 
      MapaGlobal.storeStatus(mainDir);
      
      progressBar.setValue(100);
      taskOutput.append("\n\t...done!");
      taskOutput.setCaretPosition(taskOutput.getDocument().getLength());  
      
      endTime = System.nanoTime();
   
   /*
      try{
         PrintWriter outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));       
         outFileTime.println("Time Unified WQI$" + (double)(endTime - startTime)/1000000000.0);
         outFileTime.close();
      }
      catch(Exception e){
      }
       */        
          /*      
   //matrix.renameLabel(mapa,arbol);
   //matrix.updateElements(idClusters,arbol);
   matrix.displayFullTree(arbol,0);
   int numNodes= matrix.assignarNumNode(arbol,0); 
   System.out.println("NUMERO DE NODOS:"+numNodes); 
   */   	  
      return arbolesIntrg;
   
   }


   public static void main(String args[]){
   
   
      IntegrationMain fi = new IntegrationMain();
      fi.mainProcess();
   /*
   Map<NodeCluster,LinkedList<Clusterizable>> idClusters = (Map<NodeCluster,LinkedList<Clusterizable>>)Files.readObject("clustering.clus", "Clusters");      
   NodeSchema arbol = (NodeSchema)Files.readObject("unifiedTree.utre", "Unified tree"); 
   
   InterfazUnifiedWQI mainFrame;
   if((idClusters != null) && (arbol != null))      
       mainFrame = new InterfazUnifiedWQI(arbol,idClusters);
   */
   //System.exit(0);
   }
}
 

