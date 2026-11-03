# 🐾 SuperBicho

Plataforma web que conecta **tutores de pets**, **clínicas veterinárias** e um **administrador** em um só lugar: o tutor cadastra seus pets, agenda consultas e exames, acompanha a vacinação e recebe alertas preventivos; a clínica gerencia sua agenda e seus veterinários; o administrador aprova as clínicas parceiras.

| | |
|---|---|
| **Aplicação online** | https://superbicho.onrender.com |
| **Vídeo da apresentação (entrega final)** | https://youtu.be/KjyKQ0R6Rs8 |
| **Vídeo da entrega anterior** | https://youtu.be/7f1usMVVaGY |
| **Repositório** | https://github.com/luisa-ganasevici/SuperBicho |

> O deploy usa o plano gratuito do Render: se a aplicação estiver parada, o primeiro acesso pode levar de 30 a 60 segundos para carregar.

---

## 👥 Integrantes

| Nome | RM |
|---|---|
| Ana Carolina Pereira Fontes | 562145 |
| João Victor Nascimento Adão | 563409 |
| Johnny Dias Mathias Junior | 566516 |
| Luisa Ganasevici de Abreu | 563403 |
| Matheus Moya de Oliveira | 562822 |

---

## 💡 Narrativa da solução

### O problema

O cuidado preventivo com pets depende muito da memória do tutor: vacinas vencem sem ninguém perceber, consultas são marcadas por telefone e esquecidas, e o histórico do animal fica espalhado entre clínicas diferentes. Do outro lado, clínicas não têm um canal simples para receber agendamentos nem para avisar o tutor quando algo muda.

### A proposta

O SuperBicho centraliza a rotina de saúde do pet:

- **Para o tutor:** cadastro dos pets, agendamento de consultas e exames em clínicas aprovadas, calendário de vacinação por espécie, histórico do pet e **alertas preventivos** (vacina atrasada, nunca aplicada ou perto de vencer, e cuidados da estação do ano).
- **Para a clínica:** painel com os compromissos do dia, conclusão e cancelamento de consultas e exames com motivo, e gestão dos veterinários.
- **Para o administrador:** aprovação, recusa e desativação de clínicas, garantindo que só clínicas verificadas (com CNPJ) apareçam para os tutores.

### Diferenciais

- **Alertas preventivos automáticos:** o sistema calcula o status de cada vacina a partir da idade do pet e do intervalo de reforço do catálogo, e avisa o tutor na home.
- **Alerta sazonal:** a home mostra cuidados específicos da estação atual (ex.: pulgas e carrapatos na primavera).
- **Cancelamento com motivo nos dois sentidos:** se o tutor cancela, a clínica vê o motivo; se a clínica cancela, o tutor vê o motivo. Nada é apagado sem rastro.
- **Três perfis isolados:** tutor, clínica e administrador têm login, sessão e permissões independentes.

### Decisões de design e tecnologia

| Decisão | Justificativa |
|---|---|
| Monolito **Spring MVC + Thymeleaf** | Uma única aplicação para frontend e backend simplifica deploy, sessão e segurança para o tamanho do projeto. |
| **Três `SecurityFilterChain`** com chave de sessão separada por perfil | Um login de clínica não dá acesso às rotas de tutor e vice-versa; cada perfil é um "realm" independente. |
| Clínica sempre nasce **PENDING** e Admin não tem cadastro público | Evita escalonamento de privilégio: só o administrador libera clínicas, e só o banco cria administradores. |
| **Flyway** para o schema | Toda mudança de banco é versionada e aplicada automaticamente, inclusive no deploy. |
| **Soft delete** de consultas, exames e clínicas | Mantém o histórico e o motivo do cancelamento visíveis para as duas partes. |
| Verificação de dono **na camada de serviço** | A mesma regra protege a interface web e a API REST, em um único lugar. |
| **Docker** + Render | O mesmo container roda local e em produção. |

---

## 🧰 Tecnologias

- Java 21
- Spring Boot (Web MVC, Security, Data JPA, Validation, Thymeleaf, Cache)
- Flyway (migrations)
- Oracle Database (fornecido pela FIAP)
- Gradle
- Docker
- JUnit 5 + Mockito (testes unitários)

---

## 🔐 Perfis e segurança

| Perfil | Login | Cadastro | Home |
|---|---|---|---|
| Tutor | `/login` | `/cadastro` (auto-cadastro) | `/guardian/home` |
| Clínica | `/clinica/login` | `/clinica/cadastro` (fica **pendente** até o Admin aprovar) | `/clinica/home` ou `/clinica/status` |
| Administrador | `/adm/login` | Não há cadastro público | `/adm/home` |

- Cada perfil tem seu próprio `SecurityFilterChain` e sua própria chave de sessão (`HttpSessionSecurityContextRepository`).
- Rotas sem login (ou logado com outro perfil) redirecionam para a tela de login do perfil correto.
- Senhas são armazenadas com **BCrypt**.
- Toda ação sobre pet, consulta ou exame confere se o recurso pertence ao **tutor logado** — tanto nas telas quanto na API REST.

> Um administrador só pode ser criado inserindo um registro diretamente na tabela `SB_TB_ADM` (com a senha em hash BCrypt). As credenciais de teste do administrador foram enviadas junto com a entrega no portal.

---

## 🔄 Fluxos principais

**Tutor**
1. Cadastra-se e faz login.
2. Cadastra, edita e exclui pets.
3. Agenda consulta (clínica aprovada + veterinário opcional) ou registra exame.
4. Remarca ou cancela com motivo.
5. Acompanha o calendário de vacinação e marca vacinas aplicadas.
6. Vê alertas de vacina e da estação na home, e o histórico de cada pet.

**Clínica**
1. Cadastra-se (status PENDING) e aguarda aprovação.
2. Após aprovada, vê os compromissos do dia e toda a agenda.
3. Conclui ou cancela consultas e exames (com motivo).
4. Cadastra e remove veterinários.

**Administrador**
1. Aprova ou recusa clínicas pendentes.
2. Desativa clínicas (as consultas e exames em aberto são cancelados automaticamente, com aviso).
3. Remove veterinários.

---

## 🌐 API REST (perfil Tutor)

Todas as rotas exigem login de tutor e operam **somente sobre os dados do tutor logado**. Tentativas sobre recursos de outro tutor retornam **403**.

| Método | Rota | Descrição |
|---|---|---|
| GET | `/animals` | Lista os pets do tutor logado |
| GET | `/animals/{id}` | Detalha um pet do tutor |
| POST | `/animals` | Cadastra um pet para o tutor logado |
| PUT | `/animals/{id}` | Edita um pet do tutor |
| DELETE | `/animals/{id}` | Exclui o pet, cancelando consultas e exames em aberto |
| GET | `/appointments` | Lista as consultas do tutor |
| GET | `/appointments/{id}` | Detalha uma consulta do tutor |
| POST | `/appointments` | Agenda consulta para um pet do tutor (status sempre `AGENDADO`) |
| PUT | `/appointments/{id}` | Altera uma consulta `AGENDADO` do tutor |
| PATCH | `/appointments/{id}/cancel` | Cancela uma consulta do tutor (corpo: `{"reason": "..."}`) |

Requisições `POST`, `PUT`, `PATCH` e `DELETE` exigem o header `X-CSRF-TOKEN`.

---

## 🛠️ Melhorias da 4ª Sprint

### 1. Controle de acesso por dono na API REST

Os endpoints `/animals` e `/appointments` passaram a operar apenas sobre os dados do tutor logado.

- O tutor é identificado **pelo login**, e não por dados enviados na requisição.
- A verificação de dono fica centralizada em `AnimalService.findOwnedEntity` e é usada tanto pelas telas quanto pela API.
- Listagens retornam só os pets e consultas do tutor logado; acessar recurso de outro tutor retorna **403**.
- Toda consulta nasce com status `AGENDADO` e só pode ser alterada enquanto estiver nesse status.
- O cancelamento de consulta pela API (`PATCH /appointments/{id}/cancel`) segue a mesma regra das telas: registra o motivo em vez de apagar.

### 2. Exclusão de pet com consultas e exames vinculados

O tutor agora pode excluir um pet mesmo que ele tenha consultas, exames, vacinas ou histórico.

- Novo `AnimalRemovalService` (transacional): cancela as consultas `AGENDADO` e os exames `PENDENTE` do pet com o motivo "Pet excluido" e então exclui o pet.
- Migration **V11**: consultas e exames guardam o nome do pet e do tutor e passam a usar `ON DELETE SET NULL`; histórico e vacinas usam `ON DELETE CASCADE`.
- O pet é excluído de verdade e a clínica continua vendo a consulta como **CANCELADO — Pet excluido**.
- Conflitos de integridade de dados retornam **409** com mensagem clara.

### 3. Testes

Novos testes unitários cobrem: acesso, edição e exclusão de pet de outro tutor (403), agendamento para pet de outro tutor (403), alteração de consulta cancelada, cancelamento automático na exclusão do pet e manutenção dos nomes em consultas de pets excluídos.

---

## 🗄️ Estrutura do banco (Flyway)

As migrations em `src/main/resources/db/migration` são aplicadas automaticamente na inicialização:

| Versão | Descrição |
|---|---|
| V1 | Schema inicial (tutor, pet, clínica, consulta, exame, histórico) |
| V2 | Ajustes em tutor e clínica; tabela de administrador |
| V3 | Administrador inicial |
| V4 | Renomeia a coluna de status da clínica |
| V5 | CNPJ e especialidades da clínica |
| V6 | Veterinários, cancelamento com motivo e clínica no exame |
| V7 | Especialidade do veterinário |
| V8 | Aviso para a clínica (notice) |
| V9 | Limpeza de clínicas recusadas sem CNPJ |
| V10 | Catálogo de vacinas e registro de aplicações |
| V11 | Permite excluir pet mantendo consultas e exames para a clínica |

---

## 🎓 Integração multidisciplinar

📄 Documento completo: [`docs/integracao-multidisciplinar.pdf`](docs/integracao-multidisciplinar.pdf)

| Disciplina | Como foi aplicada no SuperBicho | Evidência |
|---|---|---|
| Mastering Relational and Non-Relational Database | Modelagem relacional em Oracle (12 tabelas, PK, FK, UNIQUE, regras `ON DELETE`) versionada em 11 migrations Flyway | [`db/migration`](src/main/resources/db/migration) · DER no PDF |
| DevOps Tools & Cloud Computing | Dockerfile multi-stage, deploy no Render, credenciais por variáveis de ambiente, fluxo de branches com Pull Requests | [`Dockerfile`](Dockerfile) · [aplicação online](https://superbicho.onrender.com) |
| Compliance, Quality Assurance & Tests | 62 testes automatizados (JUnit 5 + Mockito), testes de segurança da API no Postman, BCrypt, CSRF e acesso restrito ao dono dos dados | [`src/test`](src/test) · [`docs/SuperBicho.postman_collection.json`](docs/SuperBicho.postman_collection.json) |
| Java Advanced | Aplicação Spring Boot completa: MVC + Thymeleaf, Spring Security com três perfis, Flyway, JPA, cache, validações e API REST | Este repositório |

---

## ⚙️ Pré-requisitos

- [Java 21](https://adoptium.net/) (apenas para rodar sem Docker)
- [Docker](https://www.docker.com/) (apenas para rodar via container)
- Acesso a um banco Oracle com usuário e senha válidos

Não é necessário instalar o Gradle — o projeto usa o Gradle Wrapper (`gradlew`).

## 📦 Instalação

```bash
git clone https://github.com/luisa-ganasevici/SuperBicho.git
cd SuperBicho
```

As credenciais do banco são lidas de variáveis de ambiente **obrigatórias**:

| Variável | Descrição | Obrigatória? |
|---|---|---|
| `DB_USER` | Usuário do Oracle | Sim |
| `DB_PASSWORD` | Senha do Oracle | Sim |
| `PORT` | Porta da aplicação | Não — padrão `8080` |

Linux / macOS / Git Bash:
```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

Windows (PowerShell):
```powershell
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

## ▶️ Execução

### Opção 1 — Gradle

```bash
./gradlew bootRun
```
No Windows (CMD/PowerShell): `gradlew.bat bootRun`

A aplicação sobe em `http://localhost:8080`. O Flyway aplica as migrations automaticamente.

> Enquanto a aplicação estiver rodando, o Gradle fica parado em "80% EXECUTING" — isso é normal. Para parar, use `Ctrl + C`.

### Opção 2 — Docker

```bash
docker build -t superbicho .
docker run -p 8080:8080 -e DB_USER=seu_usuario -e DB_PASSWORD=sua_senha superbicho
```

### Deploy (Render)

O Render builda a imagem a partir do `Dockerfile`. Configure `DB_USER` e `DB_PASSWORD` em **Environment**; a porta é injetada pela plataforma.

## 🧪 Testes

```bash
./gradlew test
```

Os testes de serviço usam JUnit 5 e Mockito. O teste `contextLoads` sobe a aplicação completa e precisa das variáveis `DB_USER` e `DB_PASSWORD`.
Relatório: `build/reports/tests/test/index.html`.