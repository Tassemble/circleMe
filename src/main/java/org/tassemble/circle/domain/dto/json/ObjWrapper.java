package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;


public class ObjWrapper {


    
    
    @SerializedName("coordinate")
    Coordinate coordinate;

    @SerializedName("memberInfo")
    MemberInfoDto memberInfo;

    
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
    
}
