package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	public void /*List<ArtObject>*/ listObjects(Map<Integer, ArtObject> map) {
		
		String sql = "SELECT * from objects";
		//List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
                if(!map.containsKey(res.getInt("object_id"))) {
                	ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
    						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
    						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
    						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
                map.put(artObj.getId(), artObj);
                //gli oggetti vengono creati una sola volta 
                }
				
				
				//result.add(artObj);
			}
			conn.close();
			//return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			//return null;
		}
	}
    //APPROCCIO 1 
	public int getPeso(ArtObject a1, ArtObject a2) {
		String sql = "SELECT count(*) as peso  "
				+"FROM exhibition_objects e1, exhibition_objects e2 "
				+"WHERE e1.exhibition_id = e2.exhibition_id AND "
				+"e1.object_id = ? AND e2.object_id = ?";
		
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a2.getId());
			ResultSet res = st.executeQuery();
			int peso=0;
			if(res.next()) {
				peso = res.getInt("peso");
			}
			
			st.close();
			res.close();
			conn.close();
			return peso;

			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	//APPROCCIO 2 
	//mi faccio dare dal DB tutte le cippie con il relativo peso
	
	public List<Adiacenza> getAdiacenze() {
		String sql = "SELECT e1.object_id as id1, e2.object_id as id2, count(*) as peso "
				+"FROM exhibition_objects e1, exhibition_objects e2 "
				+"WHERE e1.exhibition_id = e2.exhibition_id AND "
				+"e1.object_id > e2.object_id "//=> per evitare i duplicati
				+"GROUP BY e1.object_id, e2.object_id";
		Connection conn = DBConnect.getConnection();
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				result.add(new Adiacenza(res.getInt("id1"), res.getInt("id2"), res.getInt("peso")));//per ogni riga creo l'oggetto di classe adiacenza 
				                                                                                    //e lo vado ad inserire nella lista
			}
			st.close();
			res.close();
			conn.close();
			return result;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	
}
