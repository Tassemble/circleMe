package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;

public class CirclePointReadMode {

    Double     dis;
    
   
    String     color;
    
    
    @SerializedName("obj")
    ObjWrapper obj;
    
    
    
    
    
    
    public ObjWrapper getObj() {
        return obj;
    }


    
    public void setObj(ObjWrapper obj) {
        this.obj = obj;
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

 


}
