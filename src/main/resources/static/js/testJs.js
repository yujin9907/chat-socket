$(document).ready(function(){

    const socket = new SockJS('/websocket');
    const stomp = Stomp.over(socket);
    stomp.debug = null; // stomp 콘솔출력 X

// 메인 화면
    const main = function() {
        $("main").show();

        const subscribeId = stomp.subscribe("/topic/roomList", function(){
            // "/topic/roomList"에서 메세지가 왔을때 실행할 함수
            chatingRoomList();
        });
    };

    stomp.connect({}, function(){
        main();
    });


// ----------------- 메인화면 ---------------------------



// ----------------- 채팅방 ---------------------------

// 메세지 그리기
    const chating = function(messageInfo){
        let nickname = messageInfo.nickname;
        let message = messageInfo.message;

        message = message.replaceAll("\n", "<br>").replaceAll(" ", "&nbsp");

        const date = messageInfo.date;
        const d = new Date(date);

        const time = String(d.getHours()).padStart(2, "0")
            + ":"
            + String(d.getMinutes()).padStart(2, "0");

        let sender = "";

        if(info.getNickname() == nickname) {
            sender = "chat_me";
            nickname = "";
        } else {
            sender=  "chat_other";
        }


        const chatHtml = `
        <li>
            <div class=${sender }>
            	<div>
	            	<div class="nickname">${nickname }</div>
	            	<div class="message">
		                <span class=chat_in_time>${time }</span>
		                <span class="chat_content">${message }</span>
	                <span>
                </div>
            </div>
        </li>`;

        $(".chat ul.chat_list").append(chatHtml);

        $(".chat ul").scrollTop($(".chat ul")[0].scrollHeight);
    }


// 채팅방 구독
    const chatingConnect = function(roomNumber){
        // 기존 구독 취소
        subscribeCancle();

        // 메세지를 받을 경로
        const id1 = stomp.subscribe("/topic/message/" + roomNumber, function(result){
            const message = JSON.parse(result.body);

            // 메세지가 왔을때 실행할 함수
            chating(message);
        })

        // 입장,퇴장 알림을 받을 경로
        const id2 = stomp.subscribe("/topic/notification/" + roomNumber, function(result){
            const room = JSON.parse(result.body);
            const message = room.message;

            // 메세지가 왔을때 실행할 함수
            userList(room.users);

            const chatHtml = `
	        <li>
	        	<div class="notification">
            		<span>${message}</span>
            	</div>
	        </li>`;

            $(".chat ul.chat_list").append(chatHtml);

            $(".chat ul").scrollTop($(".chat ul")[0].scrollHeight);

        })

        subscribe.push(id1);
        subscribe.push(id2);
    }



// 채팅방 세팅
    const initRoom = function(room, nickname) {
        // 방 목록 업데이트
        stomp.send("/socket/roomList");

        $("main").hide();

        info.setNickname(nickname);
        info.setRoomNumber(room.roomNumber);

        $(".chat").show();
        $(".chat .chat_title").text(room.roomName);

        userList(room.users);
        chatingConnect(room.roomNumber);

        $(".chat_input_area textarea").focus();
    }


// 메세지 보내기
    const sendMessage = function(){
        const message = $(".chat_input_area textarea");

        if (message.val() == "") {
            return;
        }

        const roomNumber = info.getRoomNumber();
        const nickname = info.getNickname();

        const data = {
            message : message.val(),
            nickname : nickname,
        }

        stomp.send("/socket/sendMessage/" + roomNumber, {}, JSON.stringify(data));
        message.val("");
    }




    $(".chat_button_area button").click(function() {
        sendMessage();
        $(".chat_input_area textarea").focus();
    })


    $(".chat_input_area textarea").keypress(function(event) {
        if (event.keyCode == 13) {
            if (!event.shiftKey) {
                event.preventDefault();

                sendMessage();
            }
        }
    })


// 닉네임 만들고 채팅방 들어가기
    const enterChatingRoom = function(roomNumber) {

        swal({
            text: "사용하실 닉네임을 입력해주세요",
            content: "input",
            buttons: ["취소", "확인"],
            closeOnClickOutside : false
        })
            .then(function(nickname){
                if(nickname) {

                    const data = {
                        roomNumber : roomNumber,
                        nickname : nickname
                    }

                    $.ajax({
                        url: "/chatingRoom-enter",
                        type: "GET",
                        data: data,
                    })
                        .then(function(room){
                            initRoom(room, nickname);

                            // 채팅방 참가 메세지
                            room.message = nickname + "님이 참가하셨습니다";
                            stomp.send(
                                "/socket/notification/" + roomNumber, {},
                                JSON.stringify(room));

                        })
                        .fail(function(result){
                            errorMSG(result);
                        })
                }
            })
    }



// 새 채팅방 만들기
    const createRoom = function(roomName) {
        swal({
            text: "사용하실 닉네임을 입력해주세요",
            content: "input",
            buttons: ["취소", "확인"],
            closeOnClickOutside : false
        })
            .then(function(nickname){
                if(nickname) {

                    const data = {
                        roomName : roomName,
                        nickname : nickname
                    }

                    $.ajax({
                        url: "/chatingRoom",
                        type: "POST",
                        data: data,
                    })
                        .then(function(room){
                            initRoom(room, nickname)
                        })
                        .fail(function(){
                            alert("에러가 발생했습니다");
                        })
                }
            })
    }




    $(".new_chat").click(function(){
        swal({
            text: "방 이름을 입력해주세요",
            content: "input",
            buttons: ["취소", "확인"],
            closeOnClickOutside : false
        })
            .then(function(roomName){
                if(roomName) {
                    createRoom(roomName);
                }
            })
    })






    $(document).on("dblclick", "main li", function(){
        const roomNumber = $(this).data("room_number");
        enterChatingRoom(roomNumber);
    })




// 채팅방 나가기
    $(".chat_back").click(function() {
        swal({
            text: "대화방에서 나갈까요?",
            buttons: ["취소", "확인"]
        })
            .then(function(result){
                if(result) {
                    $.ajax({
                        url: "/chatingRoom-exit",
                        type: "PATCH",
                    })
                        .then(function(room){
                            const roomNumber = info.getRoomNumber();

                            if(room.users.length != 0) {
                                // 채팅방 나가기 메세지
                                room.message = info.getNickname() + "님이 퇴장하셨습니다";
                                stomp.send(
                                    "/socket/notification/" + roomNumber, {},
                                    JSON.stringify(room));
                            }

                            // 채팅방 목록 업데이트
                            stomp.send("/socket/roomList");

                            main();
                            $(".chat").hide();
                            $(".chat ul.chat_list").html("");

                            info.setRoomNumber("");
                            info.setNickname("");
                        })
                        .fail(function(){
                            errorMSG();
                        })
                }
            })
    })



// 대화 중이던 방
    const chatingRoom = function (){
        let returnRoom = null;

        $.ajax({
            url: "/chatingRoom",
            type: "GET",
            async: false,
        })
            .then(function(result){
                if(result != "") {
                    const room = result.chatingRoom;
                    const nickname = result.myNickname;
                    initRoom(room, nickname);
                    returnRoom = result;
                }
            })
            .fail(function(result){
                errorMSG(result);
            })

        return returnRoom;
    };


}) // document.ready
