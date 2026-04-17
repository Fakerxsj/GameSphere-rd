
package com.xsj.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xsj.entity.Message;
import com.xsj.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    private static final Map<Long, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session);
        log.info("✅ WebSocket 连接建立成功！会话ID: {}, URI: {}, 用户ID: {}",
                session.getId(), session.getUri(), userId);
        if (userId != null) {
            SESSION_MAP.put(userId, session);
            log.info("用户 {} 已连接到 WebSocket，当前在线用户数: {}", userId, SESSION_MAP.size());
        } else {
            log.warn("⚠️ 无法从 WebSocket 会话中提取用户ID");
        }
    }




    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("🔔 WebSocket 收到原始消息: {}", message.getPayload());
        Long userId = extractUserId(session);
        if (userId == null) {
            log.warn("⚠️ 消息处理失败：无法获取用户ID");
            return;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            Long toUserId = jsonNode.get("toUserId").asLong();
            String content = jsonNode.get("content").asText();
            Integer messageType = jsonNode.has("messageType") ? jsonNode.get("messageType").asInt() : 1;

            log.info("📝 收到消息: 从 {} 到 {}, 内容: {}", userId, toUserId, content);

            Message msg = new Message();
            msg.setFromUserId(userId);
            msg.setToUserId(toUserId);
            msg.setContent(content);
            msg.setMessageType(messageType);
            msg.setIsRead(0);
            msg.setCreateTime(new java.util.Date());

            // 这里之前可能因为缺少 @TableId 导致 save 失败
            boolean success = messageService.save(msg);
            if (!success) {
                log.error("❌ 数据库写入失败，消息未保存！");
                return;
            }
            log.info("✅ 消息已存入数据库，ID: {}", msg.getId());

            // 推送给接收方
            WebSocketSession toSession = SESSION_MAP.get(toUserId);
            if (toSession != null && toSession.isOpen()) {
                JsonNode responseNode = objectMapper.createObjectNode()
                        .put("id", msg.getId())
                        .put("fromUserId", userId)
                        .put("toUserId", toUserId)
                        .put("content", content)
                        .put("messageType", messageType)
                        .put("createTime", msg.getCreateTime().getTime());
                toSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseNode)));
            }

            // 确认给发送方
            JsonNode ackNode = objectMapper.createObjectNode()
                    .put("status", "success")
                    .put("messageId", msg.getId())
                    .put("createTime", msg.getCreateTime().getTime());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(ackNode)));

        } catch (Exception e) {
            log.error("❌ 处理消息失败，完整异常:", e);
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            SESSION_MAP.remove(userId);
            log.info("用户 {} 断开 WebSocket 连接，当前在线用户数: {}", userId, SESSION_MAP.size());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = extractUserId(session);
        log.error("❌ WebSocket 传输错误，用户: {}, 错误: {}", userId, exception.getMessage(), exception);
        if (session.isOpen()) {
            session.close();
        }
    }

    private Long extractUserId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    try {
                        return Long.parseLong(param.split("=")[1]);
                    } catch (NumberFormatException e) {
                        log.error("解析 userId 失败: {}", param);
                    }
                }
            }
        }
        return null;
    }

    public void sendMessageToUser(Long userId, String message) {
        WebSocketSession session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送消息给用户 {} 失败", userId, e);
            }
        }
    }
}
