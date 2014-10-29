package org.tassemble.circle.domain.dto.json;

import com.google.gson.annotations.SerializedName;

public class MemberInfoDto {

    
    Long    memberId;
    
    private String nickname;
    Integer sex;
    
    

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
