# defining variables for the EC2 instance module
variable "ami_value" {
    description = "AMI ID for the EC2 instance"
    type        = string
}
variable "instance_type_value" {
    description = "EC2 instance type"
    type        = string
    default     = "t2.micro" # Default to t2.micro if no instance type is provided
}
variable "subnet_id_value" {
    description = "Subnet ID for the EC2 instance"
    type        = string
    default     = "subnet-0619440e370d50abf" # Default to a specific subnet ID if no subnet ID is provided
}
variable "ec2_security_group_ids" {
  description = "List of VPC Security Group IDs to assign to the EC2 instance"
  type        = list(string)
}
variable "key_name_value" {
    description = "Key pair name for the EC2 instance"
    type        = string
    default     = "ec2-key" # Default to "ec2-key" if no key pair name is provided
}
#End variables for the EC2 instance module