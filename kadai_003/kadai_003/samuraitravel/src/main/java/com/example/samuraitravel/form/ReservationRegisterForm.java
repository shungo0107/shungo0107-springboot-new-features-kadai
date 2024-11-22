package com.example.samuraitravel.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/* 予約内容の確認ページから登録処理（コントローラのcreate()メソッド）に
 * データを渡すためのフォームクラス。
 * ReservationInputFormクラスですでにバリデーションをおこなっているため、
 * ReservationRegisterFormクラスではバリデーションを行わず、単純にデータを格納する役割に徹する。
 */
public class ReservationRegisterForm {    
    private Integer houseId;
        
    private Integer userId;    
        
    private String checkinDate;    
        
    private String checkoutDate;    
    
    private Integer numberOfPeople;
    
    private Integer amount;    
}