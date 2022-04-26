

resource "aws_s3_bucket" "figma_aws_lambda_bucket" {
  acl    = "private"
  bucket = "${var.figma_aws_lambda_bucket}"
  force_destroy = true

  versioning {
    enabled = false
  }
}

resource "aws_s3_bucket_object" "figma-jar" {
  bucket = "${var.figma_aws_lambda_bucket}"
  source      = "${var.lambda_payload_filename}"
  key       = "${var.figma_aws_lambda_bucket_key}"
  depends_on = [aws_s3_bucket.figma_aws_lambda_bucket]
}


