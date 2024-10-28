package vn.unigap.api.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.unigap.api.entity.mongodb.RequestResponse;

public interface RequestResponseRepository extends MongoRepository<RequestResponse, String> {
}
