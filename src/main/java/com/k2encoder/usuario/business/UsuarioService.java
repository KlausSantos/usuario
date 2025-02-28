package com.k2encoder.usuario.business;

import com.k2encoder.usuario.business.converter.UsuarioConverter;
import com.k2encoder.usuario.business.dto.UsuarioDTO;
import com.k2encoder.usuario.infrastructure.entity.Usuario;
import com.k2encoder.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );
    }
}
