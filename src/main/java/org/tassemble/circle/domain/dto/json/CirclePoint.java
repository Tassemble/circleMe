package org.tassemble.circle.domain.dto.json;


public class CirclePoint {
    
    double dis;
    
    Coordinate coordinate;
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
