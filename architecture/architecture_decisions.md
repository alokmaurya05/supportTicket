# Architecture Decisions

## Overview

The goal of this architectural assessment is to evaluate design patterns for TechAssist GmbH's new cloud-based Support Ticket Management System on AWS. 

The primary architectural requirement is to replace manual email/spreadsheet tracking with a secure, scalable, and low-maintenance solution that supports full CRUD operations (`Create`, `Read List`, `Read Specific`, `Update`, `Delete`) while keeping operational complexity and costs minimal under AWS Free Tier.

---

## Architectural Paradigms: Serverless vs Server-Based Architecture

### Serverless Architecture (Event-Driven & Managed)

In a serverless architecture, application execution is managed completely on-demand using managed services (e.g., AWS Lambda, Amazon DynamoDB), eliminating infrastructure management.

**Advantages:**
- **Zero Server Management:** Provisioning, patching, and operating OS/servers are fully managed by AWS.
- **Pay-per-Use Billing:** Charges are incurred only when requests execute; zero cost during idle periods.
- **Automatic & Instant Scaling:** Automatically scales horizontally in response to incoming HTTP traffic volume.
- **High Availability Built-in:** Multi-AZ fault tolerance is inherently provided by AWS managed services.

**Disadvantages:**
- **Cold Starts:** Occasional minor latency spikes during initial execution after periods of inactivity.
- **Vendor Lock-in:** Higher coupling to AWS-specific SDKs and service models.
- **Stateless Execution:** Demands stateless function code design.

### Server-Based Architecture (Virtual Machine & Relational DB)

In a traditional server-based architecture, compute workloads run on persistent virtual instances (Amazon EC2) connected to a relational database engine (Amazon RDS for MySQL).

**Advantages:**
- **Familiar Software Stack:** Standard REST frameworks (e.g., Flask, Express) and standard SQL drivers can be used.
- **Persistent Local Memory/State:** Long-running processes or stateful sessions can be maintained locally.
- **Fine-Grained OS Control:** Complete root administrative access to configure custom operating systems and network parameters.

**Disadvantages:**
- **Operational Overhead:** Requires manual or automated OS updates, security patches, system monitoring, and backup routines.
- **Idle Infrastructure Cost:** EC2 instances and RDS database instances incur hourly running charges even when zero requests are being processed.
- **Complex Auto Scaling:** Requires explicit provisioning of Auto Scaling Groups, Load Balancers, and scaling policies.

### Architectural Decision

The **Serverless Architecture** is selected for TechAssist GmbH. A serverless model matches the company’s need for a low-maintenance, highly scalable, and cost-conscious solution without incurring static hourly operational costs or unnecessary maintenance overhead.

---

## Proposed Architecture Options

### Architecture Option 1 — API Gateway + AWS Lambda + Amazon DynamoDB (Serverless)

#### Architecture Request Flow
Client / Support Staff  
→ Amazon API Gateway (REST API endpoints)  
→ AWS Lambda (CRUD Python Functions)  
→ Amazon DynamoDB (NoSQL Data Store)  
→ Amazon CloudWatch (Logs & Metrics)

#### Components & Services
- **Amazon API Gateway:** Exposes secure REST API endpoints (`POST`, `GET`, `PUT`, `DELETE`) for ticket operations and handles request routing, status codes, and input validation.
- **AWS Lambda:** Executes Python backend logic stateless on-demand to process incoming payloads and interact with storage.
- **Amazon DynamoDB:** Stores ticket items persistently in a single NoSQL table using `Ticket ID` as the primary partition key.
- **Amazon CloudWatch:** Collects API execution metrics, Lambda logs, and system error traces.
- **AWS IAM:** Enforces least-privilege policies and execution roles for compute access to DynamoDB and CloudWatch.

#### Advantages
- Completely serverless with zero persistent server management.
- Dynamic key-value item structure natively fits flexible ticket metadata (`Priority`, `Status`, `Timestamps`).
- Extremely cost-effective with generous AWS Free Tier limits (1M Lambda requests/month, 25 GB DynamoDB storage).
- Simple Terraform provisioning with minimal resource dependency chains.

#### Limitations
- No SQL query flexibility (complex relational joins are not natively supported, though not required for single-entity ticket structures).

---

## Architecture Option 2 — API Gateway + Amazon EC2 + Amazon RDS for MySQL (Server-Based)

#### Architecture Request Flow
Client / Support Staff  
→ Amazon API Gateway  
→ Amazon EC2 Instance (Python REST API Web Server)  
→ Amazon RDS for MySQL (Relational Database)  
→ Amazon CloudWatch / IAM

#### Components & Services
- **Amazon API Gateway:** Routes HTTP requests to the EC2 web server backend.
- **Amazon EC2:** Hosts a persistent Linux virtual machine running a Python application web server (e.g., Flask/FastAPI).
- **Amazon RDS for MySQL:** Managed relational database storing ticket records in a structured SQL table schema.
- **AWS IAM & CloudWatch:** Provides security access control and server logging.

#### Advantages
- Standard relational SQL table structure.
- Easily portable web application code standard across on-premises or other clouds.

#### Limitations
- Higher baseline cost: EC2 and RDS incur continuous charges regardless of usage volume.
- Increased operational burden (patching, security groups, database instance lifecycle management).
- Requires complex Terraform definitions (VPC networking, subnets, DB subnet groups, security groups, EC2 user-data scripts).

---

## Selected Architecture Choice

The selected design for implementation is **Option 1: API Gateway + AWS Lambda + Amazon DynamoDB**.

### Justification & Trade-Off Analysis

| Criteria | Option 1: Serverless (Selected) | Option 2: EC2 + RDS |
| :--- | :--- | :--- |
| **Requirement Fit** | **Optimal** — Full CRUD ticket management satisfied directly. | Satisfied, but over-engineered for key-value ticket CRUD operations. |
| **Operational Overhead** | **Zero server management** — AWS manages compute and database hardware. | **High** — Requires OS patching, web server maintenance, and DB management. |
| **Cost Efficiency** | **Free Tier Friendly** — $0 idle costs; pay per execution. | **Cost Inefficient** — Hourly charges for EC2 & RDS instances. |
| **Scalability** | **Instant & Automatic** scaling from zero to peak request loads. | Requires setting up Application Load Balancers (ALB) and Auto Scaling Groups. |
| **Terraform Complexity** | **Low to Moderate** — Concise resource declarations. | **High** — Demands complex network VPC, subnet, DB, and security configurations. |

---

## AWS Service Architectural Decisions

### 1. Compute Layer: AWS Lambda
- **Decision:** Selected over Amazon EC2.
- **Why:** Eliminates server maintenance, auto-scales instantly with inbound support ticket traffic, and incurs zero cost when no requests are being made.

### 2. API Ingestion Layer: Amazon API Gateway (REST API)
- **Decision:** Selected as the front-end entry point.
- **Why:** Provides a managed HTTP REST interface to map incoming client HTTP verbs (`POST /tickets`, `GET /tickets/{id}`, `PUT /tickets/{id}`, `DELETE /tickets/{id}`) directly to Lambda functions with built-in status codes (`200 OK`, `400 Bad Request`, `404 Not Found`).

### 3. Database Layer: Amazon DynamoDB
- **Decision:** Selected over Amazon RDS for MySQL.
- **Why:** Ticket records are self-contained documents mapped easily by `Ticket ID`. DynamoDB provides single-digit millisecond latency, single-table simplicity, and no persistent database server management.

### 4. Observability & Security: Amazon CloudWatch & AWS IAM
- **Decision:** Use CloudWatch Logs paired with IAM Least-Privilege Execution Roles.
- **Why:** IAM roles guarantee that Lambda functions can only perform allowed actions on the specific DynamoDB table, while CloudWatch records detailed execution logs for system visibility and failure debugging.

---

## Cost Considerations

The entire selected serverless stack (API Gateway + AWS Lambda + DynamoDB + CloudWatch) fits comfortably within the **AWS Free Tier**:
- **AWS Lambda:** 1,000,000 free requests and 3.2 million seconds of compute time per month.
- **Amazon DynamoDB:** 25 GB of free storage and 25 WCU/RCU of provisioned capacity (or On-Demand free allocations).
- **Amazon API Gateway:** 1,000,000 API calls per month free for the first 12 months.

This ensures zero cost during development, testing, evaluation, and eventual teardown via Terraform.