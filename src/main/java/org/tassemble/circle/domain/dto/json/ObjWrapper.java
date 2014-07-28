package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;


public class ObjWrapper {


    
    
    @SerializedName("coordinate")
    Coordinate coordinate;

    @SerializedName("memberInfo")
    MemberInfo memberInfo;

    
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
    
}
