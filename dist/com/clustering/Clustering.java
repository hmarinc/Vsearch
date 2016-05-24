   package com.clustering;

   import com.renderTree.*;
   import com.util.Files;
   import com.preprocessing.*;
   import com.integration.*;

   import java.io.* ;
   import java.util.*;
   import java.lang.Math.*;

   public class Clustering{
   
   //repositorio inicial de objetos a ingresar al algoritmo de clustering
      LinkedList<Clusterizable> repository;
      LinkedList<Clusterizable> elementi;
      LinkedList<Clusterizable> elementj; 
   //double[][] matrix;   
   //lista de clusters de salida
      LinkedList<LinkedList<Clusterizable>> clusters;
      static int iteration = 0;
      double lamnda;
   
      public Clustering(LinkedList<Clusterizable> repository, double lamnda2){
         lamnda = lamnda2;
         iteration = 0;
         this.repository = repository;
         clusters = new LinkedList<LinkedList<Clusterizable>>();
         System.out.println("\nStarting clustering with " + repository.size() + " Clusterizable objects\n");
      	
         for(Clusterizable element:repository){
            LinkedList<Clusterizable> cluster_i = new LinkedList<Clusterizable>();
            cluster_i.add(element);
            clusters.add(cluster_i);   
         }
      
         boolean continuar = false;
         do{
            continuar = clustering(); //realiza el clustering
         }while(continuar);       	               
      }
   
      private double getSimilarity(LinkedList<Clusterizable> element1,LinkedList<Clusterizable> element2){
      
         double similitud=0;
         String label1, label2;
         double max = 0.0;
      	
         for (Clusterizable s1 : element1){
            for(Clusterizable s2: element2){   
               if(s1.getForm()==s2.getForm())
                  return 0;
            			
               similitud = s1.compare(s2);
            
               if(similitud  > max)
                  max = similitud;       
            }
         }         
         return max;
      }
   
      private boolean clustering(){
      
      //matrix = new double[clusters.size()][clusters.size()];
      //Set<Double> matrix2 = new TreeSet<Double>();
      
         System.out.println("********New clustering with " + clusters.size() + " clusters. Iteration " + iteration);
      
         double mayorSimilaridad = 0.0;
         int i_index = -1;
         int j_index = -1;
      
         for(int i = 0; i < clusters.size()-1; i++ ){
            elementi = clusters.get(i);
            for(int j = i+1; j < clusters.size(); j++){
               elementj = clusters.get(j);
               double similar = getSimilarity(elementi, elementj);
               similar = similar * 100;
               similar = java.lang.Math.round(similar);
               similar = similar/100;
            //matrix[i][j] = similar;
            //matrix[j][i] = similar;
            
               if(similar > lamnda && similar > mayorSimilaridad){
                  i_index = i;
                  j_index = j;
                  mayorSimilaridad = similar; 
               }  
            }
         }
      
            /*
      for(int i = 0; i < matrix.length-1; i++ ){
         for(int j = i+1; j < matrix[0].length; j++)
            if(matrix[i][j] > lamnda && matrix[i][j]>mayorSimilaridad){
               i_index = i;
               j_index = j;
               mayorSimilaridad = matrix[i][j];   
            }
      }  
      */
         if(j_index != -1 && i_index != -1){// encontro al menos un par que cumple el criterio y se puede hacer clustering
         //formar una nueva entrada que es la union de la entrada i y la entrada j	
         // System.out.println("label1: "+elementi.getLabel()+"  simi: "+mayorSimilaridad+)
            elementi = clusters.remove(i_index);
            elementj = clusters.remove(j_index-1); //uno menos por la remocion de i, que siempre es menor que j
         
            LinkedList<Clusterizable> elementij = new LinkedList<Clusterizable>();        
         //System.out.println(" elementoi: "+elementi+" indice:"+i_index);
         //System.out.println(" elementoj: "+elementj+" indice:"+j_index);  
            	//Hijos de los tipos GroupElement
            LinkedList<UIcontent> UIchilds=null;
         
         //pasa los elementos de i a ij	
            for(Clusterizable term: elementi)
               elementij.add(term);
         
            for(Clusterizable termino: elementj)
               elementij.add(termino);
                         	
            clusters.add(elementij);
         
            elementi = null;
            elementj = null;
            iteration = iteration + 1;   
            return true;
         }   
         return false;
      }
        	   
      public void mostrarMatrizSimilaridad(double matrix[][]){
         System.out.print("\n\nMATRIZ DE SIMILARIDAD ITERACION i\n");	      
         for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++)
               System.out.print(matrix[i][j]+"\t");
         
            System.out.print("\n");	
         }
      
      }
   
   
   
      private static NodeCluster labelCluster(LinkedList<Clusterizable> cluster){
      //dado el cluster de entrada determinar el clusterizable más representativo
         int mayor = -1;
         String labelFin = "";
         Integer contador=null;
         Map<String,Integer> contarLabels = new HashMap<String,Integer> ();	
      
         LinkedList<Integer> indexTxt = new LinkedList<Integer>();
         LinkedList<Integer> indexSelect = new LinkedList<Integer>();
         LinkedList<Integer> indexRadio = new LinkedList<Integer>();
         LinkedList<Integer> indexCheckbox = new LinkedList<Integer>();   
         LinkedList<Integer> indexUICompuesto = new LinkedList<Integer>();
         LinkedList<Integer> groups = new LinkedList<Integer>();
                              
         for(int i = 0; i < cluster.size(); i++){
            Clusterizable ei = cluster.get(i);
            String label = ei.getLabel();
         
            if( ei instanceof UIcompuesto)
               indexUICompuesto.add(i);
            
            else if( ei instanceof UIcontent){
               String type = ((UIcontent)ei).getType();
               if(type.equals("radio"))
                  indexRadio.add(i);
               else if (type.equals("checkbox"))
                  indexCheckbox.add(i);
               else if (type.equals("select"))
                  indexSelect.add(i);
               else if (type.equals("text"))
                  indexTxt.add(i);
            }
            else if( ei instanceof GroupElement){
               groups.add(i);
            }
                                      
            contador = contarLabels.get(label);
            if(contador == null)
               contador = 0;   
            contador++;      
            contarLabels.put(label,contador);
            if(contador > mayor){
               mayor = contador;
               labelFin = label;   
            }
         }
      	
      	//eliminar cluster con elementos diferentes 
         if(contador==1)
         labelFin="label";
         
         //contabilizar
            int numUIs = indexTxt.size() + indexSelect.size() + indexRadio.size() + indexCheckbox.size();
         int numCompo = indexUICompuesto.size();
         int numGroup = groups.size();
      
         if( (numCompo > numUIs) && (numCompo > numGroup)){
         //NodeCluster(String label, boolean inf, FiniteUI fin, int index){
            return new NodeCluster(labelFin,null,null,indexUICompuesto.get(0));//toma el primero
         }
         else if( (numGroup >= numUIs) && (numGroup >= numCompo)){
         //encuentra el grupo con el mayor numero de hijos
            int idx = -1;
            mayor = 0;
            for(Integer k : groups){
               GroupElement g = (GroupElement)cluster.get(k);
               int numChilds = g.getChilds().size();
               if(numChilds > mayor){
                  idx = k;   
                  mayor = numChilds;
               }
            }
            return new NodeCluster(labelFin,null,null,idx);//toma el indice del grupo con el mayor numero de hijos
         }
         else{//el mas representativo es un UI simple
         //indexTxt.size() + indexSelect.size() + indexRadio.size() + indexCheckbox.size();
            if(indexCheckbox.size() > 0 && ((indexTxt.size() == 0) && (indexSelect.size() == 0) && (indexRadio.size() == 0)))
               return new NodeCluster(labelFin,"checkbox",null,-1);//el UI representativo es un checkbox
         
            String inf = null;
            FiniteUI fui = null;
         
            if(indexTxt.size() > 0){//UI infinito
               inf = "text";
            }
            if(indexSelect.size() > 0 || indexRadio.size() > 0){//existira un elemento finito
            //obtiene los valores de todos los UI finitos, forma el reporistorio y hace el clustering
               LinkedList<Clusterizable> repositoryVals = new LinkedList<Clusterizable>();
               for(Integer i1 : indexSelect){
                  UIcontent ui = (UIcontent)cluster.get(i1);
               //obtiene toda su lista de valores
                  LinkedList<String> values = ui.getValue();
                  for(String v : values)
                     repositoryVals.add(new ValSelect(ui.getForm(), v, ui.getNumNode()));
               }
               for(Integer i1 : indexRadio){
                  UIcontent ui = (UIcontent)cluster.get(i1);
               //obtiene toda su lista de valores
                  LinkedList<String> values = ui.getValue();
                  for(String v : values)
                     repositoryVals.add(new ValSelect(ui.getForm(), v, ui.getNumNode()));
               }
            
            //hace el clustering
               Clustering clusteringVals = new Clustering(repositoryVals,.70);      
               if(indexSelect.size() == 0)
                  fui = new FiniteUI("radio");
               else
                  fui = new FiniteUI("select");
            
               fui.set(clusteringVals.getClusters());
            }
            return new NodeCluster(labelFin,inf,fui,-1);//el UI representativo es un checkbox
         }                   
      //return null;//nunca deberia llagar a este punto
      }
   
      public static Map<NodeCluster,LinkedList<Clusterizable>> labelingClusters(LinkedList<LinkedList<Clusterizable>> clusterList){
      //con los cluster formados, para cada uno de ellos selecciona 
      //el nodo "Clusterizable" mas representativo de ese cluster, 
      
      //NodeCluster modela el objeto representativo del cluster dado
         Map<NodeCluster,LinkedList<Clusterizable>> idClusters;
         idClusters = new HashMap<NodeCluster,LinkedList<Clusterizable>>(); 
      
      //recorre cada cluster
         for(LinkedList<Clusterizable> cluster : clusterList){
            if(cluster.size() <= 2) //debe tener mas de dos, si no vamos con la siguiente
               continue;
         
            NodeCluster nc = labelCluster(cluster); 
         
         //***************Eliminar nodos con etiqueta label**************8   
           String et=nc.getLabel();
         	if(!et.equals("label")){
         //crea otro mapero, de string a nodeCluster
            MapaGlobal.putNodeMap(nc.getLabel(),nc);   
                 
            idClusters.put(nc,cluster);      
            }
         }
      
         return idClusters;
      }          
   
   
      public LinkedList<LinkedList<Clusterizable>> getClusters(){
         
         return clusters;
      }
   
   
      public static void showClusters(Map<NodeCluster,LinkedList<Clusterizable>> list){
         System.out.println("\n CLUSTERING \nShowing results:\n");
         Set set = list.entrySet(); 
         Iterator i = set.iterator(); 
         int conta;// numero de elementos por cluster
         while(i.hasNext()) { 
            Map.Entry me = (Map.Entry)i.next(); 
            LinkedList<Clusterizable> cluster = (LinkedList<Clusterizable>)me.getValue();
            NodeCluster nc = (NodeCluster)me.getKey(); 
            conta=0;
            System.out.print( "["); 
            for(Clusterizable c : cluster){
               System.out.print( c.show() + "; "); 
               conta=conta+1;
            }
            System.out.println( "]\nLabel = " + nc.getLabel()+"  elements:"+conta); 
         
            String inf = nc.infinito();
            FiniteUI fui = nc.getFinite();
            int idx = nc.getIndexNode();
         
            if(idx != -1){
               Clusterizable clus = cluster.get(idx);
               System.out.println("index = " + idx); 
               System.out.println(clus.show()); 
            }
            else{
               System.out.println("infinito = " + inf);
               if(fui == null)
                  System.out.println("finito = null");
               else
                  System.out.println(fui);    
            }  
         
            System.out.println(); 
         }
      }
   
   
   
      public static void main(String[] args){
      
         Map<NodeCluster,LinkedList<Clusterizable>> idClusters = (Map<NodeCluster,LinkedList<Clusterizable>>)Files.readObject("clustering.clus", "Clusters");
      
         if(idClusters == null){
            String ruta = "C:/phD/Datasets/ICQ/Only-WQIs/jobs/";
         
         //String ruta = "C:/phD/Files-Forms/Pruebas/PruebasExpress/"; 
         // String ruta = "C:/datasets/PruebasExpress/";   
         
            LinkedList<PrunedTree> ptress = PrunedTree.processByFolder(ruta,false);
         
            LinkedList<Clusterizable> repository = new LinkedList<Clusterizable>();            
            for (PrunedTree prunedTree : ptress) {  
               Collection<Clusterizable> clusterizablesFromTree = prunedTree.getClusterizableList();	          
               for(Clusterizable c : clusterizablesFromTree)
                  repository.add(c);          	
            }
              
            Preprocessing.clean(repository);      //el repositorio esta listo, primero se limpia
            System.out.println("Repository before WordNet\n");
            for(Clusterizable c : repository )
               System.out.println(c);
         
            ConexionWordNet.process(repository);  //se pasa por Wornet
            System.out.println("Repository after WordNet\n");
            for(Clusterizable c : repository )
               System.out.println(c);
         
            Clustering clustering = new Clustering(repository,0.65);                                                        
         //guarda el resultado del clustering
            idClusters = Clustering.labelingClusters(clustering.getClusters()); 
            clustering = null;
         
            Files.storeObject(idClusters, "clustering.clus", "Clusters");                   
            idClusters = (Map<NodeCluster,LinkedList<Clusterizable>>)Files.readObject("clustering.clus", "Clusters");
         }
      
         Clustering.showClusters(idClusters);
         System.exit(0);
      
      }
   }