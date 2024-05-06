package com.example.tabelog_nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeForm {
	@NotBlank(message = "現在のパスワードを入力してください")
    private String oldPassword;

    @NotBlank(message = "新しいパスワードを入力してください")
    @Size(min = 8, message = "パスワードは8文字以上で設定してください")
    private String newPassword;

    @NotBlank(message = "新しいパスワードを再入力してください")
    private String confirmNewPassword;
}
