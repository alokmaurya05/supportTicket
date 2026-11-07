# Assumptions & Constraints

## Assumptions

### Functional & Technical Assumptions
1. **API-First Interaction:** Support tickets will be managed primarily via HTTP RESTful API endpoints (CRUD operations: Create, Read, Update, Delete) rather than direct spreadsheet manual entry or unparsed email ingestion in the initial scope.
2. **Ticket Data Schema:** Each ticket contains structured data including `Ticket ID`, `Title`, `Description`, `Priority` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), `Status` (`OPEN`, `IN_PROGRESS`, `RESOLVED`), `Created by`, `Created timestamp`, and `Updated timestamp`.
3. **Stateless Compute Layer:** Application logic (processing CRUD actions on tickets) will be handled by stateless execution environments (e.g., AWS Lambda) scale-on-demand without maintaining persistent session state.
4. **Data Persistence:** Persistent storage will be provided by an AWS-managed database (e.g., DynamoDB or RDS) with low latency and single-digit millisecond retrieval by ticket ID.
5. **AWS Managed Services First:** The solution will leverage native AWS managed services (API Gateway, Lambda, DynamoDB/RDS, IAM, CloudWatch) to satisfy serverless, low-maintenance, and scalability goals.
6. **Infrastructure as Code (IaC):** All production resources must be declaratively defined, provisioned, and destroyed using Terraform without manual AWS Console edits.

## Constraints

### Technical & Architectural Constraints
1. **Scope & Simplicity:** The project scope is intentionally kept small and simple, focusing on core ticket management functions rather than complex microservice workflows (e.g., full email/chat integration will be deferred).
2. **Cost-Conscious & Free Tier Alignment:** The architecture must strictly prioritize AWS Free Tier eligible resources to minimize cost overhead throughout testing and evaluation.
3. **Reproducibility:** The full infrastructure deployment must be 100% reproducible using `terraform plan`, `terraform apply`, and cleanable via `terraform destroy`.
4. **Security & Least Privilege:** Execution roles and API endpoints must adhere to AWS IAM least-privilege principles, with no hardcoded credentials or secrets in Git or any token system Like JWT.
5. **Operational Visibility:** Essential logging and basic operational metrics must be configured (e.g., CloudWatch Logs) to log request/response failures and valid execution flows.
6. **Timeframe:** The entire project lifecycle—from requirements analysis, dual-architecture design, Terraform implementation, testing, to documentation—must be completed within the 4-week project period.