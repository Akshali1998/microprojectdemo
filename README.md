# E-Commerce Microservices — Quick Run Guide

Short, focused instructions to run the services in this repo. Includes IDE (no terminal) steps and optional terminal commands.

Checklist
- Import/open project in IntelliJ (or other JetBrains IDE).
- Fix root `pom.xml` if IntelliJ complains about missing modules (two are declared but not present).
- Build or run each service (Inventory, Order, Payment) using the IDE Run/Debug UI.
- Optionally use the provided helper script to build modules and collect logs.

Quick IDE (IntelliJ) steps — no terminal required
1. Open the project folder (the folder that contains `pom.xml`) with File → Open... in IntelliJ.
2. If prompted to import Maven projects, click Import / Enable Auto-Import.
3. Set Project SDK to Java 17: File → Project Structure → Project → Project SDK → Add JDK (point to your JDK 17). Save.
4. If IntelliJ shows errors about missing modules (it may because the root `pom.xml` references modules that are not present):
   - Edit the root `pom.xml` (in the editor) and remove or comment these two lines from the `<modules>` section:
     ```xml
     <module>notification-service</module>
     <module>shipping-service</module>
     ```
   - Save; click the refresh icon in the Maven tool window to re-import.
   - Alternatively, open each service folder individually (File → Open... → `inventory-service`, `order-service`, `payment-service`).
5. Build a module via the Maven tool window: expand the module (e.g. `inventory-service`) → Lifecycle → double-click `clean` then `verify` (or right-click main class and Run).
6. Run a service from the editor: open `src/main/java/.../InventoryServiceApplication.java` and click the green Run icon in the gutter to run the main method.
7. Create Run/Debug configurations (Run → Edit Configurations) for each service. Use the main class and module classpath, add program arguments like `--server.port=8081` if you need custom ports.
8. To start multiple services at once, create a Compound Run Configuration that includes the individual service run configurations.
9. View logs in the Run tool window. Use the Debug mode to attach breakpoints and inspect variables.

Optional: Terminal commands (copyable)
- Build a specific module (from repo root):
```bash
mvn -f inventory-service/pom.xml -B clean verify | tee logs/inventory-service-build.log
```
- Run a Spring Boot app (Java exec) from a module (if desired):
```bash
mvn -f inventory-service/pom.xml -DskipTests spring-boot:run
```
- Build all existing services using the helper script (created in `scripts/check-projects.sh`):
```bash
bash scripts/check-projects.sh
# logs written to ./logs/
```

Ports and configuration
- By default services use Spring Boot default port (8080). To avoid conflicts set `server.port` in `src/main/resources/application.properties` of each module, or pass `--server.port=XXXX` as a program argument in the Run configuration.

Troubleshooting
- Missing module errors on import: remove missing `<module>` entries from root `pom.xml` or open each module separately.
- Dependencies not resolved: open the Maven tool window and click the refresh/reimport icon.
- Port already in use: change `server.port` as above or use program arguments in the Run configuration.
- No tests found: these modules currently have no tests under `src/test/java`.

Helpful files in this repo
- `scripts/check-projects.sh` — helper script that builds each `*-service` module using its own `pom.xml` and writes logs to `logs/`.

If you want, I can:
- Edit the root `pom.xml` to remove the two missing modules so IntelliJ imports the top-level project cleanly.
- Create ready-to-use Run/Debug configurations in `.run/` so you can start services with one click in the IDE.
- Produce a short JSON build report summarizing module statuses.

That's it — tell me which of the optional edits you'd like me to do next and I'll apply them.

