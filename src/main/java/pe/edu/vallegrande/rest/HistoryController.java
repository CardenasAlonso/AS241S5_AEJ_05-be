package pe.edu.vallegrande.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.model.ApiResponse;
import pe.edu.vallegrande.repository.ApiRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/responses")
@CrossOrigin(origins = "*")
public class HistoryController {

    private final ApiRepository apiRepository;

    public HistoryController(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @GetMapping
    public Flux<ApiResponse> getAll() {
        return apiRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return apiRepository.deleteById(id);
    }
}
