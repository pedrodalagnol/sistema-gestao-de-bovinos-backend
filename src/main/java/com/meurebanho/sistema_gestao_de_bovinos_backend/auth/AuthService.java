package com.meurebanho.sistema_gestao_de_bovinos_backend.auth;

import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth.AuthResponseDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth.CadastroRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.dto.auth.LoginRequestDTO;
import com.meurebanho.sistema_gestao_de_bovinos_backend.security.JwtService;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Fazenda;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.FazendaRepository;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.Usuario;
import com.meurebanho.sistema_gestao_de_bovinos_backend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final FazendaRepository fazendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO registrar(CadastroRequestDTO request) {
        // Validação básica para ver se o email já existe
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("Email já cadastrado.");
        }

        // 1. Criar e salvar a Fazenda
        Fazenda fazenda = new Fazenda();
        fazenda.setNomeFazenda(request.nomeFazenda());
        Fazenda novaFazenda = fazendaRepository.save(fazenda);

        // 2. Criar e salvar o Usuário
        Usuario usuario = new Usuario();
        usuario.setFazenda(novaFazenda);
        usuario.setNome(request.nomeUsuario());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.senha()));
        usuarioRepository.save(usuario);

        // 3. Gerar e retornar o token JWT
        String token = jwtService.generateToken(usuario);
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );
        // Se a autenticação passar, busca o usuário e gera o token
        var usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Erro ao autenticar usuário."));

        String token = jwtService.generateToken(usuario);
        return new AuthResponseDTO(token);
    }
}