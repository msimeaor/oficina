package io.github.msimeaor.aplicacao.model.service.utilities;

import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelefoneUtilitiesService {

  public TelefoneUtilitiesService() {}

  public List<TelefoneResponseDTO> converterListaTelefoneEmListaTelefoneDTO(List<Telefone> listaTelefone) {
    if (listaTelefone == null)
      return null;

    return listaTelefone.stream().map((telefone) ->
            DozerMapper.parseObject(telefone, TelefoneResponseDTO.class))
            .collect(Collectors.toList());
  }

}
