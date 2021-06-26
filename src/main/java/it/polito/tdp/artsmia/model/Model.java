package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	//il testo mi dice un grafo pesato semplice e non orientato (all'esame verrà specificato)
	//avrà come vertici tutti gli oggetti presenti nel DB 
	private Graph<ArtObject,DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	//identity map -> al suo interno salviamo la corrispondenza tra l'id di un oggetto e l'oggetto stesso
	//questa mappa viene riempita la prima volta e poi riutilizzata
	//una volta che la mappa è riempita non faccio più una new per creare l'oggetto ma lo prendo dalla mappa
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		//grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//=> non conviene metterla nel costruttore altrimenti ogni volta che bisogna rifare un nuovo grafo
		//devo ricordarmi di metterlo in quello precedente
		//mi conviene metterlo in un metodo
		dao = new ArtsmiaDAO();
		idMap = new HashMap<Integer, ArtObject>();
		
	}
	public void creaGrafo() { //metodo chiamato dal controller quando l'utente cliccherà sul pulsante per creare il grafo
		//potrei dover mettere un parametro nel caso in cui non voglio mettere tutti gli oggetti ma solo alcuni con determinate condizioni
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//come riempire il grafo
		
		//AGGIUNTA DEI VERTICI
		//1) recupero tutti gli ArtObject dal db
		//2) li inserisco come vertici
		//List<ArtObject> vertici = dao.listObjects();
		dao.listObjects(idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		
		//AGGIUNTA DEGLI ARCHI
		
		//APPROCCIO 1 -> doppio ciclo for sui vertici -> non sempre porta ad una soluzione in tempi ragionevoli
		//-> Dati due vertici, controllo se sono collegati
		/*for(ArtObject a1 : this.grafo.vertexSet()) {
			for(ArtObject a2 : this.grafo.vertexSet()) {
				//controllo che i due oggetti siano diversi e che non ci sia già un arco
				if(!a1.equals(a2) && !this.grafo.containsEdge(a1,a2)) {
					//devo colegare a1 ad a2? => posso cheiderlo al DAO
					//il peso dell' arco deve essere almeno 1 -> "l'arco esiste se le opere sono state esposte almeno una volta assieme"
				    int peso = dao.getPeso(a1,a2);
				    if(peso>0) { //se invece non avessi avuto il bisogno di mettere pesi avrei potuto metetre if(peso>=0)
				    	Graphs.addEdge(this.grafo, a1, a2, peso);
				    	//dobbiamo trovare una query che ritorni l'eventuale peso dell'arco
				    }
				}
			}
		}
		
		System.out.println("grafo creato");
		System.out.println("vertici:" + grafo.vertexSet().size());
		System.out.println("archi:" + grafo.edgeSet().size());
		//questo approccio funziona con un numero di vertici molto basso
	}*/
	
	//APPROCCIO 2 => per numero di vertici alto
		for(Adiacenza a : dao.getAdiacenze()) {
				Graphs.addEdge(this.grafo, idMap.get(a.getId1()),idMap.get(a.getId2()), a.getPeso());
				
		}
		System.out.println("grafo creato");
		System.out.println("vertici:" + grafo.vertexSet().size());
		System.out.println("archi:" + grafo.edgeSet().size());
	}
}
