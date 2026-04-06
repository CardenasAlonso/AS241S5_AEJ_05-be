package pe.edu.vallegrande.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.model.ApiResponse;
import pe.edu.vallegrande.service.AiIntegrationService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/zerobounce")
public class ZeroBounceController {

    private final AiIntegrationService aiService;

    public ZeroBounceController(AiIntegrationService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/validate")
    public Mono<ApiResponse> validateEmail(@RequestParam String email) {
        return aiService.validateEmail(email);
    }
}
