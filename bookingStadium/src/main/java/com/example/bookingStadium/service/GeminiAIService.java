package com.example.bookingStadium.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiAIService {
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public GeminiAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public String generateResponse(String userQuery, String context) {
        try {
            // Tạo system prompt cho domain cụ thể
            String systemPrompt = buildSystemPrompt();
            String fullPrompt = systemPrompt + "\n\nNgười dùng hỏi: " + userQuery;
            
            if (context != null && !context.isEmpty()) {
                fullPrompt += "\n\nNgữ cảnh cuộc trò chuyện: " + context;
            }
            
            // Tạo request body theo format Gemini API
            Map<String, Object> requestBody = createRequestBody(fullPrompt);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Gọi Gemini API
            String url = geminiApiUrl + "?key=" + geminiApiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractResponseText(response.getBody());
            } else {
                log.error("Gemini API error: {}", response.getStatusCode());
                return null; // Trả về null để fallback
            }
            
        } catch (Exception e) {
            log.error("Error calling Gemini AI: ", e);
            return null; // Trả về null để fallback
        }
    }
    
    private String buildSystemPrompt() {
        return """
            Bạn là Mi24/7 Bot - trợ lý AI thông minh cho hệ thống đặt sân thể thao Mi24/7.
            
            THÔNG TIN HỆ THỐNG:
            - Hệ thống đặt sân bóng đá, bóng rổ, cầu lông, tennis
            - Có 3 loại người dùng: USER (khách hàng), OWNER (chủ sân), ADMIN (quản trị)
            - Hỗ trợ đặt sân online, thanh toán MoMo, đánh giá sân
            - Có tính năng tự động hủy booking nếu không xác nhận trong 15 phút
            - Các loại sân: Sân 5 người, sân 7 người, sân 11 người
            
            NHIỆM VỤ CỦA BẠN:
            1. Trả lời câu hỏi về đặt sân, giá cả, địa điểm
            2. Hướng dẫn sử dụng hệ thống
            3. Hỗ trợ khách hàng giải quyết vấn đề
            4. Cung cấp thông tin chính sách, quy định
            
            PHONG CÁCH GIAO TIẾP:
            - Thân thiện, nhiệt tình
            - Sử dụng tiếng Việt tự nhiên
            - Ngắn gọn nhưng đầy đủ thông tin
            - Luôn sẵn sàng hỗ trợ thêm
            
            LƯU Ý:
            - Nếu không biết thông tin cụ thể, hãy thừa nhận và đề xuất liên hệ hotline
            - Không đưa ra thông tin sai lệch về giá cả hay chính sách
            - Khuyến khích người dùng đăng ký tài khoản để trải nghiệm đầy đủ
            - Trả lời bằng tiếng Việt và giữ độ dài phù hợp (không quá dài)
            """;
    }
    
    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Contents array
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));
        
        // Generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 512); // Giảm để phản hồi ngắn gọn hơn
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }
    
    private String extractResponseText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.get("candidates");
            
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.get("content");
                JsonNode parts = content.get("parts");
                
                if (parts != null && parts.isArray() && parts.size() > 0) {
                    JsonNode firstPart = parts.get(0);
                    JsonNode text = firstPart.get("text");
                    
                    if (text != null) {
                        return text.asText();
                    }
                }
            }
            
            return null; // Trả về null để fallback
            
        } catch (Exception e) {
            log.error("Error parsing Gemini response: ", e);
            return null; // Trả về null để fallback
        }
    }
} 