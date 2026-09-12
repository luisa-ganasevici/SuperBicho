# 🐾 SuperBicho

Aplicação web para gestão de cuidados de pets, conectando **Tutores**, **Clínicas veterinárias** e um **Administrador**. Permite cadastro de pets, agendamento de consultas e exames, controle de vacinação, cadastro de veterinários e aprovação de clínicas parceiras.

## Tecnologias

- Java 21
- Spring Boot (Web, Security, Data JPA, Validation, Thymeleaf, Cache)
- Flyway (migrations)
- Oracle Database (fornecido pela FIAP)
- Gradle
- Docker

## Pré-requisitos

- [Java 21](https://adoptium.net/) instalado (necessário apenas se for rodar sem Docker)
- [Docker](https://www.docker.com/) instalado (necessário apenas se for rodar via container)
- Acesso ao banco Oracle da FIAP (`oracle.fiap.com.br:1521:orcl`) com usuário e senha válidos

Não é necessário instalar o Gradle — o projeto já vem com o Gradle Wrapper (`gradlew`).

## Instalação

Clone o repositório:

```bash
git clone <url-do-repositorio>
cd SuperBicho
```

As credenciais do banco de dados são lidas de variáveis de ambiente:

| Variável | Descrição | Valor padrão (dev) |
|---|---|---|
| `DB_USER` | Usuário do Oracle | `rm563403` |
| `DB_PASSWORD` | Senha do Oracle | `230107` |
| `PORT` | Porta em que a aplicação sobe | `8082` |

Se as variáveis não forem definidas, a aplicação usa os valores padrão configurados em `application.yaml` (úteis para rodar localmente pela IDE).

## Execução

### Opção 1 — Localmente com Gradle

```bash
./gradlew bootRun
```

No Windows:
```bash
gradlew.bat bootRun
```

A aplicação sobe em `http://localhost:8082`. O Flyway executa as migrations automaticamente na inicialização — não é necessário rodar nenhum script manualmente.

### Opção 2 — Com Docker

**Build da imagem:**
```bash
docker build -t superbicho .
```

**Executar o container:**
```bash
docker run -p 8082:8082 \
  -e DB_USER=rm563403 \
  -e DB_PASSWORD=230107 \
  superbicho
```

A aplicação estará disponível em `http://localhost:8082`.

### Deploy (Railway / Render)

A plataforma builda a imagem a partir do `Dockerfile` na raiz do projeto automaticamente. Configure as variáveis de ambiente `DB_USER` e `DB_PASSWORD` no painel do serviço (aba **Variables**, no Railway, ou **Environment**, no Render) — não é necessário configurar `PORT`, a plataforma injeta a sua própria porta automaticamente e a aplicação já está preparada para escutar nela.

## Acesso

A aplicação tem três perfis de usuário, cada um com sua própria tela de login:

| Perfil | Rota de login | Cadastro |
|---|---|---|
| Tutor | `http://localhost:8082/login` | `http://localhost:8082/cadastro` (auto-cadastro) |
| Clínica | `http://localhost:8082/clinica/login` | `http://localhost:8082/clinica/cadastro` (fica **pendente** até um Admin aprovar) |
| Administrador | `http://localhost:8082/adm/login` | Não há cadastro público — o primeiro Admin já vem pré-cadastrado via migration (`V3__seed_admin.sql`), e novos Admins só podem ser criados por um Admin já autenticado |

**Login do Administrador padrão (seed):**
- Email: `luisa@gmail.com`
- Senha: a senha definida no momento em que o hash em `V3__seed_admin.sql` foi gerado

Após o login, cada perfil é redirecionado para sua respectiva home:
- Tutor → `/guardian/home`
- Clínica → `/clinica/home` (ou `/clinica/status`, se ainda estiver com cadastro pendente de aprovação)
- Administrador → `/adm/home`

## Estrutura do banco

As tabelas e dados iniciais são criados automaticamente pelo Flyway a partir dos scripts em `src/main/resources/db/migration`, executados na ordem `V1` a `V10` na primeira inicialização da aplicação.