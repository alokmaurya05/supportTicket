output "ec2_instance_public_ip" {
  value = aws_instance.support_ticket_instance.public_ip # Output the public IP address of the EC2 instance after it is created
}