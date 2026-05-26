# DLQ Auditor
 
Microsserviço responsável por consumir mensagens de uma Dead Letter Queue (DLQ) na AWS SQS, auditá-las e persistir os registros de falha no PostgreSQL para análise posterior.
 
---
 
## Tecnologias
 
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | — |
| Spring Cloud AWS | 4.0.0-M1 |
| PostgreSQL | — |
 
---
 
## Arquitetura
 
O projeto utiliza **Arquitetura Hexagonal** (Ports and Adapters), escolhida por dois motivos principais.
 
**Fins educacionais:** o objetivo foi aprender e desenvolver utilizando uma arquitetura mais robusta do que as convencionalmente ensinadas, entendendo na prática como separar domínio, orquestração e infraestrutura.
 
**Preparação para mudanças futuras:** a Hexagonal isola o núcleo da aplicação de todas as decisões técnicas externas. Isso significa que evoluções como as listadas abaixo exigem mudança apenas no adapter correspondente, sem tocar nas regras de negócio:
 
- Trocar PostgreSQL por outro banco de dados
- Substituir AWS SQS por outro provider de mensageria (RabbitMQ, Kafka, Azure Service Bus)

### Por que não Layered Architecture?
 
A arquitetura em camadas tradicional (Controller → Service → Repository) foi descartada porque, embora simples de começar, tende a apresentar problemas à medida que o projeto cresce:
 
| | Layered | Hexagonal |
|---|---|---|
| Regras de negócio | Frequentemente vazam para Services ou Controllers | Isoladas nos BOs, sem dependência de framework |
| Troca de tecnologia | Impacta múltiplas camadas | Impacta apenas o adapter da tecnologia substituída |
| Testabilidade | Exige subir contexto do framework para testar lógica | Domínio testável com POJOs puros |
| Acoplamento | Alto entre camadas | Baixo — comunicação apenas por interfaces (portas) |
 
Na Layered, nada impede que um `@Service` importe diretamente uma classe JPA ou que um `@Repository` contenha lógica de negócio. Na Hexagonal, essa fronteira é estrutural — o domínio simplesmente não enxerga o que está fora dele.
 

 ### Fluxo de dados
 
```
AWS SQS (DLQ)
     │
     ▼
DlqListenerAdapter          ← adapter de entrada (infraestrutura)
     │  converte DTO → BO via SqsOrderMessageMapper
     ▼
DlqMessageServicePort       ← porta de entrada (contrato)
     │
     ▼
DlqMessageService           ← serviço de aplicação (orquestração)
     │  instancia DlqMessageBO, aplica regras de severidade
     ▼
DlqMessageRepositoryPort    ← porta de saída (contrato)
     │
     ▼
DlqMessageRepositoryAdapter ← adapter de saída (infraestrutura)
     │  converte BO → Entity via DlqMessageMapper
     ▼
PostgreSQL (tb_dlq_message)
```
 
---
 
## Estrutura do projeto
 
```
dlq-auditor/
└── src/main/java/com/fhcs/dlq/auditor/
    │
    ├── application/
    │   ├── port/
    │   │   ├── in/
    │   │   │   └── service/
    │   │   │       └── DlqMessageServicePort.java      ← porta de entrada
    │   │   └── out/
    │   │       └── persistence/
    │   │           └── DlqMessageRepositoryPort.java   ← porta de saída
    │   └── services/
    │       └── DlqMessageService.java                  ← serviço de aplicação
    │
    ├── core/
    │   └── domain/
    │       └── bo/
    │           ├── enums/
    │           │   ├── AuditStatus.java
    │           │   └── Severity.java
    │           ├── DlqMessageBO.java                   ← objeto de domínio principal
    │           ├── OrderItemBO.java
    │           └── SqsOrderMessageBO.java
    │
    └── infrastructure/
        └── adapter/
            ├── in/
            │   └── messaging/
            │       └── DlqMessage/
            │           ├── dto/
            │           │   ├── OrderItemDTO.java
            │           │   └── SqsOrderMessageDTO.java
            │           ├── listener/
            │           │   └── DlqListenerAdapter.java ← entrada via SQS
            │           └── mapper/
            │               └── SqsOrderMessageMapper.java
            └── out/
                └── persistence/
                    └── postgres/
                        ├── entity/
                        │   └── DlqMessageEntity.java
                        ├── jpa/
                        │   └── DlqMessageJpaRepository.java
                        ├── mapper/
                        │   └── DlqMessageMapper.java
                        └── repository/
                            └── DlqMessageRepositoryAdapter.java ← saída para o banco
```
 
---
 
## Descrição dos arquivos
 
### `core/` — Domínio
 
Camada central da aplicação. Não possui dependência de nenhum framework ou biblioteca externa. Todo o código aqui é Java puro.
 
#### `DlqMessageBO.java`
Objeto de domínio principal. Representa um registro de auditoria de mensagem com falha. Contém as duas regras de negócio centrais da aplicação:
 
- `inicializar(queueName, payload)` — gera o `errorId` (UUID), registra o timestamp e define o status inicial como `PENDING_ANALYSIS`.
- `definirSeveridade(quantidadeTotalProdutos)` — classifica a mensagem como `HIGH` (>100 itens), `MEDIUM` (≥50) ou `LOW` (<50).
#### `SqsOrderMessageBO.java`
Representa a mensagem de pedido recebida da DLQ. Contém o método `calcularQuantidadeTotalProdutos()`, que soma os `amount` de todos os `OrderItemBO` — resultado usado pela regra de severidade.
 
#### `OrderItemBO.java`
Representa um item do pedido com `sku` e `amount`.
 
#### `enums/AuditStatus.java`
Ciclo de vida de um registro de auditoria:
 
| Status | Descrição |
|---|---|
| `PENDING_ANALYSIS` | Criado, aguardando análise |
| `UNDER_ANALYSIS` | Análise em andamento |
| `PENDING_CONCLUSION` | Análise concluída, aguardando conclusão formal |
| `CONCLUDED` | Falha identificada e documentada |
| `ESCALATED` | Escalado por severidade ou conformidade |
| `CLOSED` | Auditoria finalizada, nenhuma ação adicional necessária |
| `DISCARDED` | Registro invalidado (duplicado ou irrelevante) |
 
#### `enums/Severity.java`
Nível de criticidade da mensagem: `HIGH`, `MEDIUM`, `LOW`.
 
---
 
### `application/` — Orquestração
 
Camada responsável por coordenar o fluxo entre domínio e infraestrutura. Conhece as portas, mas não os adapters.
 
#### `port/in/service/DlqMessageServicePort.java`
Interface que define o contrato de entrada da aplicação. Declara o método `processarMensagem(SqsOrderMessageBO)`, que será chamado pelo adapter de mensageria.
 
#### `port/out/persistence/DlqMessageRepositoryPort.java`
Interface que define o contrato de saída para persistência. Declara o método `salvar(DlqMessageBO)`. A camada de aplicação depende desta interface — nunca do adapter concreto.
 
#### `services/DlqMessageService.java`
Implementação de `DlqMessageServicePort`. Orquestra o fluxo:
 
1. Cria um `DlqMessageBO` em branco.
2. Chama `inicializar()` com a origem e o payload serializado da mensagem.
3. Chama `definirSeveridade()` com o total de produtos calculado pelo `SqsOrderMessageBO`.
4. Em caso de exceção durante o processamento, inicializa o BO com severidade `0` (que resulta em `LOW`) e persiste mesmo assim, garantindo que nenhuma mensagem da DLQ seja perdida silenciosamente.
5. Delega a persistência ao `DlqMessageRepositoryPort`.
---
 
### `infrastructure/` — Adapters
 
Camada que conecta o mundo externo (SQS, banco de dados) ao núcleo da aplicação. Toda a dependência de framework vive aqui.
 
#### `adapter/in/` — Adapter de entrada (SQS)
 
**`DlqListenerAdapter.java`**
Componente Spring anotado com `@SqsListener`. Recebe mensagens da fila configurada em `aws.sqs.dlq-queue-name`, converte o `SqsOrderMessageDTO` recebido para `SqsOrderMessageBO` via mapper e delega ao `DlqMessageServicePort`. É o único ponto de entrada da aplicação.
 
**`SqsOrderMessageMapper.java`**
Mapper estático (sem estado) responsável pela conversão `DTO → BO`. Isola o adapter do domínio: o domínio nunca conhece os DTOs da infraestrutura.
 
**`dto/SqsOrderMessageDTO.java` e `dto/OrderItemDTO.java`**
Objetos de transferência que representam a estrutura JSON esperada das mensagens SQS. O Spring Cloud AWS usa esses DTOs para deserializar o payload automaticamente.
 
#### `adapter/out/` — Adapter de saída (PostgreSQL)
 
**`DlqMessageRepositoryAdapter.java`**
Implementa `DlqMessageRepositoryPort`. Converte `DlqMessageBO → DlqMessageEntity` via mapper, persiste via `DlqMessageJpaRepository` e retorna o resultado convertido de volta para `DlqMessageBO`. A camada de aplicação nunca vê entidades JPA.
 
**`DlqMessageMapper.java`**
Mapper estático bidirecional para conversão `BO ↔ Entity`. Mantém o domínio desacoplado da camada de persistência.
 
**`DlqMessageEntity.java`**
Entidade JPA mapeada para a tabela `tb_dlq_message`. Armazena `errorId` (UUID, PK), `queueName`, `payload` (TEXT), `timestamp`, `status` e `severity` como strings (via `@Enumerated(EnumType.STRING)`).
 
**`DlqMessageJpaRepository.java`**
Interface Spring Data JPA que estende `JpaRepository<DlqMessageEntity, UUID>`. Fornece as operações básicas de CRUD sem nenhum código adicional.
 
---

## Configuração
 
### Variáveis de ambiente
 
| Variável | Descrição |
|---|---|
| `DATABASE_URL` | URL JDBC do PostgreSQL |
| `DATABASE_USERNAME` | Usuário do banco |
| `DATABASE_PASSWORD` | Senha do banco |
| `AWS_ACCESS_KEY_ID` | Access key da AWS |
| `AWS_SECRET_ACCESS_KEY` | Secret key da AWS |
| `AWS_SESSION_TOKEN` | Session token (credenciais temporárias STS) |