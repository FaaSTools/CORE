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

resource "google_storage_bucket" "deployment_bucket" {
  name          = "deployment-packages-${var.region}-${var.id}"
  location      = var.region
  force_destroy = true
}

data "archive_file" "noop-benchmark_deployment_package" {
  type        = "zip"
  source_file = "${path.root}/noop-benchmark/target/deployable/noop-benchmark-${var._version}.jar"
  output_path = "${path.root}/noop-benchmark/target/deployable/noop-benchmark-${var._version}.zip"
}


data "archive_file" "ocr-benchmark_deployment_package" {
  type        = "zip"
  source_file = "${path.root}/ocr-benchmark/target/deployable/ocr-benchmark-${var._version}.jar"
  output_path = "${path.root}/ocr-benchmark/target/deployable/ocr-benchmark-${var._version}.zip"
}

data "archive_file" "recognition-benchmark_deployment_package" {
  type        = "zip"
  source_file = "${path.root}/recognition-benchmark/target/deployable/recognition-benchmark-${var._version}.jar"
  output_path = "${path.root}/recognition-benchmark/target/deployable/recognition-benchmark-${var._version}.zip"
}

data "archive_file" "storage-benchmark_deployment_package" {
  type        = "zip"
  source_file = "${path.root}/storage-benchmark/target/deployable/storage-benchmark-${var._version}.jar"
  output_path = "${path.root}/storage-benchmark/target/deployable/storage-benchmark-${var._version}.zip"
}

data "archive_file" "synthesis-benchmark_deployment_package" {
  type        = "zip"
  source_file = "${path.root}/synthesis-benchmark/target/deployable/synthesis-benchmark-${var._version}.jar"
  output_path = "${path.root}/synthesis-benchmark/target/deployable/synthesis-benchmark-${var._version}.zip"
}

data "archive_file" "translation-benchmark_deployment_package" {
  type        = "zip"
  source_file = "${path.root}/translation-benchmark/target/deployable/translation-benchmark-${var._version}.jar"
  output_path = "${path.root}/translation-benchmark/target/deployable/translation-benchmark-${var._version}.zip"
}

resource "google_storage_bucket_object" "noop-benchmark_bucket_object" {
  name   = "noop-benchmark.zip"
  bucket = google_storage_bucket.deployment_bucket.name
  source = data.archive_file.noop-benchmark_deployment_package.output_path
  timeouts {
    create = "60m"
    update = "60m"
    delete = "60m"
  }
}

resource "google_storage_bucket_object" "ocr-benchmark_bucket_object" {
  name   = "ocr-benchmark.zip"
  bucket = google_storage_bucket.deployment_bucket.name
  source = data.archive_file.ocr-benchmark_deployment_package.output_path
  timeouts {
    create = "60m"
    update = "60m"
    delete = "60m"
  }
}

resource "google_storage_bucket_object" "recognition-benchmark_bucket_object" {
  name   = "recognition-benchmark.zip"
  bucket = google_storage_bucket.deployment_bucket.name
  source = data.archive_file.recognition-benchmark_deployment_package.output_path
  timeouts {
    create = "60m"
    update = "60m"
    delete = "60m"
  }
}

resource "google_storage_bucket_object" "storage-benchmark_bucket_object" {
  name   = "storage-benchmark.zip"
  bucket = google_storage_bucket.deployment_bucket.name
  source = data.archive_file.storage-benchmark_deployment_package.output_path
  timeouts {
    create = "60m"
    update = "60m"
    delete = "60m"
  }
}

resource "google_storage_bucket_object" "synthesis-benchmark_bucket_object" {
  name   = "synthesis-benchmark.zip"
  bucket = google_storage_bucket.deployment_bucket.name
  source = data.archive_file.synthesis-benchmark_deployment_package.output_path
  timeouts {
    create = "60m"
    update = "60m"
    delete = "60m"
  }
}

resource "google_storage_bucket_object" "translation-benchmark_bucket_object" {
  name   = "translation-benchmark.zip"
  bucket = google_storage_bucket.deployment_bucket.name
  source = data.archive_file.translation-benchmark_deployment_package.output_path
  timeouts {
    create = "60m"
    update = "60m"
    delete = "60m"
  }
}


# ------------------------------------------------------
# FUNCTIONS
# ------------------------------------------------------

resource "google_cloudfunctions_function" "noop-benchmark_function" {
  name                  = "noop-benchmark"
  runtime               = "java17"
  available_memory_mb   = 512
  source_archive_bucket = google_storage_bucket.deployment_bucket.name
  source_archive_object = google_storage_bucket_object.noop-benchmark_bucket_object.name
  trigger_http          = true
  entry_point           = "function.NoOpFunction"
  timeout               = 500
}

resource "google_cloudfunctions_function" "ocr-benchmark_function" {
  name                  = "ocr-benchmark"
  runtime               = "java17"
  available_memory_mb   = 512
  source_archive_bucket = google_storage_bucket.deployment_bucket.name
  source_archive_object = google_storage_bucket_object.ocr-benchmark_bucket_object.name
  trigger_http          = true
  entry_point           = "function.ImageRecognitionCore"
  timeout               = 500
}

resource "google_cloudfunctions_function" "recognition-benchmark_function" {
  name                  = "recognition-benchmark"
  runtime               = "java17"
  available_memory_mb   = 512
  source_archive_bucket = google_storage_bucket.deployment_bucket.name
  source_archive_object = google_storage_bucket_object.recognition-benchmark_bucket_object.name
  trigger_http          = true
  entry_point           = "function.SpeechRecognitionCore"
  timeout               = 500
}

resource "google_cloudfunctions_function" "storage-benchmark_function" {
  name                  = "storage-benchmark"
  runtime               = "java17"
  available_memory_mb   = 512
  source_archive_bucket = google_storage_bucket.deployment_bucket.name
  source_archive_object = google_storage_bucket_object.storage-benchmark_bucket_object.name
  trigger_http          = true
  entry_point           = "function.DownUpCore"
  timeout               = 500
}

resource "google_cloudfunctions_function" "synthesis-benchmark_function" {
  name                  = "synthesis-benchmark"
  runtime               = "java17"
  available_memory_mb   = 512
  source_archive_bucket = google_storage_bucket.deployment_bucket.name
  source_archive_object = google_storage_bucket_object.synthesis-benchmark_bucket_object.name
  trigger_http          = true
  entry_point           = "function.SpeechSynthesisCore"
  timeout               = 500
}

resource "google_cloudfunctions_function" "translation-benchmark_function" {
  name                  = "translation-benchmark"
  runtime               = "java17"
  available_memory_mb   = 512
  source_archive_bucket = google_storage_bucket.deployment_bucket.name
  source_archive_object = google_storage_bucket_object.translation-benchmark_bucket_object.name
  trigger_http          = true
  entry_point           = "function.TranslateCore"
  timeout               = 500
}


# ------------------------------------------------------
# IAM
# ------------------------------------------------------

# IAM entry for all users to invoke the function
resource "google_cloudfunctions_function_iam_member" "noop-benchmark_invoker" {
  project        = google_cloudfunctions_function.noop-benchmark_function.project
  region         = google_cloudfunctions_function.noop-benchmark_function.region
  cloud_function = google_cloudfunctions_function.noop-benchmark_function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}

resource "google_cloudfunctions_function_iam_member" "ocr-benchmark_invoker" {
  project        = google_cloudfunctions_function.ocr-benchmark_function.project
  region         = google_cloudfunctions_function.ocr-benchmark_function.region
  cloud_function = google_cloudfunctions_function.ocr-benchmark_function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}

resource "google_cloudfunctions_function_iam_member" "recognition-benchmark_invoker" {
  project        = google_cloudfunctions_function.recognition-benchmark_function.project
  region         = google_cloudfunctions_function.recognition-benchmark_function.region
  cloud_function = google_cloudfunctions_function.recognition-benchmark_function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}

resource "google_cloudfunctions_function_iam_member" "storage-benchmark_invoker" {
  project        = google_cloudfunctions_function.storage-benchmark_function.project
  region         = google_cloudfunctions_function.storage-benchmark_function.region
  cloud_function = google_cloudfunctions_function.storage-benchmark_function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}

resource "google_cloudfunctions_function_iam_member" "synthesis-benchmark_invoker" {
  project        = google_cloudfunctions_function.synthesis-benchmark_function.project
  region         = google_cloudfunctions_function.synthesis-benchmark_function.region
  cloud_function = google_cloudfunctions_function.synthesis-benchmark_function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}

resource "google_cloudfunctions_function_iam_member" "translation-benchmark_invoker" {
  project        = google_cloudfunctions_function.translation-benchmark_function.project
  region         = google_cloudfunctions_function.translation-benchmark_function.region
  cloud_function = google_cloudfunctions_function.translation-benchmark_function.name

  role   = "roles/cloudfunctions.invoker"
  member = "allUsers"
}

