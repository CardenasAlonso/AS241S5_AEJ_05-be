package pe.edu.vallegrande.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "api_responses")
public class ApiResponse {
    @Id
    private String id;
    private String apiName;
    private String requestData;
    private Object responseData;
    private byte[] audioData;
    private LocalDateTime timestamp = LocalDateTime.now();
}