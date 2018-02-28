class Derivative {
    // job attributers
    public String jobname
    public String name
    public String recipients = '$DEFAULT_RECIPIENTS'
    public String cron
    public String label = 'actual'
    public boolean isRelease = false

    String release() {
        return this.name.split("_", 2)[1]
    }

    Map GetParams() {
        def flags = [:]

        Derivative.declaredFields
            .findAll { !it.synthetic && it.type == String }
            .each { flags[it.name] = it.get(this) }
        return flags
    }

    Map GetFlags() {
        def flags = [:]
        Derivative.declaredFields
            .findAll { !it.synthetic && it.type == boolean}
            .each { flags[it.name] = it.get(this) }
        this.getClass().declaredFields
            .findAll { !it.synthetic && it.type == boolean}
            .each { flags[it.name] = it.get(this) }
        return flags
    }
}

class Cyclic extends Derivative {
    Cyclic() {
        this.cron = 'H H 1,8,15,22 1-12 *'
    }
}

class Foo_Derivat extends Cyclic {
    Foo_Derivat() {
        this.name = 'Foo_Derivat'
    }
}

class Foo_Release_A extends Derivative {
    Foo_Release_A() {
        this.name = 'Foo_Release_A'
        this.isRelease = true
    }
}

class Foo_Release_B extends Foo_Release_A {
    Foo_Release_B() {
        this.name = 'Foo_Release_B'
    }
}

class Foo_Release_C extends Foo_Release_A {
    Foo_Release_C() {
        this.name = 'Foo_Release_C'
    }
}

class Another_Derivat extends Derivative {
    Another_Derivat() {
        this.name = 'Another_Derivat'
    }
}


def derivatives  = [new Foo_Release_A(),
                    new Foo_Release_B(),
                    new Foo_Release_C(),
                    new Foo_Derivat(),
                    new Another_Derivat()]

def directory = 'My-Projects'

folder(directory) {
    displayName('My Projects')
    description('Folder for all projects')
}

for ( derivative in derivatives ) {
    def name = derivative.jobname != null ? derivative.jobname : derivative.name

    job(directory + '/' + name) {
        println("Generating job $derivative.name")

        if (derivative.cron) {
            triggers {
                cron(derivative.cron)
            }
        }

        parameters {
            stringParam('release', derivative.release(), '')

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

        steps {
            shell('echo $foo')
        }
    }
}
