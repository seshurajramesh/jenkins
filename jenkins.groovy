pipeline{

    environment{
        RESTART_NEEDED = 'false'
        CONF_FILENAME = 'postgresql.conf'
    }

    stages{
        stage('check git repo'){
            steps{
                checkout scm
            }
        }
        stage('list the git repo'){
            steps{
                sh 'ls -ltr'
            }
        }

    }
