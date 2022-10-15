package com.example.sockettest2.controller;

import com.example.sockettest2.dto.Message;
import com.example.sockettest2.dto.ResponseDto;
import com.example.sockettest2.dto.Room;
import com.example.sockettest2.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class TestController {

    private final SimpMessageSendingOperations messageSendingOperations;

    List<String> userList = new ArrayList<>();

    // 메인화면
    @GetMapping("/test")
    public String main() {
        return "maintest";
    }


    // 유저가 소켓에 연결 요청
    // destinationvariable = pathvariable 이랑 비슷한 거
    @MessageMapping("/{userId}")
    public void testmessage(@DestinationVariable("userId") Integer userId, Message message){
        // config에서 지정한 endpoint localhost:8080/websocket으로 연결하면
        // 클라이언트에서 /sub/{userId}를 구독함
        // /websocket으로 요청하면 소켓이 연결됨

        messageSendingOperations.convertAndSend("/sub/"+userId,
                "alarm socket connection complete");
    }

//    // 이걸 실행시키면 '이벤트'가 일어났을 때 알람을 발송해줌
//    public void alarmByMessage(Message message){
//        messageSendingOperations.convertAndSend("/sub/"+message.getUserId(), message);
//    }

    // https://kukekyakya.tistory.com/12
    // 이거 보고 뷰 이해하기
    // 참고2 https://velog.io/@gywls1474/%ED%95%AD%ED%95%B499-Weekly-I-learned-%EC%8B%A4%EC%A0%84-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%8E%B8-websocket-Stomp-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%B4%88%EB%8C%80%EC%95%8C%EB%A6%BC-%EA%B5%AC%ED%98%84
    // http 세션과 webSock 세션 연결 https://reinvestment.tistory.com/57
    // 그 2번 교과서 같은 글 https://daddyprogrammer.org/post/4691/spring-websocket-chatting-server-stomp-server/


    @MessageMapping("/alarmtest")
    @SendTo("/sub/test/{companyId}")
    public ResponseDto test2(@DestinationVariable String companyId, Message message, WebSocketSession session) throws Exception{
        // https://velog.io/@mindfulness_22/websocket-userinfo

        User userData = (User) session.getAttributes().get("keyValue");
        userList.add(userData.getUserId());

        Boolean checkId = findById(companyId);
        if(checkId==false){
            return null;
        }

        System.out.println(message.getMessage());
        return new ResponseDto(true, message);
    }

    public Boolean findById(String companyId){ // 나중에 스트링으로(uuid를 통해 세션 키값 변경하고 나면)
        for(String user : userList){
            if(companyId.equals(user)){
                return true;
            }
        }
        return false;
    }
}
