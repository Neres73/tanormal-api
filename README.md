# Tá Normal? — API

API RESTful da CitizenTech **Tá Normal?** (Global Solution 2026.2 — FIAP).
O passageiro cadastra o trajeto do dia a dia e, antes de sair de casa, recebe o aviso de **quantos minutos a viagem deve atrasar** e se vale ir por outro caminho. A API junta o status oficial das linhas (Metrô/CPTM) com os relatos dos passageiros.

**Equipe:** Enzo Augusto (RM562249) · Gustavo Neres (RM561785) · Rafaell Santiago (RM563486) · Sebastian Iriarte (RM563619)

## Tecnologias e versões

| Item | Versão |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 3.5.6 (Web, Data JPA, Validation) |
| Banco | Oracle da FIAP (produção) · H2 em memória (desenvolvimento) |
| Documentação | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Deploy | Render (Docker) |
| IDE sugerida | IntelliJ IDEA |

## Organização das camadas

```
br.com.fiap.tanormal
├── model        Entidades do domínio (mapeadas para as tabelas TN_*)
├── dao          Acesso a dados (interfaces Spring Data JPA)
├── bo           Regras de negócio (BO) + EstimadorAtraso, que calcula o atraso
├── dto          Objetos de entrada e saída da API
├── controller   Endpoints REST (GET, POST, PUT, PATCH, DELETE)
├── exception    Tratamento global de exceções (400, 404, 405, 409, 422, 500)
└── config       Swagger e carga inicial de dados
```

## Como executar

### 1. Rodar localmente sem Oracle (perfil `dev`, padrão)

```bash
mvn spring-boot:run
```

- Swagger: http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:tanormal`, usuário `sa`, sem senha)

Ao subir, a API cadastra as linhas 3-Vermelha, 11-Coral e 12-Safira com as estações, um usuário de demonstração e o trajeto **Itaim Paulista → Sé (06:30)**, que tem id 1.

No IntelliJ: abra a pasta do projeto, espere o Maven baixar as dependências e rode `TaNormalApplication`.

### 2. Rodar com o Oracle da FIAP (perfil `oracle`)

Defina as variáveis de ambiente e rode com o perfil `oracle`:

```bash
# Linux/macOS
export SPRING_PROFILES_ACTIVE=oracle
export DB_USERNAME=rm561785
export DB_PASSWORD=sua_senha
mvn spring-boot:run
```

```powershell
# Windows (PowerShell)
$env:SPRING_PROFILES_ACTIVE="oracle"
$env:DB_USERNAME="rm561785"
$env:DB_PASSWORD="sua_senha"
mvn spring-boot:run
```

No IntelliJ: *Run > Edit Configurations > Environment variables* e cole
`SPRING_PROFILES_ACTIVE=oracle;DB_USERNAME=rm561785;DB_PASSWORD=sua_senha`.

As tabelas `TN_*` são criadas automaticamente na primeira execução. O script equivalente está em [`docs/ddl-oracle.sql`](docs/ddl-oracle.sql).

### 3. Deploy no Render

1. Suba este projeto para um repositório no GitHub.
2. No Render: **New > Web Service** e conecte o repositório. O Render detecta o `Dockerfile`.
3. Em **Environment**, adicione:
   - `SPRING_PROFILES_ACTIVE` = `oracle`
   - `DB_USERNAME` = seu RM do Oracle (ex.: `rm561785`)
   - `DB_PASSWORD` = sua senha do Oracle
4. Plano **Free** e **Create Web Service**. O primeiro build leva de 5 a 10 minutos.
5. Teste em `https://<seu-servico>.onrender.com/swagger-ui.html`.

> No plano gratuito o Render desliga o serviço depois de 15 minutos sem uso. A primeira requisição depois disso leva cerca de 1 minuto para responder. Abra o link um pouco antes da apresentação.

### 4. Testes

```bash
mvn test
```

## Endpoints

| Método | Endpoint | O que faz |
|---|---|---|
| GET | `/api/usuarios` | Lista usuários |
| GET | `/api/usuarios/{id}` | Busca um usuário |
| POST | `/api/usuarios` | Cadastra usuário |
| PUT | `/api/usuarios/{id}` | Atualiza usuário |
| DELETE | `/api/usuarios/{id}` | Remove usuário |
| GET | `/api/linhas` | Lista linhas com o status oficial |
| GET | `/api/linhas/{id}` | Busca uma linha |
| GET | `/api/linhas/{id}/estacoes` | Estações da linha |
| POST | `/api/linhas` | Cadastra linha |
| PUT | `/api/linhas/{id}` | Atualiza linha |
| PATCH | `/api/linhas/{id}/status` | Atualiza o status oficial (usado pelo leitor do site da operadora) |
| DELETE | `/api/linhas/{id}` | Remove linha |
| GET | `/api/estacoes/{id}` | Busca estação |
| POST | `/api/estacoes` | Cadastra estação |
| PUT | `/api/estacoes/{id}` | Atualiza estação |
| DELETE | `/api/estacoes/{id}` | Remove estação |
| GET | `/api/trajetos?usuarioId=` | Lista trajetos (filtro opcional por usuário) |
| GET | `/api/trajetos/{id}` | Busca trajeto |
| **GET** | **`/api/trajetos/{id}/aviso?chuva=`** | **Aviso de atraso do trajeto** |
| POST | `/api/trajetos` | Cadastra trajeto |
| PUT | `/api/trajetos/{id}` | Atualiza trajeto |
| DELETE | `/api/trajetos/{id}` | Remove trajeto |
| GET | `/api/relatos?linhaId=` | Lista relatos |
| GET | `/api/relatos/{id}` | Busca relato |
| POST | `/api/relatos` | Envia relato ("PARADO", "LENTO", "LOTADO") |
| POST | `/api/relatos/{id}/confirmacoes` | Outro passageiro confirma o relato |
| PUT | `/api/relatos/{id}` | Atualiza relato |
| DELETE | `/api/relatos/{id}` | Remove relato |
| GET | `/api/ocorrencias?linhaId=&abertas=` | Histórico de ocorrências |
| GET | `/api/ocorrencias/{id}` | Busca ocorrência |
| POST | `/api/ocorrencias` | Registra ocorrência (OFICIAL também muda o status da linha) |
| PUT | `/api/ocorrencias/{id}` | Atualiza ocorrência |
| PATCH | `/api/ocorrencias/{id}/encerramento` | Encerra ocorrência (linha volta ao normal) |
| DELETE | `/api/ocorrencias/{id}` | Remove ocorrência |

## Roteiro de demonstração (Swagger ou Postman)

1. `GET /api/trajetos/1/aviso` → *"Tá normal! Pode sair às 06:30 como sempre."*
2. Registrar o problema na Linha 12-Safira:
   ```json
   POST /api/ocorrencias
   { "linhaId": 1, "status": "VELOCIDADE_REDUZIDA", "fonte": "OFICIAL",
     "descricao": "Velocidade reduzida entre Brás e Calmon Viana", "chuva": true }
   ```
3. `GET /api/trajetos/1/aviso?chuva=true` → atraso de 12 min e saída sugerida às 06:18.
4. Encerrar: `PATCH /api/ocorrencias/{id}/encerramento` → a linha volta a "Operação normal".
5. Divergência (site diz normal, passageiros dizem que está lento):
   ```json
   POST /api/relatos
   { "usuarioId": 1, "linhaId": 1, "tipo": "PARADO", "comentario": "Parado em Itaim Paulista" }
   ```
   Confirme 2 vezes em `POST /api/relatos/1/confirmacoes` e veja o aviso: ele avisa que o site diz operação normal, mas os passageiros relatam lentidão.

## Como o atraso é estimado

O `EstimadorAtraso` (pacote `bo`) usa uma regra simples e explicável:

- **Status oficial** de cada linha do trajeto: Normal = 0 min, Velocidade reduzida = 10, Operação parcial = 20, Paralisada = 40.
- **Relatos dos últimos 40 minutos**: Parado = 8 min, Lento = 4, Lotado = 2. Cada relato vale mais quando outros passageiros confirmam (até 3x). O total por linha tem teto de 25 min.
- Por linha vale o **maior** dos dois valores, para não contar o mesmo problema duas vezes. Somam-se as linhas do trajeto.
- **Chuva**: +20% no atraso.
- A partir de **15 min** de atraso o aviso sugere uma rota alternativa.

Quando o modelo de regressão de Data Science estiver treinado com o histórico de ocorrências, ele pode substituir esse cálculo sem mudar o resto da API.
