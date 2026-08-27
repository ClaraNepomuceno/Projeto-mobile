# Fix Room Schema Issue and Build Errors

The user reported that the Room database version increment "didn't work". Investigation revealed that the application failed to build because of a constructor mismatch in the `Category` class. The `Category` entity was updated to require a `userId`, but its usages in `AddCategoryFragment` and `AddTransactionFragment` were not updated. Because the app failed to build, the version increment was never deployed, leading to the same `IllegalStateException`.

## Proposed Changes

### 1. Authentication & Session Management
- Update `LoginFragment` to save the `user_id` in `SharedPreferences` upon successful login.
- Update `RegisterFragment` (if applicable) to save the `user_id` upon successful registration.

### 2. Category Model Usages
- Update `AddCategoryFragment` to retrieve the `userId` from `SharedPreferences` and pass it to the `Category` constructor.
- Update `AddTransactionFragment`'s "New Category" dialog to also retrieve the `userId` and pass it to the `Category` constructor.

### 3. Database Version
- Ensure the `AppDatabase` version is set to `11` (already done, but needs to be verified after build).

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to ensure the project compiles successfully.

### Manual Verification
- Deploy the app to the device.
- Verify that the app starts without the `IllegalStateException`.
- Verify that adding a new category works correctly.
