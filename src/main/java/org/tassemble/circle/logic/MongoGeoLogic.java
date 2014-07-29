package org.tassemble.circle.logic;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tassemble.circle.domain.dto.json.CirclePointDto;

import com.game.utils.CommonUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


@Component
public class MongoGeoLogic {

    

    
    @Autowired
    MongoClient mongoClient;
    
    
    
    public void closingALive(Long uid) {
        
        DBObject iLocationQuery = new BasicDBObject();
        iLocationQuery.put("memberInfo.memberId", uid);
        //update
        
        DBObject object = new BasicDBObject();
        DBObject memberInfo = new BasicDBObject();
        memberInfo.put("memberInfo.live", false);
        object.put("$set", memberInfo);
        getDefaultCollection().update(iLocationQuery, object);
    }
    
    
    public void becomeALive(Long uid) {
        DBObject iLocationQuery = new BasicDBObject();
        iLocationQuery.put("memberInfo.memberId", uid);
        //update
        DBObject object = new BasicDBObject();
        DBObject memberInfo = new BasicDBObject();
        memberInfo.put("memberInfo.live", true);
        object.put("$set", memberInfo);
        getDefaultCollection().update(iLocationQuery, object);
    }
    
    
    
    
    public DBCollection getDefaultCollection() {
        DB db = getDefaultDB();
        DBCollection collction = db.getCollection("location");
        
        return collction;
    }
    
    
    
    public DB getDefaultDB() {
        DB db = mongoClient.getDB("aroundme");
        
        return db;
    }
}
