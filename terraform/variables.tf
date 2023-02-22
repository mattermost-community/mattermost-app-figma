variable "lambda_function_handler" {
  default = "com.mattermost.integration.figma.StreamLambdaHandler::handleRequest"
}

variable "lambda_runtime" {
  default = "java11"
}

variable "api_path" {
  default = "{proxy+}"
}

variable "api_env_stage_name" {
  default = "default"
}

variable "lambda_payload_filename" {
  default = "../target/java-mm-figma-function.zip"
}

variable "figma_aws_lambda_bucket_key" {
  default = "figma-aws-lambda-bucket-key"
}

variable "figma_aws_lambda_bucket" {
  default = "figma-aws-lambda-bucket"
}

variable "figma_aws_lambda_encryption_key" {
  default = "mySecretKey"
}