package com.kodlamaio.common.dataAccess;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestDAL {//ODEME BİLGİLERİ 
    @NotBlank(message = "Card number is required")
    @Length(min = 16, max = 16, message = "Card number must be 16 characters long")
    private String cardNumber; //KART NUMARASINI GİR
    
    
    @NotBlank(message = "Full name is required")
    @Length(min = 3, message = "Full name must be at least 3 characters long")
    private String fullName;//KARTIN SAHİBİNİN ADI
    
    @NotNull(message = "Card expiration year is required")
    @Min(value = 2022, message = "Card expiration year must be at least current year")
    private int cardExpirationYear;//KARTIN BİTİŞ YILI
    
    
    @NotNull(message = "Card expiration month is required")
    @Min(value = 1, message = "Card expiration month must be between 1 and 12")
    @Max(value = 12, message = "Card expiration month must be between 1 and 12")
    private int cardExpirationMonth;//KARTIN BİTİŞ AYI
    
    
    @NotBlank(message = "Card CVV is required")
    @Length(min = 3, max = 3, message = "Card CVV must be 3 characters long")
    private String cardCvv;//KARTIN CVV'SI
}
