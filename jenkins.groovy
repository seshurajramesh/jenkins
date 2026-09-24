pipeline{

    environment{
        RESTART_NEEDED = 'false'
        CONF_FILENAME = 'postgresql.conf'
    }

    stages{
        steps('check git repo'){
            checkout scm
        }
    }

    stages{
        steps('list git'){
            sh {
                ls -la
            }
        }
    }
}