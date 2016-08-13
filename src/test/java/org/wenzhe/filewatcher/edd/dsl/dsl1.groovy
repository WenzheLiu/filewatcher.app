class A {
  def forward = 'forward'
  def left = 'left'
  def then = 'then'
  def fast = 'fast'
  def right = 'right'
  def to = ""
  
  def move(dir) {
    println "moveing $dir"
    this
  }
  def and(then) {this}
  def turn(dir) {
    println "turning $dir"
    this
  }
  def jump(speed, dir) {
    println "jumping $speed, and $dir"
    this
  }
  def run() {
    start to move forward and then end to turn left
  }
  def start(to) {
    println "start to"
    this
  }
  def end(to) {
    println "end to"
    this
  }
}
new A().run()
