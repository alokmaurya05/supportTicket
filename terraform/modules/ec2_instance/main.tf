# This is the main.tf file for the EC2 instance module. 
# It defines an AWS EC2 instance resource with various parameters such as AMI, instance type, subnet ID, security group IDs, key pair name, and user data script. 
# The instance is configured to associate a public IP address and has a tag for identification.
resource "aws_instance" "support_ticket_instance" {
  ami           = var.ami_value
  tags = {
    Name = "EC2InstanceSuportTicket"
  }
  instance_type = var.instance_type_value # Specify the EC2 instance type taken from the variable 
  subnet_id     = var.subnet_id_value  # Specify subnet ID taken from the variable 
  associate_public_ip_address = true # Associate a public IP address with the instance
  vpc_security_group_ids      = var.ec2_security_group_ids # Specify the new EC2 security group from the variaable
  key_name      = var.key_name_value  # Specify your key pair name taken from the variable 
 # user_data="${file("installerSetup.sh")}" # Specify the user data script to run on instance launch Here we install Java-17
  # Optional: standard formatting to guarantee execution on replace
 # user_data_replace_on_change = true 
  connection {
    type        = "ssh"
    user        = "ec2-user"  # Replace with the appropriate username for your EC2 instance
    private_key = file("C:\\Users\\Alok\\Documents\\AWS\\privateKey\\ec2-key.pem")  # Replace with the path to your private key
    host        = self.public_ip
  }

  # File provisioner to copy a file from local to the remote EC2 instance
  provisioner "file" {
    source      = "C:\\Users\\Alok\\workspaceIntelliJ\\supportTicket\\target\\support-ticket-0.0.1-SNAPSHOT.jar"  # Replace with the path to your local file
    destination = "/home/ec2-user/support-ticket-0.0.1-SNAPSHOT.jar"  # Replace with the path on the remote instance
  }

  provisioner "remote-exec" {
    inline = [
      "echo 'Hello from the remote instance'",
      "sudo yum update -y ",  # Update package lists (for Amezon Linux 2023)
      # Install Amazon Corretto 17 
      "sudo yum install -y java-17-amazon-corretto-devel",  # Install Amazon Corretto 17 (Java 17)
      "sudo java --version",  # check if Java is installed
      "nohup sudo java -jar /home/ec2-user/support-ticket-0.0.1-SNAPSHOT.jar > /home/ec2-user/app.log 2>&1 &", # Run the JAR file in the background and log output app.log inside the /home/ec2-user
      "sleep 10", # wait for a few seconds to ensure the JAR file has started
      "echo 'JAR file executed successfully'"
    ]
  }
}