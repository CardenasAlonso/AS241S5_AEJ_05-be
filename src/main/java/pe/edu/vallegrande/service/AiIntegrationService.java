package pe.edu.vallegrande.service;

import pe.edu.vallegrande.model.ApiResponse;
import reactor.core.publisher.Mono;

public interface AiIntegrationService {
    Mono<ApiResponse> validateEmail(String email);
    Mono<ApiResponse> generateTextToSpeech(String text, String voiceId);
    Mono<ApiResponse> generateSoundEffect(String prompt, Integer durationSeconds, Double promptInfluence);
}
