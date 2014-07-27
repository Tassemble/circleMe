package org.tassemble.circle.service.test;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.game.base.dao.BaseTestCase;
import com.game.utils.GsonUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class CircleServiceTest extends BaseTestCase{

    

    @Autowired
    MongoClient mongoClient;
    
    
    //db.runCommand({ geoNear: "location", near: [ 121.4905, 31.2646 ], spherical: true, maxDistance : 1, distanceMultiplier: 6371 })
    @Test
    public void test() {
        String json =  "geoNear: \"location\", near: [ 121.4905, 31.2646 ], spherical: true, maxDistance : 1, distanceMultiplier: 6371 }\"";
        DBObject dbObject = new BasicDBObject();
        dbObject.put("geoNear", "location");
        dbObject.put("near", Arrays.asList(121.4905, 31.2646));
        dbObject.put("spherical", "true");
        dbObject.put("maxDistance", 1);
        dbObject.put("distanceMultiplier", 6371000);
        
        CommandResult result = mongoClient.getDB("aroundme").command(dbObject);
        
        
        System.out.println(result.getString("results"));
//        ObjectMapper mapper = new ObjectMapper();
        
        
//        System.out.println(GsonUtils.toJson(result));
    }
}
