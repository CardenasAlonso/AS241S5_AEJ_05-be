package pe.edu.vallegrande.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.model.ApiResponse;

public interface ApiRepository extends ReactiveMongoRepository<ApiResponse, String> {
}