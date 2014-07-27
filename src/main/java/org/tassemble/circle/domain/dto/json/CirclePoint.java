package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;


public class CirclePoint {
    
    
    
    double dis;
    
    
    @SerializedName("coordinate")
    Coordinate coordinate;
    
    
    @SerializedName("memberInfo")
    MemberInfo memberInfo;
    String color;
    
    
    
    
   public static class MemberInfo {
       Long memberId;
       String nickname;
       Integer sex;
   }
    
    public static class Coordinate {
        
        double longitude;
        double latitude;
        
        
        
    }
  
}
