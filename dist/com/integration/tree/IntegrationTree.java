package com.integration.tree;

import com.renderTree.*;
import com.clustering.*;
import com.integration.*;
import java.io.* ;
import java.util.*;

public class IntegrationTree implements Serializable{
   NodeIntegration nr;                   //raiz del arbol
   int numArbol;                         //numArbol              
   int totalNodes;                       //
   int altura;   
   int matriz[][] = null;                 //la matriz de restriccion de este arbol   
   Vector<Integer> levels = null;         //lista de apoyo para calculo de distancia de los nodos en el arbol y despliegue de información
   Vector<Integer> fathers = null;
   int[] anscestors = null;
   Vector<String> vectLabels = null;

   LinkedList <NodeIntegration> leafs;                //todos los nodos hoja de este arbol en formato lista para despliegue de informacion
   Map<String,Integer> nodosInteres = null;           //todos los nodos de este arbol en formato tabla para busquedas directas
   
   
//para cada RenderTree, se creara un Integration tree, que es lo mismo 
//pero sin incluir a los nodos a los cuales no se les pudo asociar una etiqueta 
//de remplazo dado el proceso de clustering, es decir, no se incluyen todos los nodos
//con etiquetade remplazo igual a null
//Los nuevos nodos del arbol de integración no incluyen
//el contenido "Renderable", solo contienen la etiqueta de remplazo y
//una referencia al numero de nodo en el RenderTree
   public IntegrationTree(PrunedTree tree){
   
      leafs = new  LinkedList <NodeIntegration>();
      nodosInteres = new HashMap<String,Integer>();
      vectLabels = new Vector<String>();
   
      nr = clonar(tree.getTree());	 //crea la replica del arbol PrunedTree de entrada
      removeNodesNull(nr);           //ahora lo poda      
      obtenerVector(nr);             //obtiene el vector con las etiquetas de los nodos presentes en este arbol
                                     //esto sirve para el proceso de despliegue de la matriz, nombrabdo a los nodos por su etiqueta.
      
      this.numArbol = tree.getNumArbol();
      totalNodes = 0;
      altura = 0;
      calcularDist();      //Asigna numeros a cada uno de los nodos del arbol, 
                           //tambien calcula "altura", "numNodes", y los vectores
                           // de level y distancias
   
      computeMatrix();     //se calcula la matriz de restricción
   }
   private void obtenerVector(NodeIntegration root){
      LinkedList<NodeIntegration> childs = root.getChilds();
      if(childs == null){
         vectLabels.add(root.getLabel());
         //System.out.println(vectLabels);
         return;
      }
      else{
         for(int i=0;i<childs.size();i++)
            obtenerVector(childs.get(i));
      }
   }
     
   //crea el arbol de integración a la misma imagen del arbol render.  
   private NodeIntegration clonar(NodeRenderable root){   
      NodeIntegration newNode = new NodeIntegration(root);
      LinkedList<NodeRenderable> childs  = root.getChilds();
   
      if( childs == null)
         return newNode;      
   
      if( childs.size() == 0)
         return newNode;
     
      for(NodeRenderable c : childs)
         newNode.addChild(clonar(c));
   
      return newNode;   
   }


   public void showTree(){
   //muestra el arbold de integración
      showTree(nr, 0);
   
   }

   private void showTree(NodeIntegration root, int espacios){
   
   //despliega la info del nodo actual y el de sus childs
      if(root == null) 
         return;
   
      String label = root.getLabel();
      int num = root.getNumNodo();
      LinkedList<NodeIntegration> childs = root.getChilds();
   
   
      for(int j = 0; j< espacios; j++) System.out.print(" ");   
   
      if(label == null)
         label = "Nodo";
   
      System.out.println("- " + label + "(" + num + ")");
   
      if(childs == null) 
         return;
      if(childs.size() == 0 ) 
         return;
   
      for(int i = 0; i < childs.size(); i++)
         showTree(childs.get(i), espacios+3);
   }



   private void calcularDist(){
   //recorre el arbol actual en anchura y le asigna una numeración a sus nodos
      LinkedList<NodeIntegration> queue = new LinkedList<NodeIntegration>();
      LinkedList<NodeIntegration> childs = null;
      levels = new Vector<Integer>();
      fathers = new Vector<Integer>();
   
      int numNodo = 0;
      nr.setDistance(0);
      nr.setNumNodo(0);
   
      levels.add(0);
      fathers.add(-1);
   
      altura = 0;
      queue.addLast(nr);
      int dist = 0;
      String label = null;
   
      while (queue.size() != 0){
      // extraemos el nodo u de la cola Q y exploramos todos sus nodos adyacentes
         NodeIntegration u = queue.removeFirst();
         childs = u.getChilds();
      
         if(childs == null){
            leafs.add(u);    //estamos sobre un nodo hoja, ya fue numerada previamente
            label = u.getLabel();
            nodosInteres.put(label,u.getNumNodo());  //habria que ver para que sirve guardar el numero de nodo y no el nodo completo
            continue;
         }
      
         if(childs.size() == 0){//mismo caso que el anterior
            childs = null;
            leafs.add(u);
            label = u.getLabel();
            nodosInteres.put(label,u.getNumNodo());
            continue;
         }
      
         dist = u.getDistance();   
         int padre = u.getNumNodo();
      
         for(NodeIntegration v : childs){
            numNodo++;
            v.setDistance(dist+1);
            v.setNumNodo(numNodo);
            fathers.add(padre);
            levels.add(dist+1);
            queue.addLast(v);
          
         }  
      }
   
      altura = dist + 1;
      totalNodes = numNodo+1;  
   }

   public static void removeNodesNull(NodeIntegration root){
   //visita a este nodo y elimina sus hijos que sean UI y tengan label null, o que tengan label null y no tengan hijos
      if( root == null) 
         return;
        
      LinkedList<NodeIntegration> childs = root.getChilds();
   
      if(childs == null) //es una hoja, regresarse un nivel arriba
         return;
      if(childs.size() == 0){ //es un nodo sin hijos, tambien regresarse un nivel
         childs = null;
         return;
      }
   
   //este nodo tiene hijos analizarlos para remover los nodos null en cada uno de ellos
      for(NodeIntegration n : childs)
         removeNodesNull(n);
   
      int i = 0;
      LinkedList<NodeIntegration> subchilds = null;
      String label = null;
   
   //analiza a cada hijo resultante, si es hoja o no tiene hijos y su etiqueta es null, entonces se quita del arbol   	
      while(i < childs.size()){
         NodeIntegration n = childs.get(i);
         label = n.getLabel();
         if(label == null){
            subchilds = n.getChilds();
            if(subchilds == null){
               childs.remove(i);
               continue;
            }
            if(subchilds.size() == 0){
               childs.remove(i);
               continue;
            }
         }   
         else{//el nodo esta etiquetado
         //realiza un ajuste, si tiene un solo hijo etiquetado, entonces, elimina a ese hijo
            subchilds = n.getChilds();
            if(subchilds == null){
               i++;
               continue;
            }
            if(subchilds.size() == 0 || subchilds.size() == 1){
               n.emptyChilds();
               i++;
               continue;
            }
         
            if(subchilds.size() > 1){
               n.emptyLabel();
               i++;
               continue;
            }
         }
         i++;
      }
   
   }
              
   private void computeMatrix(){
   
      anscestors = new int[totalNodes];
      dfs(nr,2); //esta funcion segmenta el arbol y calcula el ancestro mas cercano para cada nodo en el arbols
        //en este punto ya se puede calcular la distancia entre cualquier par de nodos del arbol
      calcularDistanciasNodos();
   }	

   private void dfs(NodeIntegration node, int gr){
   
      int numNode = node.getNumNodo();
   
   //if node is situated in the first 
   //CASO A: section then P[node] = 1
   //CASO B: if node is situated at the beginning
   //of some section then P[node] = T[node] 
   //if none of those two cases occurs, then 
   //CASO C: P[node] = P[T[node]]
      if (levels.elementAt(numNode) < gr)  //todos los nodos en el primer nivel tienen ancestro 1
         anscestors[numNode] = 1;                                           //CASO A
      else{
         if((levels.elementAt(numNode) % gr) == 0)
            anscestors[numNode] = fathers.elementAt(numNode);               //CASO B
         else
            anscestors[numNode] = anscestors[fathers.elementAt(numNode)];   //CASO C
      }
   
      LinkedList<NodeIntegration> childs = node.getChilds();
   
      if( childs == null) 
         return;
   
      for(NodeIntegration son : childs)
         dfs(son,gr);
   
   }

   private int LCA(int x1, int y1)  {
   //as long as the node in the next section of 
   //x and y is not one common ancestor
   //we get the node situated on the smaller 
   //lever closer
      int x = x1;
      int y = y1;
   
      while (anscestors[x] != anscestors[y])
         if (levels.elementAt(x) > levels.elementAt(y))
            x = anscestors[x];
         else
            y = anscestors[y];
   
   //now they are in the same section, so we trivially compute the LCA
      while (x != y)
         if (levels.elementAt(x) > levels.elementAt(y))
            x = fathers.elementAt(x);
         else
            y = fathers.elementAt(y);
   
   //x tiene el nodo comun, aqui es donde terminaria el algoritmo para encontrar LCA
   
      int distancia = levels.elementAt(x1) - levels.elementAt(x) + levels.elementAt(y1) - levels.elementAt(x);
      return distancia;
   }


   private void calcularDistanciasNodos(){
   //calcula la distancia entre nodos de este arbol
      LinkedList<String> lista = MapaGlobal.getValues();
      int sizeMatrix = lista.size();
      matriz = new int[sizeMatrix][sizeMatrix];
   
      for (int i = 0; i < sizeMatrix; i++){
         String stri = lista.get(i);
         for(int j = 0; j < sizeMatrix; j++){
            String strj = lista.get(j);
         
         
            int numNodei = -1;
            int numNodej = -1;
         
            if(nodosInteres.containsKey(stri))
               numNodei = nodosInteres.get(stri);
         
            if(nodosInteres.containsKey(strj))
               numNodej = nodosInteres.get(strj);
         
            if( (numNodei == -1) || (numNodej == -1))
               matriz[i][j] = 0;
            else if(numNodei == numNodej) {
               matriz[i][i] = levels.elementAt(numNodei);
            }
            else{ 
               matriz[i][j] = LCA(numNodei,numNodej);
            }
         } 
      
      }
   
   }


   public  int[][] getMatrix(){
      return matriz;
   }

   public Vector<String> getVector(){
      return vectLabels;
   }

   public void showMatrix(){
   
      if(matriz==null){
         System.out.println("No hay matriz");
         return;
      }
      int numNode = numNodes();
      System.out.println("Numero de nodos->"+numNode);
      System.out.println("Altura->"+getAltura());
      System.out.print("***Matriz para este arbol:\nNodos:     \t");
      System.out.println("");
   
      LinkedList<String>lista = MapaGlobal.getValues();
   
      for (String n : lista)
         System.out.print("\t"+n);
      for (int i = 0; i < lista.size(); i++){
         System.out.println("");
         String n = lista.get(i);
         System.out.print("   "+n+"\t");
         for(int k = 0 ; k < i; k++)
            System.out.print(" "+"\t");
      
         for(int j = i ; j < lista.size(); j++){
            System.out.print(matriz[i][j]+"\t");
         }   
      }
   
   
      System.out.print("\n Vector de este arbol: ");
   
      for (String n : vectLabels)
         System.out.print(n+", ");
   
      System.out.println("");
   
   }


   public int getDistance(NodeIntegration n1, NodeIntegration n2){
   
      int x = n1.getNumNodo();
      int y = n2.getNumNodo();
   
      if(x < totalNodes && y < totalNodes)
         return LCA(x, y);
      else
         return -1;
   
   }

   public void showVectors(){
      for(int i = 0; i < totalNodes; i++)
         System.out.println("Node = " + i + ": level " + levels.elementAt(i) + ". Father " + fathers.elementAt(i) + ". Ansc " + anscestors[i]);
   }

   public int numNodes(){
      return totalNodes;
   }  
   public int getAltura(){
      return altura;
   }

   private String get(int a){
      for(NodeIntegration n:leafs){
         if(n.getNumNodo()==a)
            return n.getLabel();
      
      }
      return "sal";
   }

   public void mostrarLeafs(){
      System.out.println(leafs);
   }
   public void mostrarTabla(){
      System.out.println(nodosInteres);
   
   }
   public static void main(String[] args) {
      System.exit(0);
   }

   public void calcularDistancias(){
   
      BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
      try{
         System.out.print("Ingrese 1 para calcular una distancia, 0 u otro numero para terminar: ");
         int cont = Integer.parseInt(teclado.readLine());
      
         while(cont == 1){
            System.out.print("Ingrese numero de nodo inicial(vea el arbol impreso arriba): ");
            int x = Integer.parseInt(teclado.readLine());  
            String  n1=get(x);
            System.out.print("Ingrese numero de nodo final (vea el arbol impreso arriba): ");
            int y = Integer.parseInt(teclado.readLine());
            String  n2=get(y);
         
            System.out.println("Distancia del nodo " + n1 + " al " + n2 + " es: " +  LCA(x, y));
            System.out.print("Ingrese 1 para calcular otra distancia, 0 u otro numero para terminar: ");
            cont = Integer.parseInt(teclado.readLine());  
         }
      }
      catch(Exception e){
         System.out.println("An exception occurred: " + e.toString());
      }
   }        
}