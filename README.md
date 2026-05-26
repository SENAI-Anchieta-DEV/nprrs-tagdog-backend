# TAGDOG - Backend (nprrs-tagdog-backend)

O **TAGDOG** é um sistema de controle de acesso e monitoramento de pets desenvolvido para hotéis e estabelecimentos de hospedagem animal. A API gerencia o fluxo de autenticação, o cadastro de tutores e animais, e a integração com dispositivos IoT via MQTT.

---

## Tecnologias e Dependências

* **Linguagem:** Java 21
* **Framework:** Spring Boot
* **Segurança:** Spring Security + JWT (jjwt)
* **Banco de Dados:** 
  * **H2 Database:** Utilizado em ambiente de desenvolvimento (`dev`).
  * **PostgreSQL:** Utilizado em ambiente de produção (`prod`).
* **Migração de Dados:** Flyway
* **Comunicação IoT:** [Spring MQTTX](https://github.com/RafaelPinheiroCosta/spring-mqttx)
* **Documentação:** SpringDoc OpenAPI (Swagger)
* **Observabilidade:** Spring Actuator (Health Checks e Probes)

---

## Instalação de Dependências Externas
Este projeto utiliza uma biblioteca customizada para o protocolo MQTT que não reside no Maven Central. Antes de compilar o projeto principal, você deve instalá-la manualmente:

1. Clone o repositório da biblioteca:
   ```bash
   git clone [https://github.com/RafaelPinheiroCosta/spring-mqttx.git](https://github.com/RafaelPinheiroCosta/spring-mqttx.git)
   ```
2. Instale os artefatos no seu repositório local (.m2). Entre na pasta spring-mqttx e execute:
   ```bash
    cd spring-mqttx

    # 1. Instalar o Parent
    mvn install:install-file "-Dfile=pom.xml" "-DgroupId=com.rafaelcosta" "-DartifactId=spring-mqttx-parent" "-Dversion=1.1.0" "-Dpackaging=pom"
    
    # 2. Instalar o Core (certifique-se de ter rodado mvn package antes se os jars não existirem)
    mvn install:install-file "-Dfile=spring-mqttx-core/target/spring-mqttx-core-1.1.0.jar" "-DgroupId=com.rafaelcosta" "-DartifactId=spring-mqttx-core" "-Dversion=1.1.0" "-Dpackaging=jar"
    
    # 3. Instalar o Starter
    mvn install:install-file "-Dfile=spring-mqttx-starter/target/spring-mqttx-starter-1.1.0.jar" "-DgroupId=com.rafaelcosta" "-DartifactId=spring-mqttx-starter" "-Dversion=1.1.0" "-Dpackaging=jar"

3. Agora, volte para a pasta raiz do seu projeto principal (nprrs-tagdog-backend) e compile:
    ```bash
   cd ..
    mvn clean install
   ```
## Configuração e Variáveis de Ambiente
A aplicação utiliza variáveis de ambiente para gerenciar credenciais sensíveis. Certifique-se de configurá-las no seu sistema ou em um arquivo `.env`:

| Variável | Descrição | Padrão (Dev) |
| :--- | :--- | :--- |
| **SECURITY_JWT_SECRET** | Chave secreta para assinatura dos tokens | `mysupersecretkeymysupersecretkey123!` |
| **EMAIL_PASSWORD** | Senha de app do Gmail (App Password) | **Obrigatório** |
| **APP_CORS_ALLOWED_ORIGINS** | Origens permitidas (CORS) | `http://localhost:3000` |
| **APP_BOOTSTRAP_ADMIN_EMAIL** | E-mail do admin inicial | `admin@email.com` |
| **APP_BOOTSTRAP_ADMIN_PASSWORD** | Senha do admin inicial | `admin123` |
_Nota: Certifique-se de que a variável EMAIL_PASSWORD esteja definida no seu ambiente antes de iniciar a aplicação para evitar falhas no bean de e-mail._

### Configurações de Produção (PostgreSQL)
Para o perfil prod, configure obrigatoriamente:
* DATABASE_HOST
* DATABASE_PORT
* DATABASE_NAME
* DATABASE_USER
* DATABASE_PASSWORD

## Como Executar
### Desenvolvimento
Roda com banco H2 em memória e popula dados de teste automaticamente via seed_dev_data.sql.
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
* Swagger UI: http://localhost:8080/swagger-ui.html
* H2 Console: http://localhost:8080/h2-console

### Produção
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
## Segurança e Endpoints
A API utiliza controle de acesso baseado em funções (RBAC).

| Endpoint | Métodos | Roles Permitidas |
| :--- | :---: | :--- |
| `/api/emailtoken/**` | POST, PUT | **Livre (PermitAll)** |
| `/api/admin/**` | POST, GET, PUT, DELETE | `ADMIN` |
| `/api/local/**` | POST, DELETE | `ADMIN` |
| `/api/funcionarios/**` | POST, PUT | `ADMIN` |
| `/api/tutores/**` | POST, PUT, DELETE | `ADMIN`, `FUNCIONARIO` |
| `/api/animais/**` | POST, PUT, DELETE | `ADMIN`, `FUNCIONARIO` |
| `/api/animais/**`, `/api/tutores/**` | GET | `ADMIN`, `FUNCIONARIO`, `TUTOR` |

## Monitoramento e IoT
### MQTT
* Ambiente Dev: Broker local em `tcp://localhost:1883`.
* Ambiente Prod: Broker MQTT em `tcp://broker.emqx.io:1883`.

```bash
mosquitto_pub -h localhost -t "0806meupet/rastreador/coordenadas" -m '{\"mac\": \"AA:BB:CC:DD:EE:FF\", \"bateria_pct\": 85, \"sinal\": -65, \"rede\": \"WiFi_Local\", \"modo\": \"Normal\", \"lat\": \"-23.591316\", \"lon\": \"-46.645091\", \"velocidade_kmh\": 0.5, \"direcao_graus\": 120.0, \"precisao_hdop\": 1.2, \"distancia_casa_m\": 5, \"fuga\": false, \"atividade\": \"Parado/Dormindo\", \"data_hora\": \"25/03/2026 10:00:00\"}'
mosquitto_pub -h localhost -t "0806meupet/rastreador/coordenadas" -m '{\"mac\": \"AA:BB:CC:DD:EE:FF\", \"bateria_pct\": 82, \"sinal\": 18, \"rede\": \"Celular_Nuvem\", \"modo\": \"Normal\", \"lat\": \"-23.598687\", \"lon\": \"-46.635431\", \"velocidade_kmh\": 8.5, \"direcao_graus\": 185.0, \"precisao_hdop\": 2.1, \"distancia_casa_m\": 850, \"fuga\": true, \"atividade\": \"Correndo/Carro\", \"data_hora\": \"25/03/2026 11:00:00\"}'
```

### Health Check (Actuator)
A aplicação expõe probes de saúde para Kubernetes/Docker:
* `GET /actuator/health`
* `GET /actuator/health/liveness`
* `GET /actuator/health/readiness`

Desenvolvido por NPRRS Team – TAGDOG 2026.