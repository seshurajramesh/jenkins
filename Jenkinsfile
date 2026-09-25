pipeline{
    agent any

    triggers {
        pollSCM('* * * * *')
    }

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
        stage('move the conf file to server'){
                steps{
                    sshagent([params.DB_CREDENTIALS_ID]) {
                        sh """
                            set -e
                            scp -o StrictHostKeyChecking=no ${env.CONF_FILENAME} ${params.PG_SERVICE}@${params.DB_HOST}:/tmp/${env.CONF_FILENAME}.new
                        """
                    }
                }
            }
        }
}
