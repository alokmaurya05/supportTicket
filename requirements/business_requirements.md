# Business Requirements

## 1. Overview & Current Situation

TechAssist GmbH provides IT support for small and medium-sized businesses. Currently, support requests arrive manually via email and chat, and the support team tracks issues using spreadsheets. 

### Current Pain Points:
* **Lack of Centralization:** No central ticket management system exists.
* **Manual Operational Overhead:** Support tickets are recorded manually in spreadsheets.
* **Poor Visibility:** Tracking open, in-progress, and resolved tickets across staff is inefficient and error-prone.
* **No Programmatic Access:** There is no API for external applications or integration tools to access ticket information.
* **Scalability Bottlenecks:** The current manual process fails to scale as the customer base expands.

TechAssist GmbH requires a modern, cloud-based, serverless support ticket management system built on AWS that automates ticket operations, scales efficiently, and remains cost-conscious.

---

## 2. Core Functional Requirements

The cloud solution must provide a full CRUD (Create, Read, Update, Delete) capability via standard programmatic interfaces:

1. **Ticket Creation:** Allow authorized clients/users to create new support tickets.
2. **Bulk/List Ticket Retrieval:** Allow support staff to retrieve a list of all existing support tickets.
3. **Individual Ticket Retrieval:** Allow retrieval of specific ticket details using a unique `Ticket ID`.
4. **Ticket Update:** Allow updating existing ticket details, such as changing ticket status, priority, or description.
5. **Ticket Deletion:** Allow support staff to delete tickets from the system when required.
6. **Persistent Storage:** Stored ticket records must persist reliably across all operations.

---

## 3. Data & Schema Requirements

Each support ticket stored within the system must adhere to a standardized data structure containing at least the following attributes and allowed enum values:

### Data Attributes
* **Ticket ID:** Unique identifier for the ticket (e.g., UUID, etc.).
* **Title:** Short summary of the reported technical issue.
* **Description:** Detailed explanation of the issue.
* **Priority:** Severity rating of the ticket.
* **Status:** Current operational state of the ticket.
* **Created By:** Identifier or email of the user/reporter.
* **Created Timestamp:** ISO 8601 formatted timestamp of creation.
* **Updated Timestamp:** ISO 8601 formatted timestamp of the last modification.

### Allowed Priority Values
* `LOW`
* `MEDIUM`
* `HIGH`
* `CRITICAL`

### Allowed Status Values
* `OPEN`
* `IN_PROGRESS`
* `RESOLVED`

---

## 4. Operational & Technical Requirements

1. **Error Handling & Validation:** 
   * The system must return clear, appropriate HTTP response status codes and error messages for invalid payload requests, invalid status/priority values, or missing ticket IDs (e.g., `400 Bad Request`, `404 Not Found`).
2. **Logging & Visibility:** 
   * Basic operational logging and metrics (e.g., CloudWatch) must capture execution flows, API invocations, and application errors.
3. **Security Best Practices:** 
   * The architecture must follow AWS security standards, enforcing IAM least-privilege roles for compute components and securing data access Or Any token system Like JWT.
4. **Infrastructure as Code (IaC):** 
   * 100% of the AWS infrastructure must be provisioned and managed using Terraform. Manual AWS Console configurations are strictly prohibited.
5. **Reproducibility & Cleanup:** 
   * The Terraform setup must allow third parties to easily execute `terraform plan`, `terraform apply`, and completely teardown resources via `terraform destroy`.
6. **Cost Efficiency:** 
   * The system must utilize AWS Free Tier-eligible serverless services where possible, avoiding unnecessary infrastructure costs and expensive continuously running resources.