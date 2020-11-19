package com.spring.view;

import java.util.ArrayList;
import java.util.List;
 
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.RemoteEndpoint.Basic;
 
@Controller
@ServerEndpoint(value="/echo.do")
public class ChattingController extends TextWebSocketHandler{
    
    private static final List<Session> sessionList=new ArrayList<Session>();;
    private static final Logger logger = LoggerFactory.getLogger(ChattingController.class);
    public ChattingController() {
        // TODO Auto-generated constructor stub
        System.out.println("웹소켓(서버) 객체생성");
    }
    @RequestMapping(value="/chat.do")
    public ModelAndView getChatViewPage(ModelAndView mav) {
        mav.setViewName("chat");
        return mav;
    }
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Open session id:"+session.getId());
        try {
            final Basic basic=session.getBasicRemote();
            basic.sendText("<span style='font-weight:bold'>채팅방에 입장하셨습니다.</span><br>"
            		+ "<span style='color:red; font-weight:bold'>광고, 욕설, 반말, 비방 등의 행위에 대해선 학사 경고 등의 조치가 취해질 수 있음을 알려드립니다.</span>");
        }catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        sessionList.add(session);
    }
    /*
     * 모든 사용자에게 메시지를 전달한다.
     * @param self
     * @param message
     */
    private void sendAllSessionToMessage(Session self,String message) {
        try {
            for(Session session : ChattingController.sessionList) {
                if(!self.getId().equals(session.getId())) {
                    session.getBasicRemote().sendText("<div align='left' style='height:5px; padding-left:10px'><span style='font-weight:bold;'>" + message.split(",")[0] + "</span> : " + message.split(",")[1] + "</div>");
                }
            }
        }catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
    }
    @OnMessage
    public void onMessage(String message,Session session) {
        logger.info("Message From "+message.split(",")[1] + ": "+message.split(",")[0]);
        try {
            final Basic basic=session.getBasicRemote();
            basic.sendText("<div align='left' style='height:5px; padding-left:10px'><span style='font-weight:bold;'>" + message.split(",")[0] + "</span> : " + message.split(",")[1] + "</div>");
        }catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        sendAllSessionToMessage(session, message);
    }
    @OnError
    public void onError(Throwable e,Session session) {
        
    }
    @OnClose
    public void onClose(Session session) {
        logger.info("Session "+session.getId()+" has ended");
        sessionList.remove(session);
    }
}


