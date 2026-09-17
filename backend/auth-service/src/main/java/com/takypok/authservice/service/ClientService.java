package com.takypok.authservice.service;

import com.takypok.authservice.model.request.RegisteredClientRequest;
import com.takypok.authservice.model.response.RegisteredClientResponse;
import java.util.List;

public interface ClientService {

  List<RegisteredClientResponse> getAll();

  RegisteredClientResponse getById(String id);

  RegisteredClientResponse create(RegisteredClientRequest request);

  RegisteredClientResponse update(String id, RegisteredClientRequest request);

  void delete(String id);
}
