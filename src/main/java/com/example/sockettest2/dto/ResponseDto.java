package com.example.sockettest2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    Boolean checkResult;
    Object data;

    public ResponseDto(boolean b, Message message) {
    }
}
