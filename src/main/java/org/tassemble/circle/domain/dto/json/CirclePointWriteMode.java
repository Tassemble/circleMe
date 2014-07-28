package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;

public class CirclePointWriteMode {

    Double     dis;
    private Double     degree;
    
   
    String     color;
    
    
    @SerializedName("coordinate")
    Coordinate coordinate;

    @SerializedName("memberInfo")
    MemberInfo memberInfo;

    
    public CirclePointWriteMode() {}
    
    
    public CirclePointWriteMode(CirclePointReadMode readMode) {
        this.color = readMode.getColor();
        this.setCoordinate(readMode.getObj().getCoordinate());
        this.setMemberInfo(readMode.getObj().getMemberInfo());
        
    }
    
    public Coordinate getCoordinate() {
        return coordinate;
    }

    
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    
    public MemberInfo getMemberInfo() {
        return memberInfo;
    }

    
    public void setMemberInfo(MemberInfo memberInfo) {
        this.memberInfo = memberInfo;
    }
    
    
    
    public Double getDis() {
        return dis;
    }

    
    public void setDis(Double dis) {
        this.dis = dis;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getDegree() {
        return degree;
    }


    public void setDegree(Double degree) {
        this.degree = degree;
    }

    


}
