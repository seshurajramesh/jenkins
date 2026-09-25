pipeline{
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    environment{
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
                            echo "File ${env.CONF_FILENAME} moved to /tmp/${env.CONF_FILENAME}.new on ${params.DB_HOST}"
                        """
                    }
                }
            }

            stage('move the conf file from /tmp to pgdata path and reload'){
                steps{
                    sshagent([params.DB_CREDENTIALS_ID]) {
                        sh """
                            set -e
                            ssh -o StrictHostKeyChecking=no ${params.PG_SERVICE}@${params.DB_HOST}<<'EOF'
set -e
cp ${params.PG_CONF_DEST}/${env.CONF_FILENAME} ${params.PG_CONF_DEST}/backup_confs/${env.CONF_FILENAME}.\$(date +%Y%m%d_%H%M%S)
mv /tmp/${env.CONF_FILENAME}.new ${params.PG_CONF_DEST}/${env.CONF_FILENAME}
echo "File ${env.CONF_FILENAME} moved to ${params.PG_CONF_DEST}/${env.CONF_FILENAME} on ${params.DB_HOST}"
chmod 600 ${params.PG_CONF_DEST}/${env.CONF_FILENAME}
pg_ctl reload -D ${params.PG_CONF_DEST}
EOF
                        """
                    }
                }
            }

            stage('check if restart is needed'){
                steps{
                    sshagent([params.DB_CREDENTIALS_ID]) {
                        script {
                            def restartNeeded = sh(script: """ssh -o StrictHostKeyChecking=no ${params.PG_SERVICE}@${params.DB_HOST} \
                            'psql -t -A -c "select name from pg_settings where pending_restart = true;"'""",returnStdout: true).trim()
                            if (restartNeeded) {
                                env.RESTART_NEEDED = 'true'
                                echo "Restart is needed for the following settings on ${params.DB_HOST}: ${restartNeeded}"
                            } else {
                                env.RESTART_NEEDED = 'false'
                                echo "Restart is not needed on ${params.DB_HOST}"
                            }
                        }
                    }
                    }
            }

            stage('restart postgres if needed'){
                when {
                    expression { env.RESTART_NEEDED == 'true' }
                }
                steps{
                    sshagent([params.DB_CREDENTIALS_ID]) {
                        sh """
                            set -e
                            ssh -o StrictHostKeyChecking=no ${params.PG_SERVICE}@${params.DB_HOST} "systemctl status postgresql"
                            echo "Postgres service ${params.PG_SERVICE} restarted on ${params.DB_HOST}"
                        """
                    }
                }
}
}
}
