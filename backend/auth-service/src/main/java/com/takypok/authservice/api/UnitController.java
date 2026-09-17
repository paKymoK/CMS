package com.takypok.authservice.api;

import com.takypok.authservice.model.entity.Unit;
import com.takypok.authservice.model.request.UnitCreateRequest;
import com.takypok.authservice.model.request.UnitUpdateRequest;
import com.takypok.authservice.service.UnitService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/units")
public class UnitController {
  private final UnitService unitService;

  @GetMapping
  public ResponseEntity<ResultMessage<List<Unit>>> get(
      @RequestParam(required = false) Long departmentId) {
    return ResponseEntity.ok(ResultMessage.success(unitService.get(departmentId)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResultMessage<Unit>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ResultMessage.success(unitService.getById(id)));
  }

  @PostMapping
  public ResponseEntity<ResultMessage<Unit>> create(@RequestBody @Valid UnitCreateRequest request) {
    return ResponseEntity.ok(ResultMessage.success(unitService.create(request)));
  }

  @PutMapping
  public ResponseEntity<ResultMessage<Unit>> update(@RequestBody @Valid UnitUpdateRequest request) {
    return ResponseEntity.ok(ResultMessage.success(unitService.update(request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResultMessage<Void>> delete(@PathVariable Long id) {
    unitService.delete(id);
    return ResponseEntity.ok(ResultMessage.success(null));
  }
}
