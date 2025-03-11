package com.k2encoder.usuario.business;

import com.k2encoder.usuario.business.converter.UsuarioConverter;
import com.k2encoder.usuario.business.dto.*;
import com.k2encoder.usuario.infrastructure.entity.*;
import com.k2encoder.usuario.infrastructure.exception.ConflictException;
import com.k2encoder.usuario.infrastructure.exception.ResourceNotFoundException;
import com.k2encoder.usuario.infrastructure.repository.*;
import com.k2encoder.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email já cadastrado! " + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado1 ", e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                    ()-> new ResourceNotFoundException("Email não localizado! " + email))
            );
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email não localizado! " + email + e.getCause());
        }
    }

    public void deletaUsuarioPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(
                ()-> new ResourceNotFoundException("Email não localizado")
        );

        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO dto) {

        Endereco enderecoEntity = enderecoRepository.findById(idEndereco).orElseThrow(
                ()-> new ResourceNotFoundException("Id não localizado!")
        );

        Endereco endereco = usuarioConverter.updateEndereco(dto, enderecoEntity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto) {
        Telefone telefoneEntity = telefoneRepository.findById(idTelefone).orElseThrow(
                ()-> new ResourceNotFoundException("Id não localizado!")
        );

        Telefone telefone = usuarioConverter.updatetelefone(dto, telefoneEntity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }
}
