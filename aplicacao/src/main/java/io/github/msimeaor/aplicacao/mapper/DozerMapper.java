package io.github.msimeaor.aplicacao.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.data.domain.Page;

public class DozerMapper {

  private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

  public static <O, D> D parseObject(O origem, Class<D> destino) {
    return mapper.map(origem, destino);
  }

  public static <O, D> Page<D> parseListObject(Page<O> origem, Class<D> destino) {
    return origem.map(o -> mapper.map(o, destino));
  }

}
