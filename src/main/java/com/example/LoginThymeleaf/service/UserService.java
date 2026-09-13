package com.example.LoginThymeleaf.service;

import com.example.LoginThymeleaf.model.RegisterForm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class UserService implements UserDetailsService {
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;

    public UserService(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    public void register(RegisterForm form) {
        jdbc.update("INSERT INTO usuarios (nome, email, senha, cpf, rg, endereco, instituicao) VALUES (?, ?, ?, ?, ?, ?, ?)",
            form.getNome(), form.getEmail(), encoder.encode(form.getSenha()),
            form.getCpf(), form.getRg(), form.getEndereco(), form.getInstituicao());
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return jdbc.query("SELECT email, senha FROM usuarios WHERE email = ?",
            (rs, row) -> User.withUsername(rs.getString("email"))
                .password(rs.getString("senha")).roles("USER").build(),
            email.trim().toLowerCase(Locale.ROOT))
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
    }

    public String findName(String email) {
        return jdbc.queryForObject("SELECT nome FROM usuarios WHERE email = ?", String.class, email);
    }
}
