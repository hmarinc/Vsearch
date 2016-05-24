package com.renderTree;

import com.clustering.*;
import com.util.Files;

import java.io.* ;
import org.lobobrowser.html.*;
import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.* ;
import org.w3c.dom.* ;
import org.xml.sax.InputSource;
import org.lobobrowser.html.domimpl.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.renderer.*;
import org.lobobrowser.html.style.RenderState;
import org.lobobrowser.util.EventDispatch2;
import org.lobobrowser.util.gui.WrapperLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.w3c.dom.html2.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

  
import java.net.*; 
import java.util.logging.* ;

public class PrunedTree implements Serializable{
   String url ;
   int pageRank;
   String source;			                          // The page to be processed.
   NodeRenderable nr;                             // La raiz haciendo referencia al arbol
   int numArbol;                                  // numero de arbol

   LinkedList <Clusterizable> listUIs;            //contine unicamente nodos UIs  en este arbol
                                               //(no compuestos ni grupos) en representacion 
                                               //Clusterizable
   LinkedList <Clusterizable> clusterizableList;  //todos los nodos de
                                               //interes, en version clusterizable
                                               //UiContent para nodo UI
                                               //Compouesto para nodo UI compuesto
                                               //Group para nodo que contiene a un grupo de UIs
   Map<Integer,NodeRenderable>  nodesInterest;    //nodos NodeRenderable que 
                                               //son UI (simples o compuestos) 
                                               // o nodos raiz de un grupo.
                                               //se usa basicamente para hacer el renaming

  

   int numLeafs; //numero de hojas en este arbol
   int numNodes; //numero de nodos totales en este arbol
   
	


 
                                               
   public PrunedTree(String source,int pRank, String url, int numForm, boolean simple) {
      this.numLeafs = 0;
      this.numNodes = 0;
      this.pageRank = pRank;
      this.url = url ;   
      this.source =source;
      nr = null;	 
      this.numArbol = numForm;
      listUIs = null;
      nr = parsePage(simple);                //construye el arbol render tree
      clusterizableList = null;
      listUIs = null;
      nodesInterest = null;
      //refURL = new HashMap<Integer,String>(); 
   
      if(!simple){
         assignarNumeros();               //Asigna numeros a cada uno de los nodos del arbol, tambien calcula "altura", "numNodes", y los vectores de leves y distancias
         calcularCusterizables();          //calcula la lista de custerizables, que incluye UIs, Compuestos y grupos. Se almacenan todos los nodos de interes en una tabla
      }
   //System.out.println("Termina constructor");
   }

   public int getPageRank(){
      return pageRank;
   }
//dado un numero de nodo, se regresa el clusterizable correspondiente
   public Clusterizable getClusterizable(int numNodo){
      for(Clusterizable c : clusterizableList){
         int n = c.getNumNode();
         if(numNodo == n) 
            return c;
      }
      return null;
   }

   public void showDetails(String path){
      if(clusterizableList != null){
         try{
            PrintWriter printWriter = new PrintWriter (path + ".txt");
            printWriter.println("number form: " + numArbol);
            printWriter.println("source: "+source);
            printWriter.println("number of nodes");
            printWriter.println(numNodes);
            printWriter.println("\nnumber of leafs");
            printWriter.println(numLeafs);
            showCusterizables(printWriter);
            printWriter.close();
         }
         catch(Exception e){
            System.out.println(e.toString());
         }
      }
      else
         System.out.println("Unable to show details on this render tree. Is is not pruned or not labeled.");
   }
   public NodeRenderable getTree(){
      return nr;
   }

   public int getNumArbol(){
      return numArbol;
   
   }
   public String getUrl(){
      return url;
   
   }
   public String getSource(){
      return source;
   }


   private NodeRenderable parsePage(boolean simple) {
      Logger.getLogger("").setLevel(Level.OFF);  
      URL urlObj = null ;
   
      try {
         urlObj = new URL(url) ;
      
         URLConnection connection = urlObj.openConnection();
         InputStream in = connection.getInputStream();
         UserAgentContext context = new SimpleUserAgentContext();    
      
         final HtmlPanel panel = new HtmlPanel();
         UserAgentContext uacontext = new SimpleUserAgentContext();
         final SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(panel, uacontext);
         DocumentBuilderImpl dbi = new DocumentBuilderImpl(uacontext,rcontext);
         Document document = dbi.parse(new InputSourceImpl(in, url,"ISO-8859-1")) ;
      
         HTMLDocumentImpl nodeImpl = (HTMLDocumentImpl) document; //root node en HTML panel
      
         HtmlBlockPanel shp = new HtmlBlockPanel(java.awt.Color.WHITE, true, uacontext, rcontext, panel);
         shp.setPreferredWidth(-1);
         shp.setDefaultMarginInsets(new Insets(8, 8, 8, 8));//Insets definida en AWT
         shp.setDefaultOverflowX(RenderState.OVERFLOW_AUTO);
         shp.setDefaultOverflowY(RenderState.OVERFLOW_SCROLL);
      
         NodeRenderer nodeRenderer = shp;
         nodeRenderer.setRootNode(nodeImpl);       
      
         RBlock labelNode = new RBlock(nodeImpl, 0, uacontext, rcontext, panel, shp);
         nodeImpl.setUINode(labelNode);
      
         RBlockViewport bl = new RBlockViewport(nodeImpl, labelNode, 0, uacontext, rcontext,panel, labelNode);
         bl.setOriginalParent(labelNode);
         bl.setX(Short.MAX_VALUE);
         bl.setY(Short.MAX_VALUE);
      
         System.out.println("Building render tree for: " + url);
      
         try {
            labelNode.doLayout(200, 200, true, true, null,
               0, 0, false);
         
         } 
         finally {   
         }
      
         System.out.println("Pruning the render tree... wait ...");
      
         nr = PrunedTreeBuilder.buildTreeRenderables(labelNode, null,simple);
         
         if(!simple){
            System.out.println("Ajuste de etiquetas");
            PrunedTreeBuilder.ajustarEtiquetas(nr);
            System.out.println("Remover bloques");   
            PrunedTreeBuilder.removerBloques(nr);
            PrunedTreeBuilder.removerLabels(nr);
            PrunedTreeBuilder.colapseRadios(nr);
         
         //System.out.println("Ajuste final");
         //PrunedTreeBuilder.ajusteFinal(nr);   //los ui sin etiquetas se intentan agregar a posible grupos
         //System.out.println("Termina Ajuste final");
         }
      
         System.out.println("---- DONE ------- ");
      
         return nr;   
      
      
      }
      catch(Exception e) {
         System.out.println("Exception in parsePage(" + url + "): \n ") ;
         e.printStackTrace();
         return null;
      }
   }

   public void showTree(){
      if(nr != null){
         System.out.println("\nRender tree for: " + url);	
         PrunedTreeBuilder.showRenderableTree(nr, 0);
      }
      else
         System.out.println("\nThe render tree for " + url + " is NULL. Nothing to display\n");	
   }

   public void showTree(String path){
      if(nr != null){
         try{
            PrintWriter printWriter = new PrintWriter (path + ".txt");
            PrunedTreeBuilder.showRenderableTree(nr, 0, printWriter);
            printWriter.close();
            System.out.println("\nRender tree for: " + url + " was stored in file: \n" + path + ".txt");
         }
         catch(Exception e){
            System.out.println(e.toString());
         }
      }
      else
         System.out.println("\nThe render tree for " + url + " is NULL. Nothing to display\n");	
   }

   private void showCusterizables(PrintWriter printWriter){
      for(Clusterizable c: clusterizableList)
         printWriter.println(c);
   }

   private void assignarNumeros(){
   //recorre el arbol actual en anchura y le asigna una numeración a sus nodos
      LinkedList<NodeRenderable> queue = new LinkedList<NodeRenderable>();
      LinkedList<NodeRenderable> childs = null;
      int numNodo = 0;
      nr.setNumNodo(0);
      queue.addLast(nr);
   
      while (queue.size() != 0){
      // extraemos el nodo u de la cola Q y exploramos todos sus nodos adyacentes
         NodeRenderable u = queue.removeFirst();
         childs = u.getChilds();
      
         if(childs == null) 
            continue;
      
      
         for(int i = 0; i < childs.size(); i++){
            numNodo++;
            NodeRenderable v = childs.get(i);
            v.setNumNodo(numNodo);
            queue.addLast(v);
         }  
      }
   
      numNodes = numNodo + 1; 
   }
         
   
   public void showTreeByNumNode(){
      if(nr != null){
         System.out.println("\n\nPRUNED TREE by node number\n");	
         PrunedTreeBuilder.showRenderableTreeByNumber(nr, 0);
      }
      else
         System.out.println("\n\nThe tree is NULL. Nothing to display\n");	
   //System.exit(0);
   
   }
   
   public LinkedList<Clusterizable> getListUI(){              
      return listUIs;
   }   

   public Collection<Clusterizable> getClusterizableList(){
      return clusterizableList;
   }   
               
   private UIcontent convNodeRendeToUI(NodeRenderable nr){
   
      boolean ban=false;
      String name="";
      String valor="";
      LinkedList value = new LinkedList();  
      String type = nr.getType();
      String label = nr.getLabel();
      Renderable ren = nr.getRenderable();
      int numNode = nr.getNumNodo();
      ModelNode node = ((RUIControl)ren).getModelNode();
      NodeImpl ni = (NodeImpl)node;
   
   //Obtener atributos del nodos UI <UI NODE:nombre,label,value> 
      NamedNodeMap attribs = ni.getAttributes();
      int length = attribs.getLength();
      for (int i = 0; i < length; i++) {
         Attr attr = (Attr) attribs.item(i);
         if(attr.getNodeName().toLowerCase().equals("name"))
            name = attr.getNodeValue().toLowerCase();
         if (attr.getNodeName().toLowerCase().equals("value")){
            valor=attr.getValue().toLowerCase();
            valor = valor.trim();
            valor = valor.replaceAll("\\s+", " ");
            value.add(valor);
         }
      }			
   	
      if("select".equals(ni.getNodeName().toLowerCase())) {
         NodeList nl = ni.getChildNodes();
         int num = nl.getLength() ;
         if(num == 0)
            return null;  
         int n=0; 
         for(int j=0; j<num; j++){
            Node a1= nl.item(j);
            if((a1.getNodeName().toLowerCase()).equals("option")){
               valor=a1.getTextContent();
               valor = valor.trim();
               valor = valor.replaceAll("\\s+", " ");
               value.add(valor);
               n=n+1;
            }
         }
      }
   		
      if( valor.equals("") )
         value.add("");
   
      UIcontent ei = new UIcontent(numArbol,numNode,type,label,name,value);
      return ei;
   }

   private GroupElement convNodeRendeToGroup(NodeRenderable nr, LinkedList<NodeRenderable> childs, int cont, TreeSet<Integer> considered){
   //los primeros nodos de este grupo se consideran en la agrupacion
   
      String label = nr.getLabel();
      int numNode = nr.getNumNodo();  
      LinkedList<UIcontent> children = new LinkedList<UIcontent>();
   
      NodeRenderable ui = null;  
   
      for(int k = 0; k < cont; k++){
         ui = childs.get(k);
         considered.add(ui.getNumNodo());
         numLeafs++;
         UIcontent element = convNodeRendeToUI(ui);        
         children.add(element);   
      }       
      GroupElement ei = new GroupElement(numArbol,label,numNode,children);   	
   
      return ei;
   }

   private void calcularCusterizables(){
   //toma el estado actual del arbol para calcular todos los nodos que son UI, Compuesto o grupo
   
      nodesInterest = new HashMap<Integer,NodeRenderable>();
      listUIs =  new LinkedList<Clusterizable>();
      clusterizableList =  new LinkedList<Clusterizable>();
      TreeSet<Integer> considered = new TreeSet<Integer>();   
   //en la busqueda de los clusterizables, se realizan las agrupaciones
   //de los UI tipo radio, para manejar solamente un clusterizable
   //se utiliza un mapa, donde se almacena el nombre de la variable del radio, y una lista con la pareja [etiqueta, valor]
   
      findClusterizables(nr, considered);
   }

   private void findClusterizables(NodeRenderable root, TreeSet<Integer> considered){
   
   //find uis
      Renderable ren = root.getRenderable();
      Integer numNode = root.getNumNodo();
      String labelNode = root.getLabel();
      String type = root.getType();
      LinkedList<NodeRenderable> childs = root.getChilds();
   
      if (ren instanceof RUIControl){   //solo considera nodos que sean UI, los cuales son hojas
      
         if(considered.contains(numNode))//si este nodo ya fue considerado, por ejemplo, en un grupo, salir
            return;
      
         numLeafs++;   //estamos sobre un nuevo UI, que es una hoja
      
         if(type.equals("radio")){
            UIcontent ei = new UIcontent(numArbol,numNode,type,"",labelNode,root.getValues());
            nodesInterest.put(numNode,root);
            clusterizableList.add(ei);
            listUIs.add(ei);
            return;
         }
      
         UIcontent element = convNodeRendeToUI(root);
      
      //verifia si es un campo compuesto             
         String content = (String)element.getValue().get(0);      //Identificacion de campos compuestos: mm/dd/aaaa  mm/aaaa  nombre_apellido   
         type = element.getType();
      
         if (type.equals("text") && (content.contains("/")|| content.contains(",")||content.contains("&&")||content.matches("[0-9]?[0-9][ ][a-z]+[ ]((19|20)\\d\\d)")||content.matches("[a-z]+[ ]((19|20)\\d\\d)"))){
            UIcompuesto compo = new UIcompuesto(numArbol,numNode, element.getType(), element.getLabel(), element.getName(), element.getValue());     
            nodesInterest.put(numNode,root); 
            clusterizableList.add(compo);
         }
         else{//es UI
            nodesInterest.put(numNode,root);
            clusterizableList.add(element);
            listUIs.add(element);   
         }
      }//si estamos en nodo que contiene a otros nodos, y este esta etiquetado:
      else if (((ren instanceof RBlock) || (ren instanceof RTable) || (ren instanceof RLine) || (ren instanceof RGroup))&&  !labelNode.equals("no label")){                  
         if(childs.size()>1) {//el container debe tener solo UIs los cuales 
                           //no deben estar etiquetados y deben ser consecutivos
            int cont = 0;
            for(int i = 0; i < childs.size(); i++){
               NodeRenderable child= childs.get(i);
               Renderable rdr = child.getRenderable();
               if(rdr instanceof RUIControl){
                  String label = child.getLabel();
                  if(label.equals("no label"))
                     cont = cont + 1;
                  else
                     break;
               }
               else
                  break;
            }
            if(cont >= 2){ //Si hay mas de un UI no etiquetado consecutivo
               GroupElement group = convNodeRendeToGroup(root, childs,cont,considered);
               if(group != null){
                  nodesInterest.put(numNode,root);
                  clusterizableList.add(group);
               }
            }
         }   
      }  
   
      if(childs == null ) 
         return;
   
      for(int i = 0; i < childs.size(); i++)
         findClusterizables(childs.get(i),considered);      
   }

   public void renameNode(int numNode, String newLabel){
   
      if (nodesInterest.containsKey(numNode)){
         NodeRenderable node = nodesInterest.get(numNode);
         node.setNewLabel(newLabel);
      }
   }
   
	
      
   public static LinkedList<PrunedTree> processByFolder(String ruta, boolean simple){
      
           
      File folder = new File(ruta);
      if(!folder.exists()){
         System.out.println("The folder: \"" + ruta + "\" does not exist. Program finished.");
         return null;
      }
   
      System.out.println("Launching PRUNED TREE program from folder: \"" + ruta + "\".");
      LinkedList<PrunedTree> prunedTreeList = new LinkedList<PrunedTree>();
      int numForm     = 0;
      
      String fileName = "";
      String url = "";
      String source ="";
      String sourceUrl = "";	
      
      Vector<String> text = new Vector<String>();
         
      //open the DOMAINS file
      System.out.println("Opening: " + ruta + "DOMAINS.dat");
      java.util.List<LinkedList> infoDomains = null;
      try{
         infoDomains = (java.util.List<LinkedList>)Files.readObject(ruta + "DOMAINS.dat", "Domains info");
      }
      catch(Exception e){
         System.out.println(e.toString());
         return null;
      }
      
      int i=0;
      
      for(LinkedList list:infoDomains){
         sourceUrl = (String)list.get(0);
         sourceUrl = sourceUrl.trim();
         String id = (String)list.get(1);
         String action = (String)list.get(2);
         int pr = (Integer)list.get(3);
         
         String salida="";
         if(action.startsWith("http:"))
            salida = action;
         if(action.startsWith("/") && sourceUrl.endsWith("/")){
            String sub = action.substring(action.indexOf(action)+1);
            salida = sourceUrl+sub; 	
         }
         if(action.startsWith("/") && !sourceUrl.endsWith("/"))	
            salida = sourceUrl + action; 
         if(!action.startsWith("/") && !sourceUrl.endsWith("/"))	
            salida = sourceUrl + "/" + action; 
         if(!action.startsWith("/") && sourceUrl.endsWith("/"))	
            salida = sourceUrl + action;
         
         id = id + ".html";
         url = "file:///" + ruta + id;
         System.out.println(" URL: " + url+"("+i+"), Source: " + salida);
         PrunedTree p = new PrunedTree(salida,pr,url,numForm, simple);
         numForm++;
         i++;
         prunedTreeList.add(p); 
                                       
         if(simple)
            p.showTree(ruta + "simple_" + id);                             //la salida se va un archivo
         else{
            p.showTree(ruta + "pruned_" + id);                             //la salida se va un archivo
            p.showDetails(ruta + "details_" + id);
         }          
      }
         
                      
      if(prunedTreeList.size() > 0)
         return prunedTreeList;
      else
         return null;
         
      
   }
   
	
	
        
   public static void processByFiles(String ruta, boolean simple){
   
      File folder = new File(ruta);
      if(!folder.exists()){
         System.out.println("The folder: \"" + ruta + "\" does not exist. Program finished.");
         return;
      }
   
      String urls[] = {"form72.html"};
      String source=" http://www.abebooks.com/";
   
   
      
      String url = "";
   //LinkedList<PrunedTree> trees = new LinkedList<PrunedTree>();
   
      for (String fileName : urls){
         url = "file:///" + ruta + fileName;
         PrunedTree p = new PrunedTree(source,5,url,0, simple) ; //false = completo, true = simple
      //trees.add(p);
         if(simple)
            p.showTree("simple_" + fileName);                             //la salida se va un archivo
         else{
            p.showTree("pruned_" + fileName);
            p.showDetails(fileName);       
         }
      }            
   }

   public static void main(String[] args) {
      String ruta = "C:/phD/Files-Forms/Pruebas/PruebasExpress/";//"C:/Problem-Forms/";   
              
      boolean simple = true; //false = completo, true = simple
   
      LinkedList<PrunedTree> ptress = processByFolder(ruta,simple);
   //Files.storeObject(ptress, "prunedTrees.ptre", "Pruned tree array");      //no se puede almacenar, ya que todos los objetos Renderable deberian hacerse Serializables
   //processByFiles(ruta, simple);                    
      System.exit(0);
   }
}