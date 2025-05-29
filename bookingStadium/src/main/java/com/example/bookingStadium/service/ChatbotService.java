package com.example.bookingStadium.service;

import com.example.bookingStadium.dto.request.Chatbot.ChatbotRequest;
import com.example.bookingStadium.dto.response.Chatbot.ChatbotResponse;
import com.example.bookingStadium.entity.Booking;
import com.example.bookingStadium.entity.Stadium;
import com.example.bookingStadium.entity.Type_Of_Stadium;
import com.example.bookingStadium.repository.BookingRepository;
import com.example.bookingStadium.repository.StadiumRepository;
import com.example.bookingStadium.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class ChatbotService {
    @Autowired
    private StadiumRepository stadiumRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private TypeRepository typeRepository;
    
    @Autowired
    private GeminiAIService geminiAIService;
    
    private final Map<Pattern, String> basicFAQs;
    
    public ChatbotService() {
        basicFAQs = new HashMap<>();
        initializeBasicFAQs();
    }
    
    public ChatbotResponse processQuery(ChatbotRequest request) {
        String query = request.getQuery();
        String userId = request.getUserId();
        List<String> conversationContext = request.getConversationContext();
        
        try {
            // 1. Thử sử dụng Gemini AI trước
            String systemContext = buildSystemContext(userId);
            String contextString = "";
            if (conversationContext != null && !conversationContext.isEmpty()) {
                contextString = String.join("\n", conversationContext);
            }
            
            String fullContext = systemContext + "\n" + contextString;
            String aiResponse = geminiAIService.generateResponse(query, fullContext);
            
            if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                return ChatbotResponse.builder()
                        .answer(aiResponse)
                        .sourceType("gemini_ai")
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Gemini AI error, falling back to rule-based: " + e.getMessage());
        }
        
        // 2. Fallback về rule-based system
        return processQueryRuleBased(request);
    }
    
    private String buildSystemContext(String userId) {
        StringBuilder context = new StringBuilder();
        
        try {
            List<Stadium> stadiums = stadiumRepository.findAll();
            context.append("THÔNG TIN SÂN HIỆN TẠI:\n");
            context.append("- Tổng số sân: ").append(stadiums.size()).append("\n");
            
            List<Type_Of_Stadium> types = typeRepository.findAll();
            context.append("- Các loại sân: ");
            for (Type_Of_Stadium type : types) {
                context.append(type.getTypeName()).append(", ");
            }
            context.append("\n");
            
            if (userId != null && !userId.isEmpty()) {
                List<Booking> userBookings = bookingRepository.findByUserId(userId);
                context.append("- Số lần đặt sân của người dùng: ").append(userBookings.size()).append("\n");
            }
            
        } catch (Exception e) {
            // Silent fail
        }
        
        return context.toString();
    }
    
    private ChatbotResponse processQueryRuleBased(ChatbotRequest request) {
        String query = request.getQuery().toLowerCase();
        
        // 1. Kiểm tra FAQ
        String faqAnswer = findFAQAnswer(query);
        if (faqAnswer != null) {
            return ChatbotResponse.builder()
                    .answer(faqAnswer)
                    .sourceType("faq")
                    .build();
        }
        
        // 2. Xử lý câu hỏi về đặt sân
        if (containsKeywords(query, "đặt sân", "booking", "đặt", "thuê sân")) {
            return ChatbotResponse.builder()
                    .answer("Để đặt sân bóng trên Mi24/7:\n" +
                            "1. Đăng nhập vào tài khoản\n" +
                            "2. Tìm kiếm sân bóng phù hợp\n" +
                            "3. Chọn ngày và khung giờ trống\n" +
                            "4. Xác nhận thông tin đặt sân\n" +
                            "5. Thanh toán\n" +
                            "6. Nhận xác nhận qua email")
                    .sourceType("guide")
                    .build();
        }
        
        // 3. Xử lý câu hỏi về sân
        if (containsKeywords(query, "sân", "stadium", "giá")) {
            List<Stadium> stadiums = stadiumRepository.findAll();
            return ChatbotResponse.builder()
                    .answer("Mi24/7 hiện có " + stadiums.size() + " sân bóng đang hoạt động.\n" +
                            "Bạn có thể tìm kiếm sân theo khu vực, loại sân hoặc tên sân cụ thể.")
                    .sourceType("stadium_list")
                    .build();
        }
        
        // 4. Xử lý câu hỏi về thanh toán
        if (containsKeywords(query, "thanh toán", "payment", "trả tiền", "momo")) {
            return ChatbotResponse.builder()
                    .answer("Mi24/7 hỗ trợ các phương thức thanh toán:\n" +
                            "1. Thanh toán trực tiếp tại sân\n" +
                            "2. Ví điện tử (Momo)")
                    .sourceType("payment_info")
                    .build();
        }
        
        // 5. Trả về câu trả lời mặc định
        return ChatbotResponse.builder()
                .answer("Xin lỗi, tôi không có thông tin về câu hỏi của bạn. Bạn có thể thử hỏi về cách đặt sân, thông tin sân bóng, hoặc các câu hỏi thường gặp khác.")
                .sourceType("default")
                .build();
    }
    
    private String findFAQAnswer(String query) {
        for (Map.Entry<Pattern, String> entry : basicFAQs.entrySet()) {
            Pattern pattern = entry.getKey();
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    private void initializeBasicFAQs() {
        // Đăng ký và đăng nhập
        basicFAQs.put(Pattern.compile("(cách|làm sao|làm thế nào)?.*(đăng ký|tạo tài khoản)"), 
            "Để đăng ký tài khoản Mi24/7:\n" +
            "1. Truy cập trang chủ Mi24/7\n" +
            "2. Nhấn vào nút \"Đăng ký\"\n" +
            "3. Điền đầy đủ thông tin cá nhân\n" +
            "4. Xác nhận email\n" +
            "5. Hoàn tất đăng ký");
            
        basicFAQs.put(Pattern.compile("(cách|làm sao|làm thế nào)?.*(đăng nhập|login)"), 
            "Để đăng nhập vào tài khoản Mi24/7:\n" +
            "1. Truy cập trang chủ Mi24/7\n" +
            "2. Nhấn vào nút \"Đăng nhập\"\n" +
            "3. Nhập email và mật khẩu\n" +
            "4. Nhấn \"Đăng nhập\"");
            
        basicFAQs.put(Pattern.compile("(quên|mất|reset|thay đổi).*(mật khẩu|password)"), 
            "Để lấy lại mật khẩu đã quên:\n" +
            "1. Truy cập trang đăng nhập\n" +
            "2. Nhấn vào \"Quên mật khẩu\"\n" +
            "3. Nhập email đã đăng ký\n" +
            "4. Làm theo hướng dẫn trong email được gửi tới bạn");
        
        basicFAQs.put(Pattern.compile("(chính sách|cách|làm sao|làm thế nào)?.*(hủy|đổi|hoàn tiền|hoàn phí|refund)"), 
            "Chính sách hủy/đổi và hoàn tiền:\n" +
            "1. Hủy trước 24h: hoàn tiền 100%\n" +
            "2. Hủy trước 12h: hoàn tiền 50%\n" +
            "3. Hủy trước 6h: hoàn tiền 30%\n" +
            "4. Hủy trong vòng 6h: không hoàn tiền");
            
        basicFAQs.put(Pattern.compile("(liên hệ|contact|hỗ trợ|support|trợ giúp|help)"), 
            "Bạn có thể liên hệ Mi24/7 qua các kênh:\n" +
            "- Hotline: 0987.654.321 (8:00 - 22:00 hàng ngày)\n" +
            "- Email: support@mi247.com\n" +
            "- Chat trực tuyến trên website\n" +
            "- Fanpage Facebook: Mi247 Booking");
    }
    
    private boolean containsKeywords(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
} 