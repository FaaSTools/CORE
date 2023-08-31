#!/bin/bash

source_names=( "$@" )

source_path=../deployment-packages/
region=us-east-1
entry_point=function.NoOpFunction
role_arn=arn:aws:iam::178612579491:role/LabRole
bucket_name=deployment-time-bucket-core

# create bucket
# shellcheck disable=SC2129
aws s3api create-bucket --bucket $bucket_name --region $region >>/dev/null

start_upload=$(date +%s)
# shellcheck disable=SC2068
for i in ${!source_names[@]}; do
    source_name=${source_names[$i]}
    aws s3 cp $source_path$source_name s3://$bucket_name >>/dev/null
done
wait
end_upload=$(date +%s)

start_deploy=$(date +%s)
# shellcheck disable=SC2068
for i in ${!source_names[@]}; do
    source_name=${source_names[$i]}
    aws lambda create-function --function-name my-function-$i \
    --code S3Bucket=$bucket_name,S3Key=$source_name \
    --handler $entry_point \
    --runtime java11 \
    --region us-east-1 \
    --role $role_arn >>/dev/null
done
wait
end_deploy=$(date +%s)

upload_time=$((end_upload - start_upload))
echo "Upload: $upload_time seconds"
deployment_time=$((end_deploy - start_deploy))
echo "Deployment: $deployment_time seconds"

# shellcheck disable=SC2068
for i in ${!source_names[@]}; do
    source_name=${source_names[$i]}
    # delete deployment package
    # shellcheck disable=SC2129
    aws s3 rm --recursive s3://$bucket_name >>/dev/null
    # delete function
    aws lambda delete-function --function-name my-function-$i --region us-east-1 >>/dev/null
done
wait

# delete bucket
aws s3api delete-bucket --bucket $bucket_name --region $region
