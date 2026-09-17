package com.takypok.authservice.api;

import com.takypok.authservice.model.request.RegisteredClientRequest;
import com.takypok.authservice.model.response.RegisteredClientResponse;
import com.takypok.authservice.service.ClientService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/clients")
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  public ResponseEntity<ResultMessage<List<RegisteredClientResponse>>> getAll() {
    return ResponseEntity.ok(ResultMessage.success(clientService.getAll()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResultMessage<RegisteredClientResponse>> getById(@PathVariable String id) {
    return ResponseEntity.ok(ResultMessage.success(clientService.getById(id)));
  }

  @PostMapping
  public ResponseEntity<ResultMessage<RegisteredClientResponse>> create(
      @RequestBody @Valid RegisteredClientRequest request) {
    return ResponseEntity.ok(ResultMessage.success(clientService.create(request)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResultMessage<RegisteredClientResponse>> update(
      @PathVariable String id, @RequestBody @Valid RegisteredClientRequest request) {
    return ResponseEntity.ok(ResultMessage.success(clientService.update(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResultMessage<Void>> delete(@PathVariable String id) {
    clientService.delete(id);
    return ResponseEntity.ok(ResultMessage.success(null));
  }
}
