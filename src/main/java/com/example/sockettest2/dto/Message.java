package com.example.sockettest2.dto;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    private Integer companyId; // 메시지를 받을 유저
    private String message;
    private String nickname;
    //private Date date;

//    Message(){
//        date = new Date();
//    }
}