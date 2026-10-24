# Sistema de Gestão Financeira

## Descrição do Projeto

O Sistema de Gestão Financeira é uma aplicação mobile desenvolvida para dispositivos Android, com o objetivo de auxiliar os usuários na organização e no controle de suas finanças pessoais.

O aplicativo permite registrar receitas e despesas, gerenciar categorias financeiras, acompanhar metas e visualizar relatórios sobre a movimentação do dinheiro.

O projeto busca proporcionar uma experiência prática e intuitiva, facilitando o acompanhamento das informações financeiras e contribuindo para um planejamento mais organizado.

## Funcionalidades

### Autenticação de Usuários

Cadastro de novos usuários, autenticação por login e interface de recuperação de senha.

### Dashboard Financeiro

Visualização das principais informações financeiras, permitindo acompanhar movimentações, categorias e metas cadastradas.

### Gerenciamento de Transações

Cadastro e visualização de receitas e despesas, possibilitando o acompanhamento das movimentações financeiras.

### Gerenciamento de Categorias

Criação, visualização e organização de categorias utilizadas na classificação das transações.

### Metas Financeiras

Cadastro e acompanhamento de metas financeiras para auxiliar no planejamento de objetivos pessoais.

### Relatórios Financeiros

Visualização de informações e gráficos relacionados às movimentações financeiras.

### Configurações

Área destinada às preferências e configurações do aplicativo.

### Lembretes

Implementação de um componente responsável pelo processamento de lembretes financeiros utilizando WorkManager.

## Tecnologias Utilizadas

O projeto utiliza as seguintes tecnologias:

**Java:** Linguagem de programação utilizada no desenvolvimento da aplicação.

**Android Studio:** Ambiente de desenvolvimento utilizado para implementação e execução do aplicativo.

**XML:** Linguagem utilizada na construção das interfaces gráficas.

**Room Database:** Biblioteca responsável pelo armazenamento e gerenciamento de dados locais.

**SQLite:** Banco de dados utilizado pelo Room para persistência local.

**MySQL:** Banco de dados relacional utilizado na integração com dados remotos.

**Gradle:** Ferramenta responsável pelo gerenciamento de dependências e compilação do projeto.

**Android Jetpack:** Conjunto de bibliotecas utilizadas na estruturação e no funcionamento da aplicação.

**Navigation Component:** Biblioteca utilizada para gerenciar a navegação entre as telas.

**RecyclerView:** Componente utilizado para apresentar listas de informações.

**WorkManager:** Biblioteca utilizada para execução de tarefas em segundo plano.

**MPAndroidChart:** Biblioteca utilizada para representação gráfica de informações financeiras.

## Estrutura do Projeto

O código está organizado em pacotes responsáveis pelas diferentes funcionalidades da aplicação.

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── com/example/projeto/
        │       ├── data/
        │       │   ├── dao/
        │       │   ├── model/
        │       │   ├── remote/
        │       │   └── AppDatabase.java
        │       ├── ui/
        │       │   ├── auth/
        │       │   ├── categories/
        │       │   ├── dashboard/
        │       │   ├── goals/
        │       │   ├── reports/
        │       │   ├── settings/
        │       │   └── transactions/
        │       ├── worker/
        │       └── MainActivity.java
        ├── res/
        └── AndroidManifest.xml
```

### Organização dos Pacotes

**data:** Contém as classes responsáveis pelo gerenciamento e pela persistência dos dados.

**dao:** Contém as interfaces de acesso ao banco de dados local.

**model:** Contém as entidades utilizadas na representação das informações do sistema.

**remote:** Contém as classes relacionadas à conexão com o banco de dados MySQL.

**ui:** Contém as telas e os componentes de interface da aplicação.

**worker:** Contém os componentes responsáveis pela execução de tarefas em segundo plano.

**MainActivity:** Classe principal responsável pela inicialização da interface do aplicativo.

## Requisitos para Execução

Para executar o projeto, é necessário possuir:

Android Studio instalado.

Java compatível com a configuração do projeto.

Android SDK configurado.

Emulador Android ou dispositivo físico compatível com Android 7.0 ou superior (API 24).

Servidor MySQL configurado e acessível, caso sejam utilizadas as funcionalidades que dependem da conexão remota.

## Como Executar o Projeto

### 1. Obter o Projeto

Clone o repositório utilizando o Git:

```bash
git clone URL_DO_REPOSITORIO
```

Ou baixe o projeto em formato ZIP e extraia os arquivos.

### 2. Abrir no Android Studio

Abra o Android Studio e selecione a opção **Open**.

Localize a pasta do projeto e aguarde a sincronização das dependências do Gradle.

### 3. Configurar o Banco de Dados

O aplicativo utiliza Room para armazenamento local e possui classes de integração com MySQL.

Para utilizar a conexão remota, configure os parâmetros de conexão conforme o ambiente de execução.

O arquivo responsável pela configuração está localizado em:

```text
app/src/main/java/com/example/projeto/data/remote/MySqlConfig.java
```

### 4. Executar a Aplicação

Selecione um emulador Android ou conecte um dispositivo físico.

Clique em **Run** no Android Studio para compilar e iniciar o aplicativo.

## Objetivo do Projeto

O desenvolvimento do Sistema de Gestão Financeira tem como objetivo aplicar conhecimentos de programação mobile, persistência de dados, desenvolvimento de interfaces e integração com bancos de dados.

A aplicação também busca demonstrar a utilização de ferramentas e bibliotecas do ecossistema Android na construção de um sistema voltado ao gerenciamento financeiro pessoal.

## Status do Projeto

Projeto em desenvolvimento, sujeito a melhorias, correções e implementação de novas funcionalidades.

## Considerações Finais

O Sistema de Gestão Financeira foi desenvolvido com o propósito de oferecer uma ferramenta de apoio ao controle financeiro pessoal, reunindo funcionalidades de organização, acompanhamento e planejamento em uma única aplicação.

O projeto também contribui para o desenvolvimento de habilidades práticas relacionadas à programação Android, organização de código e gerenciamento de dados.
