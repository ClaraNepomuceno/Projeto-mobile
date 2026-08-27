# Implementation Plan - Structural Database Refactor (Multi-User & Normalization)

This plan implements the architectural improvements suggested for the database to support multi-user data isolation and proper data normalization (using IDs instead of Names for references).

## Proposed Changes

### [Component: Data Models]

#### [MODIFY] [User.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/model/User.java)
- (Already has ID, name, email, password). No changes needed unless we want to add more fields.

#### [NEW] [Account.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/model/Account.java)
- Create new entity with: `id`, `userId`, `name`, `type`, `balance`, `bankReference`.

#### [NEW] [CreditCard.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/model/CreditCard.java)
- Create new entity with: `id`, `userId`, `name`, `limit`, `used`, `closingDay`, `dueDay`.

#### [MODIFY] [Category.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/model/Category.java)
- Add `userId` field (can be null for system defaults).

#### [MODIFY] [Goal.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/model/Goal.java)
- Add `userId` field.

#### [MODIFY] [Transaction.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/model/Transaction.java)
- Add `userId` field.
- Replace `category` (String) with `categoryId` (Integer).
- Replace `accountName` (String) with `accountId` (Integer).
- Add `creditCardId` (Integer) for credit transactions.

### [Component: Data Access Objects (DAOs)]

#### [MODIFY] All DAOs (`TransactionDao`, `CategoryDao`, `GoalDao`)
- Update all `@Query` methods to include `userId` filter (e.g., `WHERE userId = :userId`).
- Add `AccountDao` and `CreditCardDao`.

### [Component: Database Configuration]

#### [MODIFY] [AppDatabase.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/data/AppDatabase.java)
- Register `Account` and `CreditCard` entities.
- Add abstract methods for `AccountDao` and `CreditCardDao`.
- Increment version to `11`.

### [Component: Authentication & Session]

#### [MODIFY] [LoginFragment.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/ui/auth/LoginFragment.java)
- Update `proceedToDashboard` to save `user_id` in `SharedPreferences`.

### [Component: UI Fragments]

#### [MODIFY] Dashboard, Transactions, Goals, Categories
- Update all data fetching logic to retrieve the `userId` from `SharedPreferences` and pass it to the DAO queries.
- Update `AddTransactionFragment` to allow selecting an Account/Card and save their IDs.

## Verification Plan

### Automated Tests
- Build the project to ensure all entity relationships and DAO queries are syntactically correct.

### Manual Verification
1.  **Multi-user Test**:
    - Log in as User A, create a transaction.
    - Log out, log in as User B.
    - Verify that User B **cannot** see User A's transaction.
2.  **Normalization Test**:
    - Rename a category and verify that transactions linked to it still show the correct updated category details (requires joining in queries or loading objects).
3.  **Account/Card Integration**:
    - Create a transaction selecting a specific account and verify the balance updates correctly.
