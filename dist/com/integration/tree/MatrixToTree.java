   package com.integration.tree;

   import com.renderTree.*;
   import com.clustering.*;
   import com.integration.*;
   import java.io.* ;
   import java.util.*;  

   public class MatrixToTree{
   
   
      public static NodeSchema ProcessMatrixToTree(LinkedList<LinkedList<Integer>> matrix, LinkedList<String> atributos){
      //la matriz es la matriz promedio, el vector de atributos es el vector de etiquetas de esta matriz, que inicialmente se toman del MapaGlobal.values
      
         int numNodeIntermedio = 0;
      
         int iteration = 0;
         LinkedList<NodeSchema> leafs = new LinkedList<NodeSchema>();
      
         for(int i = 0; i < matrix.size(); i++){
            LinkedList<Integer> row = matrix.get(i);
            leafs.add(new NodeSchema(atributos.get(i), row.get(i),-1));
         }
      
      //HOJAS
      
         while( !(matrix.size() == 1 && (matrix.get(0)).size() == 1)){
            LinkedList<NodeSchema> G = new LinkedList<NodeSchema>();
            for(int i = 0; i < matrix.size(); i++){
               NodeSchema a=leafs.get(i);
               a.setRow(i);
            }
         
            for(NodeSchema n : leafs){
               if(n.getDistance() >= 2.0){
                  leafs.set(n.getRow(),null);
                  atributos.set(n.getRow(),null);
                  G.add(n);
               }
            }
            System.out.println("Iteration(" + iteration + "): G = " + G);
            if(G.size() == 0){ 
            //ya no hay grupos, todos son hijos de la raiz
               numNodeIntermedio++;
               NodeSchema root = new NodeSchema("n"+numNodeIntermedio,0,-1);
               for(NodeSchema n : leafs)
                  root.addChild(n);
               return root;
            
            }
         //hacer los grupos   
            LinkedList<LinkedList<NodeSchema>> grupos = new LinkedList<LinkedList<NodeSchema>>();
            int i = 0;
            while(i < G.size()){
               LinkedList<NodeSchema> grupo = new LinkedList<NodeSchema>();
               NodeSchema node = G.get(i);
               grupo.add(node);
               int j = i+1;
               while(j < G.size()) {
                  NodeSchema node2 = G.get(j);
               
                  int k1 = node.getRow();
                  int k2 = node2.getRow();
                  int dist = (matrix.get(k1)).get(k2); //distancia de node a node2
               
                  System.out.println("Testing + " + node2 + ", con distancia = " + dist);
                  if(dist == 2.0){//estan en el mismo grupo
                     grupo.add(node2);
                  
                  // System.out.println("Grupo actual: " + grupo);
                  
                     G.remove(j);
                     continue;
                  }
                  j++;
               }
            
            //if(grupo.size() > 1){
               grupos.add(grupo);
               System.out.println("Grupos actual: " + grupos);
               G.remove(i);
            //continue;
            //}
            //i++;
            }
         
            System.out.println("Iteration(" + iteration + "): Grupos en G = " + grupos);
         
         //crea nodos intermedios
         
            for(i = 0; i < grupos.size(); i ++){
               LinkedList<NodeSchema> grupo = grupos.get(i);
               numNodeIntermedio++;
               NodeSchema newNode = new NodeSchema("n"+numNodeIntermedio,0,-1);
               for(NodeSchema n : grupo)
                  newNode.addChild(n);
               LinkedList<NodeSchema> hijos=newNode.getChilds();
               NodeSchema hijo=hijos.get(0);
            
               leafs.set(hijo.getRow(),newNode);
               atributos.set(hijo.getRow(),"n"+numNodeIntermedio);
            }
         
            System.out.println("Iteration(" + iteration + "): Nuevos nodos antes = " + leafs);
            i=0;
            while(i < leafs.size()){
               NodeSchema va=leafs.get(i);
               if(va==null){
                  leafs.remove(i);
                  atributos.remove(i);
                  continue;
               }
               i++;
            }
            System.out.println("Iteration(" + iteration + "): Nuevos nodos = " + leafs);
         
               
            update(matrix,leafs,atributos);  //se modifica la matrix y los atributos
         
         
         }
         return leafs.get(0);
      
      }
   	
      public static void resortedMatrix(LinkedList<LinkedList<Integer>> matrix,LinkedList<NodeSchema> newAtributes, LinkedList<String> sortedAttrib){
      
         LinkedList<Integer> index = new LinkedList<Integer>();
      
         for(int j = 0; j < sortedAttrib.size(); j++){
            for(NodeSchema node:newAtributes){
                      
               if(node.getSymbol().equals(sortedAttrib.get(j))){
                  index.add(sortedAttrib.indexOf(node.getSymbol()));
                  continue;
               }
               else {
                  if(node.getChilds()!=null){
                     LinkedList<NodeSchema> hijos=node.getChilds();
                     for(NodeSchema child: hijos){
                        if(sortedAttrib.get(j).equals(child)){
                           index.add(sortedAttrib.indexOf(sortedAttrib.get(j)));
                           continue;
                        }
                     
                     }}}
            
            }
         }
         System.out.println("");           
      }
   
      
      public static void update(LinkedList<LinkedList<Integer>> matrix, LinkedList<NodeSchema> newAtributes, LinkedList<String> atributos){
      //crea una nueva matriz, con nodos new + rest
         LinkedList<LinkedList<Integer>> newMatrix = new LinkedList<LinkedList<Integer>>();
      
         
         for(int i = 0; i < newAtributes.size(); i++){
            LinkedList<Integer> row = new LinkedList<Integer>();
            for(int j = 0; j < newAtributes.size(); j++)
               row.add(new Integer(0));
            newMatrix.add(row);
         }
      
         for(int i = 0; i < newAtributes.size(); i++){
            NodeSchema pi = newAtributes.get(i);
            for(int j = 0; j < newAtributes.size(); j++){
               NodeSchema pj = newAtributes.get(j);
               if (pi.getRow() == -1 && pj.getRow() == -1){
               
               
                  LinkedList<NodeSchema> childsPi = pi.getChilds();
                  LinkedList<NodeSchema> childsPj = pj.getChilds();
               
                  NodeSchema chil_i = childsPi.get(0);
                  NodeSchema chil_j = childsPj.get(0);
               
                  int dist = (matrix.get(chil_i.getRow())).get(chil_j.getRow());
                  LinkedList<Integer> row = newMatrix.get(i);  
               
                  if(pi.getSymbol().equals(pj.getSymbol())) 
                     row.set(j,dist-1);   
                  else
                     row.set(j,dist-2);      
               }
               else if(pi.getRow() == -1 && pj.getRow() != -1){
                  LinkedList<NodeSchema> childsPi = pi.getChilds();
               
                  NodeSchema chil_i = childsPi.get(0);
               
                  int dist = (matrix.get(chil_i.getRow())).get(pj.getRow());
                  LinkedList<Integer> row = newMatrix.get(i);  
                  row.set(j,dist-1);
               }
               else if(pi.getRow() != -1 && pj.getRow() == -1){
                  LinkedList<NodeSchema> childsPj = pj.getChilds();      
                  NodeSchema chil_j = childsPj.get(0);
               
                  int dist = (matrix.get(pi.getRow())).get(chil_j.getRow());
                  LinkedList<Integer> row = newMatrix.get(i);  
                  row.set(j,dist-1);
               }
               else if(pi.getRow() != -1 && pj.getRow() != -1){
                  LinkedList<NodeSchema> childsPj = pj.getChilds();      
                  int dist = (matrix.get(pj.getRow())).get(pi.getRow());
                  LinkedList<Integer> row = newMatrix.get(i);  
                  row.set(j,dist);
               }
               else{
                  System.out.println("ERROR!!!!!");
               }
            }   
         }
      
         matrix.clear();
         matrix.addAll(newMatrix);
      
         showAVGMatriz(matrix,atributos);
      	
      }
   
   
      public static void showSchemaTree(NodeSchema root, int espacios){
      
      //despliega la info del nodo actual y el de sus childs
         if(root == null) 
            return;
      
         
         String simbol = root.getSymbol();
         String newlabel = MapaGlobal.getLabel(simbol);
         LinkedList<NodeSchema> childs = root.getChilds();
              
         for(int j = 0; j< espacios; j++) System.out.print(" "); 
         if(newlabel != null)  
            System.out.println(simbol+"->"+newlabel);
         else
            System.out.println(simbol);
      
      
         if(childs != null){   
            for(int i = 0; i < childs.size(); i++)
               showSchemaTree(childs.get(i), espacios+3);
         }
      }
   /*
      public static void renameLabel(Map<String,String> mapa,NodeSchema root){
          
         //arbol.renameNode(numNode,idCluster);
         searchElements(mapa,root);
         
      
      }
      
      
      public static void searchElements(Map<String,String> mapa,NodeSchema root){
      
      //despliega la info del nodo actual y el de sus childs
         if(root == null) 
            return;
      
         String label = root.getSymbol();
         LinkedList<NodeSchema> childs = root.getChilds();
         for (Map.Entry<String, String> entry : mapa.entrySet()) {
            if(entry.getValue().equals(label)) {
               String sal= entry.getKey();
               root.setNewLabel(sal);
            }
         }
         
         if(childs != null){   
            for(int i = 0; i < childs.size(); i++)
               searchElements(mapa,childs.get(i));
         }
        
      }
   //Asociacion del tipo de elemento(UI-select, text,checkbox; Composed o Group) a cada simbolo del arbol integrado
      public static void updateElements(Map<NodeCluster,LinkedList<Clusterizable>> clusters, NodeSchema root){
         
         String labelTree = root.getNewLabel();
         LinkedList<NodeSchema> childs = root.getChilds();
         Set set = clusters.entrySet(); 
         Iterator j = set.iterator(); 
         while(j.hasNext()) { 
            Map.Entry me = (Map.Entry)j.next(); 
            NodeCluster key = (NodeCluster)me.getKey();
            String labelCluster = key.getLabel();
            if(labelCluster.equals(labelTree))
               root.setInfo(me);
         }
         if(childs != null){   
            for(int i = 0; i < childs.size(); i++)
               updateElements(clusters, childs.get(i));
         }
      }
      
   	
      public int assignarNumNode(NodeSchema root, int numNode){
         LinkedList<NodeSchema> childs = root.getChilds();
         root.setNumNodo(numNode);
         int numNodes;
                  
         if(root == null) 
            return 0;
           if(childs != null){    
         for(int i = 0; i < childs.size(); i++){
            numNode++;
            assignarNumNode(childs.get(i),numNode);
         }  
         }
      
         return numNodes = numNode + 1; 
      }
            
      
   
   	
   	
   	
   	
      public static void displayFullTree(NodeSchema root, int espacios){
      
      //despliega la info del nodo actual y el de sus childs
         if(root == null) 
            return;
      
         String newlabel = root.getNewLabel();
         String simbol = root.getSymbol();
         LinkedList<NodeSchema> childs = root.getChilds();
         
               
         for(int j = 0; j< espacios; j++) System.out.print(" "); 
         if(newlabel != null){ 
            Map.Entry me = root.getInfo(); 
            LinkedList<Clusterizable> cluster = (LinkedList<Clusterizable>)me.getValue();
            NodeCluster nc = (NodeCluster)me.getKey(); 
            System.out.println(simbol+"->"+newlabel);
            String inf = nc.infinito();
            FiniteUI fui = nc.getFinite();
            int idx = nc.getIndexNode();
            if(idx != -1){
               Clusterizable clus = cluster.get(idx);
               System.out.println("index = " + idx); 
               System.out.println(clus.show()); 
               if(clus instanceof GroupElement){
                  GroupElement g= (GroupElement)clus;
                  LinkedList<UIcontent> UIchilds=g.getChilds();
                  for (int j = 0; j < UIchilds.size(); j++) {
                     UIcontent ui= UIchilds.get(j);
                     String tipo=ui.getType();
                     System.out.println(tipo+", "+ui);
                     
                  }
               }
               
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
         else
            System.out.println(simbol);
      
      
         if(childs != null){   
            for(int i = 0; i < childs.size(); i++)
               displayFullTree(childs.get(i), espacios+3);
         }
      }
      
     
   
   */
   
      public static void showAVGMatriz(LinkedList<LinkedList<Integer>> matrix, LinkedList<String> atributos){
      
         if(matrix==null){
            System.out.println("No hay matriz PRELIMINAR");
            return;
         }
        
         System.out.println("**********  MATRIZ PRELIMINAR ***********");
      
         for (String n : atributos)
            System.out.print("\t"+n);
      
         for (int i = 0; i < atributos.size(); i++){
            System.out.println("");
            String n = atributos.get(i);
            System.out.print("   "+n+"\t");
            for(int k = 0 ; k < i; k++)
               System.out.print(" "+"\t");
         
            LinkedList<Integer> row = matrix.get(i);
         
            for(int j = i; j < atributos.size(); j++){
            //int cifras=(int) Math.pow(10,1);
               System.out.print(row.get(j)+"\t");
            }   
         }
         System.out.println("");
      
         System.out.println("**********  FIN DE MATRIZ ***********");
         System.out.println("");
         System.out.println("");
      
      
      }
   }