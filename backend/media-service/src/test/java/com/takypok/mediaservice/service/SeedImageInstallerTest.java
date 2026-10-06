package com.takypok.mediaservice.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.takypok.mediaservice.config.StorageProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

class SeedImageInstallerTest {
  private static final String ID = "5237a74e-adf6-5ccf-ad9b-43bc8be17680";

  @TempDir Path tmp;

  private SeedImageInstaller installer(Path imagesDir) {
    StorageProperties props = new StorageProperties();
    props.setImagesDir(imagesDir.toString());
    return new SeedImageInstaller(props);
  }

  private static Resource named(String filename, byte[] content) {
    return new ByteArrayResource(content) {
      @Override
      public String getFilename() {
        return filename;
      }
    };
  }

  @Test
  void installsMissingFilesAndCreatesTheDirectory() throws IOException {
    Path images = tmp.resolve("uploads/images");
    byte[] bytes = {1, 2, 3};

    int installed = installer(images).install(new Resource[] {named(ID + ".png", bytes)});

    assertEquals(1, installed);
    assertArrayEquals(bytes, Files.readAllBytes(images.resolve(ID + ".png")));
  }

  @Test
  void neverOverwritesAFileThatIsAlreadyThere() throws IOException {
    Files.writeString(tmp.resolve(ID + ".png"), "existing");

    int installed = installer(tmp).install(new Resource[] {named(ID + ".png", new byte[] {9})});

    assertEquals(0, installed);
    assertEquals("existing", Files.readString(tmp.resolve(ID + ".png")));
  }

  @Test
  void ignoresNamesThatAreNotAnIdPlusExtensionSoNothingCanEscapeTheDirectory() throws IOException {
    Path images = tmp.resolve("images");

    int installed =
        installer(images)
            .install(
                new Resource[] {
                  named("../escape.png", new byte[] {1}),
                  named("not-a-uuid.png", new byte[] {1}),
                  named(ID + "/../x.png", new byte[] {1}),
                  named(null, new byte[] {1})
                });

    assertEquals(0, installed);
    assertFalse(Files.exists(tmp.resolve("escape.png")));
    try (var files = Files.list(images)) {
      assertEquals(0, files.count());
    }
  }
}
