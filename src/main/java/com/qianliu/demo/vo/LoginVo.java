package com.qianliu.demo.vo;

import com.qianliu.demo.validate.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 8)
    private String password;
}
