package as.ama.startup

import akka.actor.{ Actor, Props }
import java.lang.reflect.Constructor

/**
 * Create Props (Akka initializer) needed by actor system to create instance of an actor.
 *
 * @param clazzName class name to instantiate
 */
class PropsCreator(clazzName: String, amaConfig: AmaConfig) extends Serializable {
  def create: Props = {
    val cons = getConstructor(Class.forName(clazzName))
    Props(cons.newInstance(amaConfig).asInstanceOf[Actor])
  }

  /**
   * Will look for constructor that takes AmaConfig (it its subclass) as argument.
   */
  protected def getConstructor(clazz: Class[_]): Constructor[_] = {
    clazz.getConstructors.find { cons =>
      val parameterTypes = cons.getParameterTypes
      if (parameterTypes.length == 1) {
        parameterTypes(0).asSubclass(classOf[AmaConfig])
        true
      } else {
        false
      }
    }.get
  }
}