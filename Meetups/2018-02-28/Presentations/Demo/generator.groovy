import jobs.*

def derivatives  = [new Foo_Release_A(),
                    new Foo_Release_B(),
                    new Foo_Release_C(),
                    new Foo_Derivat(),
                    new Foo_Another_Derivat()]

def directory = 'My-Projects'

folder(directory) {
    displayName('My Projects')
    description('Folder for all projects')
}

for ( derivative in derivatives ) {
    def name = derivative.jobname != null ? derivative.jobname : derivative.name

    pipelineJob(directory + '/' + name) {
        println("Generating job $derivative.name")

        if (derivative.cron) {
            triggers {
                cron(derivative.cron)
            }
        }

        parameters {
            stringParam('release', derivative.release(), '')
            stringParam('pathname', derivative.pathname(), '')

            for (param in derivative.GetParams()) {
                if (param.key != 'cron') {
                    println("$param.key - $param.value")
                    stringParam(param.key, param.value, '')
                }
            }

            for (flag in derivative.GetFlags()) {
                println("$flag.key - $flag.value")
                booleanParam(flag.key, flag.value, '')
            }
        }

        definition {
            cps {
                script(readFileFromWorkspace('pipeline.groovy'))
            }
        }
    }
}
