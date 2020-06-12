package com.fangtan.hourse.domain;

import com.fangtan.hourse.config.CustomParam;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserRegForm {
//    @Length(min = 6, max = 20, message = "validate.userRegForm.nickname")
    private String nickname;

    @CustomParam
    private String gender;

    @NotNull
//    @Email(message="validate.userRegForm.email")
    private String email;
}