package com.example.LoginThymeleaf.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {
    @NotBlank(message = "Informe seu nome.")
    @Size(max = 100, message = "O nome deve ter até 100 caracteres.")
    private String nome;
    @NotBlank(message = "Informe seu email.")
    @Email(message = "Informe um email válido.")
    @Size(max = 254, message = "O email deve ter até 254 caracteres.")
    private String email;
    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 64, message = "A senha deve ter de 8 a 64 caracteres.")
    private String senha;
    @NotBlank(message = "Confirme sua senha.")
    private String confirmacaoSenha;

    @NotBlank(message = "Informe CPF.")
    @Size(max = 20, message = "O campo CPF deve ter até 20 caracteres.")
    private String cpf;
    @NotBlank(message = "Informe RG.")
    @Size(max = 30, message = "O campo RG deve ter até 30 caracteres.")
    private String rg;
    @NotBlank(message = "Informe endereço.")
    @Size(max = 200, message = "O campo endereço deve ter até 200 caracteres.")
    private String endereco;
    @NotBlank(message = "Informe instituição.")
    @Size(max = 150, message = "O campo instituição deve ter até 150 caracteres.")
    private String instituicao;

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf == null ? null : cpf.trim(); }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg == null ? null : rg.trim(); }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco == null ? null : endereco.trim(); }
    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao == null ? null : instituicao.trim(); }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome == null ? null : nome.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT); }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getConfirmacaoSenha() { return confirmacaoSenha; }
    public void setConfirmacaoSenha(String confirmacaoSenha) { this.confirmacaoSenha = confirmacaoSenha; }
}
