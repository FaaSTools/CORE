# ------------------------------------------------------
# MODULE INPUT
# ------------------------------------------------------

variable id {}
variable region {}
variable _version {
  default = "1.0-SNAPSHOT"
}

# ------------------------------------------------------
# UPLOAD DEPLOYMENT PACKAGES
# ------------------------------------------------------

# Create a bucket
resource "aws_s3_bucket" "deployment_bucket" {
  bucket        = "deployment-packages-${var.region}-${var.id}"
  force_destroy = true
}

resource "aws_s3_object" "noop-benchmark_deployment_package" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "noop-benchmark.jar"
  acl    = "private"  # or can be "public-read"
  source = "${path.root}/noop-benchmark/target/deployable/noop-benchmark-${var._version}.jar"
  etag   = filemd5("${path.root}/noop-benchmark/target/deployable/noop-benchmark-${var._version}.jar")
}

resource "aws_s3_object" "ocr-benchmark_deployment_package" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "ocr-benchmark.jar"
  acl    = "private"  # or can be "public-read"
  source = "${path.root}/ocr-benchmark/target/deployable/ocr-benchmark-${var._version}.jar"
  etag   = filemd5("${path.root}/ocr-benchmark/target/deployable/ocr-benchmark-${var._version}.jar")
}

resource "aws_s3_object" "recognition-benchmark_deployment_package" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "recognition-benchmark.jar"
  acl    = "private"  # or can be "public-read"
  source = "${path.root}/recognition-benchmark/target/deployable/recognition-benchmark-${var._version}.jar"
  etag   = filemd5("${path.root}/recognition-benchmark/target/deployable/recognition-benchmark-${var._version}.jar")
}

resource "aws_s3_object" "storage-benchmark_deployment_package" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "storage-benchmark.jar"
  acl    = "private"  # or can be "public-read"
  source = "${path.root}/storage-benchmark/target/deployable/storage-benchmark-${var._version}.jar"
  etag   = filemd5("${path.root}/storage-benchmark/target/deployable/storage-benchmark-${var._version}.jar")
}

resource "aws_s3_object" "synthesis-benchmark_deployment_package" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "synthesis-benchmark.jar"
  acl    = "private"  # or can be "public-read"
  source = "${path.root}/synthesis-benchmark/target/deployable/synthesis-benchmark-${var._version}.jar"
  etag   = filemd5("${path.root}/synthesis-benchmark/target/deployable/synthesis-benchmark-${var._version}.jar")
}

resource "aws_s3_object" "translation-benchmark_deployment_package" {
  bucket = aws_s3_bucket.deployment_bucket.id
  key    = "translation-benchmark.jar"
  acl    = "private"  # or can be "public-read"
  source = "${path.root}/translation-benchmark/target/deployable/translation-benchmark-${var._version}.jar"
  etag   = filemd5("${path.root}/translation-benchmark/target/deployable/translation-benchmark-${var._version}.jar")
}

# ------------------------------------------------------
# FUNCTIONS
# ------------------------------------------------------

resource "aws_lambda_function" "noop-benchmark_function" {
  function_name    = "noop-benchmark"
  s3_bucket        = aws_s3_object.noop-benchmark_deployment_package.bucket
  s3_key           = aws_s3_object.noop-benchmark_deployment_package.key
  runtime          = "java17"
  handler          = "function.NoOpFunction::handleRequest"
  source_code_hash = filebase64sha256(aws_s3_object.noop-benchmark_deployment_package.source)
  role             = data.aws_iam_role.lab_role.arn
  timeout          = 500
  memory_size      = 512
  ephemeral_storage { size = 512 }
}

resource "aws_lambda_function" "ocr-benchmark_function" {
  function_name    = "ocr-benchmark"
  s3_bucket        = aws_s3_object.ocr-benchmark_deployment_package.bucket
  s3_key           = aws_s3_object.ocr-benchmark_deployment_package.key
  runtime          = "java17"
  handler          = "function.ImageRecognitionCore::handleRequest"
  source_code_hash = filebase64sha256(aws_s3_object.ocr-benchmark_deployment_package.source)
  role             = data.aws_iam_role.lab_role.arn
  timeout          = 500
  memory_size      = 512
  ephemeral_storage { size = 512 }
}

resource "aws_lambda_function" "recognition-benchmark_function" {
  function_name    = "recognition-benchmark"
  s3_bucket        = aws_s3_object.recognition-benchmark_deployment_package.bucket
  s3_key           = aws_s3_object.recognition-benchmark_deployment_package.key
  runtime          = "java17"
  handler          = "function.SpeechRecognitionCore::handleRequest"
  source_code_hash = filebase64sha256(aws_s3_object.recognition-benchmark_deployment_package.source)
  role             = data.aws_iam_role.lab_role.arn
  timeout          = 500
  memory_size      = 512
  ephemeral_storage { size = 512 }
}

resource "aws_lambda_function" "storage-benchmark_function" {
  function_name    = "storage-benchmark"
  s3_bucket        = aws_s3_object.storage-benchmark_deployment_package.bucket
  s3_key           = aws_s3_object.storage-benchmark_deployment_package.key
  runtime          = "java17"
  handler          = "function.DownUpCore::handleRequest"
  source_code_hash = filebase64sha256(aws_s3_object.storage-benchmark_deployment_package.source)
  role             = data.aws_iam_role.lab_role.arn
  timeout          = 500
  memory_size      = 512
  ephemeral_storage { size = 512 }
}

resource "aws_lambda_function" "synthesis-benchmark_function" {
  function_name    = "synthesis-benchmark"
  s3_bucket        = aws_s3_object.synthesis-benchmark_deployment_package.bucket
  s3_key           = aws_s3_object.synthesis-benchmark_deployment_package.key
  runtime          = "java17"
  handler          = "function.SpeechSynthesisCore::handleRequest"
  source_code_hash = filebase64sha256(aws_s3_object.synthesis-benchmark_deployment_package.source)
  role             = data.aws_iam_role.lab_role.arn
  timeout          = 500
  memory_size      = 512
  ephemeral_storage { size = 512 }
}

resource "aws_lambda_function" "translation-benchmark_function" {
  function_name    = "translation-benchmark"
  s3_bucket        = aws_s3_object.translation-benchmark_deployment_package.bucket
  s3_key           = aws_s3_object.translation-benchmark_deployment_package.key
  runtime          = "java17"
  handler          = "function.TranslateCore::handleRequest"
  source_code_hash = filebase64sha256(aws_s3_object.translation-benchmark_deployment_package.source)
  role             = data.aws_iam_role.lab_role.arn
  timeout          = 500
  memory_size      = 512
  ephemeral_storage { size = 512 }
}

# ------------------------------------------------------
# IAM
# ------------------------------------------------------

data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

# ------------------------------------------------------
# LOG GROUPS
# ------------------------------------------------------

resource "aws_cloudwatch_log_group" "noop-benchmark_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.noop-benchmark_function.function_name}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "ocr-benchmark_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.ocr-benchmark_function.function_name}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "recognition-benchmark_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.recognition-benchmark_function.function_name}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "storage-benchmark_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.storage-benchmark_function.function_name}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "synthesis-benchmark_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.synthesis-benchmark_function.function_name}"
  retention_in_days = 14
}

resource "aws_cloudwatch_log_group" "translation-benchmark_log_group" {
  name              = "/aws/lambda/${aws_lambda_function.translation-benchmark_function.function_name}"
  retention_in_days = 14
}
