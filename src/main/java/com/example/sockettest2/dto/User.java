package com.example.sockettest2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String userId; // 세션키값, 난수로 uniqe하게
    private Integer username;
}
