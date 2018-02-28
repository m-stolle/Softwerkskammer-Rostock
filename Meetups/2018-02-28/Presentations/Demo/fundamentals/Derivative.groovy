package fundamentals

class Derivative {
    // job attributers
    public String jobname
    public String name
    public String recipients = '$DEFAULT_RECIPIENTS'
    public String cron
    public String label = 'actual'
    public boolean isRelease = false

    String pathname() {
        return this.name.replaceFirst("_", "/")
    }

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
