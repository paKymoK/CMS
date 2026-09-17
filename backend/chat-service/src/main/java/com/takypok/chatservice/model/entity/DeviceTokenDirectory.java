package com.takypok.chatservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("device_token_directory")
public class DeviceTokenDirectory {
  @Id
  @Column("fcm_token")
  private String fcmToken;

  private String sub;
  private String platform;
}
