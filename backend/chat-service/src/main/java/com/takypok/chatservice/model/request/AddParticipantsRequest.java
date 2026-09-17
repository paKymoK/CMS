package com.takypok.chatservice.model.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddParticipantsRequest {
  @NotEmpty private List<String> participantSubs;
}
