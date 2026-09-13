# 🐾 SuperBicho

Aplicação web para gestão de cuidados de pets, conectando **Tutores**, **Clínicas veterinárias** e um **Administrador**. Permite cadastro de pets, agendamento de consultas e exames, controle de vacinação, cadastro de veterinários e aprovação de clínicas parceiras.

## Tecnologias

- Java 21
- Spring Boot (Web, Security, Data JPA, Validation, Thymeleaf, Cache)
- Flyway (migrations)
- Oracle Database (fornecido pela FIAP)
- Gradle
- Docker
- JUnit 5 + Mockito (testes unitários)

## Pré-requisitos

- [Java 21](https://adoptium.net/) instalado (necessário apenas se for rodar sem Docker)
- [Docker](https://www.docker.com/) instalado (necessário apenas se for rodar via container)
- Acesso a um banco Oracle com usuário e senha válidos

Não é necessário instalar o Gradle — o projeto já vem com o Gradle Wrapper (`gradlew`).

## Instalação

Clone o repositório:

```bash
git clone <url-do-repositorio>
cd SuperBicho
```

As credenciais do banco de dados são lidas de variáveis de ambiente **obrigatórias** — sem elas, a aplicação não sobe:

| Variável | Descrição | Obrigatória? |
|---|---|---|
| `DB_USER` | Usuário do Oracle | Sim |
| `DB_PASSWORD` | Senha do Oracle | Sim |
| `PORT` | Porta em que a aplicação sobe | Não — usa `8080` por padrão |

Defina as variáveis no seu terminal antes de rodar:

```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

No Windows (PowerShell):
```powershell
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

## Execução

### Opção 1 — Localmente com Gradle

```bash
./gradlew bootRun
```

No Windows:
```bash
gradlew.bat bootRun
```

A aplicação sobe em `http://localhost:8080`. O Flyway executa as migrations automaticamente na inicialização — não é necessário rodar nenhum script manualmente.

### Opção 2 — Com Docker

**Build da imagem:**
```bash
docker build -t superbicho .
```

**Executar o container:**
```bash
docker run -p 8080:8080 \
  -e DB_USER=seu_usuario \
  -e DB_PASSWORD=sua_senha \
  superbicho
```

A aplicação estará disponível em `http://localhost:8080`.

### Deploy (Railway / Render)

A plataforma builda a imagem a partir do `Dockerfile` na raiz do projeto automaticamente. Configure as variáveis de ambiente `DB_USER` e `DB_PASSWORD` no painel do serviço (aba **Variables**, no Railway, ou **Environment**, no Render) — não é necessário configurar `PORT`, a plataforma injeta a sua própria porta automaticamente e a aplicação já está preparada para escutar nela.

## Testes

O projeto tem testes unitários das camadas de serviço, usando JUnit 5 e Mockito como dublê de teste para isolar a lógica de negócio dos repositórios.

```bash
./gradlew test
```

O relatório é gerado em `build/reports/tests/test/index.html`.

## Acesso

A aplicação tem três perfis de usuário, cada um com sua própria tela de login:

| Perfil | Rota de login | Cadastro |
|---|---|---|
| Tutor | `http://localhost:8080/login` | `http://localhost:8080/cadastro` (auto-cadastro) |
| Clínica | `http://localhost:8080/clinica/login` | `http://localhost:8080/clinica/cadastro` (fica **pendente** até um Admin aprovar) |
| Administrador | `http://localhost:8080/adm/login` | Não há cadastro público pela interface |

> Não existe nenhuma forma de criar um Administrador pela aplicação web — não há rota de cadastro para esse perfil. Um Admin só pode ser criado inserindo diretamente um registro na tabela `SB_TB_ADM` do banco (com a senha já em hash BCrypt). **Se você for rodar a aplicação com um banco de dados próprio** (diferente do usado no desenvolvimento), vai precisar fazer esse insert manual antes de conseguir logar como Administrador.

Após o login, cada perfil é redirecionado para sua respectiva home:
- Tutor → `/guardian/home`
- Clínica → `/clinica/home` (ou `/clinica/status`, se ainda estiver com cadastro pendente de aprovação)
- Administrador → `/adm/home`

## Estrutura do banco

As tabelas e dados iniciais são criados automaticamente pelo Flyway a partir dos scripts em `src/main/resources/db/migration`, executados na ordem `V1` a `V10` na primeira inicialização da aplicação.


## Links

- **Aplicação em produção:** https://superbicho.onrender.com
- **Vídeo de demonstração:** https://youtu.be/7f1usMVVaGY?si=soycBM_Vya2pX4gC