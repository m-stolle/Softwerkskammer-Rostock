package jobs

import fundamentals.*

class Cyclic extends Derivative {
    Cyclic() {
        this.cron = 'H H 1,8,15,22 1-12 *'
    }
}
