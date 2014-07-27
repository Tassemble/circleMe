package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;

public class CirclePoint {

    Double     dis;

    @SerializedName("coordinate")
    Coordinate coordinate;

    @SerializedName("memberInfo")
    MemberInfo memberInfo;
    String     color;

   
    
    
    public Double getDis() {
        return dis;
    }

    
    public void setDis(Double dis) {
        this.dis = dis;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public static class MemberInfo {

        Long    memberId;
        String  nickname;
        Integer sex;

        public Long getMemberId() {
            return memberId;
        }

        public void setMemberId(Long memberId) {
            this.memberId = memberId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Integer getSex() {
            return sex;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }

    }

    public static class Coordinate {

        Double longitude;
        Double latitude;
        
        public Double getLongitude() {
            return longitude;
        }
        
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
        
        public Double getLatitude() {
            return latitude;
        }
        
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        
        
    }

}
