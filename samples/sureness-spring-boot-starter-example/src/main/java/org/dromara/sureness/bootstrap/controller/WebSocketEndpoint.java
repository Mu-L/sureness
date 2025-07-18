package org.dromara.sureness.bootstrap.controller;

import org.dromara.sureness.subject.SubjectSum;
import org.dromara.sureness.util.SurenessContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * @author wangtao
 */
@Component
@ServerEndpoint(value = "/webSocket/demo")
public class WebSocketEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);

    private static final String SUBJECT_KEY = "subject";

    @OnOpen
    public void onOpen(Session session) {
        logger.info("webSocket: /webSocket/demo onOpen, session is : {} ", session);
        // storage user info in session
        try {
            SubjectSum subject = SurenessContextHolder.getBindSubject();
            logger.info("the login user info is {}", subject);
            session.getUserProperties().put(SUBJECT_KEY, subject);
        } finally {
            SurenessContextHolder.clear();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("webSocket: /webSocket/demo receive message: {}, the session is : {} ",
                message, session);
        SubjectSum subject = (SubjectSum)session.getUserProperties().get(SUBJECT_KEY);
        logger.info("the message from user info is: {}", subject);
    }

    @OnClose
    public void onClose() {
        logger.info("webSocket: /webSocket/demo on Close");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("webSocket: /webSocket/demo on Error, the session is {} ",
                session, error);
    }
}
