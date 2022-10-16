<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!doctype html>
<html lang="kr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>

    <!-- sock js -->
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.2/sockjs.min.js"></script>
    <!-- STOMP -->
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

    <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>

</head>
<body>

테스트 페이지 입니다

<button id="connection" type="button">연결하기</button>
<button id="connection2" type="button">연결하기2</button>
<button id="disconnection" type="button">해제하기</button>
<input id="testmessage" type="text"><button id="btnsend" type="button">전송</button>

<div id = "testbox"></div>

<script>
    let sample = 1;

    $("#connection2").click(function () {connect2(); });
    $("#connection").click(function () {connect(); });
    $("#disconnection").click(function () {disconnect(); });
    $("#send").click(function () {senddata(); });

    // 연결
    let stomp="";

    function connect2(){
        let socket = new SockJS('/websocket');
        stomp = Stomp.over(socket);

        stomp.connect({}, function () {
            console.log('2 연결됨');
            stomp.subscribe('/sub/test/'+2, function (result){
                console.log('구독중2');
                let parsingRusult = JSON.parse(result.body);
                console.log(parsingRusult)
                viewMesaage(parsingRusult);
            });
        });

    }

    function connect(){
        let socket = new SockJS('/websocket');
        stomp = Stomp.over(socket);

        stomp.connect({}, function () {
            console.log('연결됨');
            stomp.subscribe('/sub/test/'+sample, function (result){
                console.log('구독중');
                let parsingRusult = JSON.parse(result.body);
                console.log(parsingRusult)
                viewMesaage(parsingRusult);
            });
        });

    }

    // 연결해제
    function disconnect(){
        stomp.unsubscribe();
        console.log('연결해제');
    }

    // 메시지 보내기
    $("#btnsend").click(() =>{
       senddata();
    });
    function senddata(){
        let data = {
            'subscriber':1,
            'message':'어떤짓거리를했는가',
            'nickname':'음....',
        };

        stomp.send("/pub/alarmtest/1", {}, JSON.stringify(data));
    }

    // 메시지 그리기
    function viewMesaage(message){
        let username = message.nickname;
        let messagedata = message.message;

        // message = message.replaceAll("\n", "<br>").replaceAll(" ", "&nbsp");

        let messageDraw =`<p>`+username+`이 보낸 메시지 :`+messagedata+`</p>`;
        $("#testbox").append(messageDraw);
    }
</script>
</body>
</html>