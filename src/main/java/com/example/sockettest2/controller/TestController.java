package com.example.sockettest2.controller;

import com.example.sockettest2.dto.Message;
import com.example.sockettest2.dto.Room;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class TestController {

    // 쿠키에서 방번호, 닉네임 찾기
    public Map<String, String> testfindCookie() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        Cookie[] cookies = request.getCookies();
        String nickname= "";

        if(cookies == null) {
            return null;
        }

        if(cookies != null) {
            for(int i=0;i<cookies.length;i++) {
                if("nickname".equals(cookies[i].getName())) {
                    nickname = cookies[i].getValue();
                }
            }
            Map<String, String> map = new HashMap<>();
            map.put("nickname", nickname);
            return map;
        }
        return null;
    }

    // 쿠키에 추가
    public void addCookie(String cookieName, String cookieValue) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletResponse response = attr.getResponse();

        Cookie cookie = new Cookie(cookieName, cookieValue);

        int maxage = 60 * 60 * 24 * 7;
        cookie.setMaxAge(maxage);
        response.addCookie(cookie);
    }
    public void createNickname(String nickname) {
        addCookie("nickname", nickname);
    }


    // 메인화면
    @GetMapping("/test")
    public String main() {
        return "maintest";
    }

    //	----------------------------------------------------
    // 메세지 컨트롤러

    // 채팅방에서 메세지 보내기
    @MessageMapping("/socket/sendMessage/{roomNumber}")
    @SendTo("/topic/message/{roomNumber}")
    public Message sendMessage(@DestinationVariable String roomNumber, Message message) {
        return message;
    }

}
