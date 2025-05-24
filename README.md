# DailyTasks - Gerenciador de Tarefas Empresariais (Teste)

## Descrição

O DailyTasks é um sistema de gerenciamento de tarefas projetado para auxiliar na organização e acompanhamento de atividades em um contexto empresarial. Implementado em Java, com persistência de dados utilizando PostgreSQL, este projeto permite o cadastro e controle de funcionários, equipes, listas de tarefas e as tarefas em si, incluindo suas atribuições, status e prioridades.

O sistema opera através de uma interface de console interativa e implementa dois níveis de acesso de usuário:
* **Administrador:** Possui controle total sobre todas as entidades do sistema.
* **Funcionário:** Pode visualizar as tarefas que lhe foram atribuídas e modificar o status dessas tarefas.

Este repositório (`https://github.com/IgorMoreiraM/DailyTasksTeste.git`) serve como uma demonstração e plataforma de teste para as funcionalidades de back-end e a lógica de negócios da aplicação.

## Funcionalidades Principais (Interface de Console)

* **Autenticação Segura:** Sistema de login que diferencia o acesso entre Administradores e Funcionários.
* **Perfis de Usuário:**
    * **Administrador:**
        * CRUD completo para Funcionários (incluindo definição de tipo de usuário e senha).
        * CRUD completo para Equipes.
        * Gerenciamento de associações entre Funcionários e Equipes.
        * CRUD completo para Listas de Tarefas.
        * CRUD completo para Tarefas.
        * Atribuição de tarefas a funcionários específicos.
    * **Funcionário:**
        * Visualização detalhada das tarefas que lhe foram designadas.
        * Capacidade de atualizar o status de suas próprias tarefas.
* **Gerenciamento de Dados:** Interação com banco de dados PostgreSQL para persistência de todas as informações.

## Tecnologias Utilizadas

* **Linguagem Principal:** Java (JDK 8 ou superior recomendado)
* **Banco de Dados:** PostgreSQL
* **Conectividade com BD:** JDBC Driver para PostgreSQL
* **Interface com Usuário:** Console interativo (`System.in`, `System.out`)

## Configuração e Execução do Projeto Localmente

Siga os passos abaixo para configurar e executar o projeto em seu ambiente local.

### Pré-requisitos

1.  **Java Development Kit (JDK):** Versão 8 ou superior.
2.  **PostgreSQL Server:** Instalado e em execução.
3.  **Cliente SQL para PostgreSQL:** pgAdmin, DBeaver, ou `psql` (para executar scripts SQL).
4.  **IDE Java (Opcional, mas recomendado):** IntelliJ IDEA, Eclipse, VS Code com extensões Java.
5.  **Git:** Para clonar o repositório.

### 1. Configuração do Banco de Dados

1.  **Crie um Banco de Dados:**
    No seu servidor PostgreSQL, crie um novo banco de dados para o projeto (ex: `DailyTasks`).
    ```sql
    CREATE DATABASE DailyTasks;
    ```
2.  **Crie as Tabelas (Schema):**
    Execute o script SQL fornecido no projeto (`CriacaoTabelasPrincipaisDailyTask.sql` ou o nome que você usou) para criar todas as tabelas necessárias (`Funcionario`, `Equipe`, `Funcionario_Equipe`, `Lista`, `Tarefa`). Certifique-se que as colunas `tipo_usuario` e `senha` foram adicionadas à tabela `Funcionario`.
3.  **Popule com Dados Iniciais (Recomendado):**
    Execute o script SQL de inserção de dados (`InsercaoDeDadosFicticios.sql` ou similar) para popular as tabelas com dados de teste, incluindo os usuários abaixo.

### 2. Configuração do Projeto Java

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/IgorMoreiraM/DailyTasksTeste.git](https://github.com/IgorMoreiraM/DailyTasksTeste.git)
    cd DailyTasksTeste
    ```
2.  **Configure a Conexão com o Banco de Dados:**
    * Abra o arquivo `DatabaseConnector.java` (localizado em `org/example/DatabaseConnector.java` conforme a última alteração de pacote).
    * Ajuste as constantes `URL`, `USUARIO` e `SENHA` para refletir sua configuração local do PostgreSQL:
        ```java
        private static final String URL = "jdbc:postgresql://localhost:5432/DailyTasks"; // Verifique o nome do seu banco
        private static final String USUARIO = "seu_usuario_postgres"; // Seu usuário do PostgreSQL
        private static final String SENHA = "sua_senha_postgres";   // Sua senha
        ```
3.  **Driver JDBC:**
    Certifique-se de que o driver JDBC do PostgreSQL (o arquivo `.jar`) está configurado no classpath do seu projeto. Se estiver usando uma IDE que gerencia dependências (como Maven ou Gradle, embora não tenhamos configurado explicitamente), adicione a dependência lá. Para compilação manual, você precisará especificar o caminho para o JAR.

### 3. Compilação e Execução

* **Utilizando uma IDE:**
    1.  Importe o projeto para sua IDE (IntelliJ, Eclipse, etc.).
    2.  A IDE geralmente identificará as classes com método `main`. Localize a classe principal da aplicação (ex: `Main.java` no pacote `app` ou o pacote que você definiu).
    3.  Execute o método `main` dessa classe.
* **Via Linha de Comando (Exemplo Genérico):**
    (Ajuste os caminhos dos pacotes e do driver JDBC conforme sua estrutura)
    Supondo que os arquivos `.java` estão em `src/` e as classes compiladas vão para `out/`:
    ```bash
    # Navegue até a raiz do projeto
    # Exemplo de Compilação:
    javac -cp .;caminho/para/postgresql-driver.jar -d out src/org/example/DatabaseConnector.java src/dao/*.java src/com/dailytasks/model/*.java src/app/Main.java
    # (Ajuste os pacotes src/dao/, src/com/dailytasks/model/, src/app/ para os nomes corretos dos seus pacotes)

    # Exemplo de Execução:
    java -cp .;out;caminho/para/postgresql-driver.jar app.Main
    # (Ajuste app.Main para o nome completo da sua classe principal)
    ```

## Logins de Teste

Para interagir com a aplicação via console, utilize as seguintes credenciais:

### Administrador
* **Login (email):** `admin`
* **Senha:** `@admin`

### Funcionário Exemplo
* **Login (email):** `igorhenrique`
* **Senha:** `igor1234`

**Nota Importante sobre Senhas:** Os usuários de teste acima devem ser criados no banco de dados com estas senhas em **texto plano**. O sistema, como demonstrado, realiza a comparação de senhas em texto plano. **Isto é uma prática EXTREMAMENTE INSEGURA e foi utilizada apenas para fins didáticos e de simplificação neste projeto de teste.** Em um ambiente de produção, é mandatório o uso de hashing de senhas (como bcrypt, scrypt ou Argon2).

## Como Usar a Aplicação

1.  Após executar a classe `Main`, a aplicação iniciará no console.
2.  Você será solicitado a fazer login. Utilize uma das credenciais de teste.
3.  Com base no tipo de usuário logado (admin ou funcionário), um menu com as opções disponíveis será exibido.
4.  Digite o número correspondente à opção desejada e pressione Enter.
5.  Siga as instruções no console para cada funcionalidade.

---

Este projeto é um exercício prático e uma base para o desenvolvimento de um sistema de gerenciamento de tarefas mais robusto. Sinta-se à vontade para explorar, modificar e expandir suas funcionalidades!
