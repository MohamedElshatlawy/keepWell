node {
    stage('deploy') {
	checkout scm
	sh 'cp "res/local.properties" "app/local.properties"'
        sh 'bundle install'
        sh 'bundle exec "fastlane firebase"'
    }
}