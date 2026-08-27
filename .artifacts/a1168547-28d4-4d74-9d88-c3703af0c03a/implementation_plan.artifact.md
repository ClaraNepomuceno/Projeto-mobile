# Plano: Remoção da Funcionalidade de Contas

O usuário solicitou a remoção da "parte das contas" do aplicativo. Isso envolve remover a interface de usuário, a navegação e a persistência de dados relacionada a Contas e Cartões de Crédito (que estão integrados na mesma tela).

## Mudanças Propostas

### 1. Navegação e Menus

#### [MODIFY] [mobile_navigation.xml](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/res/navigation/mobile_navigation.xml)
- Remover os fragmentos `nav_accounts` e `nav_add_account`.

#### [MODIFY] [bottom_navigation.xml](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/res/menu/bottom_navigation.xml)
- Remover o item de menu "Contas" (`nav_accounts`).

#### [MODIFY] [MainActivity.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/MainActivity.java)
- Remover `R.id.nav_accounts` da lista de destinos do `AppBarConfiguration`.

### 2. Camada de Dados (Database)

#### [MODIFY] [AppDatabase.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/AppDatabase.java)
- Remover `Account.class` e `CreditCard.class` da anotação `@Database`.
- Remover os métodos abstratos `accountDao()` e `creditCardDao()`.

### 3. Limpeza de Arquivos (Deleção)

Vou remover os seguintes arquivos e diretórios que se tornarão órfãos:
- `com.example.projeto.ui.accounts` (diretório completo, incluindo `AccountsFragment`, `AddAccountFragment` e `WalletAdapter`).
- `com.example.projeto.data.model.Account` e `com.example.projeto.data.model.CreditCard`.
- `com.example.projeto.data.dao.AccountDao` e `com.example.projeto.data.dao.CreditCardDao`.
- `com.example.projeto.data.BankSimService` (serviço de simulação de bancos).
- Layouts: `fragment_accounts.xml`, `fragment_add_account.xml`, `item_account.xml` e `item_credit_card.xml`.

---

## Observações Importantes

> [!WARNING]
> A remoção da classe `CreditCard` é necessária pois ela só era utilizada na tela de Contas. Se você pretender usar cartões de crédito em outra parte do sistema no futuro, por favor, me avise antes de eu deletar os modelos.

> [!NOTE]
> As transações atualmente salvam o nome da conta como "Carteira" de forma fixa. Essa lógica será mantida para não quebrar o histórico de transações, mas o usuário não poderá mais criar ou gerenciar contas diferentes.

## Plano de Verificação

### Testes Manuais
1. Abrir o aplicativo e verificar se o ícone de "Contas" desapareceu da barra inferior.
2. Verificar se o menu lateral (se houver) ou a barra de título não mencionam mais "Contas".
3. Tentar adicionar uma transação e garantir que o sistema continua funcionando (usando o padrão "Carteira").
4. Garantir que o app compila e roda sem erros de referências perdidas.
