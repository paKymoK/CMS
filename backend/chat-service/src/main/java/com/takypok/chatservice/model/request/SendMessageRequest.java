package com.takypok.chatservice.model.request;

import com.takypok.chatservice.model.Attachment;
import com.takypok.chatservice.model.MessageType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendMessageRequest {
  private String content;
  private MessageType messageType = MessageType.TEXT;
  private List<Attachment> attachments;

  // Set to reply within a thread instead of posting a new top-level message.
  private Long parentMessageId;
}
