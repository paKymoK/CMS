package com.takypok.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClazzUtil {
  public static boolean isImplementationClazz(Class<?> clazz, String interfaceName) {
    if (Objects.isNull(interfaceName) || Objects.isNull(clazz)) {
      return false;
    }

    Class<?>[] interfaces = clazz.getInterfaces();
    List<Class<?>> lstInterface =
        Arrays.stream(interfaces).filter(aClass -> interfaceName.equals(aClass.getName())).toList();
    return lstInterface.size() == 1;
  }
}
