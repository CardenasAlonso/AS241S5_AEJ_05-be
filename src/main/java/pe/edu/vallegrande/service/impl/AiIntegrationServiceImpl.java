package pe.edu.vallegrande.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.model.ApiResponse;
import pe.edu.vallegrande.repository.ApiRepository;
import pe.edu.vallegrande.service.AiIntegrationService;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiIntegrationServiceImpl implements AiIntegrationService {

    private final WebClient webClient;
    private final ApiRepository repository;

    // Credenciales ZeroBounce
    @Value("${apis.zerobounce.url-validate-email}")
    private String zeroBounceUrl;
    @Value("${apis.zerobounce.key}")
    private String zeroBounceKey;

    // Credenciales ElevenLabs
    @Value("${apis.elevenlabs.url-text-to-speech}")
    private String elevenLabsUrl;
    @Value("${apis.elevenlabs.url-sound-effects}")
    private String elevenLabsSoundUrl;
    @Value("${apis.elevenlabs.key}")
    private String elevenLabsKey;    
    @Value("${apis.elevenlabs.rapidapi-key}")
    private String rapidapiKey;
    @Value("${apis.elevenlabs.rapidapi-host}")
    private String rapidapiHost;

    public AiIntegrationServiceImpl(WebClient webClient, ApiRepository repository) {
        this.webClient = webClient;
        this.repository = repository;
    }

    // ZeroBounce
    // Implementación de validación de email con ZeroBounce
    @Override
    public Mono<ApiResponse> validateEmail(String email) {
        String url = zeroBounceUrl + "/validate?api_key=" + zeroBounceKey + "&email=" + email;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .flatMap(jsonResponse -> {
                    ApiResponse response = new ApiResponse();
                    response.setApiName("ZeroBounce ML");
                    response.setRequestData(email);
                    response.setResponseData(jsonResponse);
                    return repository.save(response);
                });
    }

    // ElevenLabs
    // Implementación de generación de voz con ElevenLabs
    /*@Override
    public Mono<ApiResponse> generateSpeech(String text) {
        String voiceId = "21m00Tcm4TlvDq8ikWAM"; // Voz de Rachel por defecto
        String url = elevenLabsUrl + "/text-to-speech/" + voiceId;

        Map<String, Object> body = Map.of(
                "text", text,
                "model_id", "eleven_multilingual_v2");

        return webClient.post()
                .uri(url)
                .header("xi-api-key", elevenLabsKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .flatMap(jsonResponse -> {
                    ApiResponse response = new ApiResponse();
                    response.setApiName("ElevenLabs TTS");
                    response.setRequestData(text);
                    response.setResponseData(jsonResponse);
                    return repository.save(response);
                });
    }*/
    @Override
    public Mono<ApiResponse> generateTextToSpeech(String text, String voiceId) {
        String selectedVoiceId = (voiceId == null || voiceId.isEmpty()) ? "21m00Tcm4TlvDq8ikWAM" : voiceId;
        Map<String, Object> body = Map.of("text", text);

        return webClient.post()
                .uri("https://elevenlabs-api1.p.rapidapi.com/v1/text-to-speech/" + selectedVoiceId)
                .header("x-rapidapi-key", rapidapiKey)
                .header("x-rapidapi-host", rapidapiHost)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> 
                    clientResponse.bodyToMono(String.class).flatMap(error -> 
                        Mono.error(new RuntimeException("Error TTS: " + error)))
                )
                .bodyToMono(byte[].class)
                .flatMap(audioBytes -> {
                    ApiResponse response = new ApiResponse();
                    response.setApiName("ElevenLabs TTS via RapidAPI");
                    response.setRequestData(text);
                    response.setAudioData(audioBytes); // Guardamos el audio
                    response.setResponseData("Audio generado: " + audioBytes.length + " bytes");
                    return repository.save(response);
                });
    }

    // Implementación de generación de efectos de sonido
    /*@Override
    public Mono<ApiResponse> generateSoundEffect(String prompt) {
        Map<String, Object> body = Map.of(
                "text", prompt,
                "duration_seconds", 5, // Duración del sonido - Puede ser opcional
                // Mientras mas alto mas se enfocará en el prompt mientras que un valor más bajo dará resultados mas variados
                "prompt_influence", 0.3); // Influencia del prompt en el resultado - Default: 0.5 hasta 1.0 para resultados mas específicos

        return webClient.post()
                .uri(elevenLabsSoundUrl)
                .header("xi-api-key", elevenLabsKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .flatMap(jsonResponse -> {
                    ApiResponse response = new ApiResponse();
                    response.setApiName("ElevenLabs Sound Effects");
                    response.setRequestData(prompt);
                    response.setResponseData(jsonResponse);
                    return repository.save(response);
                });
    }*/
    @Override
    public Mono<ApiResponse> generateSoundEffect(String prompt, Integer durationSeconds, Double promptInfluence) {
        Map<String, Object> body = new HashMap<>();
        body.put("text", prompt);
        body.put("prompt_influence", promptInfluence != null ? promptInfluence : 0.5);
        body.put("duration_seconds", durationSeconds != null ? durationSeconds : 5);

        return webClient.post()
                .uri(elevenLabsSoundUrl)
                .header("x-rapidapi-key", rapidapiKey)
                .header("x-rapidapi-host", rapidapiHost)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(map -> {
                    try {
                        String base64Audio = null;

                        Object topLevelAudio = map.get("audio_base64");
                        if (topLevelAudio instanceof String) {
                            base64Audio = (String) topLevelAudio;
                        }

                        if ((base64Audio == null || base64Audio.isBlank()) && map.get("data") instanceof List<?>) {
                            List<?> dataList = (List<?>) map.get("data");
                            if (!dataList.isEmpty() && dataList.get(0) instanceof Map<?, ?>) {
                                Map<?, ?> firstData = (Map<?, ?>) dataList.get(0);
                                Object contentBase64 = firstData.get("content_base64");
                                if (contentBase64 instanceof String) {
                                    base64Audio = (String) contentBase64;
                                }
                            }
                        }

                        if ((base64Audio == null || base64Audio.isBlank()) && map.get("sounds") instanceof List<?>) {
                            List<?> sounds = (List<?>) map.get("sounds");
                            if (!sounds.isEmpty() && sounds.get(0) instanceof Map<?, ?>) {
                                Map<?, ?> firstSound = (Map<?, ?>) sounds.get(0);
                                Object nestedAudio = firstSound.get("audio_base64");
                                if (nestedAudio instanceof String) {
                                    base64Audio = (String) nestedAudio;
                                }
                            }
                        }

                        if (base64Audio == null || base64Audio.isBlank()) {
                            return Mono.error(new RuntimeException("No se encontró audio_base64 ni content_base64 en la respuesta de ElevenLabs: " + map));
                        }

                        byte[] audioBytes = java.util.Base64.getDecoder().decode(base64Audio);

                        ApiResponse response = new ApiResponse();
                        response.setApiName("ElevenLabs Sound");
                        response.setRequestData(prompt);
                        response.setAudioData(audioBytes);
                        response.setResponseData("Audio generado. Tamaño: " + audioBytes.length + " bytes");

                        return repository.save(response);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error procesando JSON de ElevenLabs: " + e.getMessage(), e));
                    }
                });
    }
}