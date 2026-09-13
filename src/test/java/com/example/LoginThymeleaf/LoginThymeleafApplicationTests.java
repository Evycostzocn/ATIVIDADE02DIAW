package com.example.LoginThymeleaf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1")
class LoginThymeleafApplicationTests {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder encoder;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        jdbc.update("DELETE FROM usuarios");
    }

    @Test
    void publicPagesAndPrivateArea() throws Exception {
        for (String page : new String[]{"/login", "/register", "/recoverpassword"}) {
            mvc.perform(get(page)).andExpect(status().isOk());
        }
        mvc.perform(get("/home")).andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
        mvc.perform(get("/")).andExpect(status().is3xxRedirection());
    }

    @Test
    void registerLoginAndLogout() throws Exception {
        register(" Ana ", " ANA@example.com ", "senha1234", "senha1234")
            .andExpect(redirectedUrl("/login?registered"));
        assertEquals("123.456.789-00", jdbc.queryForObject("SELECT cpf FROM usuarios", String.class));
        assertEquals("MG-12345678", jdbc.queryForObject("SELECT rg FROM usuarios", String.class));
        assertEquals("Rua de Teste, 10", jdbc.queryForObject("SELECT endereco FROM usuarios", String.class));
        assertEquals("PUC Minas", jdbc.queryForObject("SELECT instituicao FROM usuarios", String.class));
        String hash = jdbc.queryForObject("SELECT senha FROM usuarios", String.class);
        assertNotEquals("senha1234", hash);
        assertTrue(encoder.matches("senha1234", hash));
        var result = mvc.perform(post("/login").with(csrf())
                .param("email", "ANA@example.com").param("senha", "senha1234"))
            .andExpect(authenticated().withUsername("ana@example.com"))
            .andExpect(redirectedUrl("/home")).andReturn();
        var session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
        mvc.perform(get("/home").session(session)).andExpect(status().isOk())
            .andExpect(model().attribute("nome", "Ana"));
        mvc.perform(post("/logout").session(session).with(csrf()))
            .andExpect(redirectedUrl("/login?logout")).andExpect(unauthenticated());
        assertTrue(session.isInvalid());
        mvc.perform(post("/login").with(csrf()).param("email", "ana@example.com").param("senha", "errada"))
            .andExpect(redirectedUrl("/login?error")).andExpect(unauthenticated());
    }

    @Test
    void rejectsInvalidRegistrationAndDuplicates() throws Exception {
        mvc.perform(post("/register").with(csrf()).param("nome", "Ana")
                .param("email", "ana@example.com").param("senha", "senha1234").param("confirmacaoSenha", "senha1234"))
            .andExpect(model().attributeHasFieldErrors("registerForm", "cpf", "rg", "endereco", "instituicao"));
        register("", "invalido", "123", "diferente")
            .andExpect(model().attributeHasFieldErrors("registerForm", "nome", "email", "senha", "confirmacaoSenha"));
        register("Ana", "ana@example.com", " ".repeat(8), " ".repeat(8))
            .andExpect(model().attributeHasFieldErrors("registerForm", "senha"));
        register("Ana", "ana@example.com", "á".repeat(40), "á".repeat(40))
            .andExpect(model().attributeHasFieldErrors("registerForm", "senha"));
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM usuarios", Integer.class));
        register("Ana", "ana@example.com", "senha1234", "senha1234").andExpect(status().is3xxRedirection());
        register("Outra", "ANA@example.com", "senha1234", "senha1234")
            .andExpect(model().attributeHasFieldErrors("registerForm", "email"));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM usuarios", Integer.class));
    }

    @Test
    void recoveryAndCsrf() throws Exception {
        mvc.perform(post("/recoverpassword").with(csrf()).param("email", "invalido"))
            .andExpect(status().isOk()).andExpect(model().attributeExists("erro"));
        mvc.perform(post("/recoverpassword").with(csrf()).param("email", "ana@example.com"))
            .andExpect(status().isOk()).andExpect(model().attributeExists("mensagem"));
        for (String endpoint : new String[]{"/register", "/login", "/recoverpassword", "/logout"}) {
            mvc.perform(post(endpoint)).andExpect(status().isForbidden());
        }
    }

    private org.springframework.test.web.servlet.ResultActions register(String nome, String email, String senha, String confirmacao) throws Exception {
        return mvc.perform(post("/register").with(csrf()).param("nome", nome).param("email", email)
            .param("senha", senha).param("confirmacaoSenha", confirmacao)
            .param("cpf", "123.456.789-00").param("rg", "MG-12345678")
            .param("endereco", "Rua de Teste, 10").param("instituicao", "PUC Minas"));
    }
}
