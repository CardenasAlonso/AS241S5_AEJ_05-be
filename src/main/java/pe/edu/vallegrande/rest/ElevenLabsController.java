package pe.edu.vallegrande.rest;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import pe.edu.vallegrande.model.ApiResponse;
import pe.edu.vallegrande.model.dto.SoundRequest;
import pe.edu.vallegrande.service.AiIntegrationService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/elevenlabs")
public class ElevenLabsController {

    private final AiIntegrationService aiService;

    public ElevenLabsController(AiIntegrationService aiService) {
        this.aiService = aiService;
    }

    /*@PostMapping("/tts")
    public Mono<ApiResponse> textToSpeech(@RequestBody String text) {
        return aiService.generateSpeech(text);
    }*/
    @PostMapping("/tts")
    public Mono<ApiResponse> generateTts(@RequestBody String text) {
        String cleanText = text.replace("\"", ""); 
        return aiService.generateTextToSpeech(cleanText, "21m00Tcm4TlvDq8ikWAM");
    }

    @PostMapping("/sound-effect")
    public Mono<ApiResponse> generateSoundEffect(@RequestBody SoundRequest request) {
        String cleanPrompt = request.prompt.replace("\"", "");
        return aiService.generateSoundEffect(
                cleanPrompt, 
                request.durationSeconds, 
                request.promptInfluence
        );
    }

    @PostMapping(value = "/sound-effect/play", produces = "audio/mpeg")
    public Mono<ResponseEntity<Resource>> playSoundEffect(@RequestBody SoundRequest request) {
        String cleanPrompt = request.prompt.replace("\"", "");
        
        return aiService.generateSoundEffect(
                cleanPrompt, 
                request.durationSeconds, 
                request.promptInfluence
        )
        .map(apiResponse -> {
            ByteArrayResource resource = new ByteArrayResource(apiResponse.getAudioData());
            
            return ResponseEntity.ok()
                    .contentLength(apiResponse.getAudioData().length)
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(resource);
        });
    }
}
