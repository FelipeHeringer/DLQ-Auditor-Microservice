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