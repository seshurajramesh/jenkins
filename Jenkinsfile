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

                            ssh -o StrictHostKeyChecking=no ${params.PG_SERVICE}@${params.DB_HOST} << 'EOF'
                                set -e
                                cp ${params.PG_CONF_DEST}/${env.CONF_FILENAME} ${params.PG_CONF_DEST}/backup_confs/${env.CONF_FILENAME}.\$(date +%Y%m%d_%H%M%S)
                                mv /tmp/${env.CONF_FILENAME}.new ${params.PG_CONF_DEST}/${env.CONF_FILENAME}
                                chmod 600 ${params.PG_CONF_DEST}/${env.CONF_FILENAME}
                                pg_ctl reload -D ${params.PG_CONF_DEST}
                                EOF                        
                        """
                    }
                }
            }
        }
}
