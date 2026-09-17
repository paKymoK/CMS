package com.takypok.mediaservice.model.mapper;

import com.takypok.mediaservice.model.entity.UploadFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UploadFileMapper {
  @Mapping(target = "name", source = "filename")
  @Mapping(target = "extension", source = "extension")
  public abstract UploadFile mapToEntity(String filename, String extension);
}
