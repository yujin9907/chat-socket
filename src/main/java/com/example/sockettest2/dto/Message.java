package com.example.sockettest2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@Data
public class Message {
    private Integer userId; // 메시지를 받을 유저
    private String message;
    private String nickname;
    //private Date date;

//    Message(){
//        date = new Date();
//    }
}