package utm.tn.dari.modules.annonce.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.elastic.documents.USearchQDoc;
import utm.tn.dari.modules.annonce.elastic.repositories.USearchQElasticRepo;

import java.util.ArrayList;
import java.util.List;

@Service
public class USearchQElasticSearchService {

    @Autowired
    private USearchQElasticRepo uSearchQElasticRepo;
    public List<USearchQDoc> getAllUSearchQDocsByQuery(String query){
       try {
           System.out.println(query);
           return uSearchQElasticRepo.findAllByQueryMatches(query);
       }catch (Exception e){
           e.printStackTrace();
       }
       return new ArrayList<>();
    }
    public List<USearchQDoc> getAll(String query){
        try {
            System.out.println(query);
            List<USearchQDoc> uSearchQDocs = new ArrayList<>();
             uSearchQElasticRepo.findAll().forEach(uSearchQDocs::add);
             return uSearchQDocs;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public USearchQDoc createUSearchQDoc(Long id,String query){
        try {
            USearchQDoc uSearchQDoc = new USearchQDoc(id,query);
            System.out.println("QUERY "+ query);
            return uSearchQElasticRepo.save(uSearchQDoc);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
