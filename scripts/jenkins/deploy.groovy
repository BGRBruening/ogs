#!/usr/bin/env groovy
node {
    stage('Deploy to S3') {
        deleteDir()
        step([$class: 'CopyArtifact',
            fingerprintArtifacts: true, flatten: true,
            projectName: 'OGS-6/ufz/master',
            selector: [$class: 'LastCompletedBuildSelector']])
            s3upload('*')
        build job: 'OGS-6/Deploy-Post', wait: false
    }
}

def s3upload(files) {
    step([$class: 'S3BucketPublisher',
        dontWaitForConcurrentBuildCompletion: true, entries:
        [[bucket: 'opengeosys', excludedFile: '', flatten: true, gzipFiles: false,
            managedArtifacts: true, noUploadOnFailure: true, selectedRegion: 'eu-central-1',
            sourceFile: "${files}", storageClass: 'STANDARD', uploadFromSlave: true,
            useServerSideEncryption: false]], profileName: 'S3 UFZ', userMetadata: []])
}
