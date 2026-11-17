package com.takypok.authservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import javax.imageio.ImageIO;

/** Draws a QR code here, so the secret it holds never goes to an outside service. */
public final class QrCodes {

  private QrCodes() {}

  /** A {@code data:image/png;base64,...} URI for {@code text}. */
  public static String pngDataUri(String text, int size) {
    try {
      BitMatrix matrix =
          new QRCodeWriter()
              .encode(
                  text,
                  BarcodeFormat.QR_CODE,
                  size,
                  size,
                  Map.of(
                      EncodeHintType.ERROR_CORRECTION,
                      ErrorCorrectionLevel.M,
                      EncodeHintType.MARGIN,
                      2));
      BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
        }
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageIO.write(image, "png", out);
      return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
    } catch (WriterException | IOException ex) {
      throw new IllegalStateException("Could not draw the QR code", ex);
    }
  }
}
