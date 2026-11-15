# New RDS MySQL Database resource
resource "aws_db_instance" "support_ticket_db" {
  allocated_storage      = 20                     # 20 GB (minimum/ we usefree tier standard)
  engine                 = "mysql"
  engine_version         = "8.0"                  # Stable MySQL version
  instance_class         = var.db_instance_class
  db_name                = var.db_name            # The actual database schema name
  username               = var.db_username        # The master username for the RDS database
  password               = var.db_password        # The master password for the RDS database
  parameter_group_name   = "default.mysql8.0"
  skip_final_snapshot    = true                   # Prevents hanging when deleting the stack during testing
  publicly_accessible    = true                   # Keeps the database private and secure Testing PURPOSE MAKING true
  vpc_security_group_ids = var.db_security_group_ids # Assign the security group IDs for the RDS instance

  tags = {
    Name = "supportTicketMySQL"
  }
  
}

