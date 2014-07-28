package org.tassemble.circle.domain.dto.json;

import org.tassemble.member.domain.CircleMember;

import com.google.gson.annotations.SerializedName;


public class CirclePointDto {

    Double     dis;
    private Double     degree;
    
   
    Integer     color;
    
    Coordinate coordinate;

    
    MemberInfoDto memberInfo;
    
    
    

    
    public CirclePointDto() {}
    
    
    public CirclePointDto(CirclePointReadMode readMode) {
        this.color = CircleMember.CIRCLE_COLOR_GREY;
        this.dis = readMode.getDis();
        this.setCoordinate(readMode.getObj().getCoordinate());
        this.setMemberInfo(readMode.getObj().getMemberInfo());
        
    }
    
    public Coordinate getCoordinate() {
        return coordinate;
    }

    
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    
    public MemberInfoDto getMemberInfo() {
        return memberInfo;
    }

    
    public void setMemberInfo(MemberInfoDto memberInfo) {
        this.memberInfo = memberInfo;
    }
    
    
    
    public Double getDis() {
        return dis;
    }

    
    public void setDis(Double dis) {
        this.dis = dis;
    }

    
    
    public Integer getColor() {
        return color;
    }


    
    public void setColor(Integer color) {
        this.color = color;
    }


    public Double getDegree() {
        return degree;
    }


    public void setDegree(Double degree) {
        this.degree = degree;
    }

    


}
