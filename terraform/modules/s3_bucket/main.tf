# New S3 Bucket resource
resource "aws_s3_bucket" "support_ticket_bucket" {
  bucket = var.bucket_name_value
  tags = {
    Name        = "SupportTicketBucket"
    Environment = "Dev"
  }
}

resource "aws_s3_bucket_website_configuration" "support_ticket_bucket_website" {
  bucket = aws_s3_bucket.support_ticket_bucket.id

  index_document {
    suffix = "index.html"
  }
}

  # 1. Disable Block Public Access on this bucket
resource "aws_s3_bucket_public_access_block" "support_bucket_pab" {
  bucket = aws_s3_bucket.support_ticket_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

# 2. Re-enable ACL support (disabled by default on new AWS buckets)
resource "aws_s3_bucket_ownership_controls" "support_ticket_bucket_ownership" {
  bucket = aws_s3_bucket.support_ticket_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

# 3. Apply the ACL only AFTER the two resources above finish
# resource "aws_s3_bucket_acl" "support_ticket_bucket_acl" {
#   bucket = aws_s3_bucket.support_ticket_bucket.id
#   acl    = "public-read"
# }

