package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewInputForm {

    @NotNull(message = "評価点数を入力してください。")
    private Integer score;
        
    @NotBlank(message = "コメントを入力してください。")
    private String comment;

}
