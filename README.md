# Login com Spring Boot e Thymeleaf

Atividade 02: cadastro de usuários, login por email, área protegida e logout, com interface responsiva.

## Como executar

Instale o **JDK 25**, configure `JAVA_HOME` e abra o terminal na pasta do projeto:

```bash
sh mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`. Com Maven instalado, pode usar `mvn spring-boot:run`.

Acesse [localhost:8080/login](http://localhost:8080/login) e crie uma conta. A primeira execução precisa de internet para baixar as dependências.

## Cadastro e segurança

O cadastro pede nome, email, CPF, RG, endereço, instituição, senha e confirmação. Os campos são obrigatórios e emails duplicados não são permitidos. A senha deve ter de 8 a 64 caracteres, respeitando o limite de 72 bytes do BCrypt.

O projeto usa Spring Security para autenticação e BCrypt para proteger as senhas. Os usuários ficam salvos no banco H2 local, na pasta `data/`, sem precisar instalar um banco.

## Endpoints

| Método | Endpoint | Função |
| --- | --- | --- |
| GET / POST | `/login` | Tela e autenticação |
| GET / POST | `/register` | Tela e cadastro |
| GET / POST | `/recoverpassword` | Tela e validação do email informado |
| GET | `/` e `/home` | Área protegida |
| POST | `/logout` | Encerrar sessão |

O envio de email e a redefinição de senha são opcionais na atividade e não estão implementados. A tela de recuperação informa essa limitação.

## Configuração e testes

As configurações ficam em `src/main/resources/application.properties`. Os padrões permitem executar localmente: porta `8080`, banco `jdbc:h2:file:./data/login`, usuário `sa` e senha vazia. Para alterar, use as variáveis de ambiente `PORT`, `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. Mantenha as mesmas credenciais ao reabrir um banco existente e não publique senhas reais ou a pasta `data/`.

Para executar os testes:

```bash
sh mvnw test
```
