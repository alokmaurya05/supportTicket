# defining variables for the RDS MySQL database module
variable "db_username" {
  description = "The master username for the RDS database"
  type        = string
  sensitive   = true # Hides the username from console logs
}

variable "db_password" {
  description = "The master password for the RDS database"
  type        = string
  sensitive   = true # Hides the password from console logs
}
variable "db_name" {
  description = "The name of the database schema to create in the RDS instance"
  type        = string
  default     = "support_ticket" # Default database name
}

variable "db_instance_class" {
  description = "The instance type for the RDS database"
  type        = string
  default     = "db.t3.micro" # Free tier eligible
}

variable "db_security_group_ids" {
  description = "List of VPC Security Group IDs to assign to the RDS instance"
  type        = list(string)
  default     = ["sg-01658c65e67ba4bf5","sg-01bf1c694b434bd2b","sg-00d88a7ca08516e55"] # Default to an empty list if no security groups are provided
}
#End variables for the RDS MySQL database module