pipeline{
    agent any
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
        stages{
            stage('move the conf file to server'){
                steps{
                    sshagent([params.DB_CREDENTIALS_ID]) {
                        sh """
                            set -e
                            scp -o StrictHostKeyChecking=no ${env.CONF_FILENAME} ${params.DB_USER}@${params.DB_HOST}:/tmp/${env.CONF_FILENAME}.new
                        """
                    }
                }
            }
        }
    }
}
