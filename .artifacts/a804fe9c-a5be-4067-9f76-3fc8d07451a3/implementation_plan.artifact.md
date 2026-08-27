# Implementation Plan - Fix missing `nav_add_account` symbol

The project fails to build because `R.id.nav_add_account` is referenced in `AccountsFragment.java` but is not defined in the navigation graph. This appears to be due to a previous removal of the Accounts feature that was incomplete.

## Proposed Changes

### Navigation Graph

#### [MODIFY] [mobile_navigation.xml](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/res/navigation/mobile_navigation.xml)
- Add `nav_accounts` destination for `AccountsFragment`.
- Add `nav_add_account` destination for `AddAccountFragment`.

### UI Integration (Restoring the feature)

#### [MODIFY] [navigation_drawer.xml](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/res/menu/navigation_drawer.xml)
- Add "Contas" item to the navigation drawer to allow users to navigate to the Accounts screen.

#### [MODIFY] [MainActivity.java](file:///C:/Users/Ana57393876/AndroidStudioProjects/Projeto/app/src/main/java/com/example/projeto/MainActivity.java)
- Add `R.id.nav_accounts` to the top-level destinations in `AppBarConfiguration`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugJavaWithJavac` to verify that the project now builds successfully.

### Manual Verification
- Deploy the app to a device/emulator.
- Open the navigation drawer and verify that the "Contas" item is present.
- Click on "Contas" and verify it navigates to the Accounts fragment.
- In the Accounts fragment, click the button to add an account and verify it navigates to the Add Account fragment.
